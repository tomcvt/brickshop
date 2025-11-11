package com.tomcvt.brickshop.dto;

public record RegistrationRequest(
    String username,
    String rawPassword,
    String email
) {
    
}
