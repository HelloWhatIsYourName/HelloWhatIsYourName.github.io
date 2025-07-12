package com.dataglove.web.service;

import com.dataglove.web.dto.*;
import com.dataglove.web.entity.*;
import com.dataglove.web.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DataService {
    
    @Autowired
    private SensorDataRepository sensorDataRepository;
    
    @Autowired
    private GestureResultRepository gestureResultRepository;
    
    @Autowired
    private LearningRecordRepository learningRecordRepository;
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private static final BigDecimal SUCCESS_THRESHOLD = new BigDecimal("0.8");
    
    // 传感器数据相关方法
    public PageResponse<SensorDataInfo> getSensorData(int page, int size, Long deviceId, Long userId, 
                                                      SensorData.SensorType sensorType, 
                                                      LocalDateTime startTime, LocalDateTime endTime,
                                                      String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<SensorData> sensorDataPage;
        
        if (deviceId != null && startTime != null && endTime != null) {
            sensorDataPage = sensorDataRepository.findByDeviceIdAndTimestampBetween(deviceId, startTime, endTime, pageable);
        } else if (userId != null && startTime != null && endTime != null) {
            sensorDataPage = sensorDataRepository.findByUserIdAndTimestampBetween(userId, startTime, endTime, pageable);
        } else if (deviceId != null) {
            sensorDataPage = sensorDataRepository.findByDeviceId(deviceId, pageable);
        } else if (userId != null) {
            sensorDataPage = sensorDataRepository.findByUserId(userId, pageable);
        } else if (sensorType != null) {
            sensorDataPage = sensorDataRepository.findBySensorType(sensorType, pageable);
        } else {
            sensorDataPage = sensorDataRepository.findAll(pageable);
        }
        
        List<SensorDataInfo> sensorDataInfoList = sensorDataPage.getContent().stream()
                .map(SensorDataInfo::fromSensorData)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                sensorDataInfoList,
                sensorDataPage.getNumber(),
                sensorDataPage.getSize(),
                sensorDataPage.getTotalElements(),
                sensorDataPage.getTotalPages(),
                sensorDataPage.isFirst(),
                sensorDataPage.isLast()
        );
    }
    
    // 手势识别结果相关方法
    public PageResponse<GestureResultInfo> getGestureResults(int page, int size, Long deviceId, Long userId, 
                                                            String gestureName, LocalDateTime startTime, LocalDateTime endTime,
                                                            String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<GestureResult> gestureResultPage;
        
        if (startTime != null && endTime != null) {
            gestureResultPage = gestureResultRepository.findByRecognitionTimeBetween(startTime, endTime, pageable);
        } else if (deviceId != null) {
            gestureResultPage = gestureResultRepository.findByDeviceId(deviceId, pageable);
        } else if (userId != null && gestureName != null) {
            gestureResultPage = gestureResultRepository.findByUserIdAndGestureName(userId, gestureName, pageable);
        } else if (userId != null) {
            gestureResultPage = gestureResultRepository.findByUserId(userId, pageable);
        } else if (gestureName != null) {
            gestureResultPage = gestureResultRepository.findByGestureName(gestureName, pageable);
        } else {
            gestureResultPage = gestureResultRepository.findAll(pageable);
        }
        
        List<GestureResultInfo> gestureResultInfoList = gestureResultPage.getContent().stream()
                .map(GestureResultInfo::fromGestureResult)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                gestureResultInfoList,
                gestureResultPage.getNumber(),
                gestureResultPage.getSize(),
                gestureResultPage.getTotalElements(),
                gestureResultPage.getTotalPages(),
                gestureResultPage.isFirst(),
                gestureResultPage.isLast()
        );
    }
    
    // 学习记录相关方法
    public PageResponse<LearningRecordInfo> getLearningRecords(int page, int size, Long userId, String gestureName,
                                                              String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<LearningRecord> learningRecordPage;
        
        if (userId != null) {
            learningRecordPage = learningRecordRepository.findByUserId(userId, pageable);
        } else if (gestureName != null) {
            learningRecordPage = learningRecordRepository.findByGestureName(gestureName, pageable);
        } else {
            learningRecordPage = learningRecordRepository.findAll(pageable);
        }
        
        List<LearningRecordInfo> learningRecordInfoList = learningRecordPage.getContent().stream()
                .map(LearningRecordInfo::fromLearningRecord)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                learningRecordInfoList,
                learningRecordPage.getNumber(),
                learningRecordPage.getSize(),
                learningRecordPage.getTotalElements(),
                learningRecordPage.getTotalPages(),
                learningRecordPage.isFirst(),
                learningRecordPage.isLast()
        );
    }
    
    public List<LearningRecordInfo> getTopPracticedGestures(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<LearningRecord> records = learningRecordRepository.findTopPracticedByUserId(userId, pageable);
        return records.stream()
                .map(LearningRecordInfo::fromLearningRecord)
                .collect(Collectors.toList());
    }
    
    public List<LearningRecordInfo> getTopPerformingGestures(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<LearningRecord> records = learningRecordRepository.findTopPerformingByUserId(userId, pageable);
        return records.stream()
                .map(LearningRecordInfo::fromLearningRecord)
                .collect(Collectors.toList());
    }
    
    public List<String> getUserGestures(Long userId) {
        return gestureResultRepository.findDistinctGestureNamesByUserId(userId);
    }
    
    public List<GestureResultInfo> getRecentGestureResults(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<GestureResult> results = gestureResultRepository.findLatestByUserId(userId, pageable);
        return results.stream()
                .map(GestureResultInfo::fromGestureResult)
                .collect(Collectors.toList());
    }
    
    // 处理新的手势识别结果
    public void processGestureResult(GestureResult gestureResult) {
        // 更新学习记录
        updateLearningRecord(gestureResult);
        
        // 保存手势识别结果
        gestureResultRepository.save(gestureResult);
    }
    
    private void updateLearningRecord(GestureResult gestureResult) {
        User user = gestureResult.getUser();
        String gestureName = gestureResult.getGestureName();
        
        Optional<LearningRecord> existingRecord = learningRecordRepository
                .findByUserIdAndGestureName(user.getId(), gestureName);
        
        LearningRecord record;
        if (existingRecord.isPresent()) {
            record = existingRecord.get();
        } else {
            record = new LearningRecord(user, gestureName);
        }
        
        record.incrementPracticeCount();
        record.setLastPracticeTime(LocalDateTime.now());
        
        // 判断是否成功（置信度大于阈值）
        if (gestureResult.getConfidence().compareTo(SUCCESS_THRESHOLD) >= 0) {
            record.incrementSuccessCount();
        }
        
        // 更新平均置信度
        BigDecimal currentAverage = gestureResultRepository
                .getAverageConfidenceByUserIdAndGestureName(user.getId(), gestureName);
        record.setAverageConfidence(currentAverage);
        
        learningRecordRepository.save(record);
    }
    
    // 数据统计相关方法
    public long getTotalSensorDataCount() {
        return sensorDataRepository.count();
    }
    
    public long getTotalGestureResultCount() {
        return gestureResultRepository.count();
    }
    
    public long getTodayDataCount() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        return sensorDataRepository.countByTimestampAfter(startOfDay);
    }
    
    public long getUserSensorDataCount(Long userId) {
        return sensorDataRepository.countByUserId(userId);
    }
    
    public long getUserGestureResultCount(Long userId) {
        return gestureResultRepository.countByUserId(userId);
    }
    
    public long getUserTotalPracticeCount(Long userId) {
        Long count = learningRecordRepository.getTotalPracticeCountByUserId(userId);
        return count != null ? count : 0L;
    }
    
    public long getUserTotalSuccessCount(Long userId) {
        Long count = learningRecordRepository.getTotalSuccessCountByUserId(userId);
        return count != null ? count : 0L;
    }
    
    public double getUserOverallSuccessRate(Long userId) {
        long totalPractice = getUserTotalPracticeCount(userId);
        long totalSuccess = getUserTotalSuccessCount(userId);
        
        if (totalPractice == 0) return 0.0;
        return (double) totalSuccess / totalPractice;
    }
    
    // 数据清理相关方法
    public void cleanOldData(int daysToKeep) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(daysToKeep);
        
        List<SensorData> oldSensorData = sensorDataRepository.findOldData(cutoffTime);
        if (!oldSensorData.isEmpty()) {
            sensorDataRepository.deleteAll(oldSensorData);
        }
    }
}