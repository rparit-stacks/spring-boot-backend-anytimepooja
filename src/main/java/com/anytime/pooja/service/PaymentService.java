package com.anytime.pooja.service;

import com.anytime.pooja.dto.PaymentDTO;

public interface PaymentService {
    PaymentDTO.PaymentOrderResponse createPaymentOrder(PaymentDTO.CreatePaymentOrderRequest request);
    PaymentDTO.PaymentResponse verifyPayment(PaymentDTO.VerifyPaymentRequest request);
    PaymentDTO.PaymentResponse getPaymentStatus(String paymentId);
    PaymentDTO.RefundResponse processRefund(String paymentId, PaymentDTO.RefundRequest request);
    PaymentDTO.PaymentResponse getPaymentDetails(String paymentId);
}

