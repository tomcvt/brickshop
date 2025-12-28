package com.tomcvt.brickshop.session;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

//@Component
public class InvalidSessionCookieFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!request.isRequestedSessionIdValid() && request.getRequestedSessionId() != null) {
            Cookie cookie = new Cookie("JSESSIONID", "");
            cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
            cookie.setMaxAge(0); // Expire immediately
            response.addCookie(cookie);

            // Redirect to the same URL
            String redirectUrl = request.getRequestURI();
            String queryString = request.getQueryString();
            if (queryString != null) {
                redirectUrl += "?" + queryString;
            }
            response.sendRedirect(redirectUrl);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
