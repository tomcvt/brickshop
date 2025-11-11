package com.tomcvt.brickshop.dto;

public record PassPayload(
    String oldPassword,
    String newPassword
) {
    
}
