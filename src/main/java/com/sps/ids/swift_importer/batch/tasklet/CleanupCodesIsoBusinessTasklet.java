package com.sps.ids.swift_importer.batch.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CleanupCodesIsoBusinessTasklet implements Tasklet {

    private final JdbcTemplate jdbc;

    public CleanupCodesIsoBusinessTasklet(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public RepeatStatus execute(
        StepContribution contribution,
        ChunkContext chunkContext) {

        jdbc.execute("TRUNCATE TABLE swift.codes_iso");
        return RepeatStatus.FINISHED;
    }
}