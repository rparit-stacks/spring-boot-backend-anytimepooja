package com.anytime.pooja.controller;

import com.anytime.pooja.dto.CommonDTO;
import com.anytime.pooja.dto.PanditDTO;
import com.anytime.pooja.service.AuthService;
import com.anytime.pooja.service.PanditService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pandit")
public class PanditController {

    @Autowired
    private PanditService panditService;

    @Autowired
    private AuthService authService;

    // Profile Management
    @GetMapping("/profile")
    public ResponseEntity<CommonDTO.ApiResponse<PanditDTO.ProfileResponse>> getProfile() {
        Long panditId = authService.getCurrentUserId();
        PanditDTO.ProfileResponse response = panditService.getProfile(panditId);
        return ResponseEntity.ok(buildSuccessResponse(response, "Profile retrieved successfully"));
    }

    @PutMapping("/profile")
    public ResponseEntity<CommonDTO.ApiResponse<PanditDTO.ProfileResponse>> updateProfile(
            @Valid @RequestBody PanditDTO.ProfileUpdateRequest request) {
        Long panditId = authService.getCurrentUserId();
        PanditDTO.ProfileResponse response = panditService.updateProfile(panditId, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Profile updated successfully"));
    }

    @PostMapping("/profile/image")
    public ResponseEntity<CommonDTO.ApiResponse<String>> updateProfileImage(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        Long panditId = authService.getCurrentUserId();
        // Implementation will be done later
        return ResponseEntity.ok(buildSuccessResponse("Image uploaded", "Profile image uploaded successfully"));
    }

    // KYC Management
    @PostMapping("/kyc/submit")
    public ResponseEntity<CommonDTO.ApiResponse<PanditDTO.KYCResponse>> submitKYC(
            @Valid @RequestBody PanditDTO.KYCSubmitRequest request) {
        Long panditId = authService.getCurrentUserId();
        PanditDTO.KYCResponse response = panditService.submitKYC(panditId, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "KYC submitted successfully"));
    }

    @GetMapping("/kyc/status")
    public ResponseEntity<CommonDTO.ApiResponse<PanditDTO.KYCResponse>> getKYCStatus() {
        Long panditId = authService.getCurrentUserId();
        PanditDTO.KYCResponse response = panditService.getKYCStatus(panditId);
        return ResponseEntity.ok(buildSuccessResponse(response, "KYC status retrieved successfully"));
    }

    @PutMapping("/kyc/resubmit")
    public ResponseEntity<CommonDTO.ApiResponse<PanditDTO.KYCResponse>> resubmitKYC(
            @Valid @RequestBody PanditDTO.KYCSubmitRequest request) {
        Long panditId = authService.getCurrentUserId();
        PanditDTO.KYCResponse response = panditService.resubmitKYC(panditId, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "KYC resubmitted successfully"));
    }

    // Service Management
    @GetMapping("/services")
    public ResponseEntity<CommonDTO.ApiResponse<List<PanditDTO.ServiceResponse>>> getServices() {
        Long panditId = authService.getCurrentUserId();
        List<PanditDTO.ServiceResponse> response = panditService.getServices(panditId);
        return ResponseEntity.ok(buildSuccessResponse(response, "Services retrieved successfully"));
    }

    @PostMapping("/services")
    public ResponseEntity<CommonDTO.ApiResponse<PanditDTO.ServiceResponse>> createService(
            @Valid @RequestBody PanditDTO.ServiceRequest request) {
        Long panditId = authService.getCurrentUserId();
        PanditDTO.ServiceResponse response = panditService.createService(panditId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildSuccessResponse(response, "Service created successfully"));
    }

    @PutMapping("/services/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<PanditDTO.ServiceResponse>> updateService(
            @PathVariable Long id, @Valid @RequestBody PanditDTO.ServiceRequest request) {
        Long panditId = authService.getCurrentUserId();
        PanditDTO.ServiceResponse response = panditService.updateService(panditId, id, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Service updated successfully"));
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<CommonDTO.SuccessResponse> deleteService(@PathVariable Long id) {
        Long panditId = authService.getCurrentUserId();
        panditService.deleteService(panditId, id);
        return ResponseEntity.ok(buildSuccessResponse("Service deleted successfully"));
    }

    // Availability Management
    @GetMapping("/availability")
    public ResponseEntity<CommonDTO.ApiResponse<List<PanditDTO.AvailabilityResponse>>> getAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long panditId = authService.getCurrentUserId();
        List<PanditDTO.AvailabilityResponse> response = panditService.getAvailability(panditId, date);
        return ResponseEntity.ok(buildSuccessResponse(response, "Availability retrieved successfully"));
    }

    @PostMapping("/availability")
    public ResponseEntity<CommonDTO.ApiResponse<PanditDTO.AvailabilityResponse>> createAvailability(
            @Valid @RequestBody PanditDTO.AvailabilityRequest request) {
        Long panditId = authService.getCurrentUserId();
        PanditDTO.AvailabilityResponse response = panditService.createAvailability(panditId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildSuccessResponse(response, "Availability created successfully"));
    }

    @PostMapping("/availability/bulk")
    public ResponseEntity<CommonDTO.SuccessResponse> createBulkAvailability(
            @Valid @RequestBody PanditDTO.BulkAvailabilityRequest request) {
        Long panditId = authService.getCurrentUserId();
        panditService.createBulkAvailability(panditId, request);
        return ResponseEntity.ok(buildSuccessResponse("Bulk availability created successfully"));
    }

    @PutMapping("/availability/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<PanditDTO.AvailabilityResponse>> updateAvailability(
            @PathVariable Long id, @Valid @RequestBody PanditDTO.AvailabilityRequest request) {
        Long panditId = authService.getCurrentUserId();
        PanditDTO.AvailabilityResponse response = panditService.updateAvailability(panditId, id, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Availability updated successfully"));
    }

    @DeleteMapping("/availability/{id}")
    public ResponseEntity<CommonDTO.SuccessResponse> deleteAvailability(@PathVariable Long id) {
        Long panditId = authService.getCurrentUserId();
        panditService.deleteAvailability(panditId, id);
        return ResponseEntity.ok(buildSuccessResponse("Availability deleted successfully"));
    }

    // Bank Details
    @GetMapping("/bank-details")
    public ResponseEntity<CommonDTO.ApiResponse<PanditDTO.BankDetailsResponse>> getBankDetails() {
        Long panditId = authService.getCurrentUserId();
        PanditDTO.BankDetailsResponse response = panditService.getBankDetails(panditId);
        return ResponseEntity.ok(buildSuccessResponse(response, "Bank details retrieved successfully"));
    }

    @PostMapping("/bank-details")
    public ResponseEntity<CommonDTO.ApiResponse<PanditDTO.BankDetailsResponse>> addBankDetails(
            @Valid @RequestBody PanditDTO.BankDetailsRequest request) {
        Long panditId = authService.getCurrentUserId();
        PanditDTO.BankDetailsResponse response = panditService.addBankDetails(panditId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildSuccessResponse(response, "Bank details added successfully"));
    }

    @PutMapping("/bank-details")
    public ResponseEntity<CommonDTO.ApiResponse<PanditDTO.BankDetailsResponse>> updateBankDetails(
            @Valid @RequestBody PanditDTO.BankDetailsRequest request) {
        Long panditId = authService.getCurrentUserId();
        PanditDTO.BankDetailsResponse response = panditService.updateBankDetails(panditId, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Bank details updated successfully"));
    }

    // Earnings
    @GetMapping("/earnings/summary")
    public ResponseEntity<CommonDTO.ApiResponse<PanditDTO.EarningsSummaryResponse>> getEarningsSummary() {
        Long panditId = authService.getCurrentUserId();
        PanditDTO.EarningsSummaryResponse response = panditService.getEarningsSummary(panditId);
        return ResponseEntity.ok(buildSuccessResponse(response, "Earnings summary retrieved successfully"));
    }

    @GetMapping("/earnings/details")
    public ResponseEntity<CommonDTO.ApiResponse<List<PanditDTO.EarningsDetailResponse>>> getEarningsDetails(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        Long panditId = authService.getCurrentUserId();
        List<PanditDTO.EarningsDetailResponse> response = panditService.getEarningsDetails(panditId, fromDate, toDate);
        return ResponseEntity.ok(buildSuccessResponse(response, "Earnings details retrieved successfully"));
    }

    @GetMapping("/payouts")
    public ResponseEntity<CommonDTO.ApiResponse<List<PanditDTO.EarningsDetailResponse>>> getPayouts() {
        Long panditId = authService.getCurrentUserId();
        List<PanditDTO.EarningsDetailResponse> response = panditService.getPayouts(panditId);
        return ResponseEntity.ok(buildSuccessResponse(response, "Payouts retrieved successfully"));
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

