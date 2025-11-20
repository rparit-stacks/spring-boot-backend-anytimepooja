package com.anytime.pooja.service;

import com.anytime.pooja.dto.BookingDTO;
import com.anytime.pooja.model.enums.BookingStatus;

import java.util.List;

public interface BookingService {
    // User Booking Operations
    List<BookingDTO.AvailabilitySlotResponse> checkAvailability(BookingDTO.CheckAvailabilityRequest request);
    BookingDTO.BookingResponse createBooking(Long userId, BookingDTO.CreateBookingRequest request);
    BookingDTO.BookingResponse getBooking(Long userId, Long bookingId);
    List<BookingDTO.BookingSummaryResponse> getUserBookings(Long userId, BookingStatus status, Integer page, Integer size);
    BookingDTO.BookingResponse cancelBooking(Long userId, Long bookingId, BookingDTO.CancelBookingRequest request);
    BookingDTO.BookingResponse rescheduleBooking(Long userId, Long bookingId, BookingDTO.RescheduleRequest request);
    List<BookingDTO.TimelineResponse> getBookingTimeline(Long bookingId);
    
    // Pandit Booking Operations
    List<BookingDTO.BookingSummaryResponse> getPanditBookings(Long panditId, BookingStatus status, Integer page, Integer size);
    List<BookingDTO.BookingSummaryResponse> getTodayBookings(Long panditId);
    List<BookingDTO.BookingSummaryResponse> getUpcomingBookings(Long panditId);
    BookingDTO.BookingResponse getPanditBooking(Long panditId, Long bookingId);
    BookingDTO.BookingResponse acceptBooking(Long panditId, Long bookingId);
    BookingDTO.BookingResponse rejectBooking(Long panditId, Long bookingId, String reason);
    BookingDTO.BookingResponse completeBooking(Long panditId, Long bookingId);
    BookingDTO.BookingResponse cancelBookingByPandit(Long panditId, Long bookingId, String reason);
    
    // Admin Booking Operations
    List<BookingDTO.BookingResponse> getAllBookings(BookingStatus status, Integer page, Integer size);
    BookingDTO.BookingResponse getBookingById(Long bookingId);
    BookingDTO.BookingResponse updateBookingStatus(Long bookingId, BookingDTO.UpdateStatusRequest request);
    BookingDTO.BookingResponse cancelBookingByAdmin(Long bookingId, String reason);
}

