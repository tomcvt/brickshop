package com.tomcvt.brickshop.exception;

public class NoOrderForSessionException extends RuntimeException {
    public NoOrderForSessionException(String message) {
        super(message);
    }
}
