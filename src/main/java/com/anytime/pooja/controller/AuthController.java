package com.anytime.pooja.controller;

import com.anytime.pooja.dto.AuthDTO;
import com.anytime.pooja.dto.CommonDTO;
import com.anytime.pooja.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register/user")
    public ResponseEntity<CommonDTO.ApiResponse<AuthDTO.AuthResponse>> registerUser(
            @Valid @RequestBody AuthDTO.RegisterRequest request) {
        try {
            request.setRole(com.anytime.pooja.model.enums.Role.USER);
            AuthDTO.AuthResponse response = authService.register(request);
            
            CommonDTO.ApiResponse<AuthDTO.AuthResponse> apiResponse = new CommonDTO.ApiResponse<>();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("User registered successfully. Please verify your email/phone with OTP.");
            apiResponse.setData(response);
            apiResponse.setStatusCode(HttpStatus.CREATED.value());
            apiResponse.setTimestamp(LocalDateTime.now());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } catch (Exception e) {
            CommonDTO.ApiResponse<AuthDTO.AuthResponse> apiResponse = new CommonDTO.ApiResponse<>();
            apiResponse.setSuccess(false);
            apiResponse.setMessage(e.getMessage());
            apiResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            apiResponse.setTimestamp(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }

    @PostMapping("/register/pandit")
    public ResponseEntity<CommonDTO.ApiResponse<AuthDTO.AuthResponse>> registerPandit(
            @Valid @RequestBody AuthDTO.RegisterRequest request) {
        try {
            request.setRole(com.anytime.pooja.model.enums.Role.PANDIT);
            AuthDTO.AuthResponse response = authService.register(request);
            
            CommonDTO.ApiResponse<AuthDTO.AuthResponse> apiResponse = new CommonDTO.ApiResponse<>();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Pandit registered successfully. Please verify your email/phone with OTP.");
            apiResponse.setData(response);
            apiResponse.setStatusCode(HttpStatus.CREATED.value());
            apiResponse.setTimestamp(LocalDateTime.now());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } catch (Exception e) {
            CommonDTO.ApiResponse<AuthDTO.AuthResponse> apiResponse = new CommonDTO.ApiResponse<>();
            apiResponse.setSuccess(false);
            apiResponse.setMessage(e.getMessage());
            apiResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            apiResponse.setTimestamp(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<CommonDTO.ApiResponse<AuthDTO.AuthResponse>> login(
            @Valid @RequestBody AuthDTO.LoginRequest request) {
        try {
            AuthDTO.AuthResponse response = authService.login(request);
            
            CommonDTO.ApiResponse<AuthDTO.AuthResponse> apiResponse = new CommonDTO.ApiResponse<>();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Login successful");
            apiResponse.setData(response);
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setTimestamp(LocalDateTime.now());
            
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            CommonDTO.ApiResponse<AuthDTO.AuthResponse> apiResponse = new CommonDTO.ApiResponse<>();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Invalid credentials");
            apiResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            apiResponse.setTimestamp(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<CommonDTO.ApiResponse<AuthDTO.AuthResponse>> verifyOTP(
            @Valid @RequestBody AuthDTO.VerifyOTPRequest request) {
        try {
            AuthDTO.AuthResponse response = authService.verifyOTP(request);
            
            CommonDTO.ApiResponse<AuthDTO.AuthResponse> apiResponse = new CommonDTO.ApiResponse<>();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("OTP verified successfully");
            apiResponse.setData(response);
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setTimestamp(LocalDateTime.now());
            
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            CommonDTO.ApiResponse<AuthDTO.AuthResponse> apiResponse = new CommonDTO.ApiResponse<>();
            apiResponse.setSuccess(false);
            apiResponse.setMessage(e.getMessage());
            apiResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            apiResponse.setTimestamp(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<CommonDTO.SuccessResponse> forgotPassword(
            @Valid @RequestBody AuthDTO.ForgotPasswordRequest request) {
        try {
            authService.forgotPassword(request);
            
            CommonDTO.SuccessResponse response = new CommonDTO.SuccessResponse();
            response.setSuccess(true);
            response.setMessage("OTP has been sent to your email/phone");
            response.setStatusCode(HttpStatus.OK.value());
            response.setTimestamp(LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CommonDTO.SuccessResponse response = new CommonDTO.SuccessResponse();
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setTimestamp(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<CommonDTO.SuccessResponse> resetPassword(
            @Valid @RequestBody AuthDTO.ResetPasswordRequest request) {
        try {
            authService.resetPassword(request);
            
            CommonDTO.SuccessResponse response = new CommonDTO.SuccessResponse();
            response.setSuccess(true);
            response.setMessage("Password reset successfully");
            response.setStatusCode(HttpStatus.OK.value());
            response.setTimestamp(LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CommonDTO.SuccessResponse response = new CommonDTO.SuccessResponse();
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setTimestamp(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<CommonDTO.SuccessResponse> changePassword(
            @Valid @RequestBody AuthDTO.ChangePasswordRequest request) {
        try {
            Long userId = authService.getCurrentUserId();
            if (userId == null) {
                throw new RuntimeException("User not authenticated");
            }
            
            authService.changePassword(userId, request);
            
            CommonDTO.SuccessResponse response = new CommonDTO.SuccessResponse();
            response.setSuccess(true);
            response.setMessage("Password changed successfully");
            response.setStatusCode(HttpStatus.OK.value());
            response.setTimestamp(LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CommonDTO.SuccessResponse response = new CommonDTO.SuccessResponse();
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setTimestamp(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<CommonDTO.ApiResponse<AuthDTO.AuthResponse>> getCurrentUser() {
        try {
            com.anytime.pooja.model.User user = authService.getCurrentUser();
            if (user == null) {
                throw new RuntimeException("User not authenticated");
            }
            
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
            
            CommonDTO.ApiResponse<AuthDTO.AuthResponse> apiResponse = new CommonDTO.ApiResponse<>();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("User details retrieved successfully");
            apiResponse.setData(response);
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setTimestamp(LocalDateTime.now());
            
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            CommonDTO.ApiResponse<AuthDTO.AuthResponse> apiResponse = new CommonDTO.ApiResponse<>();
            apiResponse.setSuccess(false);
            apiResponse.setMessage(e.getMessage());
            apiResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            apiResponse.setTimestamp(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonDTO.SuccessResponse> logout() {
        CommonDTO.SuccessResponse response = new CommonDTO.SuccessResponse();
        response.setSuccess(true);
        response.setMessage("Logged out successfully");
        response.setStatusCode(HttpStatus.OK.value());
        response.setTimestamp(LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
}

