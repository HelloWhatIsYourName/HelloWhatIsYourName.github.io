import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import * as authApi from '@/api/auth'
import type { UserInfo, LoginRequest, RegisterRequest } from '@/types'
import { getToken, setToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>(getToken() || '')
  const userInfo = ref<UserInfo | null>(null)
  const permissions = ref<string[]>([])
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.roles?.includes('ADMIN') || false)
  const username = computed(() => userInfo.value?.username || '')
  const avatar = computed(() => userInfo.value?.avatarUrl || '')
  
  // 登录
  const login = async (loginData: LoginRequest) => {
    try {
      const response = await authApi.login(loginData)
      token.value = response.accessToken
      userInfo.value = response.userInfo
      
      // 保存token到localStorage
      setToken(response.accessToken)
      
      // 设置权限
      permissions.value = response.userInfo.roles || []
      
      ElMessage.success('登录成功')
      return response
    } catch (error) {
      throw error
    }
  }
  
  // 注册
  const register = async (registerData: RegisterRequest) => {
    try {
      const response = await authApi.register(registerData)
      token.value = response.accessToken
      userInfo.value = response.userInfo
      
      // 保存token到localStorage
      setToken(response.accessToken)
      
      // 设置权限
      permissions.value = response.userInfo.roles || []
      
      ElMessage.success('注册成功')
      return response
    } catch (error) {
      throw error
    }
  }
  
  // 获取用户信息
  const getUserInfo = async () => {
    try {
      const response = await authApi.getCurrentUser()
      userInfo.value = response
      permissions.value = response.roles || []
      return response
    } catch (error) {
      // 如果获取用户信息失败，可能是token过期，需要重新登录
      logout()
      throw error
    }
  }
  
  // 登出
  const logout = async () => {
    try {
      await authApi.logout()
    } catch (error) {
      // 即使登出接口失败，也要清除本地数据
      console.error('登出失败:', error)
    } finally {
      // 清除本地数据
      token.value = ''
      userInfo.value = null
      permissions.value = []
      removeToken()
    }
  }
  
  // 刷新token
  const refreshToken = async (refreshToken: string) => {
    try {
      const response = await authApi.refreshToken(refreshToken)
      token.value = response.accessToken
      userInfo.value = response.userInfo
      
      // 保存新token
      setToken(response.accessToken)
      
      return response
    } catch (error) {
      // 刷新token失败，需要重新登录
      logout()
      throw error
    }
  }
  
  // 检查权限
  const hasPermission = (permission: string): boolean => {
    return permissions.value.includes(permission)
  }
  
  // 检查角色
  const hasRole = (role: string): boolean => {
    return userInfo.value?.roles?.includes(role) || false
  }
  
  // 重置状态
  const resetState = () => {
    token.value = ''
    userInfo.value = null
    permissions.value = []
  }
  
  return {
    // 状态
    token,
    userInfo,
    permissions,
    
    // 计算属性
    isLoggedIn,
    isAdmin,
    username,
    avatar,
    
    // 方法
    login,
    register,
    getUserInfo,
    logout,
    refreshToken,
    hasPermission,
    hasRole,
    resetState,
  }
})