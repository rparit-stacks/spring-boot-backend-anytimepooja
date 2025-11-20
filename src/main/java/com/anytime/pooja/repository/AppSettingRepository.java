package com.anytime.pooja.repository;

import com.anytime.pooja.model.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppSettingRepository extends JpaRepository<AppSetting, String> {
    
    Optional<AppSetting> findByKey(String key);
    
    @Query("SELECT s FROM AppSetting s WHERE s.isPublic = true")
    List<AppSetting> findPublicSettings();
    
    boolean existsByKey(String key);
}

