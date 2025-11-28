package com.tomcvt.brickshop.cvtcaptcha;

import java.util.UUID;

public record CaptchaResponse(UUID requestId, String imageUrl) {
    
}

