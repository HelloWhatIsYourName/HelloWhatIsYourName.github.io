# 数据手套系统 API 文档

## 概述

数据手套系统提供了完整的 RESTful API，支持用户管理、设备管理、数据管理等功能。所有 API 都遵循 REST 设计原则，使用 JSON 格式进行数据交换。

## 基础信息

- **Base URL**: `http://localhost:8080/api`
- **API版本**: v1
- **认证方式**: JWT Bearer Token
- **数据格式**: JSON
- **字符编码**: UTF-8

## 通用响应格式

所有API响应都遵循统一的格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1703765432000
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 没有权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 认证授权

### 登录获取Token

```http
POST /v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**响应示例**：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "roles": ["ADMIN"]
    }
  }
}
```

### 使用Token访问API

在请求头中添加Authorization字段：

```http
Authorization: Bearer <access_token>
```

## 用户管理 API

### 1. 获取用户列表

```http
GET /v1/users?page=0&size=10&keyword=&status=ACTIVE
```

**参数说明**：
- `page`: 页码，从0开始
- `size`: 每页大小
- `keyword`: 搜索关键词（可选）
- `status`: 用户状态（可选）

**权限要求**：ADMIN角色

### 2. 获取用户详情

```http
GET /v1/users/{id}
```

**权限要求**：ADMIN角色或用户本人

### 3. 创建用户

```http
POST /v1/users
Content-Type: application/json

{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "password123",
  "confirmPassword": "password123",
  "phone": "13800138000",
  "realName": "新用户"
}
```

**权限要求**：ADMIN角色

### 4. 更新用户信息

```http
PUT /v1/users/{id}
Content-Type: application/json

{
  "username": "updateduser",
  "email": "updated@example.com",
  "phone": "13800138001",
  "realName": "更新用户",
  "status": "ACTIVE",
  "roles": ["USER"]
}
```

**权限要求**：ADMIN角色或用户本人

### 5. 删除用户

```http
DELETE /v1/users/{id}
```

**权限要求**：ADMIN角色

## 设备管理 API

### 1. 获取设备列表

```http
GET /v1/devices?page=0&size=10&keyword=&status=ONLINE
```

**参数说明**：
- `page`: 页码，从0开始
- `size`: 每页大小
- `keyword`: 搜索关键词（可选）
- `status`: 设备状态（可选）

**权限要求**：ADMIN角色

### 2. 获取设备详情

```http
GET /v1/devices/{id}
```

**权限要求**：ADMIN角色

### 3. 创建设备

```http
POST /v1/devices
Content-Type: application/json

{
  "deviceId": "DEVICE_001",
  "deviceName": "数据手套#1",
  "deviceType": "DATA_GLOVE",
  "hardwareVersion": "1.0.0",
  "firmwareVersion": "1.0.0",
  "macAddress": "00:11:22:33:44:55",
  "location": "实验室A",
  "description": "用于手语识别的数据手套设备"
}
```

**权限要求**：ADMIN角色

### 4. 更新设备信息

```http
PUT /v1/devices/{id}
Content-Type: application/json

{
  "deviceName": "更新的设备名称",
  "status": "ONLINE",
  "location": "实验室B",
  "description": "更新的设备描述"
}
```

**权限要求**：ADMIN角色

### 5. 绑定设备

```http
POST /v1/devices/bind
Content-Type: application/json

{
  "deviceId": 1,
  "userId": 2
}
```

**权限要求**：ADMIN角色

### 6. 获取我的设备

```http
GET /v1/devices/my-devices
```

**权限要求**：已登录用户

## 数据管理 API

### 1. 获取传感器数据

```http
GET /v1/data/sensor-data?page=0&size=10&deviceId=1&sensorType=FLEX
```

**参数说明**：
- `page`: 页码
- `size`: 每页大小
- `deviceId`: 设备ID（可选）
- `sensorType`: 传感器类型（可选）
- `startTime`: 开始时间（可选）
- `endTime`: 结束时间（可选）

**权限要求**：ADMIN角色

### 2. 获取手势识别结果

```http
GET /v1/data/gesture-results?page=0&size=10&userId=1&gestureName=hello
```

**参数说明**：
- `page`: 页码
- `size`: 每页大小
- `userId`: 用户ID（可选）
- `gestureName`: 手势名称（可选）
- `startTime`: 开始时间（可选）
- `endTime`: 结束时间（可选）

**权限要求**：ADMIN角色

### 3. 获取学习记录

```http
GET /v1/data/learning-records?page=0&size=10&userId=1
```

**权限要求**：ADMIN角色

### 4. 获取我的数据统计

```http
GET /v1/data/statistics/my
```

**权限要求**：已登录用户

## 外部集成 API

### 1. 接收设备数据

```http
POST /v1/integration/iot/device-data
Content-Type: application/json

{
  "deviceId": "DEVICE_001",
  "sensorType": "FLEX",
  "sensorPosition": "thumb",
  "dataValue": {
    "value": 0.75,
    "unit": "voltage",
    "timestamp": "2023-12-01T10:00:00"
  },
  "timestamp": "2023-12-01T10:00:00"
}
```

**权限要求**：无（公开接口）

### 2. 更新设备状态

```http
POST /v1/integration/iot/device-status
Content-Type: application/json

{
  "deviceId": "DEVICE_001",
  "status": "ONLINE",
  "timestamp": "2023-12-01T10:00:00"
}
```

**权限要求**：无（公开接口）

### 3. 接收手势识别结果

```http
POST /v1/integration/ai/gesture-result
Content-Type: application/json

{
  "deviceId": "DEVICE_001",
  "gestureName": "hello",
  "confidence": 0.95,
  "rawData": {
    "sensorData": [0.1, 0.2, 0.3, 0.4, 0.5]
  },
  "processedData": {
    "features": [0.8, 0.9, 0.7]
  },
  "recognitionTime": "2023-12-01T10:00:00"
}
```

**权限要求**：无（公开接口）

## 错误处理

### 常见错误响应

```json
{
  "code": 400,
  "message": "请求参数错误",
  "timestamp": 1703765432000
}
```

### 验证错误

```json
{
  "code": 400,
  "message": "用户名已存在",
  "timestamp": 1703765432000
}
```

### 权限错误

```json
{
  "code": 403,
  "message": "没有权限访问此资源",
  "timestamp": 1703765432000
}
```

## 分页数据格式

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "content": [
      {
        "id": 1,
        "username": "user1",
        "email": "user1@example.com"
      }
    ],
    "page": 0,
    "size": 10,
    "total": 100,
    "totalPages": 10,
    "first": true,
    "last": false
  }
}
```

## 数据类型定义

### 用户状态
- `ACTIVE`: 活跃
- `INACTIVE`: 非活跃
- `BANNED`: 已封禁

### 设备状态
- `ONLINE`: 在线
- `OFFLINE`: 离线
- `MAINTENANCE`: 维护中

### 传感器类型
- `FLEX`: 弯曲传感器
- `STRAIN`: 应变传感器
- `IMU`: 惯性测量单元

## 限流规则

- 每个IP每分钟最多100次请求
- 登录接口每个IP每分钟最多10次请求
- 超出限制返回429状态码

## 版本更新

### v1.0.0 (2025-7-10)
- 初始版本
- 支持用户管理、设备管理、数据管理
- 提供外部集成接口

---

**注意事项**：
1. 所有时间格式均为 ISO 8601 格式
2. 密码在传输过程中会被加密
3. 敏感信息不会在响应中返回
4. 建议在生产环境中使用HTTPS协议