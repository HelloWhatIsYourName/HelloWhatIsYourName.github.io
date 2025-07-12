package com.dataglove.web.dto;

import com.dataglove.web.entity.SensorData;

import java.time.LocalDateTime;
import java.util.Map;

public class SensorDataInfo {
    
    private Long id;
    private Long deviceId;
    private String deviceName;
    private Long userId;
    private String username;
    private String sensorType;
    private String sensorPosition;
    private Map<String, Object> dataValue;
    private LocalDateTime timestamp;
    private LocalDateTime createdAt;
    
    public SensorDataInfo() {}
    
    public static SensorDataInfo fromSensorData(SensorData sensorData) {
        SensorDataInfo info = new SensorDataInfo();
        info.setId(sensorData.getId());
        info.setDeviceId(sensorData.getDevice().getId());
        info.setDeviceName(sensorData.getDevice().getDeviceName());
        info.setUserId(sensorData.getUser().getId());
        info.setUsername(sensorData.getUser().getUsername());
        info.setSensorType(sensorData.getSensorType().name());
        info.setSensorPosition(sensorData.getSensorPosition());
        info.setDataValue(sensorData.getDataValue());
        info.setTimestamp(sensorData.getTimestamp());
        info.setCreatedAt(sensorData.getCreatedAt());
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
    
    public String getSensorType() {
        return sensorType;
    }
    
    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }
    
    public String getSensorPosition() {
        return sensorPosition;
    }
    
    public void setSensorPosition(String sensorPosition) {
        this.sensorPosition = sensorPosition;
    }
    
    public Map<String, Object> getDataValue() {
        return dataValue;
    }
    
    public void setDataValue(Map<String, Object> dataValue) {
        this.dataValue = dataValue;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}