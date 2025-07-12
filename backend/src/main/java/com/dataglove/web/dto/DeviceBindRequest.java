package com.dataglove.web.dto;

import jakarta.validation.constraints.NotNull;

public class DeviceBindRequest {
    
    @NotNull(message = "设备ID不能为空")
    private Long deviceId;
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    public DeviceBindRequest() {}
    
    public DeviceBindRequest(Long deviceId, Long userId) {
        this.deviceId = deviceId;
        this.userId = userId;
    }
    
    public Long getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}