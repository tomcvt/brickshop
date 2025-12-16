package com.tomcvt.brickshop.dto;

import org.springframework.http.HttpStatus;

public record ErrorResponse(String error, String message) {
    public ErrorResponse(int statusCode, String message) {
        this(HttpStatus.valueOf(statusCode).name(), message);
    }
}