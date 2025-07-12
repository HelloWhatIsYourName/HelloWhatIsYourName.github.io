package com.dataglove.web.repository;

import com.dataglove.web.entity.LearningRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningRecordRepository extends JpaRepository<LearningRecord, Long> {
    
    @Query("SELECT lr FROM LearningRecord lr WHERE lr.user.id = :userId ORDER BY lr.lastPracticeTime DESC")
    Page<LearningRecord> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT lr FROM LearningRecord lr WHERE lr.user.id = :userId AND lr.gestureName = :gestureName")
    Optional<LearningRecord> findByUserIdAndGestureName(@Param("userId") Long userId, @Param("gestureName") String gestureName);
    
    @Query("SELECT lr FROM LearningRecord lr WHERE lr.gestureName = :gestureName ORDER BY lr.averageConfidence DESC")
    Page<LearningRecord> findByGestureName(@Param("gestureName") String gestureName, Pageable pageable);
    
    @Query("SELECT lr FROM LearningRecord lr WHERE lr.user.id = :userId ORDER BY lr.practiceCount DESC")
    List<LearningRecord> findTopPracticedByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT lr FROM LearningRecord lr WHERE lr.user.id = :userId ORDER BY lr.averageConfidence DESC")
    List<LearningRecord> findTopPerformingByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT COUNT(lr) FROM LearningRecord lr WHERE lr.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(lr.practiceCount) FROM LearningRecord lr WHERE lr.user.id = :userId")
    Long getTotalPracticeCountByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(lr.successCount) FROM LearningRecord lr WHERE lr.user.id = :userId")
    Long getTotalSuccessCountByUserId(@Param("userId") Long userId);
}