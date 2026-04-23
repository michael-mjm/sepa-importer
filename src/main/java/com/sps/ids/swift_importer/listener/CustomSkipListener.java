package com.sps.ids.swift_importer.listener;

import com.sps.ids.swift_importer.model.SwiftRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Component
public class CustomSkipListener implements SkipListener<SwiftRecord, SwiftRecord> {

    private static final Logger log = LoggerFactory.getLogger(CustomSkipListener.class);

    @Override
    public void onSkipInRead(Throwable t) {
        log.error("READ SKIP: {}", t.getMessage());
    }

    @Override
    public void onSkipInWrite(SwiftRecord item, Throwable t) {
        log.error("WRITE SKIP for {}: {}", item.getRecordKey(), t.getMessage());
    }
}