package com.tjardas.iisapi.exception;

import java.util.Map;

public class XmlValidationException extends RuntimeException {
    private final Map<String, String> errors;

    public XmlValidationException(String message, Map<String, String> errors, Throwable cause) {
        super(message, cause);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
