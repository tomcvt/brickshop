package com.tomcvt.brickshop.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.repository.UserRepository;
import com.tomcvt.brickshop.zdemo.DemoDataLoader;


@Service
@Profile({"dev", "demo"})
public class DemoDataConfig implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger log = LoggerFactory.getLogger(DemoDataConfig.class);
    private final DemoDataLoader demoDataLoader;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataConfig(DemoDataLoader demoDataLoader, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.demoDataLoader = demoDataLoader;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();
        log.info("Active profiles: {}", String.join(", ", env.getActiveProfiles()));
        log.info("Loading demo data...");
        demoDataLoader.loadDemoData();
        log.info("Demo data loaded.");
        if (env.acceptsProfiles(Profiles.of("demo"))) {
            log.info("Application is running with 'demo' profile. Demo data is available.");
        }
        if (env.acceptsProfiles(Profiles.of("dev"))) {
            log.info("Application is running with 'dev' profile. Demo data is available.");
            User adminUser = userRepository.findByUsername("superuser").orElse(null);
            if (adminUser != null) {
                adminUser.setPassword(passwordEncoder.encode("123")); // In a real application, ensure passwords are hashed
                userRepository.save(adminUser);
            } else {
                log.warn("Admin user not found in the database.");
            }
        }
    }
}
