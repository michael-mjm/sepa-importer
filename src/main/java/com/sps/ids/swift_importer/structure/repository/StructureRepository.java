package com.sps.ids.swift_importer.structure.repository;

import com.sps.ids.swift_importer.structure.runtime.AttributeDefinition;
import com.sps.ids.swift_importer.structure.runtime.AttributeFormatParser;
import com.sps.ids.swift_importer.structure.runtime.StructureDefinition;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class StructureRepository {

    private final JdbcTemplate jdbc;

    public StructureRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<StructureDefinition> loadAll() {

        String sql = """
            SELECT record_structure,
                   attribute_no,
                   semantic_name,
                   mandatory_flag
            FROM swift_stg.stg_structure_attr
        """;

        Map<String, Map<Integer, AttributeDefinition>> grouped =
            new HashMap<>();

        jdbc.query(sql, rs -> {
            String structure = rs.getString("record_structure");
            int no = rs.getInt("attribute_no");

            grouped
                .computeIfAbsent(structure, k -> new HashMap<>())
                .put(no,
                    new AttributeDefinition(
                        no,
                        rs.getString("semantic_name"),
                        "Y".equals(rs.getString("mandatory_flag")),
                        AttributeFormatParser.parse(
                            rs.getString("attribute_format")
                        )
                    )
                );
        });

        List<StructureDefinition> result = new ArrayList<>();
        grouped.forEach((structure, attrs) ->
            result.add(new StructureDefinition(structure, attrs))
        );
        return result;
    }
}
