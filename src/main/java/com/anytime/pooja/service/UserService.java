package com.anytime.pooja.service;

import com.anytime.pooja.dto.UserDTO;
import com.anytime.pooja.model.User;

import java.util.List;

public interface UserService {
    UserDTO.ProfileResponse getProfile(Long userId);
    UserDTO.ProfileResponse updateProfile(Long userId, UserDTO.UpdateProfileRequest request);
    void changePassword(Long userId, String currentPassword, String newPassword);
    void deleteAccount(Long userId);
    UserDTO.UserPreferenceResponse getPreferences(Long userId);
    UserDTO.UserPreferenceResponse updatePreferences(Long userId, UserDTO.UpdatePreferenceRequest request);
    List<UserDTO.AddressResponse> getAddresses(Long userId);
    UserDTO.AddressResponse addAddress(Long userId, UserDTO.AddressRequest request);
    UserDTO.AddressResponse updateAddress(Long userId, Long addressId, UserDTO.AddressRequest request);
    void deleteAddress(Long userId, Long addressId);
    void setDefaultAddress(Long userId, Long addressId);
    User getCurrentUser();
    Long getCurrentUserId();
}

