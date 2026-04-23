package com.sps.ids.swift_importer.structure.step;

import com.sps.ids.swift_importer.listener.SwiftXsdValidationListener;
import com.sps.ids.swift_importer.structure.model.StructureElement;
import com.sps.ids.swift_importer.structure.reader.StructureXmlReader;
import com.sps.ids.swift_importer.structure.tasklet.CleanupStructureStagingTasklet;
import com.sps.ids.swift_importer.structure.writer.StructureStagingWriter;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class StructureStagingStepsConfig {

    @Bean
    public Step cleanupStructureStagingStep(
        JobRepository jobRepository,
        PlatformTransactionManager txMgr,
        CleanupStructureStagingTasklet tasklet) {

        return new StepBuilder("cleanupStructureStagingStep", jobRepository)
            .tasklet(tasklet, txMgr)
            .build();
    }

    @Bean
    public Step loadStructureStagingStep(
        JobRepository jobRepository,
        PlatformTransactionManager txMgr,
        ItemReader<StructureElement> reader,
        ItemWriter<StructureElement> structureStagingWriter,
        SwiftXsdValidationListener xsdListener) {
        return new StepBuilder("loadStructureStagingStep", jobRepository)
            .<StructureElement, StructureElement>chunk(500, txMgr)
            .reader(reader)
            .writer(structureStagingWriter)
            .listener(xsdListener)
            .build();
    }



    // -------- Reader --------
    @Bean
    @StepScope
    public StructureXmlReader structureReader(
        @Value("#{jobParameters['swift.import.filePath']}") String filePath) {

        return new StructureXmlReader(
            new FileSystemResource(filePath)
        );
    }

    // -------- Writer --------
    @Bean
    @StepScope
    public ItemWriter<StructureElement> structureStagingWriter(
        DataSource dataSource) {

        return new StructureStagingWriter(dataSource);
    }

}
