package com.sps.ids.swift_importer.job;

import com.sps.ids.swift_importer.listener.CustomSkipListener;
import com.sps.ids.swift_importer.listener.SwiftXsdValidationListener;
import com.sps.ids.swift_importer.model.SwiftRecord;
import com.sps.ids.swift_importer.processor.SwiftValidationProcessor;
import com.sps.ids.swift_importer.reader.SwiftXmlReader;
import com.sps.ids.swift_importer.structure.runtime.StructureRegistry;
import com.sps.ids.swift_importer.writer.ErrorLoggingWriter;
import com.sps.ids.swift_importer.writer.StagingSqlWriter;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class ImportJobConfig {

    @Bean
    public Step importStep(
        JobRepository jobRepository,
        PlatformTransactionManager txMgr,
        ItemReader<SwiftRecord> reader,
        SwiftValidationProcessor processor,
        ItemWriter<SwiftRecord> writer,
        CustomSkipListener skipListener,
        SwiftXsdValidationListener xsdListener) {

        return new StepBuilder("importStep", jobRepository)
            .<SwiftRecord, SwiftRecord>chunk(1000, txMgr)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .listener(skipListener)
            .listener(xsdListener)
            .build();
    }



    // ---------------------------
    // 2) Step definition
    // ---------------------------
//    @Bean
//    public Step importStep(JobRepository jobRepo,
//                           PlatformTransactionManager txMgr,
//                           ItemReader<SwiftRecord> reader,
//                           SwiftValidationProcessor processor,
//                           ItemWriter<SwiftRecord> writer,
//                           CustomSkipListener skipListener,
//                           SwiftXsdValidationListener xsdListener) {
//
//        return new StepBuilder("importSwiftStep", jobRepo)
//            .<SwiftRecord, SwiftRecord>chunk(1000, txMgr)
//            .reader(reader)
//            //.processor(processor)
//            .writer(writer)
//            .listener(xsdListener)
//            .faultTolerant()
//            .retryLimit(3)
//            .retry(org.springframework.dao.CannotAcquireLockException.class)
//            .retry(org.springframework.dao.TransientDataAccessResourceException.class)
//            // All validation errors goes into DLQ
//            .skip(InvalidRecordException.class)
//            .skipLimit(2000)
//            .listener(skipListener)
//            .build();
//    }


    @Bean
    @StepScope
    public SwiftXmlReader reader(@Value("#{jobParameters['swift.import.filePath']}") String filePath) {
        return new SwiftXmlReader(new FileSystemResource(filePath));
    }


    @Bean
    @StepScope
    public ItemWriter<SwiftRecord> stagingWriter(DataSource dataSource) {
        return new StagingSqlWriter(dataSource);
    }


    @Bean
    @StepScope
    public ErrorLoggingWriter writer(
        DataSource dataSource,
        @Qualifier("stagingWriter") ItemWriter<SwiftRecord> stagingWriter) {

        return new ErrorLoggingWriter(dataSource, stagingWriter);
    }
}