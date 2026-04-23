package com.sps.ids.swift_importer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RetryConfig {

    @Bean
    public RetryTemplate dbRetryTemplate() {
        RetryTemplate template = new RetryTemplate();

        ExponentialBackOffPolicy backOff = new ExponentialBackOffPolicy();
        backOff.setInitialInterval(1000L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(8000L);
        template.setBackOffPolicy(backOff);

        Map<Class<? extends Throwable>, Boolean> retryable = new HashMap<>();
        retryable.put(SQLException.class, true);
        retryable.put(org.springframework.dao.CannotAcquireLockException.class, true);
        retryable.put(org.springframework.dao.DataAccessResourceFailureException.class, true);

        SimpleRetryPolicy policy = new SimpleRetryPolicy(4, retryable);
        template.setRetryPolicy(policy);

        return template;
    }
}