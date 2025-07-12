package com.dataglove.web.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "learning_records")
public class LearningRecord extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "gesture_name", nullable = false, length = 100)
    private String gestureName;
    
    @Column(name = "practice_count", nullable = false)
    private Integer practiceCount = 0;
    
    @Column(name = "success_count", nullable = false)
    private Integer successCount = 0;
    
    @Column(name = "average_confidence", precision = 5, scale = 4)
    private BigDecimal averageConfidence;
    
    @Column(name = "last_practice_time")
    private LocalDateTime lastPracticeTime;
    
    public LearningRecord() {}
    
    public LearningRecord(User user, String gestureName) {
        this.user = user;
        this.gestureName = gestureName;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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
    
    public LocalDateTime getLastPracticeTime() {
        return lastPracticeTime;
    }
    
    public void setLastPracticeTime(LocalDateTime lastPracticeTime) {
        this.lastPracticeTime = lastPracticeTime;
    }
    
    public void incrementPracticeCount() {
        this.practiceCount++;
    }
    
    public void incrementSuccessCount() {
        this.successCount++;
    }
    
    public double getSuccessRate() {
        if (practiceCount == 0) return 0.0;
        return (double) successCount / practiceCount;
    }
}