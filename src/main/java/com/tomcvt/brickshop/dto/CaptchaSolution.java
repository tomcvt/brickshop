package com.tomcvt.brickshop.dto;

import java.util.UUID;

public record CaptchaSolution(UUID requestId, String type, String solution) {
    
}
