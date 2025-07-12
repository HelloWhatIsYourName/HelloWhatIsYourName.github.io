package com.dataglove.web.dto;

import com.dataglove.web.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class UserInfo {
    
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String realName;
    private String avatarUrl;
    private String status;
    private List<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    
    public UserInfo() {}
    
    public static UserInfo fromUser(User user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setPhone(user.getPhone());
        userInfo.setRealName(user.getRealName());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        userInfo.setStatus(user.getStatus().name());
        userInfo.setRoles(user.getRoles().stream()
                .map(role -> role.getRoleName())
                .collect(Collectors.toList()));
        userInfo.setCreatedAt(user.getCreatedAt());
        userInfo.setLastLoginAt(user.getLastLoginAt());
        return userInfo;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<String> getRoles() {
        return roles;
    }
    
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }
    
    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}