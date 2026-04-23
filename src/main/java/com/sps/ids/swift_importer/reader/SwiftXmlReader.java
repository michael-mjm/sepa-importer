package com.sps.ids.swift_importer.reader;

import com.sps.ids.swift_importer.model.SwiftRecord;
import com.sps.ids.swift_importer.model.SwiftRecordType;
import com.sps.ids.swift_importer.router.SwiftTableRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.core.io.Resource;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

public class SwiftXmlReader implements ItemReader<SwiftRecord> {

    private static final Logger log = LoggerFactory.getLogger(SwiftXmlReader.class);
    private final Resource resource;

    private XMLStreamReader reader;
    private boolean finished = false;

    public SwiftXmlReader(Resource resource) {
        this.resource = resource;
    }

    @Override
    public SwiftRecord read() throws Exception {
        if (finished) return null;

        if (reader == null)
            initReader();

        while (reader.hasNext()) {
            int event = reader.next();

            if (event == XMLStreamConstants.START_ELEMENT) {
                String tag = reader.getLocalName();

                //Check if the tag is a SWIFT record
                try {
                    SwiftRecordType type = SwiftRecordType.fromXmlTag(tag);
                    return parseRecord(type, tag);
                } catch (IllegalArgumentException ignore) {
                    // No SWIFT record - continue
                }
            }
        }

        finished = true;
        return null;
    }

    private void initReader() throws Exception {
        InputStream is = resource.getInputStream();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        reader = factory.createXMLStreamReader(is);
    }

    private SwiftRecord parseRecord(SwiftRecordType type, String startTag) throws XMLStreamException {

        SwiftRecord rec = new SwiftRecord();
        rec.setType(type);
        rec.setStagingTable(SwiftTableRouter.resolve(type));

        while (reader.hasNext()) {
            int event = reader.next();

            if (event == XMLStreamConstants.END_ELEMENT && startTag.equals(reader.getLocalName())) {
                break;
            }

            if (event == XMLStreamConstants.START_ELEMENT) {
                String tag = reader.getLocalName();
                String text = readText();

                if (text == null || text.isBlank()) continue;

                switch (tag) {
                    case "modification_type" -> rec.setModificationType(text);
                    case "record_key" -> rec.setRecordKey(text);
                    case "record_structure" -> rec.setRecordStructure(text);
                    case "record_content_type" -> rec.setRecordContentType(text);
                    case "record_status" -> rec.setRecordStatus(text);
                    case "start_date" -> rec.setStartDate(text);
                    case "stop_date" -> rec.setStopDate(text);

                    default -> {
                        if (tag.startsWith("attribute_"))
                            rec.addAttribute(tag, text);
                    }
                }
            }
        }

        return rec;
    }

    private String readText() throws XMLStreamException {
        if (!reader.hasNext()) return null;

        int event = reader.next();
        if (event == XMLStreamConstants.CHARACTERS)
            return reader.getText().trim();

        return null;
    }
}