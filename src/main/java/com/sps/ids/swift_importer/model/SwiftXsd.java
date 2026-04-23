package com.sps.ids.swift_importer.model;

import java.util.Arrays;

public enum SwiftXsd {

    STRUCTURES_IDENTD(
        SwiftRecordType.STRUCTURES_IDENT,
        "schemas/STRUCTURES_IDENTD_V1.xsd"
    ),

    STRUCTURES_SEPAD(
        SwiftRecordType.STRUCTURES_SEPA,
        "schemas/STRUCTURES_SEPAD_V1.xsd"
    ),

    CODES_ISO(
        SwiftRecordType.CODES_ISO,
        "schemas/CODES_ISO_V1.xsd"
    ),

    CALENDARS_CTRY(
        SwiftRecordType.CALENDARS_CTRY,
        "schemas/CALENDARS_CTRY_V1.xsd"
    ),
    CODES_SEPA(
        SwiftRecordType.CODES_SEPA,
        "schemas/CODES_SEPA_V1.xsd"
    ),
    IDENTIFIERS_ALL(
        SwiftRecordType.IDENTIFIERS_ALL,
        "schemas/IDENTIFIERS_ALL_V1.xsd"
    );

    private final SwiftRecordType type;
    private final String classpathLocation;

    SwiftXsd(SwiftRecordType type, String classpathLocation) {
        this.type = type;
        this.classpathLocation = classpathLocation;
    }

    public static SwiftXsd fromType(SwiftRecordType type) {
        return Arrays.stream(values())
            .filter(x -> x.type == type)
            .findFirst()
            .orElseThrow(() ->
                new IllegalArgumentException("No XSD for " + type));
    }

    public String getClasspathLocation() {
        return classpathLocation;
    }
}