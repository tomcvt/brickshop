package com.tomcvt.brickshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import com.tomcvt.brickshop.auth.RateLimitingFilter;
import com.tomcvt.brickshop.auth.UserLoginFailureHandler;
import com.tomcvt.brickshop.security.UserLoginSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final AuthenticationManager authenticationManager;
    private final UserLoginSuccessHandler userLoginSuccessHandler;
    private final RateLimitingFilter rateLimitingFilter;
    private final SessionRegistry sessionRegistry;
    private final UserLoginFailureHandler userLoginFailureHandler;
    private final String[] WHITELIST = {
        "/",
        "/js/**",
        "/css/**",
        "/images/**",
        "/outsideimages/**",
        "/products", "/api/products",
        "/products/**", "/api/products/**",
        "/cart/**", "/api/cart/**",
        "/api/auth/**",
        "/login", "/logout",
        "/login.html", "/registration",
        "/api/public/**",
        "/mock-payment/**",
        "/api/captcha/**",
        "/.well-known/**",
        "/error",
        "/public/**",
        "/no-image.jpg"
    };
    private final String[] SUPERUSER_WHITELIST = {
        "/superuser/**",
        "/api/superuser/**",
        "/superuser",
        "/api/superuser"
    };
    private final String[] PACKER_WHITELIST = {
        "/packer/**",
        "/api/packer/**",
        "/packer",
        "/api/packer"
    };
    private final String[] ADMIN_WHITELIST = {
        "/admin/**",
        "/api/admin/**",
        "/admin",
        "/api/admin"
    };
    private final String[] USER_WHITELIST = {
        "/user/**",
        "/api/user/**",
        "/user",
        "/api/user"
    };
    
    //TODO refactor for config properties later

    SecurityConfig(AuthenticationManager authenticationManager, UserLoginSuccessHandler userLoginSuccessHandler, 
    RateLimitingFilter rateLimitingFilter, SessionRegistry sessionRegistry, UserLoginFailureHandler userLoginFailureHandler
    ) {
        this.authenticationManager = authenticationManager;
        this.userLoginSuccessHandler = userLoginSuccessHandler;
        this.rateLimitingFilter = rateLimitingFilter;
        this.sessionRegistry = sessionRegistry;
        this.userLoginFailureHandler = userLoginFailureHandler;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authenticationManager(authenticationManager)
            .csrf(csrf -> csrf.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            //.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(SUPERUSER_WHITELIST).hasRole("SUPERUSER")
                .requestMatchers(ADMIN_WHITELIST).hasAnyRole("ADMIN", "SUPERUSER", "MODERATOR")
                .requestMatchers(PACKER_WHITELIST).hasAnyRole("PACKER", "ADMIN", "SUPERUSER", "MODERATOR")
                .requestMatchers(USER_WHITELIST).hasAnyRole("ADMIN", "USER", "PACKER", "SUPERUSER", "MODERATOR")
                .requestMatchers(WHITELIST).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .successHandler(userLoginSuccessHandler)
                .failureHandler(userLoginFailureHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .anonymous(Customizer.withDefaults())
            .addFilterAfter(rateLimitingFilter, AnonymousAuthenticationFilter.class)
            .sessionManagement(session -> session
                .maximumSessions(20)
                .sessionRegistry(sessionRegistry)
            );
        return http.build();
    }
}
