package com.sps.ids.swift_importer.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SwiftDeadLetterRecord {
    private Long id;
    private String recordKey;
    private String modificationType;
    private String rawData;
    private String errorMessage;
    private LocalDateTime errorTimestamp;
    private Integer retryCount;
    private Boolean resolved;
}