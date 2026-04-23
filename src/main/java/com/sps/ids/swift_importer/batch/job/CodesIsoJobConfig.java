package com.sps.ids.swift_importer.batch.job;

import com.sps.ids.swift_importer.listener.SwiftImportLogListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CodesIsoJobConfig {

    @Bean
    public Job importCodesIsoJob(
        JobRepository jobRepository,
        Step cleanupCodesIsoStagingStep,
        Step loadCodesIsoStagingStep,
        Step cleanupCodesIsoBusinessStep,
        Step mapCodesIsoStep,
        SwiftImportLogListener logListener) {

        return new JobBuilder("importCodesIsoJob", jobRepository)
            .start(cleanupCodesIsoStagingStep)
            .next(loadCodesIsoStagingStep)
            .next(cleanupCodesIsoBusinessStep)
            .next(mapCodesIsoStep)
            .listener(logListener)
            .build();
    }
}
