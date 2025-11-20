package com.anytime.pooja.service.impl;

import com.anytime.pooja.dto.AdminDTO;
import com.anytime.pooja.model.*;
import com.anytime.pooja.repository.*;
import com.anytime.pooja.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PanditProfileRepository panditProfileRepository;

    @Autowired
    private KYCDetailsRepository kycDetailsRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SupportTicketRepository supportTicketRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private AppSettingRepository appSettingRepository;

    @Override
    public AdminDTO.DashboardStatsResponse getDashboardStats() {
        AdminDTO.DashboardStatsResponse response = new AdminDTO.DashboardStatsResponse();
        response.setTotalUsers(userRepository.count());
        response.setTotalPandits(panditProfileRepository.count());
        response.setTotalBookings(bookingRepository.count());
        response.setTotalOrders(orderRepository.count());
        response.setPendingKYCs(kycDetailsRepository.countByStatus(com.anytime.pooja.model.enums.KYCStatus.PENDING));
        response.setOpenTickets(supportTicketRepository.countByStatus(com.anytime.pooja.model.enums.TicketStatus.OPEN));
        return response;
    }

    @Override
    public List<AdminDTO.UserManagementResponse> getAllUsers(Integer page, Integer size, String search) {
        List<User> users = userRepository.findAll();
        if (search != null && !search.isEmpty()) {
            users = users.stream()
                .filter(u -> u.getName().contains(search) || u.getEmail().contains(search))
                .collect(java.util.stream.Collectors.toList());
        }
        return users.stream()
            .skip(page * size)
            .limit(size)
            .map(this::mapToUserManagementResponse)
            .collect(Collectors.toList());
    }

    @Override
    public AdminDTO.UserManagementResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserManagementResponse(user);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, Boolean isActive) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(isActive);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public List<AdminDTO.PanditManagementResponse> getAllPandits(Integer page, Integer size, Boolean verified) {
        List<PanditProfile> pandits = verified != null && verified
            ? panditProfileRepository.findByIsVerified(true)
            : panditProfileRepository.findAll();
        
        return pandits.stream()
            .skip(page * size)
            .limit(size)
            .map(this::mapToPanditManagementResponse)
            .collect(Collectors.toList());
    }

    @Override
    public AdminDTO.PanditManagementResponse getPanditById(Long panditId) {
        PanditProfile pandit = panditProfileRepository.findById(panditId)
            .orElseThrow(() -> new RuntimeException("Pandit not found"));
        return mapToPanditManagementResponse(pandit);
    }

    @Override
    @Transactional
    public void updatePanditStatus(Long panditId, Boolean isActive) {
        PanditProfile pandit = panditProfileRepository.findById(panditId)
            .orElseThrow(() -> new RuntimeException("Pandit not found"));
        pandit.getUser().setIsActive(isActive);
        userRepository.save(pandit.getUser());
    }

    @Override
    @Transactional
    public void verifyPandit(Long panditId) {
        PanditProfile pandit = panditProfileRepository.findById(panditId)
            .orElseThrow(() -> new RuntimeException("Pandit not found"));
        pandit.setIsVerified(true);
        panditProfileRepository.save(pandit);
    }

    @Override
    public List<AdminDTO.KYCResponse> getPendingKYCs(Integer page, Integer size) {
        List<KYCDetails> kycs = kycDetailsRepository.findByStatus(com.anytime.pooja.model.enums.KYCStatus.PENDING);
        return kycs.stream()
            .skip(page * size)
            .limit(size)
            .map(this::mapToKYCResponse)
            .collect(Collectors.toList());
    }

    @Override
    public AdminDTO.KYCResponse getKYCById(Long kycId) {
        KYCDetails kyc = kycDetailsRepository.findById(kycId)
            .orElseThrow(() -> new RuntimeException("KYC not found"));
        return mapToKYCResponse(kyc);
    }

    @Override
    @Transactional
    public AdminDTO.KYCResponse approveKYC(Long kycId, AdminDTO.KYCApprovalRequest request) {
        KYCDetails kyc = kycDetailsRepository.findById(kycId)
            .orElseThrow(() -> new RuntimeException("KYC not found"));
        
        kyc.setStatus(com.anytime.pooja.model.enums.KYCStatus.APPROVED);
        kyc.setVerifiedAt(java.time.LocalDateTime.now());
        kyc = kycDetailsRepository.save(kyc);
        
        // Verify pandit
        PanditProfile pandit = kyc.getPandit();
        pandit.setIsVerified(true);
        panditProfileRepository.save(pandit);
        
        return mapToKYCResponse(kyc);
    }

    @Override
    @Transactional
    public AdminDTO.KYCResponse rejectKYC(Long kycId, AdminDTO.KYCApprovalRequest request) {
        KYCDetails kyc = kycDetailsRepository.findById(kycId)
            .orElseThrow(() -> new RuntimeException("KYC not found"));
        
        kyc.setStatus(com.anytime.pooja.model.enums.KYCStatus.REJECTED);
        kyc.setRejectionReason(request.getRejectionReason());
        kyc = kycDetailsRepository.save(kyc);
        
        return mapToKYCResponse(kyc);
    }

    @Override
    public List<AdminDTO.PayoutResponse> getPendingPayouts() {
        // Implementation will fetch from PanditEarning
        return java.util.Collections.emptyList();
    }

    @Override
    public List<AdminDTO.PayoutResponse> getProcessedPayouts(Integer page, Integer size) {
        return java.util.Collections.emptyList();
    }

    @Override
    @Transactional
    public void processPayouts(AdminDTO.PayoutProcessRequest request) {
        // Implementation will process payouts
    }

    @Override
    @Transactional
    public void processSinglePayout(Long payoutId) {
        // Implementation will process single payout
    }

    @Override
    public List<AdminDTO.TicketManagementResponse> getAllTickets(Integer page, Integer size) {
        List<SupportTicket> tickets = supportTicketRepository.findAll();
        return tickets.stream()
            .skip(page * size)
            .limit(size)
            .map(this::mapToTicketManagementResponse)
            .collect(Collectors.toList());
    }

    @Override
    public AdminDTO.TicketManagementResponse getTicketById(Long ticketId) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return mapToTicketManagementResponse(ticket);
    }

    @Override
    public void sendNotification(AdminDTO.SendNotificationRequest request) {
        // Implementation will send notification
    }

    @Override
    public void sendBulkNotification(AdminDTO.SendNotificationRequest request) {
        // Implementation will send bulk notification
    }

    @Override
    public AdminDTO.RevenueReportResponse getRevenueReport(AdminDTO.RevenueReportRequest request) {
        AdminDTO.RevenueReportResponse response = new AdminDTO.RevenueReportResponse();
        // Implementation will calculate revenue
        return response;
    }

    @Override
    public List<AdminDTO.CouponCreateRequest> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAll();
        return coupons.stream()
            .map(this::mapToCouponRequest)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AdminDTO.CouponCreateRequest createCoupon(AdminDTO.CouponCreateRequest request) {
        Coupon coupon = new Coupon();
        coupon.setCode(request.getCode());
        coupon.setDescription(request.getDescription());
        coupon.setType(request.getType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinOrderValue(request.getMinOrderValue());
        coupon.setMaxDiscountAmount(request.getMaxDiscountAmount());
        coupon.setStartDate(request.getStartDate());
        coupon.setExpiryDate(request.getExpiryDate());
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setUsagePerUser(request.getUsagePerUser());
        coupon.setApplicableCategories(request.getApplicableCategories());
        coupon.setApplicableProducts(request.getApplicableProducts());
        coupon.setUserType(request.getUserType());
        coupon.setIsActive(true);
        coupon = couponRepository.save(coupon);
        return mapToCouponRequest(coupon);
    }

    @Override
    @Transactional
    public AdminDTO.CouponCreateRequest updateCoupon(Long couponId, AdminDTO.CouponCreateRequest request) {
        Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new RuntimeException("Coupon not found"));
        
        if (request.getCode() != null) coupon.setCode(request.getCode());
        if (request.getDescription() != null) coupon.setDescription(request.getDescription());
        if (request.getType() != null) coupon.setType(request.getType());
        if (request.getDiscountValue() != null) coupon.setDiscountValue(request.getDiscountValue());
        if (request.getMinOrderValue() != null) coupon.setMinOrderValue(request.getMinOrderValue());
        if (request.getMaxDiscountAmount() != null) coupon.setMaxDiscountAmount(request.getMaxDiscountAmount());
        if (request.getStartDate() != null) coupon.setStartDate(request.getStartDate());
        if (request.getExpiryDate() != null) coupon.setExpiryDate(request.getExpiryDate());
        if (request.getUsageLimit() != null) coupon.setUsageLimit(request.getUsageLimit());
        if (request.getUsagePerUser() != null) coupon.setUsagePerUser(request.getUsagePerUser());
        if (request.getApplicableCategories() != null) coupon.setApplicableCategories(request.getApplicableCategories());
        if (request.getApplicableProducts() != null) coupon.setApplicableProducts(request.getApplicableProducts());
        if (request.getUserType() != null) coupon.setUserType(request.getUserType());

        
        coupon = couponRepository.save(coupon);
        return mapToCouponRequest(coupon);
    }

    @Override
    @Transactional
    public void deleteCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new RuntimeException("Coupon not found"));
        coupon.setIsActive(false);
        couponRepository.save(coupon);
    }

    @Override
    public List<AdminDTO.AppSettingResponse> getAllSettings() {
        List<AppSetting> settings = appSettingRepository.findAll();
        return settings.stream()
            .map(this::mapToAppSettingResponse)
            .collect(Collectors.toList());
    }

    @Override
    public AdminDTO.AppSettingResponse getSetting(String key) {
        AppSetting setting = appSettingRepository.findByKey(key)
            .orElseThrow(() -> new RuntimeException("Setting not found"));
        return mapToAppSettingResponse(setting);
    }

    @Override
    @Transactional
    public AdminDTO.AppSettingResponse updateSetting(String key, AdminDTO.AppSettingUpdateRequest request) {
        AppSetting setting = appSettingRepository.findByKey(key)
            .orElseThrow(() -> new RuntimeException("Setting not found"));
        
        if (request.getValue() != null) setting.setValue(request.getValue());
        if (request.getDescription() != null) setting.setDescription(request.getDescription());
        
        setting = appSettingRepository.save(setting);
        return mapToAppSettingResponse(setting);
    }

    // Helper methods
    private AdminDTO.UserManagementResponse mapToUserManagementResponse(User user) {
        AdminDTO.UserManagementResponse response = new AdminDTO.UserManagementResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setIsActive(user.getIsActive());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    private AdminDTO.PanditManagementResponse mapToPanditManagementResponse(PanditProfile pandit) {
        AdminDTO.PanditManagementResponse response = new AdminDTO.PanditManagementResponse();
        response.setId(pandit.getId());
        response.setName(pandit.getUser().getName());
        response.setEmail(pandit.getUser().getEmail());
        response.setPhone(pandit.getUser().getPhone());
        response.setUserId(pandit.getUser().getId());
        response.setIsVerified(pandit.getIsVerified());
        response.setIsAvailable(pandit.getIsAvailable());
        response.setRating(pandit.getRating());
        response.setTotalBookings(pandit.getTotalBookings());
        response.setVerificationDate(pandit.getVerificationDate());
        response.setCreatedAt(pandit.getCreatedAt());
        return response;
    }

    private AdminDTO.KYCResponse mapToKYCResponse(KYCDetails kyc) {
        AdminDTO.KYCResponse response = new AdminDTO.KYCResponse();
        response.setId(kyc.getId());
        response.setPanditId(kyc.getPandit().getId());
        response.setPanditName(kyc.getPandit().getUser().getName());
        response.setDocumentType(kyc.getDocumentType() != null ? kyc.getDocumentType().name() : null);
        response.setDocumentNumber(kyc.getDocumentNumber());
        response.setFrontImageUrl(kyc.getFrontImageUrl());
        response.setBackImageUrl(kyc.getBackImageUrl());
        response.setSelfieImageUrl(kyc.getSelfieImageUrl());
        response.setStatus(kyc.getStatus());
        response.setRejectionReason(kyc.getRejectionReason());
        response.setSubmittedAt(kyc.getSubmittedAt());
        response.setVerifiedAt(kyc.getVerifiedAt());
        return response;
    }

    private AdminDTO.TicketManagementResponse mapToTicketManagementResponse(SupportTicket ticket) {
        AdminDTO.TicketManagementResponse response = new AdminDTO.TicketManagementResponse();
        response.setId(ticket.getId());
        response.setTicketNumber(ticket.getTicketNumber());
        response.setUserId(ticket.getUser().getId());
        response.setUserName(ticket.getUser().getName());
        response.setCategory(ticket.getCategory() != null ? ticket.getCategory().name() : null);
        response.setPriority(ticket.getPriority());
        response.setSubject(ticket.getSubject());
        response.setStatus(ticket.getStatus().name());
        response.setAssignedTo(ticket.getAssignedTo());
        response.setCreatedAt(ticket.getCreatedAt());
        response.setResolvedAt(ticket.getResolvedAt());
        return response;
    }

    private AdminDTO.CouponCreateRequest mapToCouponRequest(Coupon coupon) {
        AdminDTO.CouponCreateRequest response = new AdminDTO.CouponCreateRequest();
        response.setCode(coupon.getCode());
        response.setDescription(coupon.getDescription());
        response.setType(coupon.getType());
        response.setDiscountValue(coupon.getDiscountValue());
        response.setMinOrderValue(coupon.getMinOrderValue());
        response.setMaxDiscountAmount(coupon.getMaxDiscountAmount());
        response.setStartDate(coupon.getStartDate());
        response.setExpiryDate(coupon.getExpiryDate());
        response.setUsageLimit(coupon.getUsageLimit());
        response.setUsagePerUser(coupon.getUsagePerUser());
        response.setApplicableCategories(coupon.getApplicableCategories());
        response.setApplicableProducts(coupon.getApplicableProducts());
        response.setUserType(coupon.getUserType());
        return response;
    }

    private AdminDTO.AppSettingResponse mapToAppSettingResponse(AppSetting setting) {
        AdminDTO.AppSettingResponse response = new AdminDTO.AppSettingResponse();
        response.setKey(setting.getKey());
        response.setValue(setting.getValue());
        response.setDescription(setting.getDescription());
        response.setDataType(setting.getDataType());
        return response;
    }
}

