package com.sps.ids.swift_importer.structure.job;

import com.sps.ids.swift_importer.listener.SwiftImportLogListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StructureJobConfig {

    @Bean
    public Job importStructuresJob(
        JobRepository jobRepository,
        Step cleanupStructureStagingStep,
        Step loadStructureStagingStep,
        SwiftImportLogListener logListener
    ) {
        return new JobBuilder("importStructuresJob", jobRepository)
            .start(cleanupStructureStagingStep)
            .next(loadStructureStagingStep)
            .listener(logListener)
            .build();
    }
}

