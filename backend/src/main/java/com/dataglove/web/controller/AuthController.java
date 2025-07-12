package com.dataglove.web.controller;

import com.dataglove.web.common.ApiResponse;
import com.dataglove.web.dto.*;
import com.dataglove.web.security.UserPrincipal;
import com.dataglove.web.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户使用用户名和密码登录")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.login(loginRequest);
            return ApiResponse.success("登录成功", response);
        } catch (Exception e) {
            return ApiResponse.badRequest("登录失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            AuthResponse response = authService.register(registerRequest);
            return ApiResponse.success("注册成功", response);
        } catch (Exception e) {
            return ApiResponse.badRequest("注册失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    public ApiResponse<AuthResponse> refreshToken(@RequestParam String refreshToken) {
        try {
            AuthResponse response = authService.refreshToken(refreshToken);
            return ApiResponse.success("令牌刷新成功", response);
        } catch (Exception e) {
            return ApiResponse.badRequest("令牌刷新失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public ApiResponse<UserInfo> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            // 这里可以从数据库重新获取最新的用户信息
            UserInfo userInfo = new UserInfo();
            userInfo.setId(currentUser.getId());
            userInfo.setUsername(currentUser.getUsername());
            userInfo.setEmail(currentUser.getEmail());
            return ApiResponse.success("获取用户信息成功", userInfo);
        } catch (Exception e) {
            return ApiResponse.error("获取用户信息失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出（客户端需要清除本地令牌）")
    public ApiResponse<Void> logout() {
        // 在实际应用中，可以将令牌加入黑名单
        return ApiResponse.successVoid("登出成功");
    }
}