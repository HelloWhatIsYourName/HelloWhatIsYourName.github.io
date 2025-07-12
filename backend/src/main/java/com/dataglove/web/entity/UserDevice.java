package com.dataglove.web.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_devices")
public class UserDevice extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
    
    @Column(name = "bind_time", nullable = false)
    private LocalDateTime bindTime;
    
    @Column(name = "unbind_time")
    private LocalDateTime unbindTime;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    public UserDevice() {}
    
    public UserDevice(User user, Device device) {
        this.user = user;
        this.device = device;
        this.bindTime = LocalDateTime.now();
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Device getDevice() {
        return device;
    }
    
    public void setDevice(Device device) {
        this.device = device;
    }
    
    public LocalDateTime getBindTime() {
        return bindTime;
    }
    
    public void setBindTime(LocalDateTime bindTime) {
        this.bindTime = bindTime;
    }
    
    public LocalDateTime getUnbindTime() {
        return unbindTime;
    }
    
    public void setUnbindTime(LocalDateTime unbindTime) {
        this.unbindTime = unbindTime;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public void unbind() {
        this.isActive = false;
        this.unbindTime = LocalDateTime.now();
    }
}