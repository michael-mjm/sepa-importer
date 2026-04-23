package com.sps.ids.swift_importer.structure.runtime;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds StructureDefinitions for the current import run.
 *
 * Lifecycle:
 *  - cleared at job start
 *  - populated after Structure job
 *  - read-only during data imports
 */
@Component
public class StructureRegistry {

    private final Map<String, StructureDefinition> definitions =
        new ConcurrentHashMap<>();

    public void register(StructureDefinition def) {
        definitions.put(def.getRecordStructure(), def);
    }

    public StructureDefinition require(String recordStructure) {
        StructureDefinition def = definitions.get(recordStructure);
        if (def == null) {
            throw new IllegalStateException(
                "No StructureDefinition found for record_structure=" + recordStructure
            );
        }
        return def;
    }

    public void clear() {
        definitions.clear();
    }

    public boolean isEmpty() {
        return definitions.isEmpty();
    }
}