package com.sps.ids.swift_importer.validation;

import com.sps.ids.swift_importer.model.SwiftRecordType;
import com.sps.ids.swift_importer.model.SwiftXsd;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;

@Service
public class SwiftXsdValidationService {

    public void validate(File xmlFile) throws Exception {

        SwiftRecordType type = SwiftXmlMetaReader.detectType(xmlFile);

        ClassPathResource xsd = new ClassPathResource(SwiftXsd.fromType(type).getClasspathLocation());

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        Schema schema = factory.newSchema(xsd.getURL());

        // A new validator for each call (thread-safe usage)
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(xmlFile));
    }
}
