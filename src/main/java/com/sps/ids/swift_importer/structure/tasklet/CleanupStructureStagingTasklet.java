package com.sps.ids.swift_importer.structure.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Cleans up structure staging tables before importing
 * STRUCTURE XML files.
 *
 * This tasklet is designed for FULL imports only
 * (truncate and reload).
 */
@Component
public class CleanupStructureStagingTasklet implements Tasklet {

    private final JdbcTemplate jdbc;

    public CleanupStructureStagingTasklet(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public RepeatStatus execute(
        StepContribution contribution,
        ChunkContext chunkContext) {

        // Order matters if FK constraints are ever introduced
        jdbc.execute("TRUNCATE TABLE swift_stg.stg_structure_attr");
        jdbc.execute("TRUNCATE TABLE swift_stg.stg_structure_def");

        return RepeatStatus.FINISHED;
    }
}