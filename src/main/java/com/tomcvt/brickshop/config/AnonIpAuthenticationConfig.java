package com.tomcvt.brickshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
public class AnonIpAuthenticationConfig {
    private final RequestAuthenticationDetailsSource requestAuthenticationDetailsSource;

    public AnonIpAuthenticationConfig(RequestAuthenticationDetailsSource requestAuthenticationDetailsSource) {
        this.requestAuthenticationDetailsSource = requestAuthenticationDetailsSource;
    }

    @Bean
    AnonymousAuthenticationFilter anonymousAuthenticationFilter() {
        AnonymousAuthenticationFilter filter = new AnonymousAuthenticationFilter(
                "anonymousKey",
                "anonymousUser",
                AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

        filter.setAuthenticationDetailsSource(requestAuthenticationDetailsSource);
        return filter;
    }
}
