package com.sps.ids.swift_importer.structure.model;

import lombok.Data;

/**
 * Single attribute definition parsed from a STRUCTURE XML file.
 * One STRUCTURE XML record may produce multiple StructureElements
 * (one per ATTRIBUTE_n).
 */
@Data
public class StructureElement {

    private String structureName;     // e.g. "structures-identd-v1"
    private String fileVersion;        // e.g. "V1"
    private String recordStructure;    // e.g. "COUNTRY HOLIDAY"

    private int attributeNo;           // 1..50
    private String semanticName;        // ATTRIBUTE NAME
    private String attributeLabel;     // ATTRIBUTE LABEL
    private String description;
    private String format;             // e.g. 2an, YYYY-MM-DD
    private boolean mandatory;          // ALWAYS PRESENT = Y
    private boolean naturalKey;         // UNIQUE NATURAL KEY = Y
}