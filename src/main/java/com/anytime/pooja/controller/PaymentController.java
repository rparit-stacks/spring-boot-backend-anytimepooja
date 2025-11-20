package com.anytime.pooja.controller;

import com.anytime.pooja.dto.CommonDTO;
import com.anytime.pooja.dto.PaymentDTO;
import com.anytime.pooja.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<CommonDTO.ApiResponse<PaymentDTO.PaymentOrderResponse>> createPaymentOrder(
            @Valid @RequestBody PaymentDTO.CreatePaymentOrderRequest request) {
        PaymentDTO.PaymentOrderResponse response = paymentService.createPaymentOrder(request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Payment order created successfully"));
    }

    @PostMapping("/verify")
    public ResponseEntity<CommonDTO.ApiResponse<PaymentDTO.PaymentResponse>> verifyPayment(
            @Valid @RequestBody PaymentDTO.VerifyPaymentRequest request) {
        PaymentDTO.PaymentResponse response = paymentService.verifyPayment(request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Payment verified successfully"));
    }

    @GetMapping("/{paymentId}/status")
    public ResponseEntity<CommonDTO.ApiResponse<PaymentDTO.PaymentStatusResponse>> getPaymentStatus(@PathVariable String paymentId) {
        PaymentDTO.PaymentResponse payment = paymentService.getPaymentDetails(paymentId);
        PaymentDTO.PaymentStatusResponse response = new PaymentDTO.PaymentStatusResponse();
        response.setPaymentId(paymentId);
        response.setStatus(payment.getStatus());
        response.setMessage("Payment status retrieved");
        response.setLastUpdated(java.time.LocalDateTime.now());
        return ResponseEntity.ok(buildSuccessResponse(response, "Payment status retrieved successfully"));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<CommonDTO.ApiResponse<PaymentDTO.PaymentResponse>> getPaymentDetails(@PathVariable String paymentId) {
        PaymentDTO.PaymentResponse response = paymentService.getPaymentDetails(paymentId);
        return ResponseEntity.ok(buildSuccessResponse(response, "Payment details retrieved successfully"));
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<CommonDTO.ApiResponse<PaymentDTO.RefundResponse>> processRefund(
            @PathVariable String paymentId, @Valid @RequestBody PaymentDTO.RefundRequest request) {
        PaymentDTO.RefundResponse response = paymentService.processRefund(paymentId, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Refund processed successfully"));
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
}

