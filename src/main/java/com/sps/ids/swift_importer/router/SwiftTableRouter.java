package com.sps.ids.swift_importer.router;

import com.sps.ids.swift_importer.model.SwiftRecordType;

import java.util.Map;

public class SwiftTableRouter {

    private static final Map<SwiftRecordType, String> ROUTING = Map.ofEntries(

        Map.entry(SwiftRecordType.CALENDARS_CTRY, "swift.calendars_ctry"),
        Map.entry(SwiftRecordType.CALENDARS_SEPA, "swift.calendars_sepa"),

        Map.entry(SwiftRecordType.CODES_ISO, "swift_stg.stg_codes_iso"),
        Map.entry(SwiftRecordType.CODES_SEPA, "swift.codes_sepa"),

        Map.entry(SwiftRecordType.FORMATS_CTRY, "swift.formats_ctry"),
        Map.entry(SwiftRecordType.FORMATS_SEPA, "swift.formats_sepa"),

        Map.entry(SwiftRecordType.IDENTIFIERS_ALL, "swift_stg.stg_identifiers_all"),
        Map.entry(SwiftRecordType.IDENTIFIERS_SEPA, "swift.identifiers_sepa"),
        Map.entry(SwiftRecordType.IDENTIFIERS_HIST_ALL, "swift.identifiers_histall"),
        Map.entry(SwiftRecordType.IDENTIFIERS_HIST_SEPA, "swift.identifiers_histsepa"),

        Map.entry(SwiftRecordType.PARTICIPANTS_SEPA, "swift.participants_sepa"),
        Map.entry(SwiftRecordType.RELATIONSHIPS_SEPA, "swift.relationships_sepa"),

        Map.entry(SwiftRecordType.STRUCTURES_IDENT, "swift_stg.stg_structures"),
        Map.entry(SwiftRecordType.STRUCTURES_SEPA, "swift_stg.stg_structures")
    );

    public static String resolve(SwiftRecordType type) {
        return ROUTING.get(type);
    }
}