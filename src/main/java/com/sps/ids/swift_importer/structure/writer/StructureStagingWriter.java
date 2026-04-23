package com.sps.ids.swift_importer.structure.writer;

import com.sps.ids.swift_importer.structure.model.StructureElement;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Writes Structure XML attribute definitions into staging tables.
 *
 * This writer is used for STRUCTURE XML imports only
 * (e.g. structures-identd-v1, structures-sepad-v1).
 *
 * The data is strictly technical metadata and must not be
 * propagated to the business layer.
 */
@StepScope
public class StructureStagingWriter implements ItemWriter<StructureElement> {

    private final NamedParameterJdbcTemplate jdbc;

    @Value("#{jobParameters['import_run_id']}")
    private Long importRunId;

    @Value("#{jobParameters['swift.import.filePath']}")
    private String importFile;

    public StructureStagingWriter(DataSource dataSource) {
        this.jdbc = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void write(Chunk<? extends StructureElement> chunk) {

        for (StructureElement el : chunk) {

            Map<String, Object> params = new HashMap<>();

            params.put("structure_name", el.getStructureName());
            params.put("file_version", el.getFileVersion());
            params.put("record_structure", el.getRecordStructure());
            params.put("attribute_no", el.getAttributeNo());
            params.put("semantic_name", el.getSemanticName());
            params.put("attribute_label", el.getAttributeLabel());
            params.put("attribute_description", el.getDescription());
            params.put("attribute_format", el.getFormat());
            params.put("mandatory_flag", el.isMandatory() ? "Y" : "N");
            params.put("natural_key_flag", el.isNaturalKey() ? "Y" : "N");

            params.put("import_run_id", importRunId);
            params.put("import_file", importFile);

            jdbc.update(INSERT_SQL, params);
        }
    }

    private static final String INSERT_SQL = """
        INSERT INTO swift_stg.stg_structure_attr (
            structure_name,
            file_version,
            record_structure,
            attribute_no,
            semantic_name,
            attribute_label,
            attribute_description,
            attribute_format,
            mandatory_flag,
            natural_key_flag,
            import_run_id,
            import_file,
            load_ts
        )
        VALUES (
            :structure_name,
            :file_version,
            :record_structure,
            :attribute_no,
            :semantic_name,
            :attribute_label,
            :attribute_description,
            :attribute_format,
            :mandatory_flag,
            :natural_key_flag,
            :import_run_id,
            :import_file,
            SYSDATETIME()
        )
        """;
}