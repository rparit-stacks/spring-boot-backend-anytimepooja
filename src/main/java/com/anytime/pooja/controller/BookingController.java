package com.anytime.pooja.controller;

import com.anytime.pooja.dto.BookingDTO;
import com.anytime.pooja.dto.CommonDTO;
import com.anytime.pooja.model.enums.BookingStatus;
import com.anytime.pooja.service.AuthService;
import com.anytime.pooja.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private AuthService authService;

    // User Endpoints
    @PostMapping("/check-availability")
    public ResponseEntity<CommonDTO.ApiResponse<List<BookingDTO.AvailabilitySlotResponse>>> checkAvailability(
            @Valid @RequestBody BookingDTO.CheckAvailabilityRequest request) {
        List<BookingDTO.AvailabilitySlotResponse> response = bookingService.checkAvailability(request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Availability checked successfully"));
    }

    @PostMapping("/create")
    public ResponseEntity<CommonDTO.ApiResponse<BookingDTO.BookingResponse>> createBooking(
            @Valid @RequestBody BookingDTO.CreateBookingRequest request) {
        Long userId = authService.getCurrentUserId();
        BookingDTO.BookingResponse response = bookingService.createBooking(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildSuccessResponse(response, "Booking created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<BookingDTO.BookingResponse>> getBooking(@PathVariable Long id) {
        Long userId = authService.getCurrentUserId();
        BookingDTO.BookingResponse response = bookingService.getBooking(userId, id);
        return ResponseEntity.ok(buildSuccessResponse(response, "Booking retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<CommonDTO.PaginatedResponse<BookingDTO.BookingSummaryResponse>> getUserBookings(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = authService.getCurrentUserId();
        List<BookingDTO.BookingSummaryResponse> bookings = bookingService.getUserBookings(userId, status, page, size);
        return ResponseEntity.ok(buildPaginatedResponse(bookings, page, size));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<CommonDTO.ApiResponse<BookingDTO.BookingResponse>> cancelBooking(
            @PathVariable Long id, @Valid @RequestBody BookingDTO.CancelBookingRequest request) {
        Long userId = authService.getCurrentUserId();
        BookingDTO.BookingResponse response = bookingService.cancelBooking(userId, id, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Booking cancelled successfully"));
    }

    @PostMapping("/{id}/reschedule")
    public ResponseEntity<CommonDTO.ApiResponse<BookingDTO.BookingResponse>> rescheduleBooking(
            @PathVariable Long id, @Valid @RequestBody BookingDTO.RescheduleRequest request) {
        Long userId = authService.getCurrentUserId();
        BookingDTO.BookingResponse response = bookingService.rescheduleBooking(userId, id, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Booking rescheduled successfully"));
    }

    @GetMapping("/{id}/timeline")
    public ResponseEntity<CommonDTO.ApiResponse<List<BookingDTO.TimelineResponse>>> getBookingTimeline(@PathVariable Long id) {
        List<BookingDTO.TimelineResponse> response = bookingService.getBookingTimeline(id);
        return ResponseEntity.ok(buildSuccessResponse(response, "Timeline retrieved successfully"));
    }

    // Pandit Endpoints
    @GetMapping("/pandit")
    public ResponseEntity<CommonDTO.PaginatedResponse<BookingDTO.BookingSummaryResponse>> getPanditBookings(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long panditId = authService.getCurrentUserId();
        List<BookingDTO.BookingSummaryResponse> bookings = bookingService.getPanditBookings(panditId, status, page, size);
        return ResponseEntity.ok(buildPaginatedResponse(bookings, page, size));
    }

    @GetMapping("/pandit/today")
    public ResponseEntity<CommonDTO.ApiResponse<List<BookingDTO.BookingSummaryResponse>>> getTodayBookings() {
        Long panditId = authService.getCurrentUserId();
        List<BookingDTO.BookingSummaryResponse> response = bookingService.getTodayBookings(panditId);
        return ResponseEntity.ok(buildSuccessResponse(response, "Today's bookings retrieved successfully"));
    }

    @GetMapping("/pandit/upcoming")
    public ResponseEntity<CommonDTO.ApiResponse<List<BookingDTO.BookingSummaryResponse>>> getUpcomingBookings() {
        Long panditId = authService.getCurrentUserId();
        List<BookingDTO.BookingSummaryResponse> response = bookingService.getUpcomingBookings(panditId);
        return ResponseEntity.ok(buildSuccessResponse(response, "Upcoming bookings retrieved successfully"));
    }

    @GetMapping("/pandit/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<BookingDTO.BookingResponse>> getPanditBooking(@PathVariable Long id) {
        Long panditId = authService.getCurrentUserId();
        BookingDTO.BookingResponse response = bookingService.getPanditBooking(panditId, id);
        return ResponseEntity.ok(buildSuccessResponse(response, "Booking retrieved successfully"));
    }

    @PutMapping("/pandit/{id}/accept")
    public ResponseEntity<CommonDTO.ApiResponse<BookingDTO.BookingResponse>> acceptBooking(@PathVariable Long id) {
        Long panditId = authService.getCurrentUserId();
        BookingDTO.BookingResponse response = bookingService.acceptBooking(panditId, id);
        return ResponseEntity.ok(buildSuccessResponse(response, "Booking accepted successfully"));
    }

    @PutMapping("/pandit/{id}/reject")
    public ResponseEntity<CommonDTO.ApiResponse<BookingDTO.BookingResponse>> rejectBooking(
            @PathVariable Long id, @RequestParam String reason) {
        Long panditId = authService.getCurrentUserId();
        BookingDTO.BookingResponse response = bookingService.rejectBooking(panditId, id, reason);
        return ResponseEntity.ok(buildSuccessResponse(response, "Booking rejected successfully"));
    }

    @PutMapping("/pandit/{id}/complete")
    public ResponseEntity<CommonDTO.ApiResponse<BookingDTO.BookingResponse>> completeBooking(@PathVariable Long id) {
        Long panditId = authService.getCurrentUserId();
        BookingDTO.BookingResponse response = bookingService.completeBooking(panditId, id);
        return ResponseEntity.ok(buildSuccessResponse(response, "Booking completed successfully"));
    }

    @PutMapping("/pandit/{id}/cancel")
    public ResponseEntity<CommonDTO.ApiResponse<BookingDTO.BookingResponse>> cancelBookingByPandit(
            @PathVariable Long id, @RequestParam String reason) {
        Long panditId = authService.getCurrentUserId();
        BookingDTO.BookingResponse response = bookingService.cancelBookingByPandit(panditId, id, reason);
        return ResponseEntity.ok(buildSuccessResponse(response, "Booking cancelled successfully"));
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

