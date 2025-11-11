package com.tomcvt.brickshop.exception;

public class NoSuchEntityExistsException extends RuntimeException {
    public NoSuchEntityExistsException(String message) {
        super(message);
    }
}
