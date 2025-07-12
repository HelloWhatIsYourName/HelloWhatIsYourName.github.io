package com.dataglove.web.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "sensor_data")
public class SensorData extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sensor_type", nullable = false)
    private SensorType sensorType;
    
    @Column(name = "sensor_position", length = 50)
    private String sensorPosition;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data_value", nullable = false, columnDefinition = "JSON")
    private Map<String, Object> dataValue;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    public enum SensorType {
        FLEX, STRAIN, IMU
    }
    
    public SensorData() {}
    
    public SensorData(Device device, User user, SensorType sensorType, Map<String, Object> dataValue) {
        this.device = device;
        this.user = user;
        this.sensorType = sensorType;
        this.dataValue = dataValue;
        this.timestamp = LocalDateTime.now();
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
    
    public SensorType getSensorType() {
        return sensorType;
    }
    
    public void setSensorType(SensorType sensorType) {
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
}