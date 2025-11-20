package com.anytime.pooja.service.impl;

import com.anytime.pooja.dto.PanditDTO;
import com.anytime.pooja.model.*;
import com.anytime.pooja.model.enums.KYCStatus;
import com.anytime.pooja.repository.*;
import com.anytime.pooja.service.PanditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PanditServiceImpl implements PanditService {

    @Autowired
    private PanditProfileRepository panditProfileRepository;

    @Autowired
    private KYCDetailsRepository kycDetailsRepository;

    @Autowired
    private BankDetailsRepository bankDetailsRepository;

    @Autowired
    private PanditServiceRepository panditServiceRepository;

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private PanditEarningRepository panditEarningRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public PanditDTO.ProfileResponse getProfile(Long panditId) {
        PanditProfile profile = panditProfileRepository.findById(panditId)
            .orElseThrow(() -> new RuntimeException("Pandit profile not found"));
        return mapToProfileResponse(profile);
    }

    @Override
    @Transactional
    public PanditDTO.ProfileResponse updateProfile(Long panditId, PanditDTO.ProfileUpdateRequest request) {
        PanditProfile profile = panditProfileRepository.findById(panditId)
            .orElseThrow(() -> new RuntimeException("Pandit profile not found"));
        
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getExperienceYears() != null) profile.setExperienceYears(request.getExperienceYears());
        if (request.getLanguages() != null) profile.setLanguages(request.getLanguages());
        if (request.getServiceAreas() != null) profile.setServiceAreas(request.getServiceAreas());
        
        profile = panditProfileRepository.save(profile);
        return mapToProfileResponse(profile);
    }

    @Override
    @Transactional
    public void updateProfileImage(Long panditId, String imageUrl) {
        PanditProfile profile = panditProfileRepository.findById(panditId)
            .orElseThrow(() -> new RuntimeException("Pandit profile not found"));
        
        User user = profile.getUser();
        user.setProfileImageUrl(imageUrl);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateServiceAreas(Long panditId, List<String> serviceAreas) {
        PanditProfile profile = panditProfileRepository.findById(panditId)
            .orElseThrow(() -> new RuntimeException("Pandit profile not found"));
        profile.setServiceAreas(serviceAreas.stream().collect(java.util.stream.Collectors.toSet()));
        panditProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public void updateLanguages(Long panditId, List<String> languages) {
        PanditProfile profile = panditProfileRepository.findById(panditId)
            .orElseThrow(() -> new RuntimeException("Pandit profile not found"));
        profile.setLanguages(languages.stream().collect(java.util.stream.Collectors.toSet()));
        panditProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public PanditDTO.KYCResponse submitKYC(Long panditId, PanditDTO.KYCSubmitRequest request) {
        PanditProfile profile = panditProfileRepository.findById(panditId)
            .orElseThrow(() -> new RuntimeException("Pandit profile not found"));
        
        KYCDetails kyc = kycDetailsRepository.findByPanditId(panditId).orElse(new KYCDetails());
        kyc.setPandit(profile);
        kyc.setDocumentType(request.getDocumentType());
        kyc.setDocumentNumber(request.getDocumentNumber());
        kyc.setFrontImageUrl(request.getFrontImageUrl());
        kyc.setBackImageUrl(request.getBackImageUrl());
        kyc.setSelfieImageUrl(request.getSelfieImageUrl());
        kyc.setStatus(KYCStatus.PENDING);
        
        kyc = kycDetailsRepository.save(kyc);
        return mapToKYCResponse(kyc);
    }

    @Override
    public PanditDTO.KYCResponse getKYCStatus(Long panditId) {
        KYCDetails kyc = kycDetailsRepository.findByPanditId(panditId)
            .orElseThrow(() -> new RuntimeException("KYC not found"));
        return mapToKYCResponse(kyc);
    }

    @Override
    @Transactional
    public PanditDTO.KYCResponse resubmitKYC(Long panditId, PanditDTO.KYCSubmitRequest request) {
        return submitKYC(panditId, request);
    }

    @Override
    public List<PanditDTO.ServiceResponse> getServices(Long panditId) {
        List<com.anytime.pooja.model.PanditService> services = panditServiceRepository.findByPanditIdAndIsActiveTrue(panditId);
        return services.stream()
            .map(this::mapToServiceResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PanditDTO.ServiceResponse createService(Long panditId, PanditDTO.ServiceRequest request) {
        PanditProfile profile = panditProfileRepository.findById(panditId)
            .orElseThrow(() -> new RuntimeException("Pandit profile not found"));
        
        com.anytime.pooja.model.PanditService service = new com.anytime.pooja.model.PanditService();
        service.setPandit(profile);
        service.setServiceName(request.getServiceName());
        if (request.getCategoryId() != null) {
            ServiceCategory category = serviceCategoryRepository.findById(request.getCategoryId())
                .orElse(null);
            service.setCategory(category);
        }
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setDurationMinutes(request.getDurationMinutes());
        service.setMaterialsIncluded(request.getMaterialsIncluded());
        service.setHomeVisit(request.getHomeVisit());
        service.setMaxDistanceKm(request.getMaxDistanceKm());
        service.setImageUrl(request.getImageUrl());
        service.setIsActive(true);
        
        service = panditServiceRepository.save(service);
        return mapToServiceResponse(service);
    }

    @Override
    @Transactional
    public PanditDTO.ServiceResponse updateService(Long panditId, Long serviceId, PanditDTO.ServiceRequest request) {
        com.anytime.pooja.model.PanditService service = panditServiceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("Service not found"));
        
        if (!service.getPandit().getId().equals(panditId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        if (request.getServiceName() != null) service.setServiceName(request.getServiceName());
        if (request.getCategoryId() != null) {
            ServiceCategory category = serviceCategoryRepository.findById(request.getCategoryId()).orElse(null);
            service.setCategory(category);
        }
        if (request.getDescription() != null) service.setDescription(request.getDescription());
        if (request.getPrice() != null) service.setPrice(request.getPrice());
        if (request.getDurationMinutes() != null) service.setDurationMinutes(request.getDurationMinutes());
        if (request.getMaterialsIncluded() != null) service.setMaterialsIncluded(request.getMaterialsIncluded());
        if (request.getHomeVisit() != null) service.setHomeVisit(request.getHomeVisit());
        if (request.getMaxDistanceKm() != null) service.setMaxDistanceKm(request.getMaxDistanceKm());
        if (request.getImageUrl() != null) service.setImageUrl(request.getImageUrl());
        
        service = panditServiceRepository.save(service);
        return mapToServiceResponse(service);
    }

    @Override
    @Transactional
    public void deleteService(Long panditId, Long serviceId) {
        com.anytime.pooja.model.PanditService service = panditServiceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("Service not found"));
        
        if (!service.getPandit().getId().equals(panditId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        service.setIsActive(false);
        panditServiceRepository.save(service);
    }

    @Override
    public List<PanditDTO.AvailabilityResponse> getAvailability(Long panditId, LocalDate date) {
        List<Availability> availabilities = availabilityRepository.findByPanditIdAndDate(panditId, date);
        return availabilities.stream()
            .map(this::mapToAvailabilityResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PanditDTO.AvailabilityResponse createAvailability(Long panditId, PanditDTO.AvailabilityRequest request) {
        PanditProfile profile = panditProfileRepository.findById(panditId)
            .orElseThrow(() -> new RuntimeException("Pandit profile not found"));
        
        Availability availability = new Availability();
        availability.setPandit(profile);
        availability.setDate(request.getDate());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());
        availability.setSlotDuration(request.getSlotDuration());
        availability.setIsBooked(false);
        
        availability = availabilityRepository.save(availability);
        return mapToAvailabilityResponse(availability);
    }

    @Override
    @Transactional
    public void createBulkAvailability(Long panditId, PanditDTO.BulkAvailabilityRequest request) {
        PanditProfile profile = panditProfileRepository.findById(panditId)
            .orElseThrow(() -> new RuntimeException("Pandit profile not found"));
        
        LocalDate currentDate = request.getStartDate();
        while (!currentDate.isAfter(request.getEndDate())) {
            if (request.getExcludeDates() == null || !request.getExcludeDates().contains(currentDate)) {
                Availability availability = new Availability();
                availability.setPandit(profile);
                availability.setDate(currentDate);
                availability.setStartTime(request.getStartTime());
                availability.setEndTime(request.getEndTime());
                availability.setSlotDuration(request.getSlotDuration());
                availability.setIsBooked(false);
                availabilityRepository.save(availability);
            }
            currentDate = currentDate.plusDays(1);
        }
    }

    @Override
    @Transactional
    public PanditDTO.AvailabilityResponse updateAvailability(Long panditId, Long availabilityId, PanditDTO.AvailabilityRequest request) {
        Availability availability = availabilityRepository.findById(availabilityId)
            .orElseThrow(() -> new RuntimeException("Availability not found"));
        
        if (!availability.getPandit().getId().equals(panditId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        if (request.getDate() != null) availability.setDate(request.getDate());
        if (request.getStartTime() != null) availability.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) availability.setEndTime(request.getEndTime());
        if (request.getSlotDuration() != null) availability.setSlotDuration(request.getSlotDuration());
        
        availability = availabilityRepository.save(availability);
        return mapToAvailabilityResponse(availability);
    }

    @Override
    @Transactional
    public void deleteAvailability(Long panditId, Long availabilityId) {
        Availability availability = availabilityRepository.findById(availabilityId)
            .orElseThrow(() -> new RuntimeException("Availability not found"));
        
        if (!availability.getPandit().getId().equals(panditId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        availabilityRepository.delete(availability);
    }

    @Override
    public PanditDTO.BankDetailsResponse getBankDetails(Long panditId) {
        BankDetails bankDetails = bankDetailsRepository.findByPanditId(panditId)
            .orElseThrow(() -> new RuntimeException("Bank details not found"));
        return mapToBankDetailsResponse(bankDetails);
    }

    @Override
    @Transactional
    public PanditDTO.BankDetailsResponse addBankDetails(Long panditId, PanditDTO.BankDetailsRequest request) {
        PanditProfile profile = panditProfileRepository.findById(panditId)
            .orElseThrow(() -> new RuntimeException("Pandit profile not found"));
        
        BankDetails bankDetails = bankDetailsRepository.findByPanditId(panditId).orElse(new BankDetails());
        bankDetails.setPandit(profile);
        bankDetails.setAccountHolderName(request.getAccountHolderName());
        bankDetails.setAccountNumber(request.getAccountNumber());
        bankDetails.setIfscCode(request.getIfscCode());
        bankDetails.setBankName(request.getBankName());
        bankDetails.setBranchName(request.getBranchName());
        bankDetails.setAccountType(request.getAccountType());
        bankDetails.setUpiId(request.getUpiId());
        
        bankDetails = bankDetailsRepository.save(bankDetails);
        return mapToBankDetailsResponse(bankDetails);
    }

    @Override
    @Transactional
    public PanditDTO.BankDetailsResponse updateBankDetails(Long panditId, PanditDTO.BankDetailsRequest request) {
        return addBankDetails(panditId, request);
    }

    @Override
    public PanditDTO.EarningsSummaryResponse getEarningsSummary(Long panditId) {
        Double totalEarnings = panditEarningRepository.getTotalEarningsByPanditId(panditId);
        Double pendingPayouts = panditEarningRepository.getTotalEarningsByPanditIdAndStatus(
            panditId, com.anytime.pooja.model.enums.PayoutStatus.PENDING);
        Double processedPayouts = panditEarningRepository.getTotalEarningsByPanditIdAndStatus(
            panditId, com.anytime.pooja.model.enums.PayoutStatus.PROCESSED);
        
        List<PanditEarning> earnings = panditEarningRepository.findByPanditId(panditId);
        Integer totalBookings = earnings.size();
        Double avgEarning = totalBookings > 0 ? totalEarnings / totalBookings : 0.0;
        
        PanditDTO.EarningsSummaryResponse response = new PanditDTO.EarningsSummaryResponse();
        response.setTotalEarnings(totalEarnings != null ? totalEarnings : 0.0);
        response.setPendingPayouts(pendingPayouts != null ? pendingPayouts : 0.0);
        response.setProcessedPayouts(processedPayouts != null ? processedPayouts : 0.0);
        response.setTotalBookings(totalBookings);
        response.setAverageEarningPerBooking(avgEarning);
        return response;
    }

    @Override
    public List<PanditDTO.EarningsDetailResponse> getEarningsDetails(Long panditId, LocalDate fromDate, LocalDate toDate) {
        List<PanditEarning> earnings = panditEarningRepository.findByPanditIdAndDateRange(
            panditId, fromDate.atStartOfDay(), toDate.atTime(23, 59, 59));
        return earnings.stream()
            .map(this::mapToEarningsDetailResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<PanditDTO.EarningsDetailResponse> getPayouts(Long panditId) {
        List<PanditEarning> earnings = panditEarningRepository.findByPanditId(panditId);
        return earnings.stream()
            .map(this::mapToEarningsDetailResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<PanditDTO.SearchResponse> searchPandits(PanditDTO.SearchRequest request) {
        // Implementation will use complex search logic
        List<PanditProfile> pandits = panditProfileRepository.findByIsVerifiedAndIsAvailable(true, true);
        return pandits.stream()
            .map(p -> mapToSearchResponse(p, request.getLatitude(), request.getLongitude()))
            .collect(Collectors.toList());
    }

    @Override
    public List<PanditDTO.SearchResponse> getNearbyPandits(Double latitude, Double longitude, Double radius) {
        List<PanditProfile> pandits = panditProfileRepository.findByIsVerifiedAndIsAvailable(true, true);
        return pandits.stream()
            .map(p -> mapToSearchResponse(p, latitude, longitude))
            .filter(p -> p.getDistance() <= radius)
            .collect(Collectors.toList());
    }

    @Override
    public List<PanditDTO.SearchResponse> getFeaturedPandits() {
        List<PanditProfile> pandits = panditProfileRepository.findTopRatedPandits();
        return pandits.stream()
            .limit(10)
            .map(p -> mapToSearchResponse(p, null, null))
            .collect(Collectors.toList());
    }

    // Helper methods
    private PanditDTO.ProfileResponse mapToProfileResponse(PanditProfile profile) {
        PanditDTO.ProfileResponse response = new PanditDTO.ProfileResponse();
        response.setId(profile.getId());
        response.setUserId(profile.getUser().getId());
        response.setName(profile.getUser().getName());
        response.setEmail(profile.getUser().getEmail());
        response.setPhone(profile.getUser().getPhone());
        response.setBio(profile.getBio());
        response.setExperienceYears(profile.getExperienceYears());
        response.setLanguages(profile.getLanguages());
        response.setServiceAreas(profile.getServiceAreas());
        response.setRating(profile.getRating());
        response.setTotalBookings(profile.getTotalBookings());
        response.setIsVerified(profile.getIsVerified());
        response.setIsAvailable(profile.getIsAvailable());
        response.setProfileImageUrl(profile.getUser().getProfileImageUrl());
        response.setVerificationDate(profile.getVerificationDate());
        response.setCreatedAt(profile.getCreatedAt());
        return response;
    }

    private PanditDTO.KYCResponse mapToKYCResponse(KYCDetails kyc) {
        PanditDTO.KYCResponse response = new PanditDTO.KYCResponse();
        response.setId(kyc.getId());
        response.setDocumentType(kyc.getDocumentType());
        response.setDocumentNumber(maskDocumentNumber(kyc.getDocumentNumber()));
        response.setFrontImageUrl(kyc.getFrontImageUrl());
        response.setBackImageUrl(kyc.getBackImageUrl());
        response.setSelfieImageUrl(kyc.getSelfieImageUrl());
        response.setStatus(kyc.getStatus());
        response.setRejectionReason(kyc.getRejectionReason());
        response.setSubmittedAt(kyc.getSubmittedAt());
        response.setVerifiedAt(kyc.getVerifiedAt());
        return response;
    }

    private PanditDTO.ServiceResponse mapToServiceResponse(com.anytime.pooja.model.PanditService service) {
        PanditDTO.ServiceResponse response = new PanditDTO.ServiceResponse();
        response.setId(service.getId());
        response.setServiceName(service.getServiceName());
        response.setCategoryId(service.getCategory() != null ? service.getCategory().getId() : null);
        response.setCategoryName(service.getCategory() != null ? service.getCategory().getName() : null);
        response.setDescription(service.getDescription());
        response.setPrice(service.getPrice());
        response.setDurationMinutes(service.getDurationMinutes());
        response.setMaterialsIncluded(service.getMaterialsIncluded());
        response.setHomeVisit(service.getHomeVisit());
        response.setMaxDistanceKm(service.getMaxDistanceKm());
        response.setIsActive(service.getIsActive());
        response.setImageUrl(service.getImageUrl());
        response.setCreatedAt(service.getCreatedAt());
        return response;
    }

    private PanditDTO.AvailabilityResponse mapToAvailabilityResponse(Availability availability) {
        PanditDTO.AvailabilityResponse response = new PanditDTO.AvailabilityResponse();
        response.setId(availability.getId());
        response.setDate(availability.getDate());
        response.setStartTime(availability.getStartTime());
        response.setEndTime(availability.getEndTime());
        response.setIsBooked(availability.getIsBooked());
        response.setSlotDuration(availability.getSlotDuration());
        return response;
    }

    private PanditDTO.BankDetailsResponse mapToBankDetailsResponse(BankDetails bankDetails) {
        PanditDTO.BankDetailsResponse response = new PanditDTO.BankDetailsResponse();
        response.setId(bankDetails.getId());
        response.setAccountHolderName(bankDetails.getAccountHolderName());
        response.setAccountNumber(maskAccountNumber(bankDetails.getAccountNumber()));
        response.setIfscCode(bankDetails.getIfscCode());
        response.setBankName(bankDetails.getBankName());
        response.setBranchName(bankDetails.getBranchName());
        response.setAccountType(bankDetails.getAccountType());
        response.setUpiId(bankDetails.getUpiId());
        response.setIsVerified(bankDetails.getIsVerified());
        response.setAddedAt(bankDetails.getAddedAt());
        return response;
    }

    private PanditDTO.EarningsDetailResponse mapToEarningsDetailResponse(PanditEarning earning) {
        PanditDTO.EarningsDetailResponse response = new PanditDTO.EarningsDetailResponse();
        response.setId(earning.getId());
        response.setBookingNumber(earning.getBooking().getBookingNumber());
        response.setAmount(earning.getAmount());
        response.setCommissionPercentage(earning.getCommissionPercentage());
        response.setCommissionAmount(earning.getCommissionAmount());
        response.setNetAmount(earning.getNetAmount());
        response.setPayoutStatus(earning.getPayoutStatus().name());
        response.setEarnedAt(earning.getEarnedAt());
        response.setPayoutDate(earning.getPayoutDate());
        return response;
    }

    private PanditDTO.SearchResponse mapToSearchResponse(PanditProfile profile, Double latitude, Double longitude) {
        PanditDTO.SearchResponse response = new PanditDTO.SearchResponse();
        response.setId(profile.getId());
        response.setName(profile.getUser().getName());
        response.setProfileImageUrl(profile.getUser().getProfileImageUrl());
        response.setBio(profile.getBio());
        response.setExperienceYears(profile.getExperienceYears());
        response.setRating(profile.getRating());
        response.setTotalBookings(profile.getTotalBookings());
        response.setLanguages(profile.getLanguages());
        response.setServiceAreas(profile.getServiceAreas());
        response.setIsVerified(profile.getIsVerified());
        response.setIsAvailable(profile.getIsAvailable());
        
        // Calculate distance if coordinates provided
        if (latitude != null && longitude != null) {
            // TODO: Implement distance calculation using Haversine formula
            response.setDistance(0.0);
        }
        
        // Get min price from services
        List<com.anytime.pooja.model.PanditService> services = panditServiceRepository.findByPanditIdAndIsActiveTrue(profile.getId());
        Double minPrice = services.stream()
            .map(com.anytime.pooja.model.PanditService::getPrice)
            .min(Double::compareTo)
            .orElse(0.0);
        response.setMinPrice(minPrice);
        
        return response;
    }

    private String maskDocumentNumber(String documentNumber) {
        if (documentNumber == null || documentNumber.length() < 4) return "****";
        return "****" + documentNumber.substring(documentNumber.length() - 4);
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) return "****";
        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }
}

