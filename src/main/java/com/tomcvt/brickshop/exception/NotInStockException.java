package com.tomcvt.brickshop.exception;

public class NotInStockException extends RuntimeException {
    public NotInStockException(String message) {
        super(message);
    }
}
