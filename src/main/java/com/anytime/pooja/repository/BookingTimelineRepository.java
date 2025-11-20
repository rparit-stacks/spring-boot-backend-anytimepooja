package com.anytime.pooja.repository;

import com.anytime.pooja.model.Booking;
import com.anytime.pooja.model.BookingTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingTimelineRepository extends JpaRepository<BookingTimeline, Long> {
    
    List<BookingTimeline> findByBooking(Booking booking);
    
    List<BookingTimeline> findByBookingId(Long bookingId);
    
    @Query("SELECT t FROM BookingTimeline t WHERE t.booking.id = :bookingId ORDER BY t.createdAt ASC")
    List<BookingTimeline> findByBookingIdOrderByCreatedAt(@Param("bookingId") Long bookingId);
}

