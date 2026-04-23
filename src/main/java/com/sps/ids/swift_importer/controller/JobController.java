package com.sps.ids.swift_importer.controller;

import com.sps.ids.swift_importer.mapper.DlqRecordMapper;
import com.sps.ids.swift_importer.model.SwiftDeadLetterRecord;
import com.sps.ids.swift_importer.model.SwiftRecord;
import com.sps.ids.swift_importer.repository.DeadLetterQueueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final JobExplorer jobExplorer;
//    private final Job importSwiftJob;
    private final JobOperator jobOperator;
    private final DeadLetterQueueRepository dlqRepo;
    private final DlqRecordMapper dlqRecordMapper;
    private final ItemWriter<SwiftRecord> swiftWriter;
    // Domain-Jobs
    private final Job importSwiftMasterJob;
    private final Job importStructuresJob;
    private final Job importCodesIsoJob;
    private final Job importIdentifiersAllJob;

    public JobController(JobLauncher jobLauncher,
//                         Job importSwiftJob,
                         JobExplorer jobExplorer,
                         JobOperator jobOperator,
                         DeadLetterQueueRepository dlqRepo,
                         DlqRecordMapper dlqRecordMapper, @Qualifier("writer") ItemWriter<SwiftRecord> swiftWriter,
                         Job importSwiftMasterJob,
                         @Qualifier("importStructuresJob") Job importStructuresJob,
                         @Qualifier("importCodesIsoJob") Job importCodesIsoJob,
                         @Qualifier("importIdentifiersAllJob") Job importIdentifiersAllJob) {
        this.jobLauncher = jobLauncher;
//        this.importSwiftJob = importSwiftJob;
        this.jobExplorer = jobExplorer;
        this.jobOperator = jobOperator;
        this.dlqRepo = dlqRepo;
        this.dlqRecordMapper = dlqRecordMapper;
        this.swiftWriter = swiftWriter;
        this.importSwiftMasterJob = importSwiftMasterJob;
        this.importStructuresJob = importStructuresJob;
        this.importCodesIsoJob = importCodesIsoJob;
        this.importIdentifiersAllJob = importIdentifiersAllJob;
    }

    @PostMapping("/import/{domain}")
    public ResponseEntity<?> startDomainImport(
        @PathVariable String domain,
        @RequestBody Map<String, String> params) {

        Job job = resolveJob(domain);

        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("timestamp", System.currentTimeMillis())
            .addString("swift.import.filePath", params.get("filePath"))
            .addLong("import_run_id", System.currentTimeMillis())
            .toJobParameters();

        try {
            JobExecution execution = jobLauncher.run(job, jobParameters);

            return ResponseEntity.accepted().body(Map.of(
                "job", job.getName(),
                "executionId", execution.getId(),
                "status", execution.getStatus().toString()
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    private Job resolveJob(String domain) {
        return switch (domain.toLowerCase()) {
            case "swift-all" -> importSwiftMasterJob;
            case "structures-ident", "structures-sepa" -> importStructuresJob;
            case "codes-iso" -> importCodesIsoJob;
            case "identifiers-all" -> importIdentifiersAllJob;
            default -> throw new IllegalArgumentException(
                "Unknown domain: " + domain
            );
        };
    }

//    @PostMapping("/import-swift-data")
//    public ResponseEntity<Map<String, Object>> startJob(
//        @RequestBody(required = false) Map<String, String> params) {
//        try {
//            JobParametersBuilder builder = new JobParametersBuilder()
//                .addLong("timestamp", System.currentTimeMillis());
//
//            if (params != null && params.containsKey("filePath")) {
//                builder.addString("swift.import.filePath", params.get("filePath"));
//            }
//
//            JobExecution exec = jobLauncher.run(importSwiftJob, builder.toJobParameters());
//
//            Map<String, Object> resp = new LinkedHashMap<>();
//            resp.put("executionId", exec.getId());
//            resp.put("status", exec.getStatus().toString());
//            resp.put("message", "Job started");
//            return ResponseEntity.accepted().body(resp);
//
//        } catch (Exception e) {
//            log.error("Error during startup", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(Map.of("error", e.getMessage()));
//        }
//    }

    @GetMapping("/{executionId}/status")
    public ResponseEntity<Map<String, Object>> getStatus(@PathVariable Long executionId) {
        JobExecution exec = jobExplorer.getJobExecution(executionId);
        if (exec == null) return ResponseEntity.notFound().build();

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("executionId", exec.getId());
        resp.put("status", exec.getStatus().toString());
        resp.put("startTime", exec.getStartTime());
        resp.put("endTime", exec.getEndTime());
        resp.put("exitCode", exec.getExitStatus().getExitCode());

        // get statistics via StepExecution
        List<Map<String, Object>> steps = new ArrayList<>();
        long totalRead = 0;
        long totalWritten = 0;
        long totalSkipped = 0;

        for (StepExecution stepExec : exec.getStepExecutions()) {
            Map<String, Object> step = new LinkedHashMap<>();
            step.put("name", stepExec.getStepName());
            step.put("status", stepExec.getStatus().toString());
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

        // Check state (COMPLETED, FAILED, STOPPED)
        boolean restartable = exec.getStatus().isUnsuccessful() || exec.getStatus() == STOPPED;
        resp.put("restartable", restartable);

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Map<String, Object>>> getJobHistory(
        @RequestParam(defaultValue = "10") int limit) {
        try {
            // findJobInstancesByJobName(String jobName, int from, int count)
            List<JobInstance> instances = jobExplorer.findJobInstancesByJobName(
                "importSwiftJob", 0, limit);

            List<Map<String, Object>> result = new ArrayList<>();
            for (JobInstance instance : instances) {
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
            log.error("Error calling history", e);
            return ResponseEntity.internalServerError()
                .body(Collections.singletonList(Map.of("error", e.getMessage())));
        }
    }


    @PostMapping("/restart/{executionId}")
    public ResponseEntity<Map<String, Object>> restart(@PathVariable long executionId) {
        try {
            Long newExecutionId = jobOperator.restart(executionId);

            return ResponseEntity.accepted().body(Map.of(
                "previousExecutionId", executionId,
                "newExecutionId", newExecutionId,
                "message", "Job restarted successfully"
            ));
        } catch (Exception e) {
            log.error("Restart failed for execution {}", executionId, e);
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }


    @PostMapping("/stop/{executionId}")
    public ResponseEntity<Map<String, Object>> stopJob(@PathVariable Long executionId) {
        try {
            jobOperator.stop(executionId);
            return ResponseEntity.ok(Map.of(
                "executionId", executionId,
                "message", "Stop request sent"
            ));
        } catch (Exception e) {
            log.error("Error stopping", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No file uploaded"));
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.endsWith(".xml")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Only XML files allowed"));
            }

            Path uploadDir = Path.of("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path filePath = uploadDir.resolve(fileName).normalize();
            if (!filePath.startsWith(uploadDir)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid filename"));
            }

            Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("fileName", fileName);
            resp.put("filePath", filePath.toString());
            resp.put("size", file.getSize());
            resp.put("message", "File successfully uploaded");
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            log.error("Error uploading", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/dead-letter")
    public ResponseEntity<List<Map<String, Object>>> getDeadLetterRecords(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size) {

        try {
            List<SwiftDeadLetterRecord> records = dlqRepo.findAll(page, size);
            int total = dlqRepo.countTotal();

            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("page", page);
            resp.put("size", size);
            resp.put("total", total);
            resp.put("records", records);

            return ResponseEntity.ok(Collections.singletonList(resp));

        } catch (Exception e) {
            log.error("Error retrieving DLQ", e);
            return ResponseEntity.internalServerError()
                .body(Collections.singletonList(Map.of("error", e.getMessage())));
        }
    }


    @PostMapping("/dead-letter/{id}/resolve")
    public ResponseEntity<?> resolveDlq(@PathVariable long id) {
        dlqRepo.markAsResolved(id);
        return ResponseEntity.ok(Map.of("id", id, "resolved", true));
    }

    @PostMapping("/dead-letter/{id}/retry")
    public ResponseEntity<Map<String, Object>> retryDlqRecord(@PathVariable long id) {
        try {
            SwiftDeadLetterRecord dlq = dlqRepo.findById(id);

            if (dlq.getResolved()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "DLQ record already resolved"));
            }

            // Rekonstruieren
            SwiftRecord record = dlqRecordMapper.toSwiftRecord(dlq);

            swiftWriter.write(new Chunk<>(List.of(record)));

            dlqRepo.markAsResolved(id);

            return ResponseEntity.ok(Map.of(
                "id", id,
                "status", "RETRIED",
                "message", "DLQ record successfully reprocessed"
            ));

        } catch (Exception e) {
            log.error("DLQ retry failed for id {}", id, e);
            dlqRepo.incrementRetryCount(id, e.getMessage());

            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "id", id,
                    "status", "FAILED",
                    "error", e.getMessage()
                ));
        }
    }
}