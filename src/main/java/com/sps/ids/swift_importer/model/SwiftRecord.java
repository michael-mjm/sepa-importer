package com.sps.ids.swift_importer.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class SwiftRecord {

    /* =========================
     * Technical / Routing context
     * ========================= */
    private SwiftRecordType type;        // CODES_ISO, IDENTIFIERS_ALL, …
    private String domain;               // "codes-iso", "identifiers-all"
    private String stagingTable;         // swift_stg.stg_codes_iso

    /* =========================
     * Core XSD fields (1:1)
     * ========================= */
    private String modificationType;     // A, M, D
    private String recordKey;            // IDXXXXXXXXXX
    private String recordStructure;      // CURRENCY, COUNTRY, …
    private String recordContentType;    // D, H
    private String recordStatus;         // C, O, F

    private String startDate;            // ORIGINAL STRING from XML
    private String stopDate;             // ORIGINAL STRING from XML

    /* =========================
     * Attributes (1–50)
     * ========================= */
    private final Map<String, String> attributes = new HashMap<>();

    private String sourceFile;
    private Integer lineNumber;

    public void addAttribute(String name, String value) {
        if (value != null && !value.isBlank()) {
            attributes.put(name, value);
        }
    }
}