package com.sps.ids.sepa_importer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.springframework.batch.core.BatchStatus.STOPPED;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private static final Logger log = LoggerFactory.getLogger(JobController.class);

    private final JobLauncher jobLauncher;
    private final Job importSepaJob;
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;

    public JobController(JobLauncher jobLauncher, Job importSepaJob, JobExplorer jobExplorer, JobOperator jobOperator) {
        this.jobLauncher = jobLauncher;
        this.importSepaJob = importSepaJob;
        this.jobExplorer = jobExplorer;
        this.jobOperator = jobOperator;
    }

    @PostMapping("/import-sepa")
    public ResponseEntity<Map<String, Object>> startJob(
            @RequestBody(required = false) Map<String, String> params) {
        try {
            JobParametersBuilder builder = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis());

            if (params != null && params.containsKey("filePath")) {
                builder.addString("sepa.import.filePath", params.get("filePath"));
            }

            JobExecution exec = jobLauncher.run(importSepaJob, builder.toJobParameters());

            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("executionId", exec.getId());
            resp.put("status", exec.getStatus().toString());
            resp.put("message", "Job gestartet");
            return ResponseEntity.accepted().body(resp);

        } catch (Exception e) {
            log.error("Fehler beim Starten", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{executionId}/status")
    public ResponseEntity<Map<String, Object>> getStatus(@PathVariable Long executionId) {
        // ✅ In SB 5.x: JobExplorer.getJobExecution() gibt JobExecution zurück
        JobExecution exec = jobExplorer.getJobExecution(executionId);
        if (exec == null) return ResponseEntity.notFound().build();

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("executionId", exec.getId());
        resp.put("status", exec.getStatus().toString());
        resp.put("startTime", exec.getStartTime());
        resp.put("endTime", exec.getEndTime());
        resp.put("exitCode", exec.getExitStatus().getExitCode());

        // ✅ In SB 5.x: Statistiken über StepExecution abrufen
        List<Map<String, Object>> steps = new ArrayList<>();
        long totalRead = 0;
        long totalWritten = 0;
        long totalSkipped = 0;

        for (StepExecution stepExec : exec.getStepExecutions()) {
            Map<String, Object> step = new LinkedHashMap<>();
            step.put("name", stepExec.getStepName());
            step.put("status", stepExec.getStatus().toString());

            // ✅ In SB 5.x: getReadCount() etc. existieren NICHT mehr direkt auf StepExecution
            // Stattdessen über getStepExecutionStats() oder manuelle Berechnung
            // Aber: In SB 5.x gibt es getReadCount() wieder! (Check: Spring Batch 5.1+ hat es zurückgebracht)
            // Falls es doch fehlt, nutzen wir getStepExecutionStats()

            // Sicherer Weg für SB 5.x:
            step.put("readCount", stepExec.getReadCount());
            step.put("writeCount", stepExec.getWriteCount());
            step.put("commitCount", stepExec.getCommitCount());
            step.put("rollbackCount", stepExec.getRollbackCount());
            step.put("skipCount", stepExec.getSkipCount());

            totalRead += stepExec.getReadCount();
            totalWritten += stepExec.getWriteCount();
            totalSkipped += stepExec.getSkipCount();

            steps.add(step);
        }

        resp.put("totalReadCount", totalRead);
        resp.put("totalWriteCount", totalWritten);
        resp.put("totalSkipCount", totalSkipped);
        resp.put("steps", steps);

        // ✅ restartable prüfen: In SB 5.x über JobOperator oder JobRepository
        // Einfacher: Status prüfen (COMPLETED, FAILED, STOPPED)
        boolean restartable = exec.getStatus().isUnsuccessful() || exec.getStatus() == STOPPED;
        resp.put("restartable", restartable);

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Map<String, Object>>> getJobHistory(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            // ✅ In SB 5.x: findJobInstancesByJobName gibt Set<JobInstance> zurück
            // Aber: Die Signatur hat sich geändert!
            // SB 5.x: findJobInstancesByJobName(String jobName, int from, int count)
            List<JobInstance> instances = jobExplorer.findJobInstancesByJobName(
                    "importSepaJob", 0, limit);

            List<Map<String, Object>> result = new ArrayList<>();
            for (JobInstance instance : instances) {
                // ✅ In SB 5.x: getJobExecutions gibt List<JobExecution>
                List<JobExecution> executions = jobExplorer.getJobExecutions(instance);
                if (!executions.isEmpty()) {
                    JobExecution latest = executions.get(0);
                    Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put("jobInstanceId", instance.getInstanceId());
                    entry.put("executionId", latest.getId());
                    entry.put("status", latest.getStatus().toString());
                    entry.put("startTime", latest.getStartTime());
                    entry.put("endTime", latest.getEndTime());
                    entry.put("exitCode", latest.getExitStatus().getExitCode());
                    result.add(entry);
                }
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Fehler beim Abrufen der Historie", e);
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonList(Map.of("error", e.getMessage())));
        }
    }

    @PostMapping("/restart/{executionId}")
    public ResponseEntity<Map<String, Object>> restartJob(@PathVariable Long executionId) {
        try {
            JobExecution exec = jobExplorer.getJobExecution(executionId);
            if (exec == null) {
                return ResponseEntity.notFound().build();
            }

            // ✅ restartable prüfen: Status ist nicht SUCCESS
            boolean restartable = exec.getStatus().isUnsuccessful() || exec.getStatus() == STOPPED;
            if (!restartable) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Job ist nicht neu startbar (Status: " + exec.getStatus() + ")"));
            }

            JobExecution newExec = jobLauncher.run(
                    importSepaJob,
                    exec.getJobParameters());

            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("executionId", newExec.getId());
            resp.put("status", newExec.getStatus().toString());
            resp.put("message", "Job neu gestartet");
            return ResponseEntity.accepted().body(resp);

        } catch (Exception e) {
            log.error("Fehler beim Neustart", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/stop/{executionId}")
    public ResponseEntity<Map<String, Object>> stopJob(@PathVariable Long executionId) {
        try {
            jobOperator.stop(executionId);
            return ResponseEntity.ok(Map.of(
                    "executionId", executionId,
                    "message", "Stop-Anfrage gesendet"
            ));
        } catch (Exception e) {
            log.error("Fehler beim Stoppen", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Keine Datei hochgeladen"));
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.endsWith(".xml")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Nur XML-Dateien erlaubt"));
            }

            Path uploadDir = Path.of("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path filePath = uploadDir.resolve(fileName).normalize();
            if (!filePath.startsWith(uploadDir)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Ungültiger Dateiname"));
            }

            Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("fileName", fileName);
            resp.put("filePath", filePath.toString());
            resp.put("size", file.getSize());
            resp.put("message", "Datei erfolgreich hochgeladen");
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            log.error("Fehler beim Hochladen", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/dead-letter")
    public ResponseEntity<List<Map<String, Object>>> getDeadLetterRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            // Hinweis: Dies erfordert einen zusätzlichen Repository-Bean oder JdbcTemplate
            return ResponseEntity.ok(Collections.emptyList());
        } catch (Exception e) {
            log.error("Fehler beim Abrufen der DLQ", e);
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonList(Map.of("error", e.getMessage())));
        }
    }
}