package com.sps.ids.swift_importer.listener;

import com.sps.ids.swift_importer.batch.ImportStatus;
import com.sps.ids.swift_importer.repository.ImportLogRepository;
import com.sps.ids.swift_importer.testutil.BatchTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;

import java.util.Set;

import static com.sps.ids.swift_importer.testutil.BatchTestUtil.newJobExecution;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SwiftImportLogListenerTest {

    private ImportLogRepository repo;
    private SwiftImportLogListener listener;

    @BeforeEach
    void setup() {
        repo = mock(ImportLogRepository.class);

        when(repo.create(anyString(), anyLong())).thenReturn(1L);

        listener = new SwiftImportLogListener(repo);
    }

    @Test
    void importLog_completedJob() {

        JobExecution job = BatchTestUtil.newJobExecutionWithStep(10, 7);
        job.setStatus(BatchStatus.COMPLETED);
        job.setExitStatus(ExitStatus.COMPLETED);

        listener.beforeJob(job);
        listener.afterJob(job);

        verify(repo).finish(
            eq(1L),
            eq(10),   // ProcessedRows
            eq(7),    // ErrorCount (Skips)
            eq(ImportStatus.COMPLETED.name())
        );
    }

    @Test
    void importLog_xsdFailure() {

        JobExecution job = newJobExecution();

        job.setStatus(BatchStatus.FAILED);
        job.setExitStatus(new ExitStatus("XSD_VALIDATION_FAILED"));

        listener.beforeJob(job);
        listener.afterJob(job);

        verify(repo).finish(
            eq(1L),
            eq(0),                 // no Writes
            eq(0),                 // no Skips
            eq(ImportStatus.FAILED_XSD.name())
        );
    }

    @Test
    void importLog_processingFailure() {

        JobExecution job = BatchTestUtil.newJobExecutionWithStep(5, 2);
        job.setStatus(BatchStatus.FAILED);
        job.setExitStatus(ExitStatus.FAILED);

        listener.beforeJob(job);
        listener.afterJob(job);

        verify(repo).finish(
            eq(1L),
            eq(5),
            eq(2),
            eq(ImportStatus.FAILED_PROCESSING.name())
        );
    }
}