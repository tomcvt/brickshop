package com.tomcvt.brickshop.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;

public record PasswordChangeInput(
    @NotNull @Length(min = 1, max = 255) String token,
    @NotNull @Length(min = 1, max = 255) String newPassword,
    @NotNull @Length(min = 1, max = 255) String confirmPassword
) {
    
}
