package com.anytime.pooja.controller;

import com.anytime.pooja.dto.AdminDTO;
import com.anytime.pooja.dto.CommonDTO;
import com.anytime.pooja.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Dashboard
    @GetMapping("/dashboard/stats")
    public ResponseEntity<CommonDTO.ApiResponse<AdminDTO.DashboardStatsResponse>> getDashboardStats() {
        AdminDTO.DashboardStatsResponse response = adminService.getDashboardStats();
        return ResponseEntity.ok(buildSuccessResponse(response, "Dashboard stats retrieved successfully"));
    }

    // User Management
    @GetMapping("/users")
    public ResponseEntity<CommonDTO.PaginatedResponse<AdminDTO.UserManagementResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String search) {
        List<AdminDTO.UserManagementResponse> users = adminService.getAllUsers(page, size, search);
        return ResponseEntity.ok(buildPaginatedResponse(users, page, size));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<AdminDTO.UserManagementResponse>> getUserById(@PathVariable Long id) {
        AdminDTO.UserManagementResponse response = adminService.getUserById(id);
        return ResponseEntity.ok(buildSuccessResponse(response, "User retrieved successfully"));
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<CommonDTO.SuccessResponse> updateUserStatus(
            @PathVariable Long id, @RequestParam Boolean isActive) {
        adminService.updateUserStatus(id, isActive);
        return ResponseEntity.ok(buildSuccessResponse("User status updated successfully"));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<CommonDTO.SuccessResponse> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(buildSuccessResponse("User deleted successfully"));
    }

    // Pandit Management
    @GetMapping("/pandits")
    public ResponseEntity<CommonDTO.PaginatedResponse<AdminDTO.PanditManagementResponse>> getAllPandits(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) Boolean verified) {
        List<AdminDTO.PanditManagementResponse> pandits = adminService.getAllPandits(page, size, verified);
        return ResponseEntity.ok(buildPaginatedResponse(pandits, page, size));
    }

    @GetMapping("/pandits/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<AdminDTO.PanditManagementResponse>> getPanditById(@PathVariable Long id) {
        AdminDTO.PanditManagementResponse response = adminService.getPanditById(id);
        return ResponseEntity.ok(buildSuccessResponse(response, "Pandit retrieved successfully"));
    }

    @PutMapping("/pandits/{id}/status")
    public ResponseEntity<CommonDTO.SuccessResponse> updatePanditStatus(
            @PathVariable Long id, @RequestParam Boolean isActive) {
        adminService.updatePanditStatus(id, isActive);
        return ResponseEntity.ok(buildSuccessResponse("Pandit status updated successfully"));
    }

    @PutMapping("/pandits/{id}/verify")
    public ResponseEntity<CommonDTO.SuccessResponse> verifyPandit(@PathVariable Long id) {
        adminService.verifyPandit(id);
        return ResponseEntity.ok(buildSuccessResponse("Pandit verified successfully"));
    }

    // KYC Management
    @GetMapping("/kyc/pending")
    public ResponseEntity<CommonDTO.PaginatedResponse<AdminDTO.KYCResponse>> getPendingKYCs(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        List<AdminDTO.KYCResponse> kycs = adminService.getPendingKYCs(page, size);
        return ResponseEntity.ok(buildPaginatedResponse(kycs, page, size));
    }

    @GetMapping("/kyc/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<AdminDTO.KYCResponse>> getKYCById(@PathVariable Long id) {
        AdminDTO.KYCResponse response = adminService.getKYCById(id);
        return ResponseEntity.ok(buildSuccessResponse(response, "KYC retrieved successfully"));
    }

    @PutMapping("/kyc/{id}/approve")
    public ResponseEntity<CommonDTO.ApiResponse<AdminDTO.KYCResponse>> approveKYC(
            @PathVariable Long id, @Valid @RequestBody AdminDTO.KYCApprovalRequest request) {
        AdminDTO.KYCResponse response = adminService.approveKYC(id, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "KYC approved successfully"));
    }

    @PutMapping("/kyc/{id}/reject")
    public ResponseEntity<CommonDTO.ApiResponse<AdminDTO.KYCResponse>> rejectKYC(
            @PathVariable Long id, @Valid @RequestBody AdminDTO.KYCApprovalRequest request) {
        AdminDTO.KYCResponse response = adminService.rejectKYC(id, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "KYC rejected successfully"));
    }

    // Payout Management
    @GetMapping("/payouts/pending")
    public ResponseEntity<CommonDTO.ApiResponse<List<AdminDTO.PayoutResponse>>> getPendingPayouts() {
        List<AdminDTO.PayoutResponse> response = adminService.getPendingPayouts();
        return ResponseEntity.ok(buildSuccessResponse(response, "Pending payouts retrieved successfully"));
    }

    @GetMapping("/payouts/processed")
    public ResponseEntity<CommonDTO.PaginatedResponse<AdminDTO.PayoutResponse>> getProcessedPayouts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        List<AdminDTO.PayoutResponse> payouts = adminService.getProcessedPayouts(page, size);
        return ResponseEntity.ok(buildPaginatedResponse(payouts, page, size));
    }

    @PostMapping("/payouts/process")
    public ResponseEntity<CommonDTO.SuccessResponse> processPayouts(
            @Valid @RequestBody AdminDTO.PayoutProcessRequest request) {
        adminService.processPayouts(request);
        return ResponseEntity.ok(buildSuccessResponse("Payouts processed successfully"));
    }

    @PostMapping("/payouts/{id}/process")
    public ResponseEntity<CommonDTO.SuccessResponse> processSinglePayout(@PathVariable Long id) {
        adminService.processSinglePayout(id);
        return ResponseEntity.ok(buildSuccessResponse("Payout processed successfully"));
    }

    // Ticket Management
    @GetMapping("/tickets")
    public ResponseEntity<CommonDTO.PaginatedResponse<AdminDTO.TicketManagementResponse>> getAllTickets(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        List<AdminDTO.TicketManagementResponse> tickets = adminService.getAllTickets(page, size);
        return ResponseEntity.ok(buildPaginatedResponse(tickets, page, size));
    }

    @GetMapping("/tickets/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<AdminDTO.TicketManagementResponse>> getTicketById(@PathVariable Long id) {
        AdminDTO.TicketManagementResponse response = adminService.getTicketById(id);
        return ResponseEntity.ok(buildSuccessResponse(response, "Ticket retrieved successfully"));
    }

    // Notifications
    @PostMapping("/notifications/send")
    public ResponseEntity<CommonDTO.SuccessResponse> sendNotification(
            @Valid @RequestBody AdminDTO.SendNotificationRequest request) {
        adminService.sendNotification(request);
        return ResponseEntity.ok(buildSuccessResponse("Notification sent successfully"));
    }

    @PostMapping("/notifications/send-bulk")
    public ResponseEntity<CommonDTO.SuccessResponse> sendBulkNotification(
            @Valid @RequestBody AdminDTO.SendNotificationRequest request) {
        adminService.sendBulkNotification(request);
        return ResponseEntity.ok(buildSuccessResponse("Bulk notification sent successfully"));
    }

    // Reports
    @GetMapping("/reports/revenue")
    public ResponseEntity<CommonDTO.ApiResponse<AdminDTO.RevenueReportResponse>> getRevenueReport(
            @ModelAttribute AdminDTO.RevenueReportRequest request) {
        AdminDTO.RevenueReportResponse response = adminService.getRevenueReport(request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Revenue report retrieved successfully"));
    }

    // Coupons
    @GetMapping("/coupons")
    public ResponseEntity<CommonDTO.ApiResponse<List<AdminDTO.CouponCreateRequest>>> getAllCoupons() {
        List<AdminDTO.CouponCreateRequest> response = adminService.getAllCoupons();
        return ResponseEntity.ok(buildSuccessResponse(response, "Coupons retrieved successfully"));
    }

    @PostMapping("/coupons")
    public ResponseEntity<CommonDTO.ApiResponse<AdminDTO.CouponCreateRequest>> createCoupon(
            @Valid @RequestBody AdminDTO.CouponCreateRequest request) {
        AdminDTO.CouponCreateRequest response = adminService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildSuccessResponse(response, "Coupon created successfully"));
    }

    @PutMapping("/coupons/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<AdminDTO.CouponCreateRequest>> updateCoupon(
            @PathVariable Long id, @Valid @RequestBody AdminDTO.CouponCreateRequest request) {
        AdminDTO.CouponCreateRequest response = adminService.updateCoupon(id, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Coupon updated successfully"));
    }

    @DeleteMapping("/coupons/{id}")
    public ResponseEntity<CommonDTO.SuccessResponse> deleteCoupon(@PathVariable Long id) {
        adminService.deleteCoupon(id);
        return ResponseEntity.ok(buildSuccessResponse("Coupon deleted successfully"));
    }

    // Settings
    @GetMapping("/settings")
    public ResponseEntity<CommonDTO.ApiResponse<List<AdminDTO.AppSettingResponse>>> getAllSettings() {
        List<AdminDTO.AppSettingResponse> response = adminService.getAllSettings();
        return ResponseEntity.ok(buildSuccessResponse(response, "Settings retrieved successfully"));
    }

    @GetMapping("/settings/{key}")
    public ResponseEntity<CommonDTO.ApiResponse<AdminDTO.AppSettingResponse>> getSetting(@PathVariable String key) {
        AdminDTO.AppSettingResponse response = adminService.getSetting(key);
        return ResponseEntity.ok(buildSuccessResponse(response, "Setting retrieved successfully"));
    }

    @PutMapping("/settings/{key}")
    public ResponseEntity<CommonDTO.ApiResponse<AdminDTO.AppSettingResponse>> updateSetting(
            @PathVariable String key, @Valid @RequestBody AdminDTO.AppSettingUpdateRequest request) {
        AdminDTO.AppSettingResponse response = adminService.updateSetting(key, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Setting updated successfully"));
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

    private <T> CommonDTO.PaginatedResponse<T> buildPaginatedResponse(List<T> content, Integer page, Integer size) {
        CommonDTO.PaginatedResponse<T> response = new CommonDTO.PaginatedResponse<>();
        response.setContent(content);
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements((long) content.size());
        response.setTotalPages((int) Math.ceil((double) content.size() / size));
        response.setHasNext(page < response.getTotalPages() - 1);
        response.setHasPrevious(page > 0);
        response.setIsFirst(page == 0);
        response.setIsLast(page >= response.getTotalPages() - 1);
        return response;
    }
}

