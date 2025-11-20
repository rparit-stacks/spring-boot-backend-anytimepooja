package com.anytime.pooja.service;

import com.anytime.pooja.dto.AuthDTO;
import com.anytime.pooja.model.User;
import com.anytime.pooja.model.enums.Role;
import com.anytime.pooja.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;

    @Transactional
    public AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("User with this phone already exists");
        }

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : Role.USER);
        user.setIsActive(true);
        user.setIsEmailVerified(false);
        user.setIsPhoneVerified(true); // Phone verification disabled (no SMS service)
        user.setCreatedAt(LocalDateTime.now());

        user = userRepository.save(user);

        // Generate and send OTP via email only
        String otp = generateOTP();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        userRepository.save(user);

        // Send OTP via email
        try {
            emailService.sendOTPEmail(user.getEmail(), otp);
        } catch (Exception e) {
            // Log error but don't fail registration
            System.err.println("Failed to send OTP email: " + e.getMessage());
        }

        return buildAuthResponse(user);
    }

    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmailOrPhone(),
                    request.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByEmailOrPhone(
                request.getEmailOrPhone(),
                request.getEmailOrPhone()
            ).orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

            // Update last login
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            return buildAuthResponse(user);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid email/phone or password");
        }
    }

    @Transactional
    public AuthDTO.AuthResponse verifyOTP(AuthDTO.VerifyOTPRequest request) {
        // Only email verification (no SMS service)
        User user = userRepository.findByEmail(request.getEmailOrPhone())
            .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmailOrPhone()));

        if (user.getOtp() == null || !user.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired");
        }

        // Verify email only (no SMS service, so only email OTP)
        user.setIsEmailVerified(true);

        // Clear OTP
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    @Transactional
    public void forgotPassword(AuthDTO.ForgotPasswordRequest request) {
        // Only email-based password reset (no SMS service)
        User user = userRepository.findByEmail(request.getEmailOrPhone())
            .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmailOrPhone()));

        // Generate OTP and send via email only
        String otp = generateOTP();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        userRepository.save(user);

        // Send OTP via email only
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), otp);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
        }
    }

    @Transactional
    public void resetPassword(AuthDTO.ResetPasswordRequest request) {
        // Only email-based password reset (no SMS service)
        User user = userRepository.findByEmail(request.getEmailOrPhone())
            .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmailOrPhone()));

        if (user.getOtp() == null || !user.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired");
        }

        // Reset password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long userId, AuthDTO.ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        return userRepository.findByEmail(username)
            .orElse(null);
    }

    public Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    private String generateOTP() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    private AuthDTO.AuthResponse buildAuthResponse(User user) {
        AuthDTO.AuthResponse response = new AuthDTO.AuthResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setProfileImageUrl(user.getProfileImageUrl());
        response.setIsEmailVerified(user.getIsEmailVerified());
        response.setIsPhoneVerified(user.getIsPhoneVerified());
        response.setLastLogin(user.getLastLogin());
        // Session token will be managed by Spring Security
        response.setToken("SESSION_TOKEN");
        return response;
    }
}

