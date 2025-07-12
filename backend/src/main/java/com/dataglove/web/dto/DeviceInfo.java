package com.dataglove.web.dto;

import com.dataglove.web.entity.Device;

import java.time.LocalDateTime;

public class DeviceInfo {
    
    private Long id;
    private String deviceId;
    private String deviceName;
    private String deviceType;
    private String hardwareVersion;
    private String firmwareVersion;
    private String macAddress;
    private String status;
    private String location;
    private String description;
    private LocalDateTime lastHeartbeat;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean bound;
    private Long boundUserId;
    private String boundUsername;
    
    public DeviceInfo() {}
    
    public static DeviceInfo fromDevice(Device device) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setId(device.getId());
        deviceInfo.setDeviceId(device.getDeviceId());
        deviceInfo.setDeviceName(device.getDeviceName());
        deviceInfo.setDeviceType(device.getDeviceType().name());
        deviceInfo.setHardwareVersion(device.getHardwareVersion());
        deviceInfo.setFirmwareVersion(device.getFirmwareVersion());
        deviceInfo.setMacAddress(device.getMacAddress());
        deviceInfo.setStatus(device.getStatus().name());
        deviceInfo.setLocation(device.getLocation());
        deviceInfo.setDescription(device.getDescription());
        deviceInfo.setLastHeartbeat(device.getLastHeartbeat());
        deviceInfo.setCreatedAt(device.getCreatedAt());
        deviceInfo.setUpdatedAt(device.getUpdatedAt());
        
        // 检查是否已绑定用户
        device.getUserDevices().stream()
                .filter(ud -> ud.getIsActive())
                .findFirst()
                .ifPresent(ud -> {
                    deviceInfo.setBound(true);
                    deviceInfo.setBoundUserId(ud.getUser().getId());
                    deviceInfo.setBoundUsername(ud.getUser().getUsername());
                });
        
        return deviceInfo;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getDeviceName() {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    
    public String getDeviceType() {
        return deviceType;
    }
    
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    
    public String getHardwareVersion() {
        return hardwareVersion;
    }
    
    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }
    
    public String getFirmwareVersion() {
        return firmwareVersion;
    }
    
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }
    
    public String getMacAddress() {
        return macAddress;
    }
    
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getLastHeartbeat() {
        return lastHeartbeat;
    }
    
    public void setLastHeartbeat(LocalDateTime lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
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
    
    public boolean isBound() {
        return bound;
    }
    
    public void setBound(boolean bound) {
        this.bound = bound;
    }
    
    public Long getBoundUserId() {
        return boundUserId;
    }
    
    public void setBoundUserId(Long boundUserId) {
        this.boundUserId = boundUserId;
    }
    
    public String getBoundUsername() {
        return boundUsername;
    }
    
    public void setBoundUsername(String boundUsername) {
        this.boundUsername = boundUsername;
    }
}