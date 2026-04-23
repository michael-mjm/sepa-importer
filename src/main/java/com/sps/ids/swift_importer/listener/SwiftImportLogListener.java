package com.sps.ids.swift_importer.listener;

import com.sps.ids.swift_importer.batch.ImportStatus;
import com.sps.ids.swift_importer.repository.ImportLogRepository;
import org.springframework.batch.core.*;
import org.springframework.stereotype.Component;

@Component
public class SwiftImportLogListener implements JobExecutionListener {

    private final ImportLogRepository repo;

    private Long logId;

    public SwiftImportLogListener(ImportLogRepository repo) {
        this.repo = repo;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {

        String fileName = jobExecution
            .getJobParameters()
            .getString("swift.import.filePath");

        Long jobExecutionId = jobExecution.getId();

        logId = repo.create(fileName, jobExecutionId);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        int processed = 0;
        int errors = 0;

        for (StepExecution step : jobExecution.getStepExecutions()) {
            processed += step.getWriteCount();
            errors += step.getSkipCount();
            errors += step.getRollbackCount();
        }


        ImportStatus status = ImportStatus.fromJobExecution(jobExecution);

        repo.finish(logId, processed, errors, status.name());
    }
}