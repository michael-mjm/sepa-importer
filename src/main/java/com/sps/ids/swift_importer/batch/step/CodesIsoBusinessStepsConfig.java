package com.sps.ids.swift_importer.batch.step;

import com.sps.ids.swift_importer.batch.tasklet.CleanupCodesIsoBusinessTasklet;
import com.sps.ids.swift_importer.batch.tasklet.CodesIsoMappingTasklet;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CodesIsoBusinessStepsConfig {

    @Bean
    public Step cleanupCodesIsoBusinessStep(
        JobRepository jobRepository,
        PlatformTransactionManager txMgr,
        CleanupCodesIsoBusinessTasklet tasklet) {

        return new StepBuilder("cleanupCodesIsoBusinessStep", jobRepository)
            .tasklet(tasklet, txMgr)
            .build();
    }

    @Bean
    public Step mapCodesIsoStep(
        JobRepository jobRepository,
        PlatformTransactionManager txMgr,
        CodesIsoMappingTasklet tasklet) {

        return new StepBuilder("mapCodesIsoStep", jobRepository)
            .tasklet(tasklet, txMgr)
            .build();
    }
}
