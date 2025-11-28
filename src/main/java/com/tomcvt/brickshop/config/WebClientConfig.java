package com.tomcvt.brickshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // "http://localhost:8080/mock-fastapi"

    @Bean
    public WebClient webClientSelf(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8080") // base URL for your microservice
                      .build();
    }
    //TDO what ip does docker need here? 
    @Bean
    public WebClient webClientCvtCaptcha(WebClient.Builder builder) {
        return builder.baseUrl("http://146.59.92.118:8000") // base URL for CVT Captcha service
                      .build();
    }
}

    