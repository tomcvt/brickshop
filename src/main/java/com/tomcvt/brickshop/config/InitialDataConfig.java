package com.tomcvt.brickshop.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tomcvt.brickshop.utility.CategoryReferenceMap;

@Configuration
public class InitialDataConfig {
    private final SuperuserInitializer superuserInitializer;
    private final CategoryReferenceMap categoryReferenceMap;

    public InitialDataConfig(SuperuserInitializer superuserInitializer,
            CategoryReferenceMap categoryReferenceMap
    ) {
        this.superuserInitializer = superuserInitializer;
        this.categoryReferenceMap = categoryReferenceMap;
    }

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            superuserInitializer.initializeSuperuser();
            categoryReferenceMap.initMap();
        };
    }
}
