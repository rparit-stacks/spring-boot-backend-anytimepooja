package com.anytime.pooja.service.impl;

import com.anytime.pooja.dto.BookingDTO;
import com.anytime.pooja.model.*;
import com.anytime.pooja.model.enums.BookingStatus;
import com.anytime.pooja.repository.*;
import com.anytime.pooja.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingTimelineRepository bookingTimelineRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PanditProfileRepository panditProfileRepository;

    @Autowired
    private PanditServiceRepository panditServiceRepository;


    @Override
    public List<BookingDTO.AvailabilitySlotResponse> checkAvailability(BookingDTO.CheckAvailabilityRequest request) {
        List<Availability> availabilities = availabilityRepository.findAvailableSlots(
            request.getPanditId(),
            request.getDate(),
            request.getStartTime(),
            request.getStartTime().plusMinutes(request.getDurationMinutes())
        );
        
        return availabilities.stream()
            .map(av -> {
                BookingDTO.AvailabilitySlotResponse response = new BookingDTO.AvailabilitySlotResponse();
                response.setStartTime(av.getStartTime());
                response.setEndTime(av.getEndTime());
                response.setIsAvailable(!av.getIsBooked());
                response.setReason(av.getIsBooked() ? "Slot already booked" : null);
                return response;
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDTO.BookingResponse createBooking(Long userId, BookingDTO.CreateBookingRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        PanditProfile pandit = panditProfileRepository.findById(request.getPanditId())
            .orElseThrow(() -> new RuntimeException("Pandit not found"));
        
        com.anytime.pooja.model.PanditService service = panditServiceRepository.findById(request.getServiceId())
            .orElseThrow(() -> new RuntimeException("Service not found"));
        
        Address address = addressRepository.findById(request.getAddressId())
            .orElseThrow(() -> new RuntimeException("Address not found"));
        
        // Check availability
        List<Availability> availableSlots = availabilityRepository.findAvailableSlots(
            request.getPanditId(),
            request.getBookingDate(),
            request.getBookingTime(),
            request.getBookingTime().plusMinutes(service.getDurationMinutes())
        );
        
        if (availableSlots.isEmpty()) {
            throw new RuntimeException("No available slots for the selected time");
        }
        
        // Create booking
        Booking booking = new Booking();
        booking.setBookingNumber(generateBookingNumber());
        booking.setUser(user);
        booking.setPandit(pandit);
        booking.setService(service);
        booking.setBookingDate(request.getBookingDate());
        booking.setBookingTime(request.getBookingTime());
        booking.setEndTime(request.getBookingTime().plusMinutes(service.getDurationMinutes()));
        booking.setStatus(BookingStatus.PENDING);
        booking.setAddress(address);
        booking.setSpecialInstructions(request.getSpecialInstructions());
        booking.setTotalAmount(service.getPrice());
        booking.setDiscountAmount(0.0);
        booking.setFinalAmount(service.getPrice());
        booking.setPaymentStatus(com.anytime.pooja.model.enums.PaymentStatus.PENDING);
        
        booking = bookingRepository.save(booking);
        
        // Mark slot as booked
        availableSlots.get(0).setIsBooked(true);
        availabilityRepository.save(availableSlots.get(0));
        
        // Create timeline entry
        createTimelineEntry(booking.getId(), BookingStatus.PENDING, "Booking created", userId);
        
        return mapToBookingResponse(booking);
    }

    @Override
    public BookingDTO.BookingResponse getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        return mapToBookingResponse(booking);
    }

    @Override
    public List<BookingDTO.BookingSummaryResponse> getUserBookings(Long userId, BookingStatus status, Integer page, Integer size) {
        List<Booking> bookings = status != null 
            ? bookingRepository.findByUserIdAndStatus(userId, status)
            : bookingRepository.findByUserId(userId);
        
        return bookings.stream()
            .skip(page * size)
            .limit(size)
            .map(this::mapToBookingSummaryResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDTO.BookingResponse cancelBooking(Long userId, Long bookingId, BookingDTO.CancelBookingRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(request.getReason());
        booking.setCancelledBy("USER");
        booking = bookingRepository.save(booking);
        
        // Free up the slot
        freeUpSlot(booking);
        
        createTimelineEntry(bookingId, BookingStatus.CANCELLED, "Booking cancelled: " + request.getReason(), userId);
        
        return mapToBookingResponse(booking);
    }

    @Override
    @Transactional
    public BookingDTO.BookingResponse rescheduleBooking(Long userId, Long bookingId, BookingDTO.RescheduleRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        // Free old slot
        freeUpSlot(booking);
        
        // Check new availability
        List<Availability> availableSlots = availabilityRepository.findAvailableSlots(
            booking.getPandit().getId(),
            request.getNewBookingDate(),
            request.getNewBookingTime(),
            request.getNewBookingTime().plusMinutes(booking.getService().getDurationMinutes())
        );
        
        if (availableSlots.isEmpty()) {
            throw new RuntimeException("No available slots for the selected time");
        }
        
        booking.setBookingDate(request.getNewBookingDate());
        booking.setBookingTime(request.getNewBookingTime());
        booking.setEndTime(request.getNewBookingTime().plusMinutes(booking.getService().getDurationMinutes()));
        booking = bookingRepository.save(booking);
        
        // Mark new slot as booked
        availableSlots.get(0).setIsBooked(true);
        availabilityRepository.save(availableSlots.get(0));
        
        createTimelineEntry(bookingId, booking.getStatus(), "Booking rescheduled: " + request.getReason(), userId);
        
        return mapToBookingResponse(booking);
    }

    @Override
    public List<BookingDTO.TimelineResponse> getBookingTimeline(Long bookingId) {
        List<BookingTimeline> timeline = bookingTimelineRepository.findByBookingIdOrderByCreatedAt(bookingId);
        return timeline.stream()
            .map(this::mapToTimelineResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO.BookingSummaryResponse> getPanditBookings(Long panditId, BookingStatus status, Integer page, Integer size) {
        List<Booking> bookings = status != null
            ? bookingRepository.findByPanditIdAndStatus(panditId, status)
            : bookingRepository.findByPanditId(panditId);
        
        return bookings.stream()
            .skip(page * size)
            .limit(size)
            .map(this::mapToBookingSummaryResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO.BookingSummaryResponse> getTodayBookings(Long panditId) {
        List<Booking> bookings = bookingRepository.findTodayBookings(
            panditId,
            LocalDate.now(),
            BookingStatus.CONFIRMED
        );
        return bookings.stream()
            .map(this::mapToBookingSummaryResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO.BookingSummaryResponse> getUpcomingBookings(Long panditId) {
        List<Booking> bookings = bookingRepository.findUpcomingBookings(
            panditId,
            LocalDate.now(),
            BookingStatus.CONFIRMED
        );
        return bookings.stream()
            .map(this::mapToBookingSummaryResponse)
            .collect(Collectors.toList());
    }

    @Override
    public BookingDTO.BookingResponse getPanditBooking(Long panditId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (!booking.getPandit().getId().equals(panditId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        return mapToBookingResponse(booking);
    }

    @Override
    @Transactional
    public BookingDTO.BookingResponse acceptBooking(Long panditId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (!booking.getPandit().getId().equals(panditId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setConfirmedAt(LocalDateTime.now());
        booking = bookingRepository.save(booking);
        
        createTimelineEntry(bookingId, BookingStatus.CONFIRMED, "Booking confirmed by pandit", panditId);
        
        return mapToBookingResponse(booking);
    }

    @Override
    @Transactional
    public BookingDTO.BookingResponse rejectBooking(Long panditId, Long bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (!booking.getPandit().getId().equals(panditId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        booking.setStatus(BookingStatus.REJECTED);
        booking.setCancellationReason(reason);
        booking.setCancelledBy("PANDIT");
        booking = bookingRepository.save(booking);
        
        freeUpSlot(booking);
        createTimelineEntry(bookingId, BookingStatus.REJECTED, "Booking rejected: " + reason, panditId);
        
        return mapToBookingResponse(booking);
    }

    @Override
    @Transactional
    public BookingDTO.BookingResponse completeBooking(Long panditId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (!booking.getPandit().getId().equals(panditId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        booking.setStatus(BookingStatus.COMPLETED);
        booking.setCompletedAt(LocalDateTime.now());
        booking = bookingRepository.save(booking);
        
        createTimelineEntry(bookingId, BookingStatus.COMPLETED, "Booking completed", panditId);
        
        return mapToBookingResponse(booking);
    }

    @Override
    @Transactional
    public BookingDTO.BookingResponse cancelBookingByPandit(Long panditId, Long bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (!booking.getPandit().getId().equals(panditId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(reason);
        booking.setCancelledBy("PANDIT");
        booking = bookingRepository.save(booking);
        
        freeUpSlot(booking);
        createTimelineEntry(bookingId, BookingStatus.CANCELLED, "Booking cancelled by pandit: " + reason, panditId);
        
        return mapToBookingResponse(booking);
    }

    @Override
    public List<BookingDTO.BookingResponse> getAllBookings(BookingStatus status, Integer page, Integer size) {
        List<Booking> bookings = status != null
            ? bookingRepository.findByStatus(status)
            : bookingRepository.findAll();
        
        return bookings.stream()
            .skip(page * size)
            .limit(size)
            .map(this::mapToBookingResponse)
            .collect(Collectors.toList());
    }

    @Override
    public BookingDTO.BookingResponse getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        return mapToBookingResponse(booking);
    }

    @Override
    @Transactional
    public BookingDTO.BookingResponse updateBookingStatus(Long bookingId, BookingDTO.UpdateStatusRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        booking.setStatus(request.getStatus());
        if (request.getStatus() == BookingStatus.CONFIRMED) {
            booking.setConfirmedAt(LocalDateTime.now());
        } else if (request.getStatus() == BookingStatus.COMPLETED) {
            booking.setCompletedAt(LocalDateTime.now());
        }
        
        booking = bookingRepository.save(booking);
        createTimelineEntry(bookingId, request.getStatus(), request.getReason(), null);
        
        return mapToBookingResponse(booking);
    }

    @Override
    @Transactional
    public BookingDTO.BookingResponse cancelBookingByAdmin(Long bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(reason);
        booking.setCancelledBy("ADMIN");
        booking = bookingRepository.save(booking);
        
        freeUpSlot(booking);
        createTimelineEntry(bookingId, BookingStatus.CANCELLED, "Booking cancelled by admin: " + reason, null);
        
        return mapToBookingResponse(booking);
    }

    // Helper methods
    private String generateBookingNumber() {
        return "BK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private void createTimelineEntry(Long bookingId, BookingStatus status, String description, Long createdBy) {
        BookingTimeline timeline = new BookingTimeline();
        Booking booking = new Booking();
        booking.setId(bookingId);
        timeline.setBooking(booking);
        timeline.setStatus(status);
        timeline.setDescription(description);
        timeline.setCreatedBy(createdBy);
        bookingTimelineRepository.save(timeline);
    }

    private void freeUpSlot(Booking booking) {
        List<Availability> availabilities = availabilityRepository.findByPanditIdAndDate(
            booking.getPandit().getId(),
            booking.getBookingDate()
        );
        
        availabilities.stream()
            .filter(av -> av.getStartTime().equals(booking.getBookingTime()))
            .forEach(av -> {
                av.setIsBooked(false);
                availabilityRepository.save(av);
            });
    }

    private BookingDTO.BookingResponse mapToBookingResponse(Booking booking) {
        BookingDTO.BookingResponse response = new BookingDTO.BookingResponse();
        response.setId(booking.getId());
        response.setBookingNumber(booking.getBookingNumber());
        response.setUserId(booking.getUser().getId());
        response.setUserName(booking.getUser().getName());
        response.setPanditId(booking.getPandit().getId());
        response.setPanditName(booking.getPandit().getUser().getName());
        response.setServiceId(booking.getService().getId());
        response.setServiceName(booking.getService().getServiceName());
        response.setBookingDate(booking.getBookingDate());
        response.setBookingTime(booking.getBookingTime());
        response.setEndTime(booking.getEndTime());
        response.setStatus(booking.getStatus());
        response.setCancellationReason(booking.getCancellationReason());
        response.setCancelledBy(booking.getCancelledBy());
        
        // Map address
        BookingDTO.AddressInfo addressInfo = new BookingDTO.AddressInfo();
        addressInfo.setStreet(booking.getAddress().getStreet());
        addressInfo.setLandmark(booking.getAddress().getLandmark());
        addressInfo.setCity(booking.getAddress().getCity());
        addressInfo.setState(booking.getAddress().getState());
        addressInfo.setZipCode(booking.getAddress().getZipCode());
        addressInfo.setLatitude(booking.getAddress().getLatitude());
        addressInfo.setLongitude(booking.getAddress().getLongitude());
        response.setAddress(addressInfo);
        
        response.setSpecialInstructions(booking.getSpecialInstructions());
        response.setTotalAmount(booking.getTotalAmount());
        response.setDiscountAmount(booking.getDiscountAmount());
        response.setCouponCode(booking.getCouponCode());
        response.setFinalAmount(booking.getFinalAmount());
        response.setPaymentMethod(booking.getPaymentMethod());
        response.setPaymentStatus(booking.getPaymentStatus());
        response.setConfirmedAt(booking.getConfirmedAt());
        response.setCompletedAt(booking.getCompletedAt());
        response.setCreatedAt(booking.getCreatedAt());
        response.setUpdatedAt(booking.getUpdatedAt());
        return response;
    }

    private BookingDTO.BookingSummaryResponse mapToBookingSummaryResponse(Booking booking) {
        BookingDTO.BookingSummaryResponse response = new BookingDTO.BookingSummaryResponse();
        response.setId(booking.getId());
        response.setBookingNumber(booking.getBookingNumber());
        response.setPanditName(booking.getPandit().getUser().getName());
        response.setServiceName(booking.getService().getServiceName());
        response.setBookingDate(booking.getBookingDate());
        response.setBookingTime(booking.getBookingTime());
        response.setStatus(booking.getStatus());
        response.setFinalAmount(booking.getFinalAmount());
        response.setPaymentStatus(booking.getPaymentStatus());
        response.setCreatedAt(booking.getCreatedAt());
        return response;
    }

    private BookingDTO.TimelineResponse mapToTimelineResponse(BookingTimeline timeline) {
        BookingDTO.TimelineResponse response = new BookingDTO.TimelineResponse();
        response.setId(timeline.getId());
        response.setStatus(timeline.getStatus());
        response.setDescription(timeline.getDescription());
        response.setCreatedBy(timeline.getCreatedBy());
        response.setCreatedAt(timeline.getCreatedAt());
        return response;
    }
}

