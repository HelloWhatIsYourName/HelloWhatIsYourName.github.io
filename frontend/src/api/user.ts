import request from '@/utils/request'
import type {
  PageResponse,
  User,
  UserInfo,
  RegisterRequest,
  UserUpdateRequest,
  Role,
} from '@/types'

// 获取用户列表
export const getUsers = (params: {
  page?: number
  size?: number
  keyword?: string
  status?: string
  sortBy?: string
  sortDir?: string
}): Promise<PageResponse<UserInfo>> => {
  return request.get('/v1/users', params)
}

// 获取用户详情
export const getUserById = (id: number): Promise<UserInfo> => {
  return request.get(`/v1/users/${id}`)
}

// 创建用户
export const createUser = (data: RegisterRequest): Promise<UserInfo> => {
  return request.post('/v1/users', data)
}

// 更新用户信息
export const updateUser = (id: number, data: UserUpdateRequest): Promise<UserInfo> => {
  return request.put(`/v1/users/${id}`, data)
}

// 删除用户
export const deleteUser = (id: number): Promise<void> => {
  return request.delete(`/v1/users/${id}`)
}

// 重置密码
export const resetPassword = (id: number, newPassword: string): Promise<void> => {
  return request.post(`/v1/users/${id}/reset-password`, null, {
    params: { newPassword }
  })
}

// 切换用户状态
export const toggleUserStatus = (id: number): Promise<void> => {
  return request.post(`/v1/users/${id}/toggle-status`)
}

// 获取个人资料
export const getProfile = (): Promise<UserInfo> => {
  return request.get('/v1/users/profile')
}

// 更新个人资料
export const updateProfile = (data: UserUpdateRequest): Promise<UserInfo> => {
  return request.put('/v1/users/profile', data)
}

// 获取所有角色
export const getAllRoles = (): Promise<Role[]> => {
  return request.get('/v1/users/roles')
}