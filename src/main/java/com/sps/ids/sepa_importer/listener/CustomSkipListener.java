package com.sps.ids.sepa_importer.listener;

import com.sps.ids.sepa_importer.entity.SepaStructureRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Component
public class CustomSkipListener implements SkipListener<SepaStructureRecord, SepaStructureRecord> {

    private static final Logger log = LoggerFactory.getLogger(CustomSkipListener.class);

    @Override
    public void onSkipInRead(Throwable t) {
        log.error("READ SKIP: {}", t.getMessage());
    }

    @Override
    public void onSkipInWrite(SepaStructureRecord item, Throwable t) {
        log.error("WRITE SKIP fuer {}: {}", item.getRecordKey(), t.getMessage());
    }
}