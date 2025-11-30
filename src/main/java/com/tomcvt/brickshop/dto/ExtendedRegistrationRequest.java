package com.tomcvt.brickshop.dto;

public record ExtendedRegistrationRequest(
    String username,
    String rawPassword,
    String email,
    String captchaToken,
    String role
) {
    
}
