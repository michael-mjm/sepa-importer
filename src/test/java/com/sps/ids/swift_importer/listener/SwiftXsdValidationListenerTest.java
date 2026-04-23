package com.sps.ids.swift_importer.listener;

import com.sps.ids.swift_importer.exception.NotASwiftFileException;
import com.sps.ids.swift_importer.testutil.XmlTestFiles;
import com.sps.ids.swift_importer.validation.SwiftXsdValidationService;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class SwiftXsdValidationListenerTest {

    private StepExecution newStepExecution() {
        JobExecution jobExecution = new JobExecution(1L, new JobParameters());
        StepExecution stepExecution = new StepExecution("testStep", jobExecution);
        return stepExecution;
    }

    @Test
    void beforeStep_validSwiftXml_passes() throws Exception {

        String xml = """
        <?xml version="1.0" encoding="UTF-8"?>
        <dataexport product="identifiers-all-v1" filetype="full" filedate="20260327">
            <identifiers-all-v1>
                <modification_type>A</modification_type>
                <record_key>ID000000000F</record_key>
                <record_structure>IDENTIFIER</record_structure>
                <record_content_type>D</record_content_type>
                <record_status>C</record_status>
                <start_date>1977-08-29</start_date>
                <attribute_1>006FVDI</attribute_1>
                <attribute_2>006FVDI</attribute_2>
                <attribute_3>006FVDI</attribute_3>
                <attribute_4>HO</attribute_4>
                <attribute_5>DE</attribute_5>
                <attribute_6>EEA,EMU,IBAN,SEPA</attribute_6>
                <attribute_7>AACSDE33XXX</attribute_7>
                <attribute_8>BIC11</attribute_8>
                <attribute_10>ACH,FIN,FINPLUS,RT-RPS,RTGS,SWFA,SWIA</attribute_10>
                <attribute_11>FI</attribute_11>
                <attribute_12>SUPE</attribute_12>
                <attribute_14>AACSDE33XXX</attribute_14>
                <attribute_15>AACSDE33XXX</attribute_15>
                <attribute_16>ou=xxx,o=aacsde33,o=swift</attribute_16>
                <attribute_17>39050000</attribute_17>
                <attribute_18>AACSDE33XXX</attribute_18>
                <attribute_19>SPARKASSE AACHEN</attribute_19>
                <attribute_21>MUENSTERPLATZ 7-9</attribute_21>
                <attribute_23>AACHEN, NORDRHEIN-WESTFALEN, 52059</attribute_23>
                <attribute_27>MUENSTERPLATZ</attribute_27>
                <attribute_28>7-9</attribute_28>
                <attribute_32>AACHEN</attribute_32>
                <attribute_35>POB 1000</attribute_35>
                <attribute_36>52059</attribute_36>
                <attribute_37>NORDRHEIN-WESTFALEN</attribute_37>
                <attribute_39>GERMANY</attribute_39>
                <attribute_41>SWIFTX</attribute_41>
                <attribute_42>Europe/Berlin</attribute_42>
            </identifiers-all-v1>
        </dataexport>
        """;

        File file = XmlTestFiles.writeTempXml(xml);

        SwiftXsdValidationService validationService = new SwiftXsdValidationService();
        SwiftXsdValidationListener listener = new SwiftXsdValidationListener(validationService, file.getAbsolutePath());

        StepExecution step = newStepExecution();

        assertDoesNotThrow(() ->
            listener.beforeStep(step)
        );

        assertEquals(
            ExitStatus.EXECUTING.getExitCode(),
            step.getExitStatus().getExitCode()
        );
    }

    @Test
    void beforeStep_notASwiftFile_fails() throws Exception {

        String xml = """
            <exportData>
                <some-random-tag>
                    foo
                </some-random-tag>
            </exportData>
            """;

        File file = XmlTestFiles.writeTempXml(xml);

        SwiftXsdValidationService validationService = new SwiftXsdValidationService();
        SwiftXsdValidationListener listener = new SwiftXsdValidationListener(validationService, file.getAbsolutePath());

        StepExecution step = newStepExecution();

        NotASwiftFileException ex = assertThrows(
            NotASwiftFileException.class,
            () -> listener.beforeStep(step)
        );

        assertEquals(
            "NOT_A_SWIFT_FILE",
            step.getExitStatus().getExitCode()
        );
    }

    @Test
    void beforeStep_xsdValidationFails_setsExitStatus() throws Exception {

        String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <dataexport product="identifiers-all-v1" filetype="full" filedate="20260327">
                <identifiers-all-v1>
                    <!-- modification_type is intentionally omitted -->
                </identifiers-all-v1>
            </dataexport>
            """;

        File file = XmlTestFiles.writeTempXml(xml);

        SwiftXsdValidationService validationService = new SwiftXsdValidationService();
        SwiftXsdValidationListener listener = new SwiftXsdValidationListener(validationService, file.getAbsolutePath());

        StepExecution step = newStepExecution();

        Exception ex = assertThrows(
            Exception.class,
            () -> listener.beforeStep(step)
        );

        assertEquals(
            "XSD_VALIDATION_FAILED",
            step.getExitStatus().getExitCode()
        );

        assertTrue(
            ex.getMessage().contains("modification_type")
        );
    }

    @Test
    void beforeStep_invalidXml_fails() throws Exception {

        String xml = """
            <exportData><identifiers-all-v1></exportData>
            """;

        File file = XmlTestFiles.writeTempXml(xml);

        SwiftXsdValidationService validationService = new SwiftXsdValidationService();
        SwiftXsdValidationListener listener = new SwiftXsdValidationListener(validationService, file.getAbsolutePath());

        StepExecution step = newStepExecution();

        RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> listener.beforeStep(step)
        );
    }
}