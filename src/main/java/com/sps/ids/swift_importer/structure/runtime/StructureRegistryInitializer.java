package com.sps.ids.swift_importer.structure.runtime;

import com.sps.ids.swift_importer.structure.repository.StructureRepository;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class StructureRegistryInitializer implements JobExecutionListener {

    private final StructureRegistry registry;
    private final StructureRepository repo;

    public StructureRegistryInitializer(
        StructureRegistry registry,
        StructureRepository repo) {
        this.registry = registry;
        this.repo = repo;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        registry.clear();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (registry.isEmpty()) {
            repo.loadAll().forEach(registry::register);
        }
    }
}