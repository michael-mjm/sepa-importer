package com.sps.ids.swift_importer.structure.reader;

import com.sps.ids.swift_importer.structure.model.StructureElement;
import org.springframework.batch.item.ItemReader;
import org.springframework.core.io.Resource;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Streaming reader for SWIFT Structure XML files
 * (e.g. structures-identd-v1, structures-sepad-v1).
 */
public class StructureXmlReader implements ItemReader<StructureElement> {

    private final Resource resource;
    private XMLStreamReader reader;

    private Iterator<StructureElement> bufferedElements;

    public StructureXmlReader(Resource resource) {
        this.resource = resource;
    }

    @Override
    public StructureElement read() throws Exception {

        // Drain buffered elements first
        if (bufferedElements != null && bufferedElements.hasNext()) {
            return bufferedElements.next();
        }

        if (reader == null) {
            initReader();
        }

        while (reader.hasNext()) {
            int event = reader.next();

            if (event == XMLStreamConstants.START_ELEMENT) {
                String tag = reader.getLocalName();

                if (tag.startsWith("structures-")) {
                    bufferedElements = parseStructureRecord(tag).iterator();
                    return bufferedElements.hasNext()
                        ? bufferedElements.next()
                        : null;
                }
            }
        }

        return null;
    }

    private void initReader() throws Exception {
        InputStream is = resource.getInputStream();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        reader = factory.createXMLStreamReader(is);
    }

    private Iterable<StructureElement> parseStructureRecord(String startTag)
        throws XMLStreamException {

        Map<String, String> values = new LinkedHashMap<>();

        while (reader.hasNext()) {
            int event = reader.next();

            if (event == XMLStreamConstants.END_ELEMENT
                && startTag.equals(reader.getLocalName())) {
                break;
            }

            if (event == XMLStreamConstants.START_ELEMENT) {
                String tag = reader.getLocalName();
                String text = readText();

                if (text != null && !text.isBlank()) {
                    values.put(tag, text.trim());
                }
            }
        }

        return StructureElementMapper.map(
            startTag,
            values
        );
    }

    private String readText() throws XMLStreamException {
        if (!reader.hasNext()) return null;
        int event = reader.next();
        if (event == XMLStreamConstants.CHARACTERS) {
            return reader.getText();
        }
        return null;
    }
}