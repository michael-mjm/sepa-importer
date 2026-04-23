package com.sps.ids.swift_importer.batch.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CodesIsoMappingTasklet implements Tasklet {

    private final JdbcTemplate jdbc;

    public CodesIsoMappingTasklet(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public RepeatStatus execute(
        StepContribution contribution,
        ChunkContext chunkContext) {

        jdbc.execute("""
            INSERT INTO swift.codes_iso (
                code_set_group,
                code_set_name,
                code,
                source_record_key,
                short_name,
                long_name,
                description,
                valid_from,
                valid_to,
                source_file
            )
            SELECT
                'ISO20022',              -- Code set group (constant context)
                record_structure,        -- e.g. COUNTRY, ExternalPurpose1Code
                attribute_2,             -- actual code
                record_key,
                attribute_1,
                attribute_3,
                attribute_4,
                TRY_CONVERT(date, start_date),
                TRY_CONVERT(date, stop_date),
                import_file
            FROM swift_stg.stg_codes_iso
            WHERE record_content_type = 'D'
              AND record_status = 'C';
            
        """);

        return RepeatStatus.FINISHED;
    }
}