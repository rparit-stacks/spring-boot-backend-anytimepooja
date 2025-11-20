package com.anytime.pooja.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SMSService {

    @Value("${twilio.account.sid:}")
    private String accountSid;

    @Value("${twilio.auth.token:}")
    private String authToken;

    @Value("${twilio.phone.number:}")
    private String phoneNumber;

    public void sendOTP(String phoneNumber, String otp) {
        try {
            // TODO: Integrate with Twilio SDK
            // For now, just log the OTP
            System.out.println("SMS OTP to " + phoneNumber + ": " + otp);
            
            // Uncomment when Twilio is configured:
            /*
            Twilio.init(accountSid, authToken);
            Message message = Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(this.phoneNumber),
                "Your OTP for Anytime Pooja is: " + otp + ". Valid for 10 minutes."
            ).create();
            */
        } catch (Exception e) {
            throw new RuntimeException("Failed to send SMS: " + e.getMessage());
        }
    }

    public void sendBookingConfirmation(String phoneNumber, String bookingNumber) {
        try {
            // TODO: Integrate with Twilio SDK
            System.out.println("SMS Booking Confirmation to " + phoneNumber + ": Booking " + bookingNumber + " confirmed");
        } catch (Exception e) {
            throw new RuntimeException("Failed to send SMS: " + e.getMessage());
        }
    }
}

