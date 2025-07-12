package com.dataglove.web.repository;

import com.dataglove.web.entity.GestureResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GestureResultRepository extends JpaRepository<GestureResult, Long> {
    
    @Query("SELECT gr FROM GestureResult gr WHERE gr.device.id = :deviceId ORDER BY gr.recognitionTime DESC")
    Page<GestureResult> findByDeviceId(@Param("deviceId") Long deviceId, Pageable pageable);
    
    @Query("SELECT gr FROM GestureResult gr WHERE gr.user.id = :userId ORDER BY gr.recognitionTime DESC")
    Page<GestureResult> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT gr FROM GestureResult gr WHERE gr.gestureName = :gestureName ORDER BY gr.recognitionTime DESC")
    Page<GestureResult> findByGestureName(@Param("gestureName") String gestureName, Pageable pageable);
    
    @Query("SELECT gr FROM GestureResult gr WHERE gr.user.id = :userId AND gr.gestureName = :gestureName ORDER BY gr.recognitionTime DESC")
    Page<GestureResult> findByUserIdAndGestureName(@Param("userId") Long userId, @Param("gestureName") String gestureName, Pageable pageable);
    
    @Query("SELECT gr FROM GestureResult gr WHERE gr.recognitionTime BETWEEN :startTime AND :endTime ORDER BY gr.recognitionTime DESC")
    Page<GestureResult> findByRecognitionTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                                     @Param("endTime") LocalDateTime endTime, 
                                                     Pageable pageable);
    
    @Query("SELECT COUNT(gr) FROM GestureResult gr WHERE gr.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(gr) FROM GestureResult gr WHERE gr.user.id = :userId AND gr.gestureName = :gestureName")
    long countByUserIdAndGestureName(@Param("userId") Long userId, @Param("gestureName") String gestureName);
    
    @Query("SELECT COUNT(gr) FROM GestureResult gr WHERE gr.user.id = :userId AND gr.gestureName = :gestureName AND gr.confidence >= :minConfidence")
    long countSuccessfulByUserIdAndGestureName(@Param("userId") Long userId, @Param("gestureName") String gestureName, @Param("minConfidence") java.math.BigDecimal minConfidence);
    
    @Query("SELECT AVG(gr.confidence) FROM GestureResult gr WHERE gr.user.id = :userId AND gr.gestureName = :gestureName")
    java.math.BigDecimal getAverageConfidenceByUserIdAndGestureName(@Param("userId") Long userId, @Param("gestureName") String gestureName);
    
    @Query("SELECT DISTINCT gr.gestureName FROM GestureResult gr WHERE gr.user.id = :userId")
    List<String> findDistinctGestureNamesByUserId(@Param("userId") Long userId);
    
    @Query("SELECT gr FROM GestureResult gr WHERE gr.user.id = :userId ORDER BY gr.recognitionTime DESC")
    List<GestureResult> findLatestByUserId(@Param("userId") Long userId, Pageable pageable);
}