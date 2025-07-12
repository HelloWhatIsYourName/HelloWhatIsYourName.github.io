package com.dataglove.web.dto;

import com.dataglove.web.entity.SensorData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Map;

public class DeviceDataDTO {
    
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;
    
    @NotNull(message = "传感器类型不能为空")
    private SensorData.SensorType sensorType;
    
    private String sensorPosition;
    
    @NotNull(message = "数据值不能为空")
    private Map<String, Object> dataValue;
    
    private LocalDateTime timestamp;
    
    public DeviceDataDTO() {}
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public SensorData.SensorType getSensorType() {
        return sensorType;
    }
    
    public void setSensorType(SensorData.SensorType sensorType) {
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