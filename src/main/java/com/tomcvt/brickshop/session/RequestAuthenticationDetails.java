package com.tomcvt.brickshop.session;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import jakarta.servlet.http.HttpServletRequest;

public final class RequestAuthenticationDetails extends WebAuthenticationDetails {
    private final String ipAddress;

    public RequestAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.ipAddress = extractIpAddress(request);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    private static String extractIpAddress(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            return forwardedFor.split(",")[0].trim();
        }
        return ipAddress;
    }
}
