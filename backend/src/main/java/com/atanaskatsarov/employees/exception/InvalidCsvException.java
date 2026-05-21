package com.atanaskatsarov.employees.exception;

public class InvalidCsvException extends RuntimeException {
    public InvalidCsvException(String message) {
        super(message);
    }
}
