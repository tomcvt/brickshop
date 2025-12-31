package com.tomcvt.brickshop.config;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.tomcvt.brickshop.session.RequestAuthenticationDetails;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class RequestAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, RequestAuthenticationDetails> {
    @Override
    public RequestAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new RequestAuthenticationDetails(context);
    }
    
}
