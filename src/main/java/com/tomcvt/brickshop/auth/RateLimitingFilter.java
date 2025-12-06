package com.tomcvt.brickshop.auth;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tomcvt.brickshop.model.WrapUserDetails;
import com.tomcvt.brickshop.service.RateLimiterService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    private final RateLimiterService rateLimiterService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final String[] EXCLUDE_URLS = {
        "/js/**",
        "/css/**",
        "/images/**",
        "/outsideimages/**",
        "/.well-known/**"
    };

    public RateLimitingFilter(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
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

        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //For now, rate limit everyone except static resources
        /*
        if (principal instanceof WrapUserDetails) {
            // Authenticated users are not rate-limited
            filterChain.doFilter(request, response);
            return;
        }
            */
        for (String url : EXCLUDE_URLS) {
            if (pathMatcher.match(url, request.getRequestURI())) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        boolean allowed = rateLimiterService.checkAndIncrement(clientIp);

        if (!allowed) {
            response.setStatus(429);
            response.getWriter().write("Rate limit exceeded. Please try again later.");
            response.getWriter().flush();
            return;
        }
        filterChain.doFilter(request, response);
    }
    
}
