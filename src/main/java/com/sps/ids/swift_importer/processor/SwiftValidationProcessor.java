package com.sps.ids.swift_importer.processor;

import com.sps.ids.swift_importer.exception.InvalidRecordException;
import com.sps.ids.swift_importer.model.SwiftRecord;
import com.sps.ids.swift_importer.structure.runtime.StructureDefinition;
import com.sps.ids.swift_importer.structure.runtime.StructureRegistry;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class SwiftValidationProcessor implements ItemProcessor<SwiftRecord, SwiftRecord> {

    private final StructureRegistry registry;

    public SwiftValidationProcessor(StructureRegistry registry) {
        this.registry = registry;
    }

    @Override
    public SwiftRecord process(SwiftRecord rec) throws InvalidRecordException {

        // Defensive System-Checks
        if (rec.getStagingTable() == null) {
            throw new IllegalStateException(
                "No table routing for type " + rec.getType()
            );
        }


        StructureDefinition def = registry.require(rec.getRecordStructure());
        def.validate(rec); // mandatory fields etc.

        return rec;
    }
}
