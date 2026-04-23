package com.sps.ids.swift_importer.batch.orchestration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.JobStepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwiftOrchestrationJobConfig {

    @Bean
    public Job importSwiftMasterJob(
        JobRepository jobRepository,
        Step importStructuresStep,
        Step importCodesIsoStep,
        Step importIdentifiersAllStep) {

        return new JobBuilder("importSwiftMasterJob", jobRepository)
            .start(importStructuresStep)
            .next(importCodesIsoStep)
            .next(importIdentifiersAllStep)
            .build();
    }

    @Bean
    public Step importStructuresStep(
        JobRepository jobRepository,
        JobLauncher jobLauncher,
        Job importStructuresJob) {

        return new JobStepBuilder(
            new StepBuilder("importStructuresStep", jobRepository)
        )
            .job(importStructuresJob)
            .launcher(jobLauncher)
            .build();
    }

    @Bean
    public Step importCodesIsoStep(
        JobRepository jobRepository,
        JobLauncher jobLauncher,
        Job importCodesIsoJob) {

        return new JobStepBuilder(
            new StepBuilder("importCodesIsoStep", jobRepository)
        )
            .job(importCodesIsoJob)
            .launcher(jobLauncher)
            .build();
    }

    @Bean
    public Step importIdentifiersAllStep(
        JobRepository jobRepository,
        JobLauncher jobLauncher,
        Job importIdentifiersAllJob) {

        return new JobStepBuilder(
            new StepBuilder("importIdentifiersAllStep", jobRepository)
        )
            .job(importIdentifiersAllJob)
            .launcher(jobLauncher)
            .build();
    }
}
