package com.tomcvt.brickshop.cvtcaptcha;


import java.util.UUID;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;

public record CaptchaRequest(
    @NotNull UUID requestId, @NotNull @Length(min = 1, max = 100) String type) {
    
}
