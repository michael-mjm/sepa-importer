package com.sps.ids.sepa_importer.processor;

import com.sps.ids.sepa_importer.entity.SepaStructureRecord;
import com.sps.ids.sepa_importer.exception.InvalidRecordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class SepaProcessor implements ItemProcessor<SepaStructureRecord, SepaStructureRecord> {

    private static final Logger log = LoggerFactory.getLogger(SepaProcessor.class);

    @Override
    public SepaStructureRecord process(SepaStructureRecord record) throws Exception {
        validateRecordKey(record);
        validateModificationType(record);
        validateContentType(record);
        validateStatus(record);
        validateDates(record);

        log.debug("Record {} validiert", record.getRecordKey());
        return record;
    }

    private void validateRecordKey(SepaStructureRecord record) throws InvalidRecordException {
        if (record.getRecordKey() == null || record.getRecordKey().trim().isEmpty()) {
            throw new InvalidRecordException("RecordKey darf nicht leer sein");
        }
    }

    private void validateModificationType(SepaStructureRecord record) throws InvalidRecordException {
        String v = record.getModificationType();
        if (v != null && !v.matches("[AMD]")) {
            throw new InvalidRecordException("Ungueltiger ModificationType: " + v);
        }
    }

    private void validateContentType(SepaStructureRecord record) throws InvalidRecordException {
        String v = record.getRecordContentType();
        if (v != null && !v.matches("[DH]")) {
            throw new InvalidRecordException("Ungueltiger RecordContentType: " + v);
        }
    }

    private void validateStatus(SepaStructureRecord record) throws InvalidRecordException {
        String v = record.getRecordStatus();
        if (v != null && !v.matches("[CFO]")) {
            throw new InvalidRecordException("Ungueltiger RecordStatus: " + v);
        }
    }

    private void validateDates(SepaStructureRecord record) throws InvalidRecordException {
        if (record.getStartDate() != null && record.getStopDate() != null) {
            if (record.getStartDate().isAfter(record.getStopDate())) {
                throw new InvalidRecordException(
                        "StartDate " + record.getStartDate()
                                + " nach StopDate " + record.getStopDate());
            }
        }
    }
}