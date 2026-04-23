package com.sps.ids.swift_importer.model;

public enum SwiftRecordType {

    CALENDARS_CTRY("calendars-ctry-v1"),
    CALENDARS_SEPA("calendars-sepa-v1"),

    CODES_ISO("codes-iso-v1"),
    CODES_SEPA("codes-sepa-v1"),

    FORMATS_CTRY("formats-ctry-v1"),
    FORMATS_SEPA("formats-sepa-v1"),

    IDENTIFIERS_ALL("identifiers-all-v1"),
    IDENTIFIERS_SEPA("identifiers-sepa-v1"),
    IDENTIFIERS_HIST_ALL("identifiers-histall-v1"),
    IDENTIFIERS_HIST_SEPA("identifiers-histsepa-v1"),

    PARTICIPANTS_SEPA("participants-sepa-v1"),

    RELATIONSHIPS_SEPA("relationships-sepa-v1"),

    STRUCTURES_IDENT("structures-identd-v1"),
    STRUCTURES_SEPA("structures-sepad-v1");

    private final String xmlTag;

    SwiftRecordType(String xmlTag) {
        this.xmlTag = xmlTag;
    }

    public String getXmlTag() {
        return xmlTag;
    }

    public static SwiftRecordType fromXmlTag(String tag) {
        for (SwiftRecordType t : values()) {
            if (t.xmlTag.equalsIgnoreCase(tag)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unsupported SWIFT XML tag: " + tag);
    }
}
