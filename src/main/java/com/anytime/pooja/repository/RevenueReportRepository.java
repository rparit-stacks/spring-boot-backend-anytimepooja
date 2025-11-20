package com.anytime.pooja.repository;

import com.anytime.pooja.model.RevenueReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RevenueReportRepository extends JpaRepository<RevenueReport, Long> {
    
    Optional<RevenueReport> findByReportDate(LocalDate reportDate);
    
    @Query("SELECT r FROM RevenueReport r WHERE r.reportDate >= :fromDate AND r.reportDate <= :toDate ORDER BY r.reportDate ASC")
    List<RevenueReport> findByDateRange(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
    
    @Query("SELECT r FROM RevenueReport r ORDER BY r.reportDate DESC")
    List<RevenueReport> findAllOrderByDateDesc();
}

