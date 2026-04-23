package com.sps.ids.swift_importer.validation;

import com.sps.ids.swift_importer.exception.NotASwiftFileException;
import com.sps.ids.swift_importer.model.SwiftRecordType;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class SwiftXmlMetaReader {

    public static SwiftRecordType detectType(File xmlFile) {

        XMLInputFactory factory = XMLInputFactory.newInstance();

        try (InputStream is = new FileInputStream(xmlFile)) {

            XMLStreamReader reader = factory.createXMLStreamReader(is);

            while (reader.hasNext()) {
                int event = reader.next();

                if (event == XMLStreamConstants.START_ELEMENT) {
                    String tag = reader.getLocalName();

                    // --- 1) Technical container → ignore
                    if ("dataexport".equals(tag)) {
                        continue;
                    }

                    // --- 2) First technical data set decides
                    try {
                        return SwiftRecordType.fromXmlTag(tag);
                    } catch (IllegalArgumentException e) {
                        throw new NotASwiftFileException(
                            "Unknown SWIFT record type: " + tag
                        );
                    }
                }
            }

        } catch (NotASwiftFileException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                "The XML file could not be read: " + xmlFile.getName(), e
            );
        }

        // --- 3) Not a single relevant technical record found
        throw new NotASwiftFileException(
            "No SWIFT records found in file"
        );
    }
}