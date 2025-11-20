package com.anytime.pooja.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * WebSocket Security Interceptor
 * Modern approach for securing STOMP messages without deprecated classes
 */
@Component
public class WebSocketSecurityInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null) {
            StompCommand command = accessor.getCommand();
            
            // Handle CONNECT command - verify authentication
            if (StompCommand.CONNECT.equals(command)) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated()) {
                    accessor.setUser(auth);
                } else {
                    throw new SecurityException("User not authenticated");
                }
            }
            
            // Handle SUBSCRIBE command - verify user can subscribe to destination
            if (StompCommand.SUBSCRIBE.equals(command)) {
                String destination = accessor.getDestination();
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                
                if (auth == null || !auth.isAuthenticated()) {
                    throw new SecurityException("User not authenticated");
                }
                
                // Verify user can access the destination
                if (!isAuthorized(destination, auth)) {
                    throw new SecurityException("User not authorized to subscribe to: " + destination);
                }
            }
            
            // Handle SEND command - verify user can send to destination
            if (StompCommand.SEND.equals(command)) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth == null || !auth.isAuthenticated()) {
                    throw new SecurityException("User not authenticated");
                }
            }
        }
        
        return message;
    }

    /**
     * Check if user is authorized to access the destination
     */
    private boolean isAuthorized(String destination, Authentication auth) {
        if (destination == null) {
            return false;
        }
        
        // Allow user-specific queues
        if (destination.startsWith("/user/") || destination.startsWith("/queue/")) {
            return true;
        }
        
        // Allow conversation topics for authenticated users
        if (destination.startsWith("/topic/conversation/")) {
            return true;
        }
        
        // Allow general topics for authenticated users
        if (destination.startsWith("/topic/")) {
            return true;
        }
        
        return false;
    }
}

