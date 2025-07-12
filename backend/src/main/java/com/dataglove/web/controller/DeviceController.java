package com.dataglove.web.controller;

import com.dataglove.web.common.ApiResponse;
import com.dataglove.web.dto.*;
import com.dataglove.web.entity.Device;
import com.dataglove.web.security.UserPrincipal;
import com.dataglove.web.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/devices")
@Tag(name = "设备管理", description = "设备管理相关接口")
public class DeviceController {
    
    @Autowired
    private DeviceService deviceService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取设备列表", description = "分页获取设备列表，支持关键字搜索和状态过滤")
    public ApiResponse<PageResponse<DeviceInfo>> getDevices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Device.DeviceStatus status,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            PageResponse<DeviceInfo> response = deviceService.getDevices(page, size, keyword, status, sortBy, sortDir);
            return ApiResponse.success("获取设备列表成功", response);
        } catch (Exception e) {
            return ApiResponse.error("获取设备列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取设备详情", description = "根据设备ID获取设备详细信息")
    public ApiResponse<DeviceInfo> getDeviceById(@PathVariable Long id) {
        try {
            DeviceInfo deviceInfo = deviceService.getDeviceById(id);
            return ApiResponse.success("获取设备详情成功", deviceInfo);
        } catch (Exception e) {
            return ApiResponse.error("获取设备详情失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/device-id/{deviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "根据设备ID获取设备", description = "根据设备唯一标识获取设备信息")
    public ApiResponse<DeviceInfo> getDeviceByDeviceId(@PathVariable String deviceId) {
        try {
            DeviceInfo deviceInfo = deviceService.getDeviceByDeviceId(deviceId);
            return ApiResponse.success("获取设备信息成功", deviceInfo);
        } catch (Exception e) {
            return ApiResponse.error("获取设备信息失败: " + e.getMessage());
        }
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建设备", description = "管理员创建新设备")
    public ApiResponse<DeviceInfo> createDevice(@Valid @RequestBody DeviceCreateRequest createRequest) {
        try {
            DeviceInfo deviceInfo = deviceService.createDevice(createRequest);
            return ApiResponse.success("创建设备成功", deviceInfo);
        } catch (Exception e) {
            return ApiResponse.badRequest("创建设备失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新设备信息", description = "更新设备基本信息")
    public ApiResponse<DeviceInfo> updateDevice(@PathVariable Long id, @Valid @RequestBody DeviceUpdateRequest updateRequest) {
        try {
            DeviceInfo deviceInfo = deviceService.updateDevice(id, updateRequest);
            return ApiResponse.success("更新设备信息成功", deviceInfo);
        } catch (Exception e) {
            return ApiResponse.badRequest("更新设备信息失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除设备", description = "管理员删除设备")
    public ApiResponse<Void> deleteDevice(@PathVariable Long id) {
        try {
            deviceService.deleteDevice(id);
            return ApiResponse.successVoid("删除设备成功");
        } catch (Exception e) {
            return ApiResponse.badRequest("删除设备失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/bind")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "绑定设备", description = "管理员绑定设备到用户")
    public ApiResponse<Void> bindDevice(@Valid @RequestBody DeviceBindRequest bindRequest) {
        try {
            deviceService.bindDevice(bindRequest);
            return ApiResponse.successVoid("设备绑定成功");
        } catch (Exception e) {
            return ApiResponse.badRequest("设备绑定失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/unbind")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "解绑设备", description = "管理员解绑用户设备")
    public ApiResponse<Void> unbindDevice(@RequestParam Long deviceId, @RequestParam Long userId) {
        try {
            deviceService.unbindDevice(deviceId, userId);
            return ApiResponse.successVoid("设备解绑成功");
        } catch (Exception e) {
            return ApiResponse.badRequest("设备解绑失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/my-devices")
    @Operation(summary = "获取我的设备", description = "获取当前用户绑定的设备列表")
    public ApiResponse<List<DeviceInfo>> getMyDevices(@AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            List<DeviceInfo> devices = deviceService.getUserDevices(currentUser.getId());
            return ApiResponse.success("获取我的设备成功", devices);
        } catch (Exception e) {
            return ApiResponse.error("获取我的设备失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/my-devices/{deviceId}/unbind")
    @Operation(summary = "解绑我的设备", description = "用户解绑自己的设备")
    public ApiResponse<Void> unbindMyDevice(@PathVariable Long deviceId, @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            deviceService.unbindDevice(deviceId, currentUser.getId());
            return ApiResponse.successVoid("设备解绑成功");
        } catch (Exception e) {
            return ApiResponse.badRequest("设备解绑失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/heartbeat")
    @Operation(summary = "设备心跳", description = "设备发送心跳信号")
    public ApiResponse<Void> deviceHeartbeat(@RequestParam String deviceId) {
        try {
            deviceService.updateDeviceHeartbeat(deviceId);
            return ApiResponse.successVoid("心跳更新成功");
        } catch (Exception e) {
            return ApiResponse.badRequest("心跳更新失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/status")
    @Operation(summary = "更新设备状态", description = "更新设备在线状态")
    public ApiResponse<Void> updateDeviceStatus(@RequestParam String deviceId, @RequestParam Device.DeviceStatus status) {
        try {
            deviceService.updateDeviceStatus(deviceId, status);
            return ApiResponse.successVoid("设备状态更新成功");
        } catch (Exception e) {
            return ApiResponse.badRequest("设备状态更新失败: " + e.getMessage());
        }
    }
}