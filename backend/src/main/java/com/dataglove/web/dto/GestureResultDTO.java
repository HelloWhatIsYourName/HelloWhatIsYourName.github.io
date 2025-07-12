package com.dataglove.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class GestureResultDTO {
    
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;
    
    @NotBlank(message = "手势名称不能为空")
    private String gestureName;
    
    @NotNull(message = "置信度不能为空")
    private BigDecimal confidence;
    
    private Map<String, Object> rawData;
    
    private Map<String, Object> processedData;
    
    private LocalDateTime recognitionTime;
    
    public GestureResultDTO() {}
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getGestureName() {
        return gestureName;
    }
    
    public void setGestureName(String gestureName) {
        this.gestureName = gestureName;
    }
    
    public BigDecimal getConfidence() {
        return confidence;
    }
    
    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }
    
    public Map<String, Object> getRawData() {
        return rawData;
    }
    
    public void setRawData(Map<String, Object> rawData) {
        this.rawData = rawData;
    }
    
    public Map<String, Object> getProcessedData() {
        return processedData;
    }
    
    public void setProcessedData(Map<String, Object> processedData) {
        this.processedData = processedData;
    }
    
    public LocalDateTime getRecognitionTime() {
        return recognitionTime;
    }
    
    public void setRecognitionTime(LocalDateTime recognitionTime) {
        this.recognitionTime = recognitionTime;
    }
}