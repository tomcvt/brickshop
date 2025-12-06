package com.tomcvt.brickshop.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.repository.UserRepository;

@Component
public class SuperuserInitializer {
    private static final Logger log = LoggerFactory.getLogger(SuperuserInitializer.class);
    private final UserRepository userRepository;
    private final String username;
    private final String password;
    private final String email;
    
    public SuperuserInitializer(UserRepository userRepository,
        @Value("${com.tomcvt.superuser.username}") String username,
        @Value("${com.tomcvt.superuser.password}") String password,
        @Value("${com.tomcvt.superuser.email}") String email
    ) {
        this.userRepository = userRepository;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public void initializeSuperuser() {
        User superuser = userRepository.findByUsername(username).orElse(null);
        if (superuser == null) {
            superuser = new User();
            superuser.setUsername(username);
            superuser.setPassword(password); // In a real application, ensure this is hashed
            superuser.setEmail(email);
            superuser.setRole("SUPERUSER");
            superuser.setEnabled(true);
            userRepository.save(superuser);
            log.info("Superuser created with username: {}", username);
        } else {
            log.info("Superuser already exists with username: {}", username);
        }
    }

}
