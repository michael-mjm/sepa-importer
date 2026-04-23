package com.sps.ids.swift_importer.batch;


import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;

public enum ImportStatus {

    STARTED,
    COMPLETED,

    FAILED_XSD,          // XSD / Schema error (before reading)
    FAILED_PROCESSING,   // Runtime error in batch
    FAILED_DB,           // DB / Deadlock / Constraint
    FAILED_UNKNOWN;

    public static ImportStatus fromJobExecution(JobExecution job) {

        ExitStatus exit = job.getExitStatus();

        if (exit.getExitCode().startsWith("XSD_")) {
            return FAILED_XSD;
        }

        return switch (job.getStatus()) {
            case COMPLETED -> COMPLETED;
            case FAILED -> FAILED_PROCESSING;
            default -> FAILED_UNKNOWN;
        };
    }
}

