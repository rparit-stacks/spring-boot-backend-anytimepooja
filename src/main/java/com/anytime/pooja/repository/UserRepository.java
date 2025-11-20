package com.anytime.pooja.repository;

import com.anytime.pooja.model.User;
import com.anytime.pooja.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByPhone(String phone);
    
    Optional<User> findByEmailOrPhone(String email, String phone);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhone(String phone);
    
    List<User> findByRole(Role role);
    
    List<User> findByRoleAndIsActive(Role role, Boolean isActive);
    
    List<User> findByIsEmailVerified(Boolean isEmailVerified);
    
    List<User> findByIsPhoneVerified(Boolean isPhoneVerified);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true")
    List<User> findActiveUsersByRole(@Param("role") Role role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.createdAt >= :fromDate")
    Long countByRoleAndCreatedAtAfter(@Param("role") Role role, @Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT u FROM User u WHERE u.email LIKE %:search% OR u.phone LIKE %:search% OR u.name LIKE %:search%")
    List<User> searchUsers(@Param("search") String search);
}

