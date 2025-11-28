package com.tomcvt.brickshop.cvtcaptcha;

import java.util.UUID;

public record CaptchaRequest(UUID requestId, String type) {
    
}
