package com.sps.ids.swift_importer.batch.job;

import com.sps.ids.swift_importer.listener.SwiftImportLogListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class IdentifiersAllJobConfig {

    @Bean
    public Job importIdentifiersAllJob(
        JobRepository jobRepository,
        Step cleanupIdentifiersAllStagingStep,
        Step loadIdentifiersAllStagingStep,
        SwiftImportLogListener logListener) {

        return new JobBuilder("importIdentifiersAllJob", jobRepository)
            .start(cleanupIdentifiersAllStagingStep)
            .next(loadIdentifiersAllStagingStep)
            .listener(logListener)
            .build();
    }
}
