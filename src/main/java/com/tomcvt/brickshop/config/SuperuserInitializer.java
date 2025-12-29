package com.tomcvt.brickshop.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.service.AuthService;

@Component
public class SuperuserInitializer {
    private static final Logger log = LoggerFactory.getLogger(SuperuserInitializer.class);
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final String username;
    private final String password;
    private final String email;
    
    public SuperuserInitializer(AuthService authService,
        PasswordEncoder passwordEncoder,
        @Value("${com.tomcvt.superuser.username}") String username,
        @Value("${com.tomcvt.superuser.password}") String password,
        @Value("${com.tomcvt.superuser.email}") String email
    ) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public void initializeSuperuser() {
        User superuser = authService.getUserByUsername(username);
        if (superuser == null) {
            authService.registerActivatedUser(username, password, email, "SUPERUSER");
        } else {
            log.info("Superuser already exists, updating password.");
            authService.changePassword(superuser, password);
        }
    }

}
