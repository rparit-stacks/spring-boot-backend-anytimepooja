package com.anytime.pooja.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Let Spring Security handle authorization - don't manually skip public endpoints
        // This filter only handles additional authentication logic if needed
        try {
            String sessionId = getSessionIdFromRequest(request);
            
            // Session-based authentication is handled by Spring Security automatically
            // This filter can be used for additional validation or logging if needed
            if (sessionId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Additional authentication logic can be added here if needed
                // For now, Spring Security's session management handles this
            }
        } catch (Exception ex) {
            logger.error("Could not process authentication in security context", ex);
        }

        // Always continue the filter chain - let Spring Security handle authorization
        filterChain.doFilter(request, response);
    }

    private String getSessionIdFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return request.getSession(false) != null ? request.getSession().getId() : null;
    }
}

