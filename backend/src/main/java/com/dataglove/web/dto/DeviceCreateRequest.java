package com.dataglove.web.dto;

import com.dataglove.web.entity.Device;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DeviceCreateRequest {
    
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;
    
    @NotBlank(message = "设备名称不能为空")
    @Size(min = 2, max = 100, message = "设备名称长度必须在2-100个字符之间")
    private String deviceName;
    
    private Device.DeviceType deviceType = Device.DeviceType.DATA_GLOVE;
    
    private String hardwareVersion;
    
    private String firmwareVersion;
    
    private String macAddress;
    
    private String location;
    
    private String description;
    
    public DeviceCreateRequest() {}
    
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
    
    public Device.DeviceType getDeviceType() {
        return deviceType;
    }
    
    public void setDeviceType(Device.DeviceType deviceType) {
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
}