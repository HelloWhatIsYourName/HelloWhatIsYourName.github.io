package com.dataglove.web.controller;

import com.dataglove.web.common.ApiResponse;
import com.dataglove.web.dto.*;
import com.dataglove.web.entity.*;
import com.dataglove.web.repository.*;
import com.dataglove.web.service.DataService;
import com.dataglove.web.service.DeviceService;
import org.springframework.security.crypto.password.PasswordEncoder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/v1/integration")
@Tag(name = "外部集成", description = "与外部系统集成的接口")
public class IntegrationController {
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private UserDeviceRepository userDeviceRepository;
    
    @Autowired
    private SensorDataRepository sensorDataRepository;
    
    @Autowired
    private GestureResultRepository gestureResultRepository;
    
    @Autowired
    private DeviceService deviceService;
    
    @Autowired
    private DataService dataService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;
    
    // IoT Hub 集成接口
    @PostMapping("/iot/device-data")
    @Operation(summary = "接收设备数据", description = "接收来自IoT Hub的设备数据")
    public ApiResponse<Void> receiveDeviceData(@Valid @RequestBody DeviceDataDTO data) {
        try {
            // 查找设备
            Device device = deviceRepository.findByDeviceId(data.getDeviceId())
                    .orElseThrow(() -> new RuntimeException("设备不存在: " + data.getDeviceId()));
            
            // 查找绑定的用户
            Optional<UserDevice> userDevice = userDeviceRepository.findActiveBindingByDeviceId(device.getId());
            if (!userDevice.isPresent()) {
                return ApiResponse.badRequest("设备未绑定用户");
            }
            
            // 创建传感器数据
            SensorData sensorData = new SensorData();
            sensorData.setDevice(device);
            sensorData.setUser(userDevice.get().getUser());
            sensorData.setSensorType(data.getSensorType());
            sensorData.setSensorPosition(data.getSensorPosition());
            sensorData.setDataValue(data.getDataValue());
            sensorData.setTimestamp(data.getTimestamp() != null ? data.getTimestamp() : LocalDateTime.now());
            
            // 保存数据
            sensorDataRepository.save(sensorData);
            
            // 更新设备心跳
            deviceService.updateDeviceHeartbeat(data.getDeviceId());
            
            return ApiResponse.successVoid("设备数据接收成功");
        } catch (Exception e) {
            return ApiResponse.error("设备数据接收失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/iot/device-status")
    @Operation(summary = "更新设备状态", description = "接收来自IoT Hub的设备状态更新")
    public ApiResponse<Void> updateDeviceStatus(@Valid @RequestBody DeviceStatusDTO status) {
        try {
            deviceService.updateDeviceStatus(status.getDeviceId(), status.getStatus());
            return ApiResponse.successVoid("设备状态更新成功");
        } catch (Exception e) {
            return ApiResponse.error("设备状态更新失败: " + e.getMessage());
        }
    }
    
    // AI服务集成接口
    @PostMapping("/ai/gesture-result")
    @Operation(summary = "接收手势识别结果", description = "接收来自AI服务的手势识别结果")
    public ApiResponse<Void> receiveGestureResult(@Valid @RequestBody GestureResultDTO result) {
        try {
            // 查找设备
            Device device = deviceRepository.findByDeviceId(result.getDeviceId())
                    .orElseThrow(() -> new RuntimeException("设备不存在: " + result.getDeviceId()));
            
            // 查找绑定的用户
            Optional<UserDevice> userDevice = userDeviceRepository.findActiveBindingByDeviceId(device.getId());
            if (!userDevice.isPresent()) {
                return ApiResponse.badRequest("设备未绑定用户");
            }
            
            // 创建手势识别结果
            GestureResult gestureResult = new GestureResult();
            gestureResult.setDevice(device);
            gestureResult.setUser(userDevice.get().getUser());
            gestureResult.setGestureName(result.getGestureName());
            gestureResult.setConfidence(result.getConfidence());
            gestureResult.setRawData(result.getRawData());
            gestureResult.setProcessedData(result.getProcessedData());
            gestureResult.setRecognitionTime(result.getRecognitionTime() != null ? 
                    result.getRecognitionTime() : LocalDateTime.now());
            
            // 处理手势识别结果（更新学习记录）
            dataService.processGestureResult(gestureResult);
            
            return ApiResponse.successVoid("手势识别结果接收成功");
        } catch (Exception e) {
            return ApiResponse.error("手势识别结果接收失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/ai/training-status/{taskId}")
    @Operation(summary = "获取训练状态", description = "获取AI模型训练状态")
    public ApiResponse<TrainingStatusDTO> getTrainingStatus(@PathVariable String taskId) {
        try {
            // 这里应该调用AI服务获取训练状态
            // 目前返回模拟数据
            TrainingStatusDTO status = new TrainingStatusDTO();
            status.setTaskId(taskId);
            status.setStatus("COMPLETED");
            status.setProgress(100.0);
            status.setMessage("训练完成");
            
            return ApiResponse.success("获取训练状态成功", status);
        } catch (Exception e) {
            return ApiResponse.error("获取训练状态失败: " + e.getMessage());
        }
    }
    
    // Unity客户端集成接口
    @PostMapping("/mobile/login")
    @Operation(summary = "移动端登录", description = "Unity客户端登录接口")
    public ApiResponse<AuthResponse> mobileLogin(@Valid @RequestBody MobileLoginDTO loginDTO) {
        try {
            // 这里可以复用现有的认证逻辑
            // 或者为移动端提供特殊的认证方式
            return ApiResponse.error("移动端登录功能待实现");
        } catch (Exception e) {
            return ApiResponse.error("移动端登录失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/mobile/user/sync-data")
    @Operation(summary = "用户数据同步", description = "Unity客户端用户数据同步接口")
    public ApiResponse<UserSyncDataDTO> syncUserData(@RequestParam String userId) {
        try {
            // 这里应该返回用户的同步数据
            UserSyncDataDTO syncData = new UserSyncDataDTO();
            syncData.setUserId(userId);
            syncData.setLastSyncTime(LocalDateTime.now());
            
            return ApiResponse.success("用户数据同步成功", syncData);
        } catch (Exception e) {
            return ApiResponse.error("用户数据同步失败: " + e.getMessage());
        }
    }
    
    // 临时初始化接口 - 仅用于设置管理员密码
    @PostMapping("/admin/init-password")
    @Operation(summary = "初始化管理员密码", description = "临时接口：用于初始化管理员密码")
    public ApiResponse<String> initAdminPassword(@RequestParam String newPassword) {
        try {
            User admin = userRepository.findByUsername("admin")
                    .orElseThrow(() -> new RuntimeException("管理员用户不存在"));
            
            String hashedPassword = passwordEncoder.encode(newPassword);
            admin.setPasswordHash(hashedPassword);
            userRepository.save(admin);
            
            return ApiResponse.success("管理员密码初始化成功", "密码哈希: " + hashedPassword);
        } catch (Exception e) {
            return ApiResponse.error("管理员密码初始化失败: " + e.getMessage());
        }
    }
    
    // 辅助DTO类
    public static class TrainingStatusDTO {
        private String taskId;
        private String status;
        private Double progress;
        private String message;
        
        public String getTaskId() {
            return taskId;
        }
        
        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public Double getProgress() {
            return progress;
        }
        
        public void setProgress(Double progress) {
            this.progress = progress;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    public static class MobileLoginDTO {
        private String username;
        private String password;
        private String deviceInfo;
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public String getDeviceInfo() {
            return deviceInfo;
        }
        
        public void setDeviceInfo(String deviceInfo) {
            this.deviceInfo = deviceInfo;
        }
    }
    
    public static class UserSyncDataDTO {
        private String userId;
        private LocalDateTime lastSyncTime;
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public LocalDateTime getLastSyncTime() {
            return lastSyncTime;
        }
        
        public void setLastSyncTime(LocalDateTime lastSyncTime) {
            this.lastSyncTime = lastSyncTime;
        }
    }
}