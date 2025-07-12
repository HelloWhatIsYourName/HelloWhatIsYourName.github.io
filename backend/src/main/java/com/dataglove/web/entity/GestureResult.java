package com.dataglove.web.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "gesture_results")
public class GestureResult extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "gesture_name", nullable = false, length = 100)
    private String gestureName;
    
    @Column(name = "confidence", nullable = false, precision = 5, scale = 4)
    private BigDecimal confidence;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_data", columnDefinition = "JSON")
    private Map<String, Object> rawData;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "processed_data", columnDefinition = "JSON")
    private Map<String, Object> processedData;
    
    @Column(name = "recognition_time", nullable = false)
    private LocalDateTime recognitionTime;
    
    public GestureResult() {}
    
    public GestureResult(Device device, User user, String gestureName, BigDecimal confidence) {
        this.device = device;
        this.user = user;
        this.gestureName = gestureName;
        this.confidence = confidence;
        this.recognitionTime = LocalDateTime.now();
    }
    
    public Device getDevice() {
        return device;
    }
    
    public void setDevice(Device device) {
        this.device = device;
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
    
    public BigDecimal getConfidence() {
        return confidence;
    }
    
    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }
    
    public Map<String, Object> getRawData() {
        return rawData;
    }
    
    public void setRawData(Map<String, Object> rawData) {
        this.rawData = rawData;
    }
    
    public Map<String, Object> getProcessedData() {
        return processedData;
    }
    
    public void setProcessedData(Map<String, Object> processedData) {
        this.processedData = processedData;
    }
    
    public LocalDateTime getRecognitionTime() {
        return recognitionTime;
    }
    
    public void setRecognitionTime(LocalDateTime recognitionTime) {
        this.recognitionTime = recognitionTime;
    }
}