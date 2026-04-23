package com.sps.ids.swift_importer.structure.runtime;

import com.sps.ids.swift_importer.exception.InvalidRecordException;
import com.sps.ids.swift_importer.model.SwiftRecord;

import java.util.Map;

public class StructureDefinition {

    private final String recordStructure;
    private final Map<Integer, AttributeDefinition> attributes;

    public StructureDefinition(
        String recordStructure,
        Map<Integer, AttributeDefinition> attributes) {
        this.recordStructure = recordStructure;
        this.attributes = attributes;
    }

    public String getRecordStructure() {
        return recordStructure;
    }

    /**
     * Minimal post-XSD semantic validation.
     */
    public void validate(SwiftRecord record) throws InvalidRecordException {

        // Header rows are ignored
        if (!"D".equals(record.getRecordContentType())) {
            return;
        }

        for (AttributeDefinition attr : attributes.values()) {
            String value = record.getAttributes().get("attribute_" + attr.getAttributeNo());

            if (attr.isMandatory() && isEmpty(value)) {
                throw new InvalidRecordException(
                    "Mandatory attribute missing: "
                        + attr.getSemanticName()
                        + " (attribute_" + attr.getAttributeNo() + ")"
                );
            }
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.isBlank()
            || "not used".equalsIgnoreCase(value);
    }
}