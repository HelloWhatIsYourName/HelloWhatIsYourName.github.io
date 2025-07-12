package com.dataglove.web.repository;

import com.dataglove.web.entity.UserDevice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    
    @Query("SELECT ud FROM UserDevice ud WHERE ud.user.id = :userId AND ud.isActive = true")
    List<UserDevice> findActiveDevicesByUserId(@Param("userId") Long userId);
    
    @Query("SELECT ud FROM UserDevice ud WHERE ud.device.id = :deviceId AND ud.isActive = true")
    Optional<UserDevice> findActiveBindingByDeviceId(@Param("deviceId") Long deviceId);
    
    @Query("SELECT ud FROM UserDevice ud WHERE ud.user.id = :userId AND ud.device.id = :deviceId")
    Optional<UserDevice> findByUserIdAndDeviceId(@Param("userId") Long userId, @Param("deviceId") Long deviceId);
    
    @Query("SELECT ud FROM UserDevice ud WHERE ud.user.id = :userId")
    Page<UserDevice> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT ud FROM UserDevice ud WHERE ud.device.id = :deviceId")
    Page<UserDevice> findByDeviceId(@Param("deviceId") Long deviceId, Pageable pageable);
    
    @Query("SELECT COUNT(ud) FROM UserDevice ud WHERE ud.isActive = true")
    long countActiveBindings();
}