package com.anytime.pooja.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.anytime.pooja.dto.CommonDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        CommonDTO.ErrorResponse errorResponse = new CommonDTO.ErrorResponse();
        errorResponse.setSuccess(false);
        errorResponse.setMessage("Forbidden: You don't have permission to access this resource");
        errorResponse.setError(accessDeniedException.getMessage());
        errorResponse.setStatusCode(403);
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setPath(request.getRequestURI());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}

