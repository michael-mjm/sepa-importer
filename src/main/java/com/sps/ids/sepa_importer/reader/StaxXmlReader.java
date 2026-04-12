package com.sps.ids.sepa_importer.reader;

import com.sps.ids.sepa_importer.entity.SepaStructureRecord;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.core.io.Resource;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StaxXmlReader implements ItemReader<SepaStructureRecord> {

    private static final Logger log = LoggerFactory.getLogger(StaxXmlReader.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Resource resource;
    private XMLStreamReader reader;
    private boolean finished = false;

    public StaxXmlReader(Resource resource) {
        this.resource = resource;
    }

    @Override
    public SepaStructureRecord read() throws Exception {
        if (finished) {
            return null;
        }

        if (reader == null) {
            InputStream is = resource.getInputStream();
            XMLInputFactory factory = XMLInputFactory.newInstance();
            reader = factory.createXMLStreamReader(is);
        }

        try {
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT
                        && "structures-sepad-v1".equals(reader.getLocalName())) {
                    return parseRecord();
                }
            }
            finished = true;
            return null;
        } catch (XMLStreamException e) {
            log.error("Fehler beim Parsen des XML Streams", e);
            throw new RuntimeException(e);
        }
    }

    private SepaStructureRecord parseRecord() throws XMLStreamException {
        SepaStructureRecord rec = new SepaStructureRecord();

        while (reader.hasNext()) {
            int event = reader.next();

            if (event == XMLStreamConstants.END_ELEMENT
                    && "structures-sepad-v1".equals(reader.getLocalName())) {
                break;
            }

            if (event == XMLStreamConstants.START_ELEMENT) {
                String tag = reader.getLocalName();
                String val = readText();

                if (val != null && !val.isEmpty()) {
                    switch (tag) {
                        case "modification_type" -> rec.setModificationType(val);
                        case "record_key" -> rec.setRecordKey(val);
                        case "record_structure" -> rec.setRecordStructure(val);
                        case "record_content_type" -> rec.setRecordContentType(val);
                        case "record_status" -> rec.setRecordStatus(val);
                        case "start_date" -> rec.setStartDate(LocalDate.parse(val, DATE_FMT));
                        case "stop_date" -> rec.setStopDate(LocalDate.parse(val, DATE_FMT));
                        case "attribute_1" -> rec.setAttribute1(val);
                        case "attribute_2" -> rec.setAttribute2(val);
                        case "attribute_3" -> rec.setAttribute3(val);
                        case "attribute_4" -> rec.setAttribute4(val);
                        case "attribute_5" -> rec.setAttribute5(val);
                        case "attribute_6" -> rec.setAttribute6(val);
                        case "attribute_7" -> rec.setAttribute7(val);
                        case "attribute_8" -> rec.setAttribute8(val);
                        case "attribute_9" -> rec.setAttribute9(val);
                        case "attribute_10" -> rec.setAttribute10(val);
                    }
                }
            }
        }
        return rec;
    }

    private String readText() throws XMLStreamException {
        if (reader.hasNext()) {
            int next = reader.next();
            if (next == XMLStreamConstants.CHARACTERS) {
                return reader.getText().trim();
            }
        }
        return null;
    }

    @PreDestroy
    public void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (XMLStreamException e) {
                log.warn("Fehler beim Schliessen des XML Readers", e);
            }
        }
    }
}