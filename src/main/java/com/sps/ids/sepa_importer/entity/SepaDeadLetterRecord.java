package com.sps.ids.sepa_importer.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SepaDeadLetterRecord {
    private Long id;
    private String recordKey;
    private String modificationType;
    private String rawData;
    private String errorMessage;
    private LocalDateTime errorTimestamp;
    private Integer retryCount;
    private Boolean resolved;
}