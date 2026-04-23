package com.sps.ids.swift_importer.exception;

public class InvalidRecordException extends Exception {
    public InvalidRecordException(String message) {
        super(message);
    }

    public InvalidRecordException(String message, Throwable cause) {
        super(message, cause);
    }
}