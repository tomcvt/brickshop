package com.tomcvt.brickshop.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tomcvt.brickshop.dto.ExtendedRegistrationRequest;
import com.tomcvt.brickshop.dto.RegistrationRequest;
import com.tomcvt.brickshop.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {
    private final AuthService authService;

    public AuthApiController(AuthService authService) {
        this.authService = authService;
    }

    //TODO register user with email and role USER
    @PostMapping("/register")
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
        if (role.equals("USER") || role.equals("PACKER")) {
            return role;
        }
        throw new IllegalArgumentException("Invalid role for registration: " + role);
    }
}
