package com.anytime.pooja.repository;

import com.anytime.pooja.model.AdminUser;
import com.anytime.pooja.model.enums.AdminRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    
    Optional<AdminUser> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<AdminUser> findByRole(AdminRole role);
    
    List<AdminUser> findByIsActiveTrue();
    
    @Query("SELECT a FROM AdminUser a WHERE a.role = :role AND a.isActive = true")
    List<AdminUser> findActiveByRole(@Param("role") AdminRole role);
}

