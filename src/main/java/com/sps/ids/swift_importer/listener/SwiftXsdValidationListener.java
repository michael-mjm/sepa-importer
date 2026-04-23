package com.sps.ids.swift_importer.listener;

import com.sps.ids.swift_importer.exception.NotASwiftFileException;
import com.sps.ids.swift_importer.validation.SwiftXsdValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@StepScope
public class SwiftXsdValidationListener implements StepExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(SwiftXsdValidationListener.class);

    private final SwiftXsdValidationService validator;
    private final String filePath;

    public SwiftXsdValidationListener(
        SwiftXsdValidationService validator,
        @Value("#{jobParameters['swift.import.filePath']}") String filePath) {

        this.validator = validator;
        this.filePath = filePath;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {

        File xml = new File(filePath);
        log.info("XSD validation started for file: {}", xml.getAbsolutePath());

        try {
            validator.validate(xml);
            log.info("XSD validation successful");

        } catch (NotASwiftFileException e) {
            log.error("Not a swift file", e);

            stepExecution.setExitStatus(
                new ExitStatus("NOT_A_SWIFT_FILE", e.getMessage())
            );
            throw e;

        } catch (Exception e) {
            log.error("XSD validation failed", e);

            // STEP & JOB stop hard
            stepExecution.setExitStatus(
                new ExitStatus("XSD_VALIDATION_FAILED", e.getMessage())
            );

            // Important: Throw exception - Step aborts
            throw new IllegalStateException(
                "XSD validation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return stepExecution.getExitStatus();
    }
}