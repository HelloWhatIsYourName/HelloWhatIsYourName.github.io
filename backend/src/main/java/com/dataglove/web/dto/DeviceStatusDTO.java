package com.dataglove.web.dto;

import com.dataglove.web.entity.Device;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class DeviceStatusDTO {
    
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;
    
    @NotNull(message = "设备状态不能为空")
    private Device.DeviceStatus status;
    
    private LocalDateTime timestamp;
    
    public DeviceStatusDTO() {}
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public Device.DeviceStatus getStatus() {
        return status;
    }
    
    public void setStatus(Device.DeviceStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}