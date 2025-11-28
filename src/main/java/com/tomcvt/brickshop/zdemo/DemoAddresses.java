package com.tomcvt.brickshop.zdemo;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.tomcvt.brickshop.model.ShipmentAddress;
import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.repository.ShipmentAddressRepository;
import com.tomcvt.brickshop.repository.UserRepository;

@Component
@Profile({"dev", "demo"})
public class DemoAddresses {
    private final ShipmentAddressRepository shipmentAddressRepository;
    private final UserRepository userRepository;
    private final DemoCache demoCache;
    private final Environment env;

    public DemoAddresses(ShipmentAddressRepository shipmentAddressRepository , DemoCache demoCache, 
        UserRepository userRepository, Environment env) {
        this.shipmentAddressRepository = shipmentAddressRepository;
        this.demoCache = demoCache;
        this.userRepository = userRepository;
        this.env = env;
    }

    public void createDemoAddresses() {
        Long demoUserId1 = demoCache.demoUsersIds.get(0);
        Long demoUserId2 = demoCache.demoUsersIds.get(1);
        User demoUser1 = userRepository.findById(demoUserId1).orElseThrow(() -> new RuntimeException("No demo user found"));
        User demoUser2 = userRepository.findById(demoUserId2).orElseThrow(() -> new RuntimeException("No demo user found"));
        shipmentAddressRepository.save(new ShipmentAddress(demoUser1, "Packer Packager", "456 Warehouse Rd", "98-765", "Logistic City", "Poland", "098-765-4321"));
        shipmentAddressRepository.save(new ShipmentAddress(demoUser2, "User Usery", "789 Residential St", "34-567", "Hometown", "Poland", "567-890-1234"));
        shipmentAddressRepository.save(new ShipmentAddress(demoUser2, "User Usery", "321 Second St", "45-678", "Another City", "Poland", "678-901-2345"));
        if (env.acceptsProfiles(Profiles.of("dev"))) {
            User adminUser = userRepository.findByUsername("superuser").orElseThrow(() -> new RuntimeException("No admin user found"));
            shipmentAddressRepository.save(new ShipmentAddress(adminUser, "Dev Devy", "111 Dev St", "12-345", "Dev City", "Poland", "111-222-3333"));
            shipmentAddressRepository.save(new ShipmentAddress(adminUser, "Dev Devy", "222 Dev Ave", "23-456", "Dev Town", "Poland", "444-555-6666"));
        }
    }
}
