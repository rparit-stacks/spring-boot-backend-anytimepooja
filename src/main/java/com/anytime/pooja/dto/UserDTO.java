package com.anytime.pooja.dto;

import com.anytime.pooja.model.enums.AddressType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class UserDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileResponse {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String profileImageUrl;
        private Boolean isEmailVerified;
        private Boolean isPhoneVerified;
        private LocalDateTime lastLogin;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfileRequest {
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        private String name;

        @Email(message = "Email should be valid")
        private String email;

        @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
        private String phone;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressRequest {
        @NotBlank(message = "Address type is required")
        private AddressType addressType;

        @NotBlank(message = "Street is required")
        @Size(max = 200, message = "Street must not exceed 200 characters")
        private String street;

        @Size(max = 100, message = "Landmark must not exceed 100 characters")
        private String landmark;

        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City must not exceed 100 characters")
        private String city;

        @NotBlank(message = "State is required")
        @Size(max = 100, message = "State must not exceed 100 characters")
        private String state;

        @Size(max = 100, message = "Country must not exceed 100 characters")
        private String country = "India";

        @Size(max = 10, message = "Zip code must not exceed 10 characters")
        private String zipCode;

        private Double latitude;
        private Double longitude;

        private Boolean isDefault = false;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressResponse {
        private Long id;
        private AddressType addressType;
        private String street;
        private String landmark;
        private String city;
        private String state;
        private String country;
        private String zipCode;
        private Double latitude;
        private Double longitude;
        private Boolean isDefault;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPreferenceResponse {
        private String language;
        private Boolean notificationEnabled;
        private Boolean emailNotification;
        private Boolean smsNotification;
        private Boolean pushNotification;
        private String theme;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePreferenceRequest {
        private String language;
        private Boolean notificationEnabled;
        private Boolean emailNotification;
        private Boolean smsNotification;
        private Boolean pushNotification;
        private String theme;
    }
}

