package com.dataglove.web.service;

import com.dataglove.web.dto.*;
import com.dataglove.web.entity.Role;
import com.dataglove.web.entity.User;
import com.dataglove.web.repository.RoleRepository;
import com.dataglove.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public PageResponse<UserInfo> getUsers(int page, int size, String keyword, User.UserStatus status, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<User> userPage;
        
        if (StringUtils.hasText(keyword) && status != null) {
            // 同时按关键字和状态过滤
            userPage = userRepository.findByKeyword(keyword, pageable)
                    .map(user -> user.getStatus() == status ? user : null);
        } else if (StringUtils.hasText(keyword)) {
            // 只按关键字过滤
            userPage = userRepository.findByKeyword(keyword, pageable);
        } else if (status != null) {
            // 只按状态过滤
            userPage = userRepository.findByStatus(status, pageable);
        } else {
            // 不过滤
            userPage = userRepository.findAll(pageable);
        }
        
        List<UserInfo> userInfoList = userPage.getContent().stream()
                .map(UserInfo::fromUser)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                userInfoList,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isFirst(),
                userPage.isLast()
        );
    }
    
    public UserInfo getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return UserInfo.fromUser(user);
    }
    
    public UserInfo createUser(RegisterRequest registerRequest) {
        // 验证用户名、邮箱是否已存在
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        if (registerRequest.getPhone() != null && 
            !registerRequest.getPhone().isEmpty() && 
            userRepository.existsByPhone(registerRequest.getPhone())) {
            throw new RuntimeException("手机号已存在");
        }
        
        // 验证密码确认
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new RuntimeException("密码确认不匹配");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setRealName(registerRequest.getRealName());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setStatus(User.UserStatus.ACTIVE);
        
        // 分配默认角色
        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("默认角色不存在"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);
        
        user = userRepository.save(user);
        
        return UserInfo.fromUser(user);
    }
    
    public UserInfo updateUser(Long id, UserUpdateRequest updateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 验证用户名、邮箱是否已被其他用户使用
        if (StringUtils.hasText(updateRequest.getUsername()) && 
            !updateRequest.getUsername().equals(user.getUsername()) &&
            userRepository.existsByUsername(updateRequest.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        if (StringUtils.hasText(updateRequest.getEmail()) && 
            !updateRequest.getEmail().equals(user.getEmail()) &&
            userRepository.existsByEmail(updateRequest.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        if (StringUtils.hasText(updateRequest.getPhone()) && 
            !updateRequest.getPhone().equals(user.getPhone()) &&
            userRepository.existsByPhone(updateRequest.getPhone())) {
            throw new RuntimeException("手机号已存在");
        }
        
        // 更新用户信息
        if (StringUtils.hasText(updateRequest.getUsername())) {
            user.setUsername(updateRequest.getUsername());
        }
        if (StringUtils.hasText(updateRequest.getEmail())) {
            user.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getPhone() != null) {
            user.setPhone(updateRequest.getPhone());
        }
        if (StringUtils.hasText(updateRequest.getRealName())) {
            user.setRealName(updateRequest.getRealName());
        }
        if (StringUtils.hasText(updateRequest.getAvatarUrl())) {
            user.setAvatarUrl(updateRequest.getAvatarUrl());
        }
        if (updateRequest.getStatus() != null) {
            user.setStatus(updateRequest.getStatus());
        }
        
        // 更新角色
        if (updateRequest.getRoles() != null && !updateRequest.getRoles().isEmpty()) {
            Set<Role> newRoles = new HashSet<>();
            for (String roleName : updateRequest.getRoles()) {
                Role role = roleRepository.findByRoleName(roleName)
                        .orElseThrow(() -> new RuntimeException("角色不存在: " + roleName));
                newRoles.add(role);
            }
            user.setRoles(newRoles);
        }
        
        user = userRepository.save(user);
        
        return UserInfo.fromUser(user);
    }
    
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查是否是管理员
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getRoleName()));
        
        if (isAdmin) {
            long adminCount = userRepository.findAll().stream()
                    .mapToLong(u -> u.getRoles().stream()
                            .anyMatch(role -> "ADMIN".equals(role.getRoleName())) ? 1L : 0L)
                    .sum();
            
            if (adminCount <= 1) {
                throw new RuntimeException("不能删除最后一个管理员");
            }
        }
        
        userRepository.delete(user);
    }
    
    public void changePassword(Long userId, PasswordChangeRequest passwordChangeRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 验证原密码
        if (!passwordEncoder.matches(passwordChangeRequest.getOldPassword(), user.getPasswordHash())) {
            throw new RuntimeException("原密码不正确");
        }
        
        // 验证新密码确认
        if (!passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getConfirmPassword())) {
            throw new RuntimeException("新密码确认不匹配");
        }
        
        // 更新密码
        user.setPasswordHash(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));
        userRepository.save(user);
    }
    
    public void resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查是否是管理员
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getRoleName()));
        
        if (isAdmin && user.getStatus() == User.UserStatus.ACTIVE) {
            long activeAdminCount = userRepository.findAll().stream()
                    .mapToLong(u -> u.getRoles().stream()
                            .anyMatch(role -> "ADMIN".equals(role.getRoleName())) && 
                            u.getStatus() == User.UserStatus.ACTIVE ? 1L : 0L)
                    .sum();
            
            if (activeAdminCount <= 1) {
                throw new RuntimeException("不能禁用最后一个活跃管理员");
            }
        }
        
        user.setStatus(user.getStatus() == User.UserStatus.ACTIVE ? 
                User.UserStatus.INACTIVE : User.UserStatus.ACTIVE);
        userRepository.save(user);
    }
    
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}