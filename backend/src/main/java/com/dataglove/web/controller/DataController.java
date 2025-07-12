package com.dataglove.web.controller;

import com.dataglove.web.common.ApiResponse;
import com.dataglove.web.dto.*;
import com.dataglove.web.entity.SensorData;
import com.dataglove.web.security.UserPrincipal;
import com.dataglove.web.service.DataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/data")
@Tag(name = "数据管理", description = "数据管理相关接口")
public class DataController {
    
    @Autowired
    private DataService dataService;
    
    // 传感器数据相关接口
    @GetMapping("/sensor-data")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取传感器数据", description = "分页获取传感器数据，支持多种过滤条件")
    public ApiResponse<PageResponse<SensorDataInfo>> getSensorData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) SensorData.SensorType sensorType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            PageResponse<SensorDataInfo> response = dataService.getSensorData(
                    page, size, deviceId, userId, sensorType, startTime, endTime, sortBy, sortDir);
            return ApiResponse.success("获取传感器数据成功", response);
        } catch (Exception e) {
            return ApiResponse.error("获取传感器数据失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/sensor-data/my")
    @Operation(summary = "获取我的传感器数据", description = "获取当前用户的传感器数据")
    public ApiResponse<PageResponse<SensorDataInfo>> getMySensorData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) SensorData.SensorType sensorType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            PageResponse<SensorDataInfo> response = dataService.getSensorData(
                    page, size, deviceId, currentUser.getId(), sensorType, startTime, endTime, sortBy, sortDir);
            return ApiResponse.success("获取我的传感器数据成功", response);
        } catch (Exception e) {
            return ApiResponse.error("获取我的传感器数据失败: " + e.getMessage());
        }
    }
    
    // 手势识别结果相关接口
    @GetMapping("/gesture-results")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取手势识别结果", description = "分页获取手势识别结果，支持多种过滤条件")
    public ApiResponse<PageResponse<GestureResultInfo>> getGestureResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String gestureName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "recognitionTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            PageResponse<GestureResultInfo> response = dataService.getGestureResults(
                    page, size, deviceId, userId, gestureName, startTime, endTime, sortBy, sortDir);
            return ApiResponse.success("获取手势识别结果成功", response);
        } catch (Exception e) {
            return ApiResponse.error("获取手势识别结果失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/gesture-results/my")
    @Operation(summary = "获取我的手势识别结果", description = "获取当前用户的手势识别结果")
    public ApiResponse<PageResponse<GestureResultInfo>> getMyGestureResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) String gestureName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "recognitionTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            PageResponse<GestureResultInfo> response = dataService.getGestureResults(
                    page, size, deviceId, currentUser.getId(), gestureName, startTime, endTime, sortBy, sortDir);
            return ApiResponse.success("获取我的手势识别结果成功", response);
        } catch (Exception e) {
            return ApiResponse.error("获取我的手势识别结果失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/gesture-results/recent")
    @Operation(summary = "获取最近的手势识别结果", description = "获取当前用户最近的手势识别结果")
    public ApiResponse<List<GestureResultInfo>> getRecentGestureResults(
            @RequestParam(defaultValue = "10") int limit,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            List<GestureResultInfo> results = dataService.getRecentGestureResults(currentUser.getId(), limit);
            return ApiResponse.success("获取最近手势识别结果成功", results);
        } catch (Exception e) {
            return ApiResponse.error("获取最近手势识别结果失败: " + e.getMessage());
        }
    }
    
    // 学习记录相关接口
    @GetMapping("/learning-records")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取学习记录", description = "分页获取学习记录")
    public ApiResponse<PageResponse<LearningRecordInfo>> getLearningRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String gestureName,
            @RequestParam(defaultValue = "lastPracticeTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            PageResponse<LearningRecordInfo> response = dataService.getLearningRecords(
                    page, size, userId, gestureName, sortBy, sortDir);
            return ApiResponse.success("获取学习记录成功", response);
        } catch (Exception e) {
            return ApiResponse.error("获取学习记录失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/learning-records/my")
    @Operation(summary = "获取我的学习记录", description = "获取当前用户的学习记录")
    public ApiResponse<PageResponse<LearningRecordInfo>> getMyLearningRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String gestureName,
            @RequestParam(defaultValue = "lastPracticeTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            PageResponse<LearningRecordInfo> response = dataService.getLearningRecords(
                    page, size, currentUser.getId(), gestureName, sortBy, sortDir);
            return ApiResponse.success("获取我的学习记录成功", response);
        } catch (Exception e) {
            return ApiResponse.error("获取我的学习记录失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/learning-records/top-practiced")
    @Operation(summary = "获取最常练习的手势", description = "获取当前用户最常练习的手势")
    public ApiResponse<List<LearningRecordInfo>> getTopPracticedGestures(
            @RequestParam(defaultValue = "5") int limit,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            List<LearningRecordInfo> records = dataService.getTopPracticedGestures(currentUser.getId(), limit);
            return ApiResponse.success("获取最常练习手势成功", records);
        } catch (Exception e) {
            return ApiResponse.error("获取最常练习手势失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/learning-records/top-performing")
    @Operation(summary = "获取表现最好的手势", description = "获取当前用户表现最好的手势")
    public ApiResponse<List<LearningRecordInfo>> getTopPerformingGestures(
            @RequestParam(defaultValue = "5") int limit,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            List<LearningRecordInfo> records = dataService.getTopPerformingGestures(currentUser.getId(), limit);
            return ApiResponse.success("获取表现最好手势成功", records);
        } catch (Exception e) {
            return ApiResponse.error("获取表现最好手势失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/gestures/my")
    @Operation(summary = "获取我的手势列表", description = "获取当前用户练习过的所有手势")
    public ApiResponse<List<String>> getMyGestures(@AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            List<String> gestures = dataService.getUserGestures(currentUser.getId());
            return ApiResponse.success("获取我的手势列表成功", gestures);
        } catch (Exception e) {
            return ApiResponse.error("获取我的手势列表失败: " + e.getMessage());
        }
    }
    
    // 数据统计相关接口
    @GetMapping("/statistics/overview")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取数据概览统计", description = "获取系统整体数据统计")
    public ApiResponse<DataStatistics> getDataStatistics() {
        try {
            DataStatistics statistics = new DataStatistics();
            statistics.setTotalSensorDataCount(dataService.getTotalSensorDataCount());
            statistics.setTotalGestureResultCount(dataService.getTotalGestureResultCount());
            statistics.setTodayDataCount(dataService.getTodayDataCount());
            return ApiResponse.success("获取数据统计成功", statistics);
        } catch (Exception e) {
            return ApiResponse.error("获取数据统计失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/statistics/my")
    @Operation(summary = "获取我的数据统计", description = "获取当前用户的数据统计")
    public ApiResponse<UserDataStatistics> getMyDataStatistics(@AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            UserDataStatistics statistics = new UserDataStatistics();
            statistics.setUserId(currentUser.getId());
            statistics.setUsername(currentUser.getUsername());
            statistics.setSensorDataCount(dataService.getUserSensorDataCount(currentUser.getId()));
            statistics.setGestureResultCount(dataService.getUserGestureResultCount(currentUser.getId()));
            statistics.setTotalPracticeCount(dataService.getUserTotalPracticeCount(currentUser.getId()));
            statistics.setTotalSuccessCount(dataService.getUserTotalSuccessCount(currentUser.getId()));
            statistics.setOverallSuccessRate(dataService.getUserOverallSuccessRate(currentUser.getId()));
            return ApiResponse.success("获取我的数据统计成功", statistics);
        } catch (Exception e) {
            return ApiResponse.error("获取我的数据统计失败: " + e.getMessage());
        }
    }
    
    // 数据清理接口
    @DeleteMapping("/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "清理历史数据", description = "清理指定天数之前的历史数据")
    public ApiResponse<Void> cleanupOldData(@RequestParam(defaultValue = "30") int daysToKeep) {
        try {
            dataService.cleanOldData(daysToKeep);
            return ApiResponse.successVoid("历史数据清理成功");
        } catch (Exception e) {
            return ApiResponse.error("历史数据清理失败: " + e.getMessage());
        }
    }
    
    // 数据统计DTO内部类
    public static class DataStatistics {
        private long totalSensorDataCount;
        private long totalGestureResultCount;
        private long todayDataCount;
        
        public long getTotalSensorDataCount() {
            return totalSensorDataCount;
        }
        
        public void setTotalSensorDataCount(long totalSensorDataCount) {
            this.totalSensorDataCount = totalSensorDataCount;
        }
        
        public long getTotalGestureResultCount() {
            return totalGestureResultCount;
        }
        
        public void setTotalGestureResultCount(long totalGestureResultCount) {
            this.totalGestureResultCount = totalGestureResultCount;
        }
        
        public long getTodayDataCount() {
            return todayDataCount;
        }
        
        public void setTodayDataCount(long todayDataCount) {
            this.todayDataCount = todayDataCount;
        }
    }
    
    public static class UserDataStatistics {
        private Long userId;
        private String username;
        private long sensorDataCount;
        private long gestureResultCount;
        private long totalPracticeCount;
        private long totalSuccessCount;
        private double overallSuccessRate;
        
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public long getSensorDataCount() {
            return sensorDataCount;
        }
        
        public void setSensorDataCount(long sensorDataCount) {
            this.sensorDataCount = sensorDataCount;
        }
        
        public long getGestureResultCount() {
            return gestureResultCount;
        }
        
        public void setGestureResultCount(long gestureResultCount) {
            this.gestureResultCount = gestureResultCount;
        }
        
        public long getTotalPracticeCount() {
            return totalPracticeCount;
        }
        
        public void setTotalPracticeCount(long totalPracticeCount) {
            this.totalPracticeCount = totalPracticeCount;
        }
        
        public long getTotalSuccessCount() {
            return totalSuccessCount;
        }
        
        public void setTotalSuccessCount(long totalSuccessCount) {
            this.totalSuccessCount = totalSuccessCount;
        }
        
        public double getOverallSuccessRate() {
            return overallSuccessRate;
        }
        
        public void setOverallSuccessRate(double overallSuccessRate) {
            this.overallSuccessRate = overallSuccessRate;
        }
    }
}