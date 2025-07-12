package com.dataglove.web.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "devices")
public class Device extends BaseEntity {
    
    @NotBlank(message = "设备ID不能为空")
    @Column(name = "device_id", unique = true, nullable = false, length = 100)
    private String deviceId;
    
    @NotBlank(message = "设备名称不能为空")
    @Size(min = 2, max = 100, message = "设备名称长度必须在2-100个字符之间")
    @Column(name = "device_name", nullable = false, length = 100)
    private String deviceName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false)
    private DeviceType deviceType = DeviceType.DATA_GLOVE;
    
    @Column(name = "hardware_version", length = 50)
    private String hardwareVersion;
    
    @Column(name = "firmware_version", length = 50)
    private String firmwareVersion;
    
    @Column(name = "mac_address", length = 17)
    private String macAddress;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeviceStatus status = DeviceStatus.OFFLINE;
    
    @Column(name = "location", length = 200)
    private String location;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "last_heartbeat")
    private LocalDateTime lastHeartbeat;
    
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserDevice> userDevices = new HashSet<>();
    
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SensorData> sensorData = new HashSet<>();
    
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<GestureResult> gestureResults = new HashSet<>();
    
    public enum DeviceType {
        DATA_GLOVE
    }
    
    public enum DeviceStatus {
        ONLINE, OFFLINE, MAINTENANCE
    }
    
    public Device() {}
    
    public Device(String deviceId, String deviceName) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
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
    
    public DeviceType getDeviceType() {
        return deviceType;
    }
    
    public void setDeviceType(DeviceType deviceType) {
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
    
    public DeviceStatus getStatus() {
        return status;
    }
    
    public void setStatus(DeviceStatus status) {
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
    
    public Set<UserDevice> getUserDevices() {
        return userDevices;
    }
    
    public void setUserDevices(Set<UserDevice> userDevices) {
        this.userDevices = userDevices;
    }
    
    public Set<SensorData> getSensorData() {
        return sensorData;
    }
    
    public void setSensorData(Set<SensorData> sensorData) {
        this.sensorData = sensorData;
    }
    
    public Set<GestureResult> getGestureResults() {
        return gestureResults;
    }
    
    public void setGestureResults(Set<GestureResult> gestureResults) {
        this.gestureResults = gestureResults;
    }
}