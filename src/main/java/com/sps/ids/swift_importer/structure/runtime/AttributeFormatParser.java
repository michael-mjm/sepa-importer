package com.sps.ids.swift_importer.structure.runtime;

public final class AttributeFormatParser {

    public static AttributeFormat parse(String raw) {

        if (raw == null || raw.isBlank()) {
            return new AttributeFormat.Unknown(null);
        }

        return switch (raw.trim().toUpperCase()) {
            case "YYYY-MM-DD" ->
                new AttributeFormat.IsoDate();
            default -> {
                if (raw.endsWith("AN")) {
                    yield new AttributeFormat.AlphaNumeric(
                        Integer.parseInt(raw.replaceAll("\\D", ""))
                    );
                }
                if (raw.endsWith("N")) {
                    yield new AttributeFormat.Numeric(
                        Integer.parseInt(raw.replaceAll("\\D", ""))
                    );
                }
                yield new AttributeFormat.Unknown(raw);
            }
        };
    }

    private AttributeFormatParser() {}
}