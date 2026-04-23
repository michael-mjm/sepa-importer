package com.sps.ids.swift_importer.testutil;

import java.io.File;
import java.nio.file.Files;

public final class XmlTestFiles {

    private XmlTestFiles() {
    }

    public static File writeTempXml(String xmlContent) throws Exception {
        File file = File.createTempFile("swift-test-", ".xml");
        file.deleteOnExit();
        Files.writeString(file.toPath(), xmlContent);
        return file;
    }
}