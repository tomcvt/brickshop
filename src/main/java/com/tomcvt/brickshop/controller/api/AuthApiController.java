package com.tomcvt.brickshop.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tomcvt.brickshop.dto.EmailInput;
import com.tomcvt.brickshop.dto.ExtendedRegistrationRequest;
import com.tomcvt.brickshop.dto.PasswordChangeInput;
import com.tomcvt.brickshop.dto.RegistrationRequest;
import com.tomcvt.brickshop.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {
    private final AuthService authService;

    public AuthApiController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/recover-password")
    public ResponseEntity<String> recoverPassword(@RequestBody EmailInput emailInput) {
        authService.initiatePasswordRecovery(emailInput.email());
        return ResponseEntity.ok("If email is registered, we sent you a password reset link");
    }
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordChangeInput input) {
        authService.resetPasswordWithToken(input.token(), input.newPassword(), input.confirmPassword());
        return ResponseEntity.ok("Password has been reset successfully");
    }

    //TODO register user with email and role USER
    //temporarily protected with SUPERUSER role
    @PreAuthorize("hasRole('SUPERUSER')")
    @PostMapping("/register-w-email")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationRequest request) {
        authService.registerUser(request.username(), request.rawPassword(), request.email(), "USER");
        return ResponseEntity.ok("User " + request.username() + " registered successfully");
    }
    @PostMapping("/register-w-captcha")
    public ResponseEntity<String> registerUserWithCaptcha(@RequestBody ExtendedRegistrationRequest request) {
        String role = validateRoleForRegistration(request.role());
        authService.registerUserWithCaptcha(request.username(), request.password(), request.email(), request.captchaToken(), role);
        return ResponseEntity.ok("User " + request.username() + " registered successfully");
    }

    private String validateRoleForRegistration(String role) {
        if (role == null || role.isBlank()) {
            return "USER";
        }
        if (role.equals("USER") || role.equals("PACKER") || role.equals("MODERATOR")) {
            return role;
        }
        throw new IllegalArgumentException("Invalid role for registration: " + role);
    }
}
