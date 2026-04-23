package com.sps.ids.swift_importer.structure.runtime;

/**
 * Runtime representation of a single attribute definition
 * derived from a SWIFT Structure XML.
 *
 * This class is used exclusively for post-XSD semantic validation.
 */
public class AttributeDefinition {

    private final int attributeNo;          // 1..50
    private final String semanticName;       // e.g. COUNTRY_CODE
    private final boolean mandatory;         // ALWAYS_PRESENT = Y
    private final AttributeFormat format;    // optional, lightweight

    public AttributeDefinition(
        int attributeNo,
        String semanticName,
        boolean mandatory,
        AttributeFormat format
    ) {
        this.attributeNo = attributeNo;
        this.semanticName = semanticName;
        this.mandatory = mandatory;
        this.format = format;
    }

    public int getAttributeNo() {
        return attributeNo;
    }

    public String getSemanticName() {
        return semanticName;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public AttributeFormat getFormat() {
        return format;
    }
}