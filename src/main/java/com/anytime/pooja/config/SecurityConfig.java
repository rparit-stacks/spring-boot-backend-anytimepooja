package com.anytime.pooja.config;

import com.anytime.pooja.security.CustomAuthenticationEntryPoint;
import com.anytime.pooja.security.CustomAccessDeniedHandler;
import com.anytime.pooja.security.CustomAuthenticationFilter;
import com.anytime.pooja.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private CustomAuthenticationFilter authenticationFilter;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .authorizeHttpRequests(auth -> auth
                // Public endpoints (No authentication required)
                .requestMatchers(
                    "/api/auth/**",
                    "/api/cms/**",
                    "/api/products/**",  // Public product browsing
                    "/api/categories/**", // Public category browsing
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/api-docs/**",
                    "/v3/api-docs/**",
                    "/actuator/health",
                    "/error",
                    "/favicon.ico"
                ).permitAll()
                
                // User Management - All authenticated users can access
                .requestMatchers("/api/users/profile", "/api/users/update-profile").hasAnyRole("USER", "PANDIT", "ADMIN")
                .requestMatchers("/api/users/**").hasAnyRole("USER", "PANDIT", "ADMIN")
                
                // Shopping endpoints - USER and ADMIN only
                .requestMatchers("/api/cart/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/orders/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/wishlist/**").hasAnyRole("USER", "ADMIN")
                
                // Reviews - All authenticated users
                .requestMatchers("/api/reviews/**").hasAnyRole("USER", "PANDIT", "ADMIN")
                
                // Pandit endpoints - PANDIT and ADMIN only
                .requestMatchers("/api/pandit/**").hasAnyRole("PANDIT", "ADMIN")
                
                // Booking endpoints - USER, PANDIT, and ADMIN
                .requestMatchers("/api/bookings/**").hasAnyRole("USER", "PANDIT", "ADMIN")
                
                // Admin endpoints - ADMIN only
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Chat endpoints - All authenticated users
                .requestMatchers("/api/chat/**").hasAnyRole("USER", "PANDIT", "ADMIN")
                
                // WebSocket endpoints - Security handled via WebSocketSecurityInterceptor
                .requestMatchers("/ws/**").permitAll()
                
                // Notification endpoints - All authenticated users
                .requestMatchers("/api/notifications/**").hasAnyRole("USER", "PANDIT", "ADMIN")
                
                // Payment endpoints - USER and ADMIN only
                .requestMatchers("/api/payments/**").hasAnyRole("USER", "ADMIN")
                
                // Support endpoints - All authenticated users
                .requestMatchers("/api/support/**").hasAnyRole("USER", "PANDIT", "ADMIN")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

