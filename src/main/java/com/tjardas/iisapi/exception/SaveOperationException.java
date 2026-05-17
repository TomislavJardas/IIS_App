package com.tjardas.iisapi.exception;

public class SaveOperationException extends RuntimeException {
    public SaveOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
