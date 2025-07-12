// 用户相关类型
export interface User {
  id: number
  username: string
  email: string
  phone?: string
  realName?: string
  avatarUrl?: string
  status: 'ACTIVE' | 'INACTIVE' | 'BANNED'
  roles: string[]
  createdAt: string
  lastLoginAt?: string
}

export interface UserInfo {
  id: number
  username: string
  email: string
  phone?: string
  realName?: string
  avatarUrl?: string
  status: string
  roles: string[]
  createdAt: string
  lastLoginAt?: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
  confirmPassword: string
  phone?: string
  realName?: string
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  userInfo: UserInfo
}

export interface UserUpdateRequest {
  username?: string
  email?: string
  phone?: string
  realName?: string
  avatarUrl?: string
  status?: 'ACTIVE' | 'INACTIVE' | 'BANNED'
  roles?: string[]
}

export interface PasswordChangeRequest {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

// 设备相关类型
export interface Device {
  id: number
  deviceId: string
  deviceName: string
  deviceType: 'DATA_GLOVE'
  hardwareVersion?: string
  firmwareVersion?: string
  macAddress?: string
  status: 'ONLINE' | 'OFFLINE' | 'MAINTENANCE'
  location?: string
  description?: string
  lastHeartbeat?: string
  createdAt: string
  updatedAt: string
  bound: boolean
  boundUserId?: number
  boundUsername?: string
}

export interface DeviceCreateRequest {
  deviceId: string
  deviceName: string
  deviceType?: 'DATA_GLOVE'
  hardwareVersion?: string
  firmwareVersion?: string
  macAddress?: string
  location?: string
  description?: string
}

export interface DeviceUpdateRequest {
  deviceName?: string
  deviceType?: 'DATA_GLOVE'
  hardwareVersion?: string
  firmwareVersion?: string
  macAddress?: string
  status?: 'ONLINE' | 'OFFLINE' | 'MAINTENANCE'
  location?: string
  description?: string
}

export interface DeviceBindRequest {
  deviceId: number
  userId: number
}

// 数据相关类型
export interface SensorData {
  id: number
  deviceId: number
  deviceName: string
  userId: number
  username: string
  sensorType: 'FLEX' | 'STRAIN' | 'IMU'
  sensorPosition?: string
  dataValue: Record<string, any>
  timestamp: string
  createdAt: string
}

export interface GestureResult {
  id: number
  deviceId: number
  deviceName: string
  userId: number
  username: string
  gestureName: string
  confidence: number
  rawData?: Record<string, any>
  processedData?: Record<string, any>
  recognitionTime: string
  createdAt: string
}

export interface LearningRecord {
  id: number
  userId: number
  username: string
  gestureName: string
  practiceCount: number
  successCount: number
  averageConfidence?: number
  successRate: number
  lastPracticeTime?: string
  createdAt: string
  updatedAt: string
}

// 通用类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data?: T
  timestamp: number
}

export interface PageResponse<T = any> {
  content: T[]
  page: number
  size: number
  total: number
  totalPages: number
  first: boolean
  last: boolean
}

export interface Role {
  id: number
  roleName: string
  description?: string
  createdAt: string
}

// 统计相关类型
export interface DataStatistics {
  totalSensorDataCount: number
  totalGestureResultCount: number
  todayDataCount: number
}

export interface UserDataStatistics {
  userId: number
  username: string
  sensorDataCount: number
  gestureResultCount: number
  totalPracticeCount: number
  totalSuccessCount: number
  overallSuccessRate: number
}

// 系统状态类型
export interface SystemInfo {
  version: string
  uptime: string
  memory: {
    used: number
    total: number
  }
  cpu: {
    usage: number
  }
}

// 路由元信息类型
export interface RouteMeta {
  title?: string
  icon?: string
  hidden?: boolean
  roles?: string[]
  keepAlive?: boolean
  affix?: boolean
}

// 菜单项类型
export interface MenuItem {
  id: string
  title: string
  icon?: string
  path?: string
  children?: MenuItem[]
  roles?: string[]
  hidden?: boolean
}

// 表格列类型
export interface TableColumn {
  prop: string
  label: string
  width?: number
  minWidth?: number
  align?: 'left' | 'center' | 'right'
  sortable?: boolean
  formatter?: (row: any, column: any, cellValue: any) => string
  type?: 'selection' | 'index' | 'expand'
}

// 表单项类型
export interface FormItem {
  prop: string
  label: string
  type: 'input' | 'password' | 'select' | 'radio' | 'checkbox' | 'date' | 'textarea'
  required?: boolean
  rules?: any[]
  options?: Array<{ label: string; value: any }>
  placeholder?: string
  span?: number
}

// 图表数据类型
export interface ChartData {
  name: string
  value: number
  [key: string]: any
}

export interface ChartSeries {
  name: string
  type: string
  data: ChartData[]
  [key: string]: any
}