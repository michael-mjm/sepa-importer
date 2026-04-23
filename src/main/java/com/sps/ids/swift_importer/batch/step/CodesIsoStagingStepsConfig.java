package com.sps.ids.swift_importer.batch.step;

import com.sps.ids.swift_importer.batch.tasklet.CleanupCodesIsoStagingTasklet;
import com.sps.ids.swift_importer.exception.InvalidRecordException;
import com.sps.ids.swift_importer.listener.CustomSkipListener;
import com.sps.ids.swift_importer.listener.SwiftXsdValidationListener;
import com.sps.ids.swift_importer.model.SwiftRecord;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CodesIsoStagingStepsConfig {

    @Bean
    public Step cleanupCodesIsoStagingStep(
        JobRepository jobRepository,
        PlatformTransactionManager txMgr,
        CleanupCodesIsoStagingTasklet tasklet) {

        return new StepBuilder("cleanupCodesIsoStagingStep", jobRepository)
            .tasklet(tasklet, txMgr)
            .build();
    }

    @Bean
    public Step loadCodesIsoStagingStep(
        JobRepository jobRepository,
        PlatformTransactionManager txMgr,
        ItemReader<SwiftRecord> reader,
        ItemWriter<SwiftRecord> writer,
        SwiftXsdValidationListener xsdListener,
        CustomSkipListener skipListener) {

        return new StepBuilder("loadCodesIsoStagingStep", jobRepository)
            .<SwiftRecord, SwiftRecord>chunk(1000, txMgr)
            .reader(reader)
            .writer(writer)

            // ---- VALIDATION ----
            .listener(xsdListener)

            // ---- FAULT TOLERANCE ----
            .faultTolerant()

            // Retry: technical errors (DB, locks, network)
            .retryLimit(3)
            .retry(org.springframework.dao.CannotAcquireLockException.class)
            .retry(org.springframework.dao.TransientDataAccessResourceException.class)

            // Skip: technical errors → DLQ
            .skip(InvalidRecordException.class)
            .skipLimit(2000)

            .listener(skipListener)

            .build();
    }
}
