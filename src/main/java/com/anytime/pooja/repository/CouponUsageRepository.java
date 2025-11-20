package com.anytime.pooja.repository;

import com.anytime.pooja.model.Coupon;
import com.anytime.pooja.model.CouponUsage;
import com.anytime.pooja.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {
    
    List<CouponUsage> findByCoupon(Coupon coupon);
    
    List<CouponUsage> findByCouponId(Long couponId);
    
    List<CouponUsage> findByUser(User user);
    
    List<CouponUsage> findByUserId(Long userId);
    
    @Query("SELECT COUNT(cu) FROM CouponUsage cu WHERE cu.coupon.id = :couponId")
    Long countUsageByCouponId(@Param("couponId") Long couponId);
    
    @Query("SELECT COUNT(cu) FROM CouponUsage cu WHERE cu.coupon.id = :couponId AND cu.user.id = :userId")
    Long countUsageByCouponIdAndUserId(@Param("couponId") Long couponId, @Param("userId") Long userId);
}

