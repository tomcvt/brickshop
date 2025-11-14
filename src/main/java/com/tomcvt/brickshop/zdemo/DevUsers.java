package com.tomcvt.brickshop.zdemo;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.tomcvt.brickshop.service.AuthService;

@Component
@Profile({"dev"})
public class DevUsers {
    private final AuthService authService;
    private final DemoCache demoCache;
    public DevUsers(AuthService authService, DemoCache demoCache) {
        this.authService = authService;
        this.demoCache = demoCache;
    }

    public void createDevUsers() {
        demoCache.demoUsersIds.add(authService.registerActivatedUser("admin", "123", "abc@mail.com", "ADMIN").getId());
        demoCache.demoUsersIds.add(authService.registerActivatedUser("packer", "123", "abe@mail.com", "PACKER").getId());
        demoCache.demoUsersIds.add(authService.registerActivatedUser("user", "123", "sdf@mail.com", "USER").getId());
    }
}
