package com.sps.ids.swift_importer.writer;

import com.sps.ids.swift_importer.model.SwiftRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@StepScope
public class ErrorLoggingWriter implements ItemWriter<SwiftRecord> {

    private static final Logger log = LoggerFactory.getLogger(ErrorLoggingWriter.class);

    private final JdbcTemplate jdbc;
    private final ItemWriter<SwiftRecord> delegate;

    @Value("#{jobParameters['import_run_id']}")
    private Long importRunId;

    public ErrorLoggingWriter(
        DataSource dataSource,
        ItemWriter<SwiftRecord> delegate) {

        this.jdbc = new JdbcTemplate(dataSource);
        this.delegate = delegate;
    }

    @Override
    public void write(Chunk<? extends SwiftRecord> chunk) throws Exception {

        try {
            delegate.write(chunk);
        } catch (Exception ex) {

            for (SwiftRecord rec : chunk.getItems()) {
                saveToDlq(rec, ex);
            }
            throw ex;
        }
    }

    private void saveToDlq(SwiftRecord rec, Exception ex) {

        jdbc.update("""
        INSERT INTO swift.dead_letter_queue (
            import_run_id,
            source_table,
            domain,
            record_key,
            modification_type,
            payload,
            error_message,
            error_type
        )
        VALUES (?,?,?,?,?,?,?,?)
    """,
            importRunId,
            rec.getStagingTable(),
            rec.getDomain(),
            rec.getRecordKey(),
            rec.getModificationType(),
            DlqJsonMapper.toJson(rec),
            ex.getMessage(),
            ex.getClass().getSimpleName()
        );

        log.warn("Record {} written to DLQ", rec.getRecordKey());
    }
}