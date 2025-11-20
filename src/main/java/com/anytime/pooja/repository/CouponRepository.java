package com.anytime.pooja.repository;

import com.anytime.pooja.model.Coupon;
import com.anytime.pooja.model.enums.CouponType;
import com.anytime.pooja.model.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
    Optional<Coupon> findByCode(String code);
    
    List<Coupon> findByIsActiveTrue();
    
    List<Coupon> findByType(CouponType type);
    
    List<Coupon> findByUserType(UserType userType);
    
    @Query("SELECT c FROM Coupon c WHERE c.code = :code AND c.isActive = true AND c.startDate <= :date AND c.expiryDate >= :date")
    Optional<Coupon> findValidCoupon(@Param("code") String code, @Param("date") LocalDate date);
    
    @Query("SELECT c FROM Coupon c WHERE c.isActive = true AND c.startDate <= :date AND c.expiryDate >= :date")
    List<Coupon> findActiveCoupons(@Param("date") LocalDate date);
}

