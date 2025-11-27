package com.tomcvt.brickshop.zdemo;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.repository.UserRepository;

@Component
@Profile({"dev", "demo"})
public class DemoOrders {
    private final DemoPersistenceLayer demoPersistenceLayer;
    private final UserRepository userRepository;

    public DemoOrders(DemoPersistenceLayer demoPersistenceLayer, UserRepository userRepository) {
        this.demoPersistenceLayer = demoPersistenceLayer;
        this.userRepository = userRepository;
    }

    public void createDemoOrders() {
        User demoUser = userRepository.findById(2L).orElseThrow(() -> new RuntimeException("No demo user found"));
        for (int i = 0; i < 20; i++) {
            demoPersistenceLayer.createDemoOrder(demoUser, Long.valueOf(i + 1));
        }
    }

}
