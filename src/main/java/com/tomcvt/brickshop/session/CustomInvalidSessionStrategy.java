package com.tomcvt.brickshop.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.session.InvalidSessionStrategy;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CustomInvalidSessionStrategy implements InvalidSessionStrategy {
    private static final Logger log = LoggerFactory.getLogger(CustomInvalidSessionStrategy.class);
    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie cookie = new Cookie("JSESSIONID", "");
        cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
        cookie.setMaxAge(0); 
        response.addCookie(cookie);
        log.info("Invalid session detected. Cleared JSESSIONID cookie.");
        String redirectUrl = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null) {
            redirectUrl += "?" + queryString;
        }
        response.sendRedirect(redirectUrl);
    }
}
