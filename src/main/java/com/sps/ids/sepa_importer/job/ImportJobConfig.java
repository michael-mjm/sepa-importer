package com.sps.ids.sepa_importer.job;

import com.sps.ids.sepa_importer.entity.SepaStructureRecord;
import com.sps.ids.sepa_importer.exception.InvalidRecordException;
import com.sps.ids.sepa_importer.listener.CustomSkipListener;
import com.sps.ids.sepa_importer.processor.SepaProcessor;
import com.sps.ids.sepa_importer.reader.StaxXmlReader;
import com.sps.ids.sepa_importer.writer.ErrorLoggingWriter;
import com.sps.ids.sepa_importer.writer.RetryableItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class ImportJobConfig {

    @Value("${sepa.import.filePath:C:/data/STRUCTURES-SEPAD-V1-F-2026-01-07.xml}")
    private String filePath;

    @Bean
    public Job importSepaJob(JobRepository jobRepo, Step importStep) {
        return new JobBuilder("importSepaJob", jobRepo)
                .incrementer(new RunIdIncrementer())
                .start(importStep)
                .build();
    }

    @Bean
    public Step importStep(JobRepository jobRepo,
                           PlatformTransactionManager txMgr,
                           ItemReader<SepaStructureRecord> reader,
                           SepaProcessor processor,
                           ItemWriter<SepaStructureRecord> writer,
                           CustomSkipListener skipListener) {
        return new StepBuilder("importStep", jobRepo)
                .<SepaStructureRecord, SepaStructureRecord>chunk(1000, txMgr)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .retry(org.springframework.dao.CannotAcquireLockException.class)
                .retry(org.springframework.dao.TransientDataAccessResourceException.class)
                .retryLimit(3)
                .skip(InvalidRecordException.class)
                .skipLimit(100)
                .listener(skipListener)
                .build();
    }

    @Bean
    public ItemReader<SepaStructureRecord> reader() {
        return new StaxXmlReader(new FileSystemResource(filePath));
    }

    @Bean
    public JdbcBatchItemWriter<SepaStructureRecord> sqlWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<SepaStructureRecord>()
                .beanMapped()
                .sql("""
                    INSERT INTO SepaStructureRecords
                    (ModificationType, RecordKey, RecordStructure, RecordContentType, RecordStatus,
                     StartDate, StopDate, Attribute1, Attribute2, Attribute3, Attribute4,
                     Attribute5, Attribute6, Attribute7, Attribute8, Attribute9, Attribute10)
                    VALUES
                    (:modificationType, :recordKey, :recordStructure, :recordContentType, :recordStatus,
                     :startDate, :stopDate, :attribute1, :attribute2, :attribute3, :attribute4,
                     :attribute5, :attribute6, :attribute7, :attribute8, :attribute9, :attribute10)
                    """)
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public ItemWriter<SepaStructureRecord> writer(JdbcBatchItemWriter<SepaStructureRecord> sqlWriter,
                                                  RetryTemplate dbRetryTemplate,
                                                  DataSource dataSource) {
        RetryableItemWriter<SepaStructureRecord> retryWriter =
                new RetryableItemWriter<>(sqlWriter, dbRetryTemplate);
        return new ErrorLoggingWriter(dataSource, retryWriter);
    }
}