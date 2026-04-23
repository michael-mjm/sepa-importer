package com.sps.ids.swift_importer.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sps.ids.swift_importer.model.SwiftRecord;

public class DlqJsonMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String toJson(SwiftRecord record) {
        try {
            return MAPPER.writeValueAsString(record);
        } catch (Exception e) {
            return "{\"error\":\"serialization failed\"}";
        }
    }
}