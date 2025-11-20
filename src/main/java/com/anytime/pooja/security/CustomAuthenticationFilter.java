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
        
        // Skip authentication filter for public endpoints
        // Note: context-path is /api, so paths are relative to that
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/") || 
            path.startsWith("/api/cms/") ||
            path.startsWith("/api/products/") ||
            path.startsWith("/api/categories/") ||
            path.startsWith("/swagger-ui") ||
            path.startsWith("/api-docs") ||
            path.startsWith("/v3/api-docs") ||
            path.equals("/api/actuator/health") ||
            path.equals("/api/error") ||
            path.equals("/favicon.ico") ||
            path.startsWith("/auth/") ||
            path.startsWith("/cms/") ||
            path.startsWith("/products/") ||
            path.startsWith("/categories/")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String sessionId = getSessionIdFromRequest(request);
            
            if (sessionId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Session-based authentication
                // The session is managed by Spring Security automatically
                // This filter can be used for additional validation if needed
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

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

