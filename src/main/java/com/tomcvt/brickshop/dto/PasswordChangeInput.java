package com.tomcvt.brickshop.dto;

public record PasswordChangeInput(
    String token,
    String newPassword,
    String confirmPassword
) {
    
}
