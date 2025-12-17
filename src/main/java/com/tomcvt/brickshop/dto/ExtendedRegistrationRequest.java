package com.tomcvt.brickshop.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record ExtendedRegistrationRequest(
    @NotNull @Length(min = 1, max = 255) String username,
    @NotNull @Length(min = 1, max = 255) String password,
    @Email @NotNull @Length(min = 1, max = 255) String email,
    @NotNull @Length(min = 1, max = 255) String captchaToken,
    @NotNull @Length(min = 1, max = 255) String role
) {
    
}
