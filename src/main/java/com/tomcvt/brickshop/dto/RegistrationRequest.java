package com.tomcvt.brickshop.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RegistrationRequest(
    @NotNull @Length(min = 1, max = 255) String username,
    @NotNull @Length(min = 1, max = 255) String rawPassword,
    @Email @NotNull @Length(min = 1, max = 255) String email
) {
    
}
