package com.anytime.pooja.repository;

import com.anytime.pooja.model.Booking;
import com.anytime.pooja.model.PanditProfile;
import com.anytime.pooja.model.User;
import com.anytime.pooja.model.enums.BookingStatus;
import com.anytime.pooja.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    Optional<Booking> findByBookingNumber(String bookingNumber);
    
    List<Booking> findByUser(User user);
    
    List<Booking> findByUserId(Long userId);
    
    List<Booking> findByPandit(PanditProfile pandit);
    
    List<Booking> findByPanditId(Long panditId);
    
    List<Booking> findByStatus(BookingStatus status);
    
    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);
    
    List<Booking> findByPanditIdAndStatus(Long panditId, BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId ORDER BY b.bookingDate DESC, b.bookingTime DESC")
    List<Booking> findByUserIdOrderByDateDesc(@Param("userId") Long userId);
    
    @Query("SELECT b FROM Booking b WHERE b.pandit.id = :panditId ORDER BY b.bookingDate DESC, b.bookingTime DESC")
    List<Booking> findByPanditIdOrderByDateDesc(@Param("panditId") Long panditId);
    
    @Query("SELECT b FROM Booking b WHERE b.pandit.id = :panditId AND b.bookingDate = :date AND b.status IN :statuses")
    List<Booking> findByPanditIdAndDateAndStatusIn(@Param("panditId") Long panditId,
                                                    @Param("date") LocalDate date,
                                                    @Param("statuses") List<BookingStatus> statuses);
    
    @Query("SELECT b FROM Booking b WHERE b.pandit.id = :panditId AND b.bookingDate = :date AND b.status = :status")
    List<Booking> findTodayBookings(@Param("panditId") Long panditId,
                                    @Param("date") LocalDate date,
                                    @Param("status") BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.pandit.id = :panditId AND b.bookingDate >= :date AND b.status = :status ORDER BY b.bookingDate ASC, b.bookingTime ASC")
    List<Booking> findUpcomingBookings(@Param("panditId") Long panditId,
                                       @Param("date") LocalDate date,
                                       @Param("status") BookingStatus status);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = :status")
    Long countByStatus(@Param("status") BookingStatus status);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt >= :fromDate AND b.createdAt <= :toDate")
    Long countByDateRange(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
    
    @Query("SELECT SUM(b.finalAmount) FROM Booking b WHERE b.paymentStatus = :status AND b.createdAt >= :fromDate AND b.createdAt <= :toDate")
    Double getRevenueByDateRange(@Param("status") PaymentStatus status,
                                 @Param("fromDate") LocalDateTime fromDate,
                                 @Param("toDate") LocalDateTime toDate);
}

