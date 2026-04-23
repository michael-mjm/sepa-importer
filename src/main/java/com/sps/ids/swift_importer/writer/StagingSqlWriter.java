package com.sps.ids.swift_importer.writer;

import com.sps.ids.swift_importer.model.SwiftRecord;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@StepScope
public class StagingSqlWriter implements ItemWriter<SwiftRecord> {

    private final NamedParameterJdbcTemplate jdbc;

    @Value("#{jobParameters['import_run_id']}")
    private Long importRunId;

    @Value("#{jobParameters['swift.import.filePath']}")
    private String importFile;

    public StagingSqlWriter(DataSource dataSource) {
        this.jdbc = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void write(Chunk<? extends SwiftRecord> chunk) {
        for (SwiftRecord rec : chunk) {

            Map<String, Object> params = new HashMap<>();

            // technical metadata
            params.put("import_run_id", importRunId);
            params.put("import_file", importFile);

            // Core XSD fields
            params.put("modification_type", rec.getModificationType());
            params.put("record_key", rec.getRecordKey());
            params.put("record_structure", rec.getRecordStructure());
            params.put("record_content_type", rec.getRecordContentType());
            params.put("record_status", rec.getRecordStatus());
            params.put("start_date", rec.getStartDate());
            params.put("stop_date", rec.getStopDate());

            // Attributes
            rec.getAttributes().forEach((k, v) -> {
                if (v != null && !v.isBlank()) {
                    params.put(k, v);
                }
            });

            String table = rec.getStagingTable(); // table should be swift_stg.*

            String sql = buildInsertSql(table, params);

            jdbc.update(sql, params);
        }
    }

    private String buildInsertSql(String table, Map<String, Object> params) {

        String columns = String.join(", ", params.keySet());
        String values  = ":" + String.join(", :", params.keySet());

        return "INSERT INTO " + table +
            " (" + columns + ") VALUES (" + values + ")";
    }
}
