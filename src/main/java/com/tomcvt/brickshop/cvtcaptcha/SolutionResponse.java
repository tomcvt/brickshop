package com.tomcvt.brickshop.cvtcaptcha;

import java.util.UUID;

public record SolutionResponse(
    UUID requestId,
    String type,
    String solution
) {
    
}

