package com.tomcvt.brickshop.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record EmailInput(
    @Email @NotNull @Length(min = 1, max = 255) String email) {
    
}
