import request from '@/utils/request'
import type {
  PageResponse,
  Device,
  DeviceCreateRequest,
  DeviceUpdateRequest,
  DeviceBindRequest,
} from '@/types'

// 获取设备列表
export const getDevices = (params: {
  page?: number
  size?: number
  keyword?: string
  status?: string
  sortBy?: string
  sortDir?: string
}): Promise<PageResponse<Device>> => {
  return request.get('/v1/devices', params)
}

// 获取设备详情
export const getDeviceById = (id: number): Promise<Device> => {
  return request.get(`/v1/devices/${id}`)
}

// 根据设备ID获取设备
export const getDeviceByDeviceId = (deviceId: string): Promise<Device> => {
  return request.get(`/v1/devices/device-id/${deviceId}`)
}

// 创建设备
export const createDevice = (data: DeviceCreateRequest): Promise<Device> => {
  return request.post('/v1/devices', data)
}

// 更新设备信息
export const updateDevice = (id: number, data: DeviceUpdateRequest): Promise<Device> => {
  return request.put(`/v1/devices/${id}`, data)
}

// 删除设备
export const deleteDevice = (id: number): Promise<void> => {
  return request.delete(`/v1/devices/${id}`)
}

// 绑定设备
export const bindDevice = (data: DeviceBindRequest): Promise<void> => {
  return request.post('/v1/devices/bind', data)
}

// 解绑设备
export const unbindDevice = (deviceId: number, userId: number): Promise<void> => {
  return request.post('/v1/devices/unbind', null, {
    params: { deviceId, userId }
  })
}

// 获取我的设备
export const getMyDevices = (): Promise<Device[]> => {
  return request.get('/v1/devices/my-devices')
}

// 解绑我的设备
export const unbindMyDevice = (deviceId: number): Promise<void> => {
  return request.post(`/v1/devices/my-devices/${deviceId}/unbind`)
}

// 设备心跳
export const deviceHeartbeat = (deviceId: string): Promise<void> => {
  return request.post('/v1/devices/heartbeat', null, {
    params: { deviceId }
  })
}

// 更新设备状态
export const updateDeviceStatus = (deviceId: string, status: string): Promise<void> => {
  return request.post('/v1/devices/status', null, {
    params: { deviceId, status }
  })
}