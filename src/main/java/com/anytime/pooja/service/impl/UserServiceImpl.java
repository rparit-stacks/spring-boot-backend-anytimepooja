package com.anytime.pooja.service.impl;

import com.anytime.pooja.dto.UserDTO;
import com.anytime.pooja.model.Address;
import com.anytime.pooja.model.User;
import com.anytime.pooja.model.UserPreference;
import com.anytime.pooja.repository.AddressRepository;
import com.anytime.pooja.repository.UserPreferenceRepository;
import com.anytime.pooja.repository.UserRepository;
import com.anytime.pooja.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDTO.ProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToProfileResponse(user);
    }

    @Override
    @Transactional
    public UserDTO.ProfileResponse updateProfile(Long userId, UserDTO.UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (request.getName() != null) user.setName(request.getName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        
        user = userRepository.save(user);
        return mapToProfileResponse(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public UserDTO.UserPreferenceResponse getPreferences(Long userId) {
        UserPreference preference = userPreferenceRepository.findByUserId(userId)
            .orElseGet(() -> createDefaultPreference(userId));
        return mapToPreferenceResponse(preference);
    }

    @Override
    @Transactional
    public UserDTO.UserPreferenceResponse updatePreferences(Long userId, UserDTO.UpdatePreferenceRequest request) {
        UserPreference preference = userPreferenceRepository.findByUserId(userId)
            .orElseGet(() -> createDefaultPreference(userId));
        
        if (request.getLanguage() != null) preference.setLanguage(request.getLanguage());
        if (request.getNotificationEnabled() != null) preference.setNotificationEnabled(request.getNotificationEnabled());
        if (request.getEmailNotification() != null) preference.setEmailNotification(request.getEmailNotification());
        if (request.getSmsNotification() != null) preference.setSmsNotification(request.getSmsNotification());
        if (request.getPushNotification() != null) preference.setPushNotification(request.getPushNotification());
        if (request.getTheme() != null) preference.setTheme(request.getTheme());
        
        preference = userPreferenceRepository.save(preference);
        return mapToPreferenceResponse(preference);
    }

    @Override
    public List<UserDTO.AddressResponse> getAddresses(Long userId) {
        List<Address> addresses = addressRepository.findByUserIdOrderByDefault(userId);
        return addresses.stream()
            .map(this::mapToAddressResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO.AddressResponse addAddress(Long userId, UserDTO.AddressRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Address address = new Address();
        address.setUser(user);
        address.setAddressType(request.getAddressType());
        address.setStreet(request.getStreet());
        address.setLandmark(request.getLandmark());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setZipCode(request.getZipCode());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : false);
        
        if (address.getIsDefault()) {
            // Unset other default addresses
            addressRepository.findByUserIdAndIsDefaultTrue(userId).ifPresent(addr -> {
                addr.setIsDefault(false);
                addressRepository.save(addr);
            });
        }
        
        address = addressRepository.save(address);
        return mapToAddressResponse(address);
    }

    @Override
    @Transactional
    public UserDTO.AddressResponse updateAddress(Long userId, Long addressId, UserDTO.AddressRequest request) {
        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new RuntimeException("Address not found"));
        
        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        if (request.getAddressType() != null) address.setAddressType(request.getAddressType());
        if (request.getStreet() != null) address.setStreet(request.getStreet());
        if (request.getLandmark() != null) address.setLandmark(request.getLandmark());
        if (request.getCity() != null) address.setCity(request.getCity());
        if (request.getState() != null) address.setState(request.getState());
        if (request.getCountry() != null) address.setCountry(request.getCountry());
        if (request.getZipCode() != null) address.setZipCode(request.getZipCode());
        if (request.getLatitude() != null) address.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) address.setLongitude(request.getLongitude());
        if (request.getIsDefault() != null) {
            address.setIsDefault(request.getIsDefault());
            if (request.getIsDefault()) {
                addressRepository.findByUserIdAndIsDefaultTrue(userId).ifPresent(addr -> {
                    if (!addr.getId().equals(addressId)) {
                        addr.setIsDefault(false);
                        addressRepository.save(addr);
                    }
                });
            }
        }
        
        address = addressRepository.save(address);
        return mapToAddressResponse(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new RuntimeException("Address not found"));
        
        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new RuntimeException("Address not found"));
        
        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        // Unset other default addresses
        addressRepository.findByUserIdAndIsDefaultTrue(userId).ifPresent(addr -> {
            addr.setIsDefault(false);
            addressRepository.save(addr);
        });
        
        address.setIsDefault(true);
        addressRepository.save(address);
    }

    @Override
    public User getCurrentUser() {
        // Implementation will use SecurityContext
        return null; // To be implemented
    }

    @Override
    public Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    // Helper methods
    private UserDTO.ProfileResponse mapToProfileResponse(User user) {
        UserDTO.ProfileResponse response = new UserDTO.ProfileResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setProfileImageUrl(user.getProfileImageUrl());
        response.setIsEmailVerified(user.getIsEmailVerified());
        response.setIsPhoneVerified(user.getIsPhoneVerified());
        response.setLastLogin(user.getLastLogin());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    private UserDTO.AddressResponse mapToAddressResponse(Address address) {
        UserDTO.AddressResponse response = new UserDTO.AddressResponse();
        response.setId(address.getId());
        response.setAddressType(address.getAddressType());
        response.setStreet(address.getStreet());
        response.setLandmark(address.getLandmark());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setCountry(address.getCountry());
        response.setZipCode(address.getZipCode());
        response.setLatitude(address.getLatitude());
        response.setLongitude(address.getLongitude());
        response.setIsDefault(address.getIsDefault());
        response.setCreatedAt(address.getCreatedAt());
        return response;
    }

    private UserDTO.UserPreferenceResponse mapToPreferenceResponse(UserPreference preference) {
        UserDTO.UserPreferenceResponse response = new UserDTO.UserPreferenceResponse();
        response.setLanguage(preference.getLanguage());
        response.setNotificationEnabled(preference.getNotificationEnabled());
        response.setEmailNotification(preference.getEmailNotification());
        response.setSmsNotification(preference.getSmsNotification());
        response.setPushNotification(preference.getPushNotification());
        response.setTheme(preference.getTheme());
        return response;
    }

    private UserPreference createDefaultPreference(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserPreference preference = new UserPreference();
        preference.setUser(user);
        preference.setLanguage("english");
        preference.setNotificationEnabled(true);
        preference.setEmailNotification(true);
        preference.setSmsNotification(true);
        preference.setPushNotification(true);
        preference.setTheme("light");
        return userPreferenceRepository.save(preference);
    }
}

