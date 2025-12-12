package com.tomcvt.brickshop.zdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

import com.tomcvt.brickshop.service.AuthService;

@Component
@Profile({"dev","demo"})
public class DemoUsers {
    private final AuthService authService;
    private final DemoCache demoCache;
    private final String DEV_PASSWORD = "123";
    private final String DEMO_PASSWORD;
    private final String demoEmail;
    private Environment env;
    public DemoUsers(AuthService authService, DemoCache demoCache,
            @Value("${com.tomcvt.demo.password}") String demoPassword,
            @Value("${com.tomcvt.demo.email}") String demoEmail,
            Environment env
    ) {
        this.authService = authService;
        this.demoCache = demoCache;
        this.DEMO_PASSWORD = demoPassword;
        this.demoEmail = demoEmail;
        this.env = env;
    }

    public void createDemoUsers() {
        String password = env.acceptsProfiles(Profiles.of("dev")) ? DEV_PASSWORD : DEMO_PASSWORD;
        demoCache.demoUsersIds.add(authService.registerActivatedUser("packer", password, demoEmail, "PACKER").getId());
        demoCache.demoUsersIds.add(authService.registerActivatedUser("user", password, demoEmail, "USER").getId());
        demoCache.demoUsersIds.add(authService.registerActivatedUser("moderator", password, demoEmail, "MODERATOR").getId());
        demoCache.demoUsersIds.add(authService.registerActivatedUser("admin", password, demoEmail, "ADMIN").getId());
    }
}
