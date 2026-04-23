package com.sps.ids.swift_importer.validation;

import com.sps.ids.swift_importer.exception.NotASwiftFileException;
import com.sps.ids.swift_importer.model.SwiftRecordType;
import com.sps.ids.swift_importer.testutil.XmlTestFiles;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SwiftXmlMetaReaderTest {

    @Test
    void detectType_validIdentifiersAll() throws Exception {

        String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <dataexport product="identifiers-all-v1" filetype="full" filedate="20260327">
                <identifiers-all-v1>
                    <record_key>ID000000000F</record_key>
                </identifiers-all-v1>
            </dataexport>
            """;

        File file = XmlTestFiles.writeTempXml(xml);

        SwiftRecordType type = SwiftXmlMetaReader.detectType(file);

        assertEquals(SwiftRecordType.IDENTIFIERS_ALL, type);
    }

    @Test
    void detectType_unknownSwiftRecord_throwsException() throws Exception {

        String xml = """
            <exportData>
                <some-random-tag>
                    foo
                </some-random-tag>
            </exportData>
            """;

        File file = XmlTestFiles.writeTempXml(xml);

        assertThrows(NotASwiftFileException.class,
            () -> SwiftXmlMetaReader.detectType(file)
        );
    }

    @Test
    void detectType_onlyExportData_throwsException() throws Exception {

        String xml = """
            <exportData>
            </exportData>
            """;

        File file = XmlTestFiles.writeTempXml(xml);

        assertThrows(
            NotASwiftFileException.class,
            () -> SwiftXmlMetaReader.detectType(file)
        );
    }

    @Test
    void detectType_invalidXml_throwsRuntimeException() throws Exception {

        String xml = "<exportData><foo></exportData>";

        File file = XmlTestFiles.writeTempXml(xml);

        assertThrows(
            RuntimeException.class,
            () -> SwiftXmlMetaReader.detectType(file)
        );
    }
}