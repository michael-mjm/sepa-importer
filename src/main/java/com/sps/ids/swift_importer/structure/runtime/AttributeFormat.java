package com.sps.ids.swift_importer.structure.runtime;

/**
 * Lightweight attribute format description.
 *
 * This is NOT a full validator and must NOT duplicate XSD logic.
 * It is intended for minimal plausibility checks only.
 */
public sealed interface AttributeFormat
    permits AttributeFormat.AlphaNumeric,
    AttributeFormat.Numeric,
    AttributeFormat.IsoDate,
    AttributeFormat.FreeText,
    AttributeFormat.Unknown {

    record AlphaNumeric(int maxLength) implements AttributeFormat {}

    record Numeric(int maxLength) implements AttributeFormat {}

    record FreeText(int maxLength) implements AttributeFormat {}

    record IsoDate() implements AttributeFormat {}

    record Unknown(String rawDefinition) implements AttributeFormat {}
}