package com.sps.ids.swift_importer.validation;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;

public class SwiftXsdValidator {

    private final Schema schema;

    public SwiftXsdValidator(File xsdFile) throws Exception {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        this.schema = factory.newSchema(xsdFile);
    }

    public void validate(File xmlFile) throws Exception {
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(xmlFile));
    }
}