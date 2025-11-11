package com.tomcvt.brickshop.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import com.tomcvt.brickshop.utility.DevDataLoader;

@Configuration
@Profile({"dev", "demo"})
public class DemoDataConfig {
    private static final Logger log = LoggerFactory.getLogger(DemoDataConfig.class);
    private final DevDataLoader devDataLoader;

    public DemoDataConfig(DevDataLoader devDataLoader) {
        this.devDataLoader = devDataLoader;
    }

    @Bean
    public CommandLineRunner loadDemoData(Environment env) {
        return args -> {
            log.info("Active profiles: {}", String.join(", ", env.getActiveProfiles()));
            log.info("Loading demo data...");
            devDataLoader.loadDevData();
            log.info("Demo data loaded.");
        };
    }
}
