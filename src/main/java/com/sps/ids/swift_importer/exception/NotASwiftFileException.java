package com.sps.ids.swift_importer.exception;

public class NotASwiftFileException extends RuntimeException {
    public NotASwiftFileException(String message) {
        super(message);
    }
}