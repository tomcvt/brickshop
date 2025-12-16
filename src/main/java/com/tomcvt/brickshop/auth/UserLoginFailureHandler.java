package com.tomcvt.brickshop.auth;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.tomcvt.brickshop.exception.IllegalUsageException;
import com.tomcvt.brickshop.network.BanRegistry;
import com.tomcvt.brickshop.service.NtfyService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserLoginFailureHandler implements AuthenticationFailureHandler {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserLoginFailureHandler.class);
    private final LoginTracker loginTracker;
    private final BanRegistry banRegistry;
    private final NtfyService ntfyService;

    public UserLoginFailureHandler(LoginTracker loginTracker, BanRegistry banRegistry, NtfyService ntfyService) {
        this.loginTracker = loginTracker;
        this.banRegistry = banRegistry;
        this.ntfyService = ntfyService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException, ServletException {
        String clientIp = request.getRemoteAddr();
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            clientIp = xff.split(",")[0].trim();
        }
        String username = request.getParameter("username");
        log.info("Login failed for user: {}", username);
        try {
            loginTracker.recordFailedLogin(username);
        } catch (IllegalUsageException e) {
            log.error("Error recording failed login for user {}: {}", username, e.getMessage());
            banRegistry.banIp(clientIp);
            ntfyService.sendNotificationUrgent("Security Alert", "IP " + clientIp + " has been banned due to excessive failed login attempts.");
            log.warn("Banned IP {} due to excessive failed login attempts", clientIp);
            response.setStatus(429);
            response.getWriter().write("Too many failed login attempts. Your IP has been temporarily banned.");
            response.getWriter().flush();
            return;
        }
        response.sendRedirect("/login?error=true");
    }
    
}
