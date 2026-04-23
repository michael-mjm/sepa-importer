package com.sps.ids.swift_importer.structure.reader;

import com.sps.ids.swift_importer.structure.model.StructureElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class StructureElementMapper {

    static List<StructureElement> map(
        String structureName,
        Map<String, String> values) {

        String contentType = values.get("record_content_type");
        if (!"D".equalsIgnoreCase(contentType)) {
            return List.of(); // ignore header
        }

        List<StructureElement> result = new ArrayList<>();

        String recordStructure = values.get("record_structure");

        for (int i = 1; i <= 50; i++) {

            String label = values.get("attribute_" + i);
            if (label == null) {
                continue;
            }

            StructureElement el = new StructureElement();
            el.setStructureName(structureName);
            el.setFileVersion(values.get("attribute_2")); // FILE VERSION
            el.setRecordStructure(recordStructure);
            el.setAttributeNo(i);

            el.setSemanticName(label);
            el.setAttributeLabel(values.get("attribute_5"));
            el.setDescription(values.get("attribute_6"));
            el.setFormat(values.get("attribute_7"));

            el.setMandatory("Y".equalsIgnoreCase(values.get("attribute_10")));
            el.setNaturalKey("Y".equalsIgnoreCase(values.get("attribute_9")));

            result.add(el);
        }

        return result;
    }

    private StructureElementMapper() {
    }
}