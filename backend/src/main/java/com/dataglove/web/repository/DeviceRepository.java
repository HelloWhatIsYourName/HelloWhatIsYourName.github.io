package com.dataglove.web.repository;

import com.dataglove.web.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    
    Optional<Device> findByDeviceId(String deviceId);
    
    Boolean existsByDeviceId(String deviceId);
    
    Boolean existsByMacAddress(String macAddress);
    
    @Query("SELECT d FROM Device d WHERE d.status = :status")
    Page<Device> findByStatus(@Param("status") Device.DeviceStatus status, Pageable pageable);
    
    @Query("SELECT d FROM Device d WHERE d.deviceName LIKE %:keyword% OR d.deviceId LIKE %:keyword% OR d.location LIKE %:keyword%")
    Page<Device> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT COUNT(d) FROM Device d WHERE d.status = :status")
    long countByStatus(@Param("status") Device.DeviceStatus status);
}