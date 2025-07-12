package com.dataglove.web.dto;

import com.dataglove.web.entity.GestureResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class GestureResultInfo {
    
    private Long id;
    private Long deviceId;
    private String deviceName;
    private Long userId;
    private String username;
    private String gestureName;
    private BigDecimal confidence;
    private Map<String, Object> rawData;
    private Map<String, Object> processedData;
    private LocalDateTime recognitionTime;
    private LocalDateTime createdAt;
    
    public GestureResultInfo() {}
    
    public static GestureResultInfo fromGestureResult(GestureResult gestureResult) {
        GestureResultInfo info = new GestureResultInfo();
        info.setId(gestureResult.getId());
        info.setDeviceId(gestureResult.getDevice().getId());
        info.setDeviceName(gestureResult.getDevice().getDeviceName());
        info.setUserId(gestureResult.getUser().getId());
        info.setUsername(gestureResult.getUser().getUsername());
        info.setGestureName(gestureResult.getGestureName());
        info.setConfidence(gestureResult.getConfidence());
        info.setRawData(gestureResult.getRawData());
        info.setProcessedData(gestureResult.getProcessedData());
        info.setRecognitionTime(gestureResult.getRecognitionTime());
        info.setCreatedAt(gestureResult.getCreatedAt());
        return info;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getDeviceName() {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}