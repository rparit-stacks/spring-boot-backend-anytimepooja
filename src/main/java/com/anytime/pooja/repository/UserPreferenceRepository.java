package com.anytime.pooja.repository;

import com.anytime.pooja.model.User;
import com.anytime.pooja.model.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    
    Optional<UserPreference> findByUser(User user);
    
    Optional<UserPreference> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
}

