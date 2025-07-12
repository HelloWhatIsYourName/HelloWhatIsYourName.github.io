package com.dataglove.web.dto;

import com.dataglove.web.entity.LearningRecord;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LearningRecordInfo {
    
    private Long id;
    private Long userId;
    private String username;
    private String gestureName;
    private Integer practiceCount;
    private Integer successCount;
    private BigDecimal averageConfidence;
    private Double successRate;
    private LocalDateTime lastPracticeTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public LearningRecordInfo() {}
    
    public static LearningRecordInfo fromLearningRecord(LearningRecord learningRecord) {
        LearningRecordInfo info = new LearningRecordInfo();
        info.setId(learningRecord.getId());
        info.setUserId(learningRecord.getUser().getId());
        info.setUsername(learningRecord.getUser().getUsername());
        info.setGestureName(learningRecord.getGestureName());
        info.setPracticeCount(learningRecord.getPracticeCount());
        info.setSuccessCount(learningRecord.getSuccessCount());
        info.setAverageConfidence(learningRecord.getAverageConfidence());
        info.setSuccessRate(learningRecord.getSuccessRate());
        info.setLastPracticeTime(learningRecord.getLastPracticeTime());
        info.setCreatedAt(learningRecord.getCreatedAt());
        info.setUpdatedAt(learningRecord.getUpdatedAt());
        return info;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getGestureName() {
        return gestureName;
    }
    
    public void setGestureName(String gestureName) {
        this.gestureName = gestureName;
    }
    
    public Integer getPracticeCount() {
        return practiceCount;
    }
    
    public void setPracticeCount(Integer practiceCount) {
        this.practiceCount = practiceCount;
    }
    
    public Integer getSuccessCount() {
        return successCount;
    }
    
    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }
    
    public BigDecimal getAverageConfidence() {
        return averageConfidence;
    }
    
    public void setAverageConfidence(BigDecimal averageConfidence) {
        this.averageConfidence = averageConfidence;
    }
    
    public Double getSuccessRate() {
        return successRate;
    }
    
    public void setSuccessRate(Double successRate) {
        this.successRate = successRate;
    }
    
    public LocalDateTime getLastPracticeTime() {
        return lastPracticeTime;
    }
    
    public void setLastPracticeTime(LocalDateTime lastPracticeTime) {
        this.lastPracticeTime = lastPracticeTime;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}