package com.sps.ids.sepa_importer.entity;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SepaStructureRecord {
    private String modificationType;
    private String recordKey;
    private String recordStructure;
    private String recordContentType;
    private String recordStatus;
    private LocalDate startDate;
    private LocalDate stopDate;
    private String attribute1;
    private String attribute2;
    private String attribute3;
    private String attribute4;
    private String attribute5;
    private String attribute6;
    private String attribute7;
    private String attribute8;
    private String attribute9;
    private String attribute10;

    @Override
    public String toString() {
        return "SepaStructureRecord{recordKey='" + recordKey
                + "', modType='" + modificationType
                + "', contentType='" + recordContentType + "'}";
    }
}