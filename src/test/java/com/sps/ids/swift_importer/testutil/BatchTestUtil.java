package com.sps.ids.swift_importer.testutil;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;

public final class BatchTestUtil {

    private BatchTestUtil() {
    }

    public static JobExecution newJobExecution() {
        JobParameters jobParameters = new JobParametersBuilder()
            .addString("swift.import.filePath", "test.xml")
            .toJobParameters();

        return new JobExecution(1L, jobParameters);
    }

    public static JobExecution newJobExecutionWithStep(
        int writeCount,
        int skipCount) {

        JobExecution jobExecution = newJobExecution();

        StepExecution step = jobExecution.createStepExecution("testStep");

        step.setWriteCount(writeCount);

        if (skipCount > 0) {
            step.setProcessSkipCount(skipCount);
        }

        step.setRollbackCount(0);

        return jobExecution;
    }
}