package com.tomcvt.brickshop.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitialDataConfig {
    private final SuperuserInitializer superuserInitializer;

    public InitialDataConfig(SuperuserInitializer superuserInitializer) {
        this.superuserInitializer = superuserInitializer;
    }

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            superuserInitializer.initializeSuperuser();
        };
    }
}
