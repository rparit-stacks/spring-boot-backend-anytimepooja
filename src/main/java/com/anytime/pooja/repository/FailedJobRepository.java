package com.anytime.pooja.repository;

import com.anytime.pooja.model.FailedJob;
import com.anytime.pooja.model.enums.JobType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FailedJobRepository extends JpaRepository<FailedJob, Long> {
    
    List<FailedJob> findByJobType(JobType jobType);
    
    @Query("SELECT f FROM FailedJob f WHERE f.retriedAt IS NULL ORDER BY f.failedAt ASC")
    List<FailedJob> findUnretriedJobs();
    
    @Query("SELECT f FROM FailedJob f WHERE f.jobType = :jobType AND f.retriedAt IS NULL ORDER BY f.failedAt ASC")
    List<FailedJob> findUnretriedByJobType(@Param("jobType") JobType jobType);
    
    @Query("SELECT f FROM FailedJob f WHERE f.failedAt >= :fromDate AND f.failedAt <= :toDate ORDER BY f.failedAt DESC")
    List<FailedJob> findByDateRange(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
}

