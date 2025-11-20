package com.anytime.pooja.service;

import com.anytime.pooja.dto.AdminDTO;

import java.util.List;

public interface AdminService {
    // Dashboard
    AdminDTO.DashboardStatsResponse getDashboardStats();
    
    // User Management
    List<AdminDTO.UserManagementResponse> getAllUsers(Integer page, Integer size, String search);
    AdminDTO.UserManagementResponse getUserById(Long userId);
    void updateUserStatus(Long userId, Boolean isActive);
    void deleteUser(Long userId);
    
    // Pandit Management
    List<AdminDTO.PanditManagementResponse> getAllPandits(Integer page, Integer size, Boolean verified);
    AdminDTO.PanditManagementResponse getPanditById(Long panditId);
    void updatePanditStatus(Long panditId, Boolean isActive);
    void verifyPandit(Long panditId);
    
    // KYC Management
    List<AdminDTO.KYCResponse> getPendingKYCs(Integer page, Integer size);
    AdminDTO.KYCResponse getKYCById(Long kycId);
    AdminDTO.KYCResponse approveKYC(Long kycId, AdminDTO.KYCApprovalRequest request);
    AdminDTO.KYCResponse rejectKYC(Long kycId, AdminDTO.KYCApprovalRequest request);
    
    // Payout Management
    List<AdminDTO.PayoutResponse> getPendingPayouts();
    List<AdminDTO.PayoutResponse> getProcessedPayouts(Integer page, Integer size);
    void processPayouts(AdminDTO.PayoutProcessRequest request);
    void processSinglePayout(Long payoutId);
    
    // Ticket Management
    List<AdminDTO.TicketManagementResponse> getAllTickets(Integer page, Integer size);
    AdminDTO.TicketManagementResponse getTicketById(Long ticketId);
    
    // Notifications
    void sendNotification(AdminDTO.SendNotificationRequest request);
    void sendBulkNotification(AdminDTO.SendNotificationRequest request);
    
    // Reports
    AdminDTO.RevenueReportResponse getRevenueReport(AdminDTO.RevenueReportRequest request);
    
    // Coupons
    List<AdminDTO.CouponCreateRequest> getAllCoupons();
    AdminDTO.CouponCreateRequest createCoupon(AdminDTO.CouponCreateRequest request);
    AdminDTO.CouponCreateRequest updateCoupon(Long couponId, AdminDTO.CouponCreateRequest request);
    void deleteCoupon(Long couponId);
    
    // Settings
    List<AdminDTO.AppSettingResponse> getAllSettings();
    AdminDTO.AppSettingResponse getSetting(String key);
    AdminDTO.AppSettingResponse updateSetting(String key, AdminDTO.AppSettingUpdateRequest request);
}

