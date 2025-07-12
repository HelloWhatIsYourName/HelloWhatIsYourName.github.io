import request from '@/utils/request'
import type {
  PageResponse,
  SensorData,
  GestureResult,
  LearningRecord,
  DataStatistics,
  UserDataStatistics,
} from '@/types'

// 获取传感器数据
export const getSensorData = (params: {
  page?: number
  size?: number
  deviceId?: number
  userId?: number
  sensorType?: string
  startTime?: string
  endTime?: string
  sortBy?: string
  sortDir?: string
}): Promise<PageResponse<SensorData>> => {
  return request.get('/v1/data/sensor-data', params)
}

// 获取我的传感器数据
export const getMySensorData = (params: {
  page?: number
  size?: number
  deviceId?: number
  sensorType?: string
  startTime?: string
  endTime?: string
  sortBy?: string
  sortDir?: string
}): Promise<PageResponse<SensorData>> => {
  return request.get('/v1/data/sensor-data/my', params)
}

// 获取手势识别结果
export const getGestureResults = (params: {
  page?: number
  size?: number
  deviceId?: number
  userId?: number
  gestureName?: string
  startTime?: string
  endTime?: string
  sortBy?: string
  sortDir?: string
}): Promise<PageResponse<GestureResult>> => {
  return request.get('/v1/data/gesture-results', params)
}

// 获取我的手势识别结果
export const getMyGestureResults = (params: {
  page?: number
  size?: number
  deviceId?: number
  gestureName?: string
  startTime?: string
  endTime?: string
  sortBy?: string
  sortDir?: string
}): Promise<PageResponse<GestureResult>> => {
  return request.get('/v1/data/gesture-results/my', params)
}

// 获取最近的手势识别结果
export const getRecentGestureResults = (limit: number = 10): Promise<GestureResult[]> => {
  return request.get('/v1/data/gesture-results/recent', { limit })
}

// 获取学习记录
export const getLearningRecords = (params: {
  page?: number
  size?: number
  userId?: number
  gestureName?: string
  sortBy?: string
  sortDir?: string
}): Promise<PageResponse<LearningRecord>> => {
  return request.get('/v1/data/learning-records', params)
}

// 获取我的学习记录
export const getMyLearningRecords = (params: {
  page?: number
  size?: number
  gestureName?: string
  sortBy?: string
  sortDir?: string
}): Promise<PageResponse<LearningRecord>> => {
  return request.get('/v1/data/learning-records/my', params)
}

// 获取最常练习的手势
export const getTopPracticedGestures = (limit: number = 5): Promise<LearningRecord[]> => {
  return request.get('/v1/data/learning-records/top-practiced', { limit })
}

// 获取表现最好的手势
export const getTopPerformingGestures = (limit: number = 5): Promise<LearningRecord[]> => {
  return request.get('/v1/data/learning-records/top-performing', { limit })
}

// 获取我的手势列表
export const getMyGestures = (): Promise<string[]> => {
  return request.get('/v1/data/gestures/my')
}

// 获取数据概览统计
export const getDataStatistics = (): Promise<DataStatistics> => {
  return request.get('/v1/data/statistics/overview')
}

// 获取我的数据统计
export const getMyDataStatistics = (): Promise<UserDataStatistics> => {
  return request.get('/v1/data/statistics/my')
}

// 清理历史数据
export const cleanupOldData = (daysToKeep: number = 30): Promise<void> => {
  return request.delete('/v1/data/cleanup', { daysToKeep })
}