package com.dataglove.web.controller;

import com.dataglove.web.common.ApiResponse;
import com.dataglove.web.dto.*;
import com.dataglove.web.entity.Role;
import com.dataglove.web.entity.User;
import com.dataglove.web.security.UserPrincipal;
import com.dataglove.web.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
@Tag(name = "用户管理", description = "用户管理相关接口")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取用户列表", description = "分页获取用户列表，支持关键字搜索和状态过滤")
    public ApiResponse<PageResponse<UserInfo>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) User.UserStatus status,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            PageResponse<UserInfo> response = userService.getUsers(page, size, keyword, status, sortBy, sortDir);
            return ApiResponse.success("获取用户列表成功", response);
        } catch (Exception e) {
            return ApiResponse.error("获取用户列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    public ApiResponse<UserInfo> getUserById(@PathVariable Long id) {
        try {
            UserInfo userInfo = userService.getUserById(id);
            return ApiResponse.success("获取用户详情成功", userInfo);
        } catch (Exception e) {
            return ApiResponse.error("获取用户详情失败: " + e.getMessage());
        }
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建用户", description = "管理员创建新用户")
    public ApiResponse<UserInfo> createUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            UserInfo userInfo = userService.createUser(registerRequest);
            return ApiResponse.success("创建用户成功", userInfo);
        } catch (Exception e) {
            return ApiResponse.badRequest("创建用户失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(summary = "更新用户信息", description = "更新用户基本信息")
    public ApiResponse<UserInfo> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest updateRequest) {
        try {
            UserInfo userInfo = userService.updateUser(id, updateRequest);
            return ApiResponse.success("更新用户信息成功", userInfo);
        } catch (Exception e) {
            return ApiResponse.badRequest("更新用户信息失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除用户", description = "管理员删除用户")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ApiResponse.successVoid("删除用户成功");
        } catch (Exception e) {
            return ApiResponse.badRequest("删除用户失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/change-password")
    @PreAuthorize("#id == authentication.principal.id")
    @Operation(summary = "修改密码", description = "用户修改自己的密码")
    public ApiResponse<Void> changePassword(@PathVariable Long id, 
                                           @Valid @RequestBody PasswordChangeRequest passwordChangeRequest) {
        try {
            userService.changePassword(id, passwordChangeRequest);
            return ApiResponse.successVoid("密码修改成功");
        } catch (Exception e) {
            return ApiResponse.badRequest("密码修改失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "重置密码", description = "管理员重置用户密码")
    public ApiResponse<Void> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        try {
            userService.resetPassword(id, newPassword);
            return ApiResponse.successVoid("密码重置成功");
        } catch (Exception e) {
            return ApiResponse.badRequest("密码重置失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "切换用户状态", description = "管理员启用/禁用用户")
    public ApiResponse<Void> toggleUserStatus(@PathVariable Long id) {
        try {
            userService.toggleUserStatus(id);
            return ApiResponse.successVoid("用户状态切换成功");
        } catch (Exception e) {
            return ApiResponse.badRequest("用户状态切换失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/profile")
    @Operation(summary = "获取个人资料", description = "获取当前登录用户的个人资料")
    public ApiResponse<UserInfo> getProfile(@AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            UserInfo userInfo = userService.getUserById(currentUser.getId());
            return ApiResponse.success("获取个人资料成功", userInfo);
        } catch (Exception e) {
            return ApiResponse.error("获取个人资料失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/profile")
    @Operation(summary = "更新个人资料", description = "更新当前登录用户的个人资料")
    public ApiResponse<UserInfo> updateProfile(@AuthenticationPrincipal UserPrincipal currentUser,
                                              @Valid @RequestBody UserUpdateRequest updateRequest) {
        try {
            // 普通用户不能修改状态和角色
            updateRequest.setStatus(null);
            updateRequest.setRoles(null);
            
            UserInfo userInfo = userService.updateUser(currentUser.getId(), updateRequest);
            return ApiResponse.success("更新个人资料成功", userInfo);
        } catch (Exception e) {
            return ApiResponse.badRequest("更新个人资料失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取所有角色", description = "获取系统中的所有角色")
    public ApiResponse<List<Role>> getAllRoles() {
        try {
            List<Role> roles = userService.getAllRoles();
            return ApiResponse.success("获取角色列表成功", roles);
        } catch (Exception e) {
            return ApiResponse.error("获取角色列表失败: " + e.getMessage());
        }
    }
}