package com.tomcvt.brickshop.dto;

import java.util.UUID;

public record CaptchaVerificationResponse(UUID requestId, boolean success) {
    
}
