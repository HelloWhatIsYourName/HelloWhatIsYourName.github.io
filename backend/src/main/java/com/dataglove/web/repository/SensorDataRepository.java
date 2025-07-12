package com.dataglove.web.repository;

import com.dataglove.web.entity.SensorData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    
    @Query("SELECT sd FROM SensorData sd WHERE sd.device.id = :deviceId ORDER BY sd.timestamp DESC")
    Page<SensorData> findByDeviceId(@Param("deviceId") Long deviceId, Pageable pageable);
    
    @Query("SELECT sd FROM SensorData sd WHERE sd.user.id = :userId ORDER BY sd.timestamp DESC")
    Page<SensorData> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT sd FROM SensorData sd WHERE sd.device.id = :deviceId AND sd.timestamp BETWEEN :startTime AND :endTime ORDER BY sd.timestamp DESC")
    Page<SensorData> findByDeviceIdAndTimestampBetween(@Param("deviceId") Long deviceId, 
                                                       @Param("startTime") LocalDateTime startTime, 
                                                       @Param("endTime") LocalDateTime endTime, 
                                                       Pageable pageable);
    
    @Query("SELECT sd FROM SensorData sd WHERE sd.user.id = :userId AND sd.timestamp BETWEEN :startTime AND :endTime ORDER BY sd.timestamp DESC")
    Page<SensorData> findByUserIdAndTimestampBetween(@Param("userId") Long userId, 
                                                     @Param("startTime") LocalDateTime startTime, 
                                                     @Param("endTime") LocalDateTime endTime, 
                                                     Pageable pageable);
    
    @Query("SELECT sd FROM SensorData sd WHERE sd.sensorType = :sensorType ORDER BY sd.timestamp DESC")
    Page<SensorData> findBySensorType(@Param("sensorType") SensorData.SensorType sensorType, Pageable pageable);
    
    @Query("SELECT COUNT(sd) FROM SensorData sd WHERE sd.device.id = :deviceId")
    long countByDeviceId(@Param("deviceId") Long deviceId);
    
    @Query("SELECT COUNT(sd) FROM SensorData sd WHERE sd.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(sd) FROM SensorData sd WHERE sd.timestamp >= :startTime")
    long countByTimestampAfter(@Param("startTime") LocalDateTime startTime);
    
    @Query("SELECT sd FROM SensorData sd WHERE sd.timestamp < :cutoffTime")
    List<SensorData> findOldData(@Param("cutoffTime") LocalDateTime cutoffTime);
}