package com.sps.ids.swift_importer.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.retry.support.RetryTemplate;

public class RetryableItemWriter<T> implements ItemWriter<T> {

    private static final Logger log = LoggerFactory.getLogger(RetryableItemWriter.class);

    private final ItemWriter<T> delegate;
    private final RetryTemplate retryTemplate;

    public RetryableItemWriter(ItemWriter<T> delegate, RetryTemplate retryTemplate) {
        this.delegate = delegate;
        this.retryTemplate = retryTemplate;
    }

    @Override
    public void write(Chunk<? extends T> chunk) throws Exception {
        retryTemplate.execute(ctx -> {
            if (ctx.getRetryCount() > 0) {
                log.warn("Retry #{} for {} Items", ctx.getRetryCount(), chunk.size());
            }
            delegate.write(chunk);
            return null;
        });
    }
}