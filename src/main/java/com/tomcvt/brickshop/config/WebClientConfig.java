package com.tomcvt.brickshop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // "http://localhost:8080/mock-fastapi"
    private final String localUrl;

    public WebClientConfig(@Value("${com.tomcvt.domain.name}") String domainName) {
        this.localUrl = domainName;
    }

    @Bean
    public WebClient webClientSelf(WebClient.Builder builder) {
        return builder.baseUrl(localUrl) // base URL for your microservice
                      .build();
    }
    //TDO what ip does docker need here? 
    @Bean
    public WebClient webClientCvtCaptcha(WebClient.Builder builder) {
        return builder.baseUrl("https://captcha.tomcvt.pl") // base URL for CVT Captcha service
                      .build();
    }
    @Bean
    public WebClient webClientNtfy(WebClient.Builder builder) {
        return builder.baseUrl("https://ntfy.sh") // base URL for Ntfy 
                      .build();
    }
}

    