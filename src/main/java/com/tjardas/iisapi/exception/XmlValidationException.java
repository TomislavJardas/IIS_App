package com.tjardas.iisapi.exception;

import java.util.List;

public class XmlValidationException extends RuntimeException {
    private final List<String> errors;

    public XmlValidationException(String message, List<String> errors, Throwable cause) {
        super(message, cause);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
