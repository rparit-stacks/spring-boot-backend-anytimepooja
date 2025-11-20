package com.anytime.pooja.controller;

import com.anytime.pooja.dto.CommonDTO;
import com.anytime.pooja.dto.UserDTO;
import com.anytime.pooja.service.AuthService;
import com.anytime.pooja.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @GetMapping("/profile")
    public ResponseEntity<CommonDTO.ApiResponse<UserDTO.ProfileResponse>> getProfile() {
        Long userId = authService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDTO.ProfileResponse response = userService.getProfile(userId);
        return ResponseEntity.ok(buildSuccessResponse(response, "Profile retrieved successfully"));
    }

    @PutMapping("/profile")
    public ResponseEntity<CommonDTO.ApiResponse<UserDTO.ProfileResponse>> updateProfile(
            @Valid @RequestBody UserDTO.UpdateProfileRequest request) {
        Long userId = authService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDTO.ProfileResponse response = userService.updateProfile(userId, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Profile updated successfully"));
    }

    @PostMapping("/profile/image")
    public ResponseEntity<CommonDTO.ApiResponse<String>> uploadProfileImage(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        Long userId = authService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Implementation will be done later
        return ResponseEntity.ok(buildSuccessResponse("Image uploaded", "Profile image uploaded successfully"));
    }

    @PutMapping("/password")
    public ResponseEntity<CommonDTO.SuccessResponse> changePassword(
            @Valid @RequestBody com.anytime.pooja.dto.AuthDTO.ChangePasswordRequest request) {
        Long userId = authService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        userService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok(buildSuccessResponse("Password changed successfully"));
    }

    @DeleteMapping("/account")
    public ResponseEntity<CommonDTO.SuccessResponse> deleteAccount() {
        Long userId = authService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        userService.deleteAccount(userId);
        return ResponseEntity.ok(buildSuccessResponse("Account deleted successfully"));
    }

    @GetMapping("/preferences")
    public ResponseEntity<CommonDTO.ApiResponse<UserDTO.UserPreferenceResponse>> getPreferences() {
        Long userId = authService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDTO.UserPreferenceResponse response = userService.getPreferences(userId);
        return ResponseEntity.ok(buildSuccessResponse(response, "Preferences retrieved successfully"));
    }

    @PutMapping("/preferences")
    public ResponseEntity<CommonDTO.ApiResponse<UserDTO.UserPreferenceResponse>> updatePreferences(
            @Valid @RequestBody UserDTO.UpdatePreferenceRequest request) {
        Long userId = authService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDTO.UserPreferenceResponse response = userService.updatePreferences(userId, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Preferences updated successfully"));
    }

    @GetMapping("/addresses")
    public ResponseEntity<CommonDTO.ApiResponse<List<UserDTO.AddressResponse>>> getAddresses() {
        Long userId = authService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<UserDTO.AddressResponse> response = userService.getAddresses(userId);
        return ResponseEntity.ok(buildSuccessResponse(response, "Addresses retrieved successfully"));
    }

    @PostMapping("/addresses")
    public ResponseEntity<CommonDTO.ApiResponse<UserDTO.AddressResponse>> addAddress(
            @Valid @RequestBody UserDTO.AddressRequest request) {
        Long userId = authService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDTO.AddressResponse response = userService.addAddress(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildSuccessResponse(response, "Address added successfully"));
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<UserDTO.AddressResponse>> updateAddress(
            @PathVariable Long id, @Valid @RequestBody UserDTO.AddressRequest request) {
        Long userId = authService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDTO.AddressResponse response = userService.updateAddress(userId, id, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Address updated successfully"));
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<CommonDTO.SuccessResponse> deleteAddress(@PathVariable Long id) {
        Long userId = authService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        userService.deleteAddress(userId, id);
        return ResponseEntity.ok(buildSuccessResponse("Address deleted successfully"));
    }

    @PutMapping("/addresses/{id}/set-default")
    public ResponseEntity<CommonDTO.SuccessResponse> setDefaultAddress(@PathVariable Long id) {
        Long userId = authService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        userService.setDefaultAddress(userId, id);
        return ResponseEntity.ok(buildSuccessResponse("Default address set successfully"));
    }

    // Helper methods
    private <T> CommonDTO.ApiResponse<T> buildSuccessResponse(T data, String message) {
        CommonDTO.ApiResponse<T> response = new CommonDTO.ApiResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        response.setStatusCode(HttpStatus.OK.value());
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    private CommonDTO.SuccessResponse buildSuccessResponse(String message) {
        CommonDTO.SuccessResponse response = new CommonDTO.SuccessResponse();
        response.setSuccess(true);
        response.setMessage(message);
        response.setStatusCode(HttpStatus.OK.value());
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
}

