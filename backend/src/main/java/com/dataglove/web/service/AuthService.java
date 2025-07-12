package com.dataglove.web.service;

import com.dataglove.web.common.JwtUtil;
import com.dataglove.web.dto.*;
import com.dataglove.web.entity.Role;
import com.dataglove.web.entity.User;
import com.dataglove.web.repository.RoleRepository;
import com.dataglove.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String accessToken = jwtUtil.generateToken(authentication);
        String refreshToken = jwtUtil.generateRefreshToken(loginRequest.getUsername());
        
        // 更新最后登录时间
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        
        UserInfo userInfo = UserInfo.fromUser(user);
        
        return new AuthResponse(accessToken, refreshToken, userInfo);
    }
    
    public AuthResponse register(RegisterRequest registerRequest) {
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
        
        // 自动登录
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getUsername(),
                        registerRequest.getPassword()
                )
        );
        
        String accessToken = jwtUtil.generateToken(authentication);
        String refreshToken = jwtUtil.generateRefreshToken(registerRequest.getUsername());
        
        UserInfo userInfo = UserInfo.fromUser(user);
        
        return new AuthResponse(accessToken, refreshToken, userInfo);
    }
    
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new RuntimeException("无效的刷新令牌");
        }
        
        String username = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        String newAccessToken = jwtUtil.generateToken(user.getUsername());
        String newRefreshToken = jwtUtil.generateRefreshToken(username);
        
        UserInfo userInfo = UserInfo.fromUser(user);
        
        return new AuthResponse(newAccessToken, newRefreshToken, userInfo);
    }
}