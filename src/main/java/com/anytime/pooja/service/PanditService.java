package com.anytime.pooja.service;

import com.anytime.pooja.dto.PanditDTO;
import com.anytime.pooja.model.enums.KYCStatus;

import java.time.LocalDate;
import java.util.List;

public interface PanditService {
    // Profile Management
    PanditDTO.ProfileResponse getProfile(Long panditId);
    PanditDTO.ProfileResponse updateProfile(Long panditId, PanditDTO.ProfileUpdateRequest request);
    void updateProfileImage(Long panditId, String imageUrl);
    void updateServiceAreas(Long panditId, List<String> serviceAreas);
    void updateLanguages(Long panditId, List<String> languages);
    
    // KYC Management
    PanditDTO.KYCResponse submitKYC(Long panditId, PanditDTO.KYCSubmitRequest request);
    PanditDTO.KYCResponse getKYCStatus(Long panditId);
    PanditDTO.KYCResponse resubmitKYC(Long panditId, PanditDTO.KYCSubmitRequest request);
    
    // Service Management
    List<PanditDTO.ServiceResponse> getServices(Long panditId);
    PanditDTO.ServiceResponse createService(Long panditId, PanditDTO.ServiceRequest request);
    PanditDTO.ServiceResponse updateService(Long panditId, Long serviceId, PanditDTO.ServiceRequest request);
    void deleteService(Long panditId, Long serviceId);
    
    // Availability Management
    List<PanditDTO.AvailabilityResponse> getAvailability(Long panditId, LocalDate date);
    PanditDTO.AvailabilityResponse createAvailability(Long panditId, PanditDTO.AvailabilityRequest request);
    void createBulkAvailability(Long panditId, PanditDTO.BulkAvailabilityRequest request);
    PanditDTO.AvailabilityResponse updateAvailability(Long panditId, Long availabilityId, PanditDTO.AvailabilityRequest request);
    void deleteAvailability(Long panditId, Long availabilityId);
    
    // Bank Details
    PanditDTO.BankDetailsResponse getBankDetails(Long panditId);
    PanditDTO.BankDetailsResponse addBankDetails(Long panditId, PanditDTO.BankDetailsRequest request);
    PanditDTO.BankDetailsResponse updateBankDetails(Long panditId, PanditDTO.BankDetailsRequest request);
    
    // Earnings
    PanditDTO.EarningsSummaryResponse getEarningsSummary(Long panditId);
    List<PanditDTO.EarningsDetailResponse> getEarningsDetails(Long panditId, LocalDate fromDate, LocalDate toDate);
    List<PanditDTO.EarningsDetailResponse> getPayouts(Long panditId);
    
    // Search
    List<PanditDTO.SearchResponse> searchPandits(PanditDTO.SearchRequest request);
    List<PanditDTO.SearchResponse> getNearbyPandits(Double latitude, Double longitude, Double radius);
    List<PanditDTO.SearchResponse> getFeaturedPandits();
}

