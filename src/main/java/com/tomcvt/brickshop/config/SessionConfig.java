package com.tomcvt.brickshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

@Configuration
public class SessionConfig {
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
 /*  Doesnt work as expected, fall back to default onceperrequestfilter */
 /*
    @Bean
    public InvalidSessionStrategy invalidSessionStrategy() {
        return new CustomInvalidSessionStrategy();
    }
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }
    @Bean
    public SessionManagementFilter sessionManagementFilter(SessionAuthenticationStrategy sessionAuthenticationStrategy,
                                   InvalidSessionStrategy invalidSessionStrategy) {
        SessionManagementFilter filter = new SessionManagementFilter(securityContextRepository(), sessionAuthenticationStrategy);
        filter.setInvalidSessionStrategy(invalidSessionStrategy);
        return filter;
    }
        */

}
