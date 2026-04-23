package com.sps.ids.swift_importer.mapper;

import com.sps.ids.swift_importer.model.SwiftDeadLetterRecord;
import com.sps.ids.swift_importer.model.SwiftRecord;
import org.springframework.stereotype.Component;

@Component
public class DlqRecordMapper {

    public SwiftRecord toSwiftRecord(SwiftDeadLetterRecord dlq) {
        SwiftRecord rec = new SwiftRecord();
        rec.setRecordKey(dlq.getRecordKey());
        rec.setModificationType(dlq.getModificationType());

        // Minimal retry – Writer primarily requires table + key
        // If necessary: JSON object here later

        return rec;
    }
}
