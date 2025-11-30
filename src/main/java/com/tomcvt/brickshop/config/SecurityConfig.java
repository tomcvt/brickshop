package com.tomcvt.brickshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.tomcvt.brickshop.security.UserLoginSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final AuthenticationManager authenticationManager;
    private final UserLoginSuccessHandler userLoginSuccessHandler;
    private final String[] WHITELIST = {
        "/js/**",
        "/css/**",
        "/images/**",
        "/products", "/api/products",
        "/products/**", "/api/products/**",
        "/cart/**", "/api/cart/**",
        "/api/auth/**",
        "/login", "/logout",
        "/api/public/**"
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

    SecurityConfig(AuthenticationManager authenticationManager, UserLoginSuccessHandler userLoginSuccessHandler) {
        this.authenticationManager = authenticationManager;
        this.userLoginSuccessHandler = userLoginSuccessHandler;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authenticationManager(authenticationManager)
            .csrf(csrf -> csrf.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            //.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(ADMIN_WHITELIST).hasRole("ADMIN")
                .requestMatchers(PACKER_WHITELIST).hasAnyRole("PACKER", "ADMIN")
                .requestMatchers(USER_WHITELIST).hasAnyRole("ADMIN", "USER", "PACKER")
                .requestMatchers(WHITELIST).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .successHandler(userLoginSuccessHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .anonymous(Customizer.withDefaults());
        return http.build();
    }
}
