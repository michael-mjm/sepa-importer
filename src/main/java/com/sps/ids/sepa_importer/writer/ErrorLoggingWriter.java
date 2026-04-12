package com.sps.ids.sepa_importer.writer;

import com.sps.ids.sepa_importer.entity.SepaStructureRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class ErrorLoggingWriter implements ItemWriter<SepaStructureRecord> {

    private static final Logger log = LoggerFactory.getLogger(ErrorLoggingWriter.class);

    private final JdbcTemplate jdbc;
    private final ItemWriter<SepaStructureRecord> delegate;

    public ErrorLoggingWriter(DataSource dataSource, ItemWriter<SepaStructureRecord> delegate) {
        this.jdbc = new JdbcTemplate(dataSource);
        this.delegate = delegate;
    }

    @Override
    public void write(Chunk<? extends SepaStructureRecord> chunk) throws Exception {
        try {
            delegate.write(chunk);
        } catch (Exception e) {
            log.error("Batch-Schreiben fehlgeschlagen", e);
            for (SepaStructureRecord rec : chunk.getItems()) {
                saveToDlq(rec, e.getMessage());
            }
            throw e;
        }
    }

    private void saveToDlq(SepaStructureRecord rec, String errMsg) {
        jdbc.update(
                "INSERT INTO SepaDeadLetterQueue (RecordKey, ModificationType, RawData, ErrorMessage) VALUES (?,?,?,?)",
                rec.getRecordKey(), rec.getModificationType(), rec.toString(), errMsg
        );
        log.warn("Record {} in DLQ gespeichert", rec.getRecordKey());
    }
}