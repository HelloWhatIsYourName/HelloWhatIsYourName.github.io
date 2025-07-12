import request from '@/utils/request'
import type {
  LoginRequest,
  RegisterRequest,
  AuthResponse,
  UserInfo,
  PasswordChangeRequest,
} from '@/types'

// 用户登录
export const login = (data: LoginRequest): Promise<AuthResponse> => {
  return request.post('/v1/auth/login', data)
}

// 用户注册
export const register = (data: RegisterRequest): Promise<AuthResponse> => {
  return request.post('/v1/auth/register', data)
}

// 刷新token
export const refreshToken = (refreshToken: string): Promise<AuthResponse> => {
  return request.post('/v1/auth/refresh', null, { params: { refreshToken } })
}

// 获取当前用户信息
export const getCurrentUser = (): Promise<UserInfo> => {
  return request.get('/v1/auth/me')
}

// 用户登出
export const logout = (): Promise<void> => {
  return request.post('/v1/auth/logout')
}

// 修改密码
export const changePassword = (userId: number, data: PasswordChangeRequest): Promise<void> => {
  return request.post(`/v1/users/${userId}/change-password`, data)
}