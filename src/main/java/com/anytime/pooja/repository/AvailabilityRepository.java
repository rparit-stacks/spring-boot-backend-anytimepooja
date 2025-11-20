package com.anytime.pooja.repository;

import com.anytime.pooja.model.Availability;
import com.anytime.pooja.model.PanditProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    
    List<Availability> findByPandit(PanditProfile pandit);
    
    List<Availability> findByPanditId(Long panditId);
    
    List<Availability> findByPanditAndDate(PanditProfile pandit, LocalDate date);
    
    List<Availability> findByPanditIdAndDate(Long panditId, LocalDate date);
    
    List<Availability> findByPanditIdAndDateAndIsBookedFalse(Long panditId, LocalDate date);
    
    @Query("SELECT a FROM Availability a WHERE a.pandit.id = :panditId AND a.date = :date AND a.isBooked = false AND a.startTime <= :endTime AND a.endTime >= :startTime")
    List<Availability> findAvailableSlots(@Param("panditId") Long panditId, 
                                           @Param("date") LocalDate date,
                                           @Param("startTime") LocalTime startTime,
                                           @Param("endTime") LocalTime endTime);
    
    @Query("SELECT a FROM Availability a WHERE a.pandit.id = :panditId AND a.date >= :fromDate AND a.date <= :toDate")
    List<Availability> findByPanditIdAndDateRange(@Param("panditId") Long panditId,
                                                   @Param("fromDate") LocalDate fromDate,
                                                   @Param("toDate") LocalDate toDate);
    
    @Query("SELECT a FROM Availability a WHERE a.pandit.id = :panditId AND a.date = :date AND a.isBooked = false ORDER BY a.startTime ASC")
    List<Availability> findAvailableSlotsByDate(@Param("panditId") Long panditId, @Param("date") LocalDate date);
}

