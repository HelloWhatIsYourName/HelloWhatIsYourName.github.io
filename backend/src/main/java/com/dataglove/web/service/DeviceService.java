package com.dataglove.web.service;

import com.dataglove.web.dto.*;
import com.dataglove.web.entity.Device;
import com.dataglove.web.entity.User;
import com.dataglove.web.entity.UserDevice;
import com.dataglove.web.repository.DeviceRepository;
import com.dataglove.web.repository.UserDeviceRepository;
import com.dataglove.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DeviceService {
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserDeviceRepository userDeviceRepository;
    
    public PageResponse<DeviceInfo> getDevices(int page, int size, String keyword, 
                                              Device.DeviceStatus status, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Device> devicePage;
        
        if (StringUtils.hasText(keyword) && status != null) {
            // 同时按关键字和状态过滤
            devicePage = deviceRepository.findByKeyword(keyword, pageable)
                    .map(device -> device.getStatus() == status ? device : null);
        } else if (StringUtils.hasText(keyword)) {
            // 只按关键字过滤
            devicePage = deviceRepository.findByKeyword(keyword, pageable);
        } else if (status != null) {
            // 只按状态过滤
            devicePage = deviceRepository.findByStatus(status, pageable);
        } else {
            // 不过滤
            devicePage = deviceRepository.findAll(pageable);
        }
        
        List<DeviceInfo> deviceInfoList = devicePage.getContent().stream()
                .map(DeviceInfo::fromDevice)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                deviceInfoList,
                devicePage.getNumber(),
                devicePage.getSize(),
                devicePage.getTotalElements(),
                devicePage.getTotalPages(),
                devicePage.isFirst(),
                devicePage.isLast()
        );
    }
    
    public DeviceInfo getDeviceById(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("设备不存在"));
        return DeviceInfo.fromDevice(device);
    }
    
    public DeviceInfo getDeviceByDeviceId(String deviceId) {
        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("设备不存在"));
        return DeviceInfo.fromDevice(device);
    }
    
    public DeviceInfo createDevice(DeviceCreateRequest createRequest) {
        // 验证设备ID是否已存在
        if (deviceRepository.existsByDeviceId(createRequest.getDeviceId())) {
            throw new RuntimeException("设备ID已存在");
        }
        
        // 验证MAC地址是否已存在
        if (StringUtils.hasText(createRequest.getMacAddress()) && 
            deviceRepository.existsByMacAddress(createRequest.getMacAddress())) {
            throw new RuntimeException("MAC地址已存在");
        }
        
        Device device = new Device();
        device.setDeviceId(createRequest.getDeviceId());
        device.setDeviceName(createRequest.getDeviceName());
        device.setDeviceType(createRequest.getDeviceType());
        device.setHardwareVersion(createRequest.getHardwareVersion());
        device.setFirmwareVersion(createRequest.getFirmwareVersion());
        device.setMacAddress(createRequest.getMacAddress());
        device.setLocation(createRequest.getLocation());
        device.setDescription(createRequest.getDescription());
        device.setStatus(Device.DeviceStatus.OFFLINE);
        
        device = deviceRepository.save(device);
        
        return DeviceInfo.fromDevice(device);
    }
    
    public DeviceInfo updateDevice(Long id, DeviceUpdateRequest updateRequest) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("设备不存在"));
        
        // 验证MAC地址是否已被其他设备使用
        if (StringUtils.hasText(updateRequest.getMacAddress()) && 
            !updateRequest.getMacAddress().equals(device.getMacAddress()) &&
            deviceRepository.existsByMacAddress(updateRequest.getMacAddress())) {
            throw new RuntimeException("MAC地址已存在");
        }
        
        // 更新设备信息
        if (StringUtils.hasText(updateRequest.getDeviceName())) {
            device.setDeviceName(updateRequest.getDeviceName());
        }
        if (updateRequest.getDeviceType() != null) {
            device.setDeviceType(updateRequest.getDeviceType());
        }
        if (StringUtils.hasText(updateRequest.getHardwareVersion())) {
            device.setHardwareVersion(updateRequest.getHardwareVersion());
        }
        if (StringUtils.hasText(updateRequest.getFirmwareVersion())) {
            device.setFirmwareVersion(updateRequest.getFirmwareVersion());
        }
        if (updateRequest.getMacAddress() != null) {
            device.setMacAddress(updateRequest.getMacAddress());
        }
        if (updateRequest.getStatus() != null) {
            device.setStatus(updateRequest.getStatus());
        }
        if (updateRequest.getLocation() != null) {
            device.setLocation(updateRequest.getLocation());
        }
        if (updateRequest.getDescription() != null) {
            device.setDescription(updateRequest.getDescription());
        }
        
        device = deviceRepository.save(device);
        
        return DeviceInfo.fromDevice(device);
    }
    
    public void deleteDevice(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("设备不存在"));
        
        // 检查是否有活跃的绑定关系
        Optional<UserDevice> activeBinding = userDeviceRepository.findActiveBindingByDeviceId(id);
        if (activeBinding.isPresent()) {
            throw new RuntimeException("设备已绑定用户，无法删除");
        }
        
        deviceRepository.delete(device);
    }
    
    public void bindDevice(DeviceBindRequest bindRequest) {
        Device device = deviceRepository.findById(bindRequest.getDeviceId())
                .orElseThrow(() -> new RuntimeException("设备不存在"));
        
        User user = userRepository.findById(bindRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查设备是否已被其他用户绑定
        Optional<UserDevice> existingBinding = userDeviceRepository.findActiveBindingByDeviceId(device.getId());
        if (existingBinding.isPresent()) {
            throw new RuntimeException("设备已被其他用户绑定");
        }
        
        // 检查用户是否已经绑定了该设备
        Optional<UserDevice> userDeviceBinding = userDeviceRepository.findByUserIdAndDeviceId(
                user.getId(), device.getId());
        
        if (userDeviceBinding.isPresent()) {
            UserDevice userDevice = userDeviceBinding.get();
            if (userDevice.getIsActive()) {
                throw new RuntimeException("用户已绑定该设备");
            } else {
                // 重新激活绑定
                userDevice.setIsActive(true);
                userDevice.setBindTime(LocalDateTime.now());
                userDevice.setUnbindTime(null);
                userDeviceRepository.save(userDevice);
            }
        } else {
            // 创建新的绑定关系
            UserDevice userDevice = new UserDevice(user, device);
            userDeviceRepository.save(userDevice);
        }
    }
    
    public void unbindDevice(Long deviceId, Long userId) {
        UserDevice userDevice = userDeviceRepository.findByUserIdAndDeviceId(userId, deviceId)
                .orElseThrow(() -> new RuntimeException("绑定关系不存在"));
        
        if (!userDevice.getIsActive()) {
            throw new RuntimeException("设备未绑定");
        }
        
        userDevice.unbind();
        userDeviceRepository.save(userDevice);
    }
    
    public List<DeviceInfo> getUserDevices(Long userId) {
        List<UserDevice> userDevices = userDeviceRepository.findActiveDevicesByUserId(userId);
        return userDevices.stream()
                .map(ud -> DeviceInfo.fromDevice(ud.getDevice()))
                .collect(Collectors.toList());
    }
    
    public void updateDeviceHeartbeat(String deviceId) {
        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("设备不存在"));
        
        device.setLastHeartbeat(LocalDateTime.now());
        device.setStatus(Device.DeviceStatus.ONLINE);
        deviceRepository.save(device);
    }
    
    public void updateDeviceStatus(String deviceId, Device.DeviceStatus status) {
        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("设备不存在"));
        
        device.setStatus(status);
        if (status == Device.DeviceStatus.ONLINE) {
            device.setLastHeartbeat(LocalDateTime.now());
        }
        deviceRepository.save(device);
    }
    
    public long getDeviceCount() {
        return deviceRepository.count();
    }
    
    public long getOnlineDeviceCount() {
        return deviceRepository.countByStatus(Device.DeviceStatus.ONLINE);
    }
    
    public long getOfflineDeviceCount() {
        return deviceRepository.countByStatus(Device.DeviceStatus.OFFLINE);
    }
    
    public long getActiveBindingCount() {
        return userDeviceRepository.countActiveBindings();
    }
}