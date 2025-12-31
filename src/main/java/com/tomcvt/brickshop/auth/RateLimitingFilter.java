package com.tomcvt.brickshop.auth;

import java.io.IOException;

import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tomcvt.brickshop.exception.IllegalUsageException;
import com.tomcvt.brickshop.network.BanRegistry;
import com.tomcvt.brickshop.service.RateLimiterService;
import com.tomcvt.brickshop.session.RequestAuthenticationDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RateLimitingFilter.class);
    private final RateLimiterService rateLimiterService;
    private final BanRegistry banRegistry;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final String[] EXCLUDE_URLS = {
        "/js/**",
        "/css/**",
        "/images/**",
        "/outsideimages/**",
        "/.well-known/**",
        "/api/**",
        "/favicon.ico"
    };
    //TODO make configurable
    private static final String[] EXCLUDE_IP = {
        "0.0.0.0.0.0.0.1"
    };
    private static final String[] BLOCKED_STRINGS = {
        ".env", "git", "ssh", "config"
    };

    public RateLimitingFilter(RateLimiterService rateLimiterService, BanRegistry banRegistry) {
        this.rateLimiterService = rateLimiterService;
        this.banRegistry = banRegistry;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        String clientIp = request.getRemoteAddr();

        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            clientIp = xff.split(",")[0].trim();
        }
        for (String ip : EXCLUDE_IP) {
            if (ip.equals(clientIp)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        for (String blocked : BLOCKED_STRINGS) {
            if (request.getRequestURI().toLowerCase().contains(blocked)) {
                response.setStatus(403);
                response.getWriter().write("Bye bye.");
                response.getWriter().flush();
                banRegistry.banIp(clientIp);
                log.warn("Blocked request to {} from IP {}", request.getRequestURI(), clientIp);
                return;
            }
        }
        log.debug("Checking rate limiting for URI {} IP: {}", request.getRequestURI(), clientIp);

        if(banRegistry.isIpBanned(clientIp)) {
            response.setStatus(429);
            response.getWriter().write("Your IP has been temporarily banned due to excessive requests.");
            response.getWriter().flush();
            return;
        }
        for (String url : EXCLUDE_URLS) {
            if (pathMatcher.match(url, request.getRequestURI())) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var details = (RequestAuthenticationDetails) auth.getDetails();
        String ipAddress = details.getIpAddress();
        String status = rateLimiterService.checkStatus(ipAddress);
        //log.debug("Rate limiting status for IP {}: {}", ipAddress, status);

        boolean allowed = true;
        //TODO so make static util method for writing json response
        try {
            allowed = rateLimiterService.checkAndIncrement(clientIp);
        } catch (IllegalUsageException e) {
            banRegistry.banIp(clientIp);
            response.setStatus(429);
            response.getWriter().write("Your IP has been temporarily banned due to excessive requests.");
            response.getWriter().flush();
            return;
        }

        if (!allowed) {
            response.setStatus(429);
            response.getWriter().write("Rate limit exceeded. Please try again later.");
            response.getWriter().flush();
            return;
        }
        filterChain.doFilter(request, response);
    }
    
}
