package com.tomcvt.brickshop.dto;

public record ExtendedRegistrationRequest(
    String username,
    String password,
    String email,
    String captchaToken,
    String role
) {
    
}
