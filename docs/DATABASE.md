# 数据库设计文档

## 概述

数据手套系统数据库设计基于MySQL 8.0，采用关系型数据库模式，支持用户管理、设备管理、数据采集、手势识别和学习记录等功能。

## 数据库信息

- **数据库名**: `data_glove_db`
- **字符集**: `utf8mb4`
- **排序规则**: `utf8mb4_unicode_ci`
- **引擎**: `InnoDB`

## 表结构设计

### 1. 用户系统表

#### 1.1 用户表 (users)

| 字段名 | 数据类型 | 长度 | 非空 | 默认值 | 主键 | 索引 | 说明 |
|--------|----------|------|------|--------|------|------|------|
| id | BIGINT | - | Y | AUTO_INCREMENT | Y | - | 用户ID |
| username | VARCHAR | 50 | Y | - | - | UNIQUE | 用户名 |
| email | VARCHAR | 100 | Y | - | - | UNIQUE | 邮箱 |
| phone | VARCHAR | 20 | N | - | - | UNIQUE | 手机号 |
| password_hash | VARCHAR | 255 | Y | - | - | - | 密码哈希 |
| real_name | VARCHAR | 100 | N | - | - | - | 真实姓名 |
| avatar_url | VARCHAR | 500 | N | - | - | - | 头像URL |
| status | ENUM | - | N | ACTIVE | - | Y | 账户状态 |
| created_at | TIMESTAMP | - | N | CURRENT_TIMESTAMP | - | - | 创建时间 |
| updated_at | TIMESTAMP | - | N | CURRENT_TIMESTAMP | - | - | 更新时间 |
| last_login_at | TIMESTAMP | - | N | NULL | - | - | 最后登录时间 |

**枚举值说明**：
- `status`: ACTIVE(活跃), INACTIVE(非活跃), BANNED(已封禁)

#### 1.2 角色表 (roles)

| 字段名 | 数据类型 | 长度 | 非空 | 默认值 | 主键 | 索引 | 说明 |
|--------|----------|------|------|--------|------|------|------|
| id | BIGINT | - | Y | AUTO_INCREMENT | Y | - | 角色ID |
| role_name | VARCHAR | 50 | Y | - | - | UNIQUE | 角色名称 |
| description | VARCHAR | 200 | N | - | - | - | 角色描述 |
| created_at | TIMESTAMP | - | N | CURRENT_TIMESTAMP | - | - | 创建时间 |

#### 1.3 用户角色关联表 (user_roles)

| 字段名 | 数据类型 | 长度 | 非空 | 默认值 | 主键 | 索引 | 说明 |
|--------|----------|------|------|--------|------|------|------|
| user_id | BIGINT | - | Y | - | Y | - | 用户ID |
| role_id | BIGINT | - | Y | - | Y | - | 角色ID |
| created_at | TIMESTAMP | - | N | CURRENT_TIMESTAMP | - | - | 创建时间 |

**外键约束**：
- `user_id` → `users(id)` ON DELETE CASCADE
- `role_id` → `roles(id)` ON DELETE CASCADE

### 2. 设备系统表

#### 2.1 设备表 (devices)

| 字段名 | 数据类型 | 长度 | 非空 | 默认值 | 主键 | 索引 | 说明 |
|--------|----------|------|------|--------|------|------|------|
| id | BIGINT | - | Y | AUTO_INCREMENT | Y | - | 设备ID |
| device_id | VARCHAR | 100 | Y | - | - | UNIQUE | 设备唯一标识 |
| device_name | VARCHAR | 100 | Y | - | - | - | 设备名称 |
| device_type | ENUM | - | N | DATA_GLOVE | - | Y | 设备类型 |
| hardware_version | VARCHAR | 50 | N | - | - | - | 硬件版本 |
| firmware_version | VARCHAR | 50 | N | - | - | - | 固件版本 |
| mac_address | VARCHAR | 17 | N | - | - | - | MAC地址 |
| status | ENUM | - | N | OFFLINE | - | Y | 设备状态 |
| location | VARCHAR | 200 | N | - | - | - | 设备位置 |
| description | TEXT | - | N | - | - | - | 设备描述 |
| last_heartbeat | TIMESTAMP | - | N | NULL | - | - | 最后心跳时间 |
| created_at | TIMESTAMP | - | N | CURRENT_TIMESTAMP | - | - | 创建时间 |
| updated_at | TIMESTAMP | - | N | CURRENT_TIMESTAMP | - | - | 更新时间 |

**枚举值说明**：
- `device_type`: DATA_GLOVE(数据手套)
- `status`: ONLINE(在线), OFFLINE(离线), MAINTENANCE(维护)

#### 2.2 用户设备绑定表 (user_devices)

| 字段名 | 数据类型 | 长度 | 非空 | 默认值 | 主键 | 索引 | 说明 |
|--------|----------|------|------|--------|------|------|------|
| id | BIGINT | - | Y | AUTO_INCREMENT | Y | - | 绑定ID |
| user_id | BIGINT | - | Y | - | - | Y | 用户ID |
| device_id | BIGINT | - | Y | - | - | Y | 设备ID |
| bind_time | TIMESTAMP | - | N | CURRENT_TIMESTAMP | - | - | 绑定时间 |
| unbind_time | TIMESTAMP | - | N | NULL | - | - | 解绑时间 |
| is_active | BOOLEAN | - | N | TRUE | - | - | 是否活跃 |

**外键约束**：
- `user_id` → `users(id)` ON DELETE CASCADE
- `device_id` → `devices(id)` ON DELETE CASCADE

**唯一约束**：
- `uk_user_device_active` (user_id, device_id, is_active)

### 3. 数据系统表

#### 3.1 传感器数据表 (sensor_data)

| 字段名 | 数据类型 | 长度 | 非空 | 默认值 | 主键 | 索引 | 说明 |
|--------|----------|------|------|--------|------|------|------|
| id | BIGINT | - | Y | AUTO_INCREMENT | Y | - | 数据ID |
| device_id | BIGINT | - | Y | - | - | Y | 设备ID |
| user_id | BIGINT | - | Y | - | - | Y | 用户ID |
| sensor_type | ENUM | - | Y | - | - | Y | 传感器类型 |
| sensor_position | VARCHAR | 50 | N | - | - | - | 传感器位置 |
| data_value | JSON | - | Y | - | - | - | 传感器数据 |
| timestamp | TIMESTAMP | - | N | CURRENT_TIMESTAMP | - | - | 数据时间戳 |
| created_at | TIMESTAMP | - | N | CURRENT_TIMESTAMP | - | - | 创建时间 |

**枚举值说明**：
- `sensor_type`: FLEX(弯曲传感器), STRAIN(应变传感器), IMU(惯性测量单元)

**外键约束**：
- `device_id` → `devices(id)` ON DELETE CASCADE
- `user_id` → `users(id)` ON DELETE CASCADE

**复合索引**：
- `idx_device_timestamp` (device_id, timestamp)
- `idx_user_timestamp` (user_id, timestamp)

#### 3.2 手势识别结果表 (gesture_results)

| 字段名 | 数据类型 | 长度 | 非空 | 默认值 | 主键 | 索引 | 说明 |
|--------|----------|------|------|--------|------|------|------|
| id | BIGINT | - | Y | AUTO_INCREMENT | Y | - | 识别结果ID |
| device_id | BIGINT | - | Y | - | - | Y | 设备ID |
| user_id | BIGINT | - | Y | - | - | Y | 用户ID |
| gesture_name | VARCHAR | 100 | Y | - | - | Y | 手势名称 |
| confidence | DECIMAL | 5,4 | Y | - | - | - | 置信度 |
| raw_data | JSON | - | N | - | - | - | 原始数据 |
| processed_data | JSON | - | N | - | - | - | 处理后数据 |
| recognition_time | TIMESTAMP | - | N | CURRENT_TIMESTAMP | - | - | 识别时间 |
| created_at | TIMESTAMP | - | N | CURRENT_TIMESTAMP | - | - | 创建时间 |

**外键约束**：
- `device_id` → `devices(id)` ON DELETE CASCADE
- `user_id` → `users(id)` ON DELETE CASCADE

**复合索引**：
- `idx_device_recognition` (device_id, recognition_time)
- `idx_user_recognition` (user_id, recognition_time)

#### 3.3 学习记录表 (learning_records)

| 字段名 | 数据类型 | 长度 | 非空 | 默认值 | 主键 | 索引 | 说明 |
|--------|----------|------|------|--------|------|------|------|
| id | BIGINT | - | Y | AUTO_INCREMENT | Y | - | 学习记录ID |
| user_id | BIGINT | - | Y | - | - | Y | 用户ID |
| gesture_name | VARCHAR | 100 | Y | - | - | Y | 手势名称 |
| practice_count | INT | - | N | 0 | - | - | 练习次数 |
| success_count | INT | - | N | 0 | - | - | 成功次数 |
| average_confidence | DECIMAL | 5,4 | N | - | - | - | 平均置信度 |
| last_practice_time | TIMESTAMP | - | N | NULL | - | - | 最后练习时间 |
| created_at | TIMESTAMP | - | N | CURRENT_TIMESTAMP | - | - | 创建时间 |
| updated_at | TIMESTAMP | - | N | CURRENT_TIMESTAMP | - | - | 更新时间 |

**外键约束**：
- `user_id` → `users(id)` ON DELETE CASCADE

**唯一约束**：
- `uk_user_gesture` (user_id, gesture_name)

### 4. 系统表

#### 4.1 系统日志表 (system_logs)

| 字段名 | 数据类型 | 长度 | 非空 | 默认值 | 主键 | 索引 | 说明 |
|--------|----------|------|------|--------|------|------|------|
| id | BIGINT | - | Y | AUTO_INCREMENT | Y | - | 日志ID |
| user_id | BIGINT | - | N | - | - | Y | 用户ID |
| action | VARCHAR | 100 | Y | - | - | Y | 操作类型 |
| resource | VARCHAR | 200 | N | - | - | - | 资源 |
| details | TEXT | - | N | - | - | - | 详细信息 |
| ip_address | VARCHAR | 45 | N | - | - | - | IP地址 |
| user_agent | TEXT | - | N | - | - | - | 用户代理 |
| created_at | TIMESTAMP | - | N | CURRENT_TIMESTAMP | - | Y | 创建时间 |

**外键约束**：
- `user_id` → `users(id)` ON DELETE SET NULL

## 数据字典

### 用户状态枚举
- `ACTIVE`: 活跃状态，可以正常使用系统
- `INACTIVE`: 非活跃状态，账户被暂时停用
- `BANNED`: 已封禁状态，账户被永久封禁

### 设备状态枚举
- `ONLINE`: 设备在线，可以正常通信
- `OFFLINE`: 设备离线，无法通信
- `MAINTENANCE`: 设备维护中，暂时不可用

### 传感器类型枚举
- `FLEX`: 弯曲传感器，检测关节弯曲角度
- `STRAIN`: 应变传感器，检测肌肉紧张程度
- `IMU`: 惯性测量单元，检测运动和姿态

## 索引策略

### 主键索引
- 所有表都有基于 `id` 的主键索引
- 主键采用 `AUTO_INCREMENT` 自增策略

### 唯一索引
- `users.username`: 用户名唯一性
- `users.email`: 邮箱唯一性
- `users.phone`: 手机号唯一性
- `roles.role_name`: 角色名称唯一性
- `devices.device_id`: 设备ID唯一性

### 普通索引
- `users.status`: 用户状态查询
- `devices.status`: 设备状态查询
- `devices.device_type`: 设备类型查询
- `sensor_data.sensor_type`: 传感器类型查询
- `gesture_results.gesture_name`: 手势名称查询

### 复合索引
- `sensor_data(device_id, timestamp)`: 设备数据时间查询
- `sensor_data(user_id, timestamp)`: 用户数据时间查询
- `gesture_results(device_id, recognition_time)`: 设备识别时间查询
- `gesture_results(user_id, recognition_time)`: 用户识别时间查询

## 数据分区策略

### 时间分区
对于大数据量的表，建议使用时间分区：

```sql
-- 传感器数据表按月分区
ALTER TABLE sensor_data 
PARTITION BY RANGE (YEAR(timestamp) * 100 + MONTH(timestamp)) (
    PARTITION p202312 VALUES LESS THAN (202401),
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    -- ...
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- 手势识别结果表按月分区
ALTER TABLE gesture_results 
PARTITION BY RANGE (YEAR(recognition_time) * 100 + MONTH(recognition_time)) (
    PARTITION p202312 VALUES LESS THAN (202401),
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    -- ...
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
```

## 备份策略

### 逻辑备份
```sql
-- 完整备份
mysqldump -u username -p --single-transaction --routines --triggers data_glove_db > backup.sql

-- 只备份结构
mysqldump -u username -p --no-data data_glove_db > schema.sql

-- 只备份数据
mysqldump -u username -p --no-create-info data_glove_db > data.sql
```

### 物理备份
```sql
-- 使用 MySQL Enterprise Backup
mysqlbackup --user=username --password=password --backup-dir=/backup/full backup

-- 使用 Percona XtraBackup
xtrabackup --user=username --password=password --backup --target-dir=/backup/full
```

## 性能优化建议

### 1. 查询优化
- 使用覆盖索引减少回表操作
- 避免在 WHERE 子句中使用函数
- 使用 LIMIT 限制返回结果集大小
- 合理使用连接查询和子查询

### 2. 索引优化
- 定期分析表和索引使用情况
- 删除不必要的索引
- 对频繁查询的字段创建索引
- 使用复合索引优化多字段查询

### 3. 表结构优化
- 选择合适的数据类型
- 避免 NULL 值，使用合理的默认值
- 合理设计表分区
- 定期清理历史数据

### 4. 配置优化
```sql
-- 关键配置参数
innodb_buffer_pool_size = 4G
innodb_log_file_size = 256M
innodb_flush_log_at_trx_commit = 1
innodb_file_per_table = 1
query_cache_size = 256M
max_connections = 1000
```

## 数据迁移

### 版本升级脚本
```sql
-- V1.1.0 升级脚本
ALTER TABLE users ADD COLUMN avatar_url VARCHAR(500) AFTER real_name;
ALTER TABLE devices ADD COLUMN location VARCHAR(200) AFTER mac_address;
CREATE INDEX idx_users_status ON users(status);
```

### 数据导入导出
```sql
-- 导出CSV格式
SELECT * INTO OUTFILE '/tmp/users.csv'
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
FROM users;

-- 导入CSV格式
LOAD DATA INFILE '/tmp/users.csv'
INTO TABLE users
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n';
```

## 安全配置

### 1. 用户权限
```sql
-- 创建应用用户
CREATE USER 'dataglove_app'@'%' IDENTIFIED BY 'strong_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON data_glove_db.* TO 'dataglove_app'@'%';

-- 创建只读用户
CREATE USER 'dataglove_readonly'@'%' IDENTIFIED BY 'readonly_password';
GRANT SELECT ON data_glove_db.* TO 'dataglove_readonly'@'%';
```

### 2. 数据加密
```sql
-- 启用透明数据加密
ALTER TABLE users ENCRYPTION='Y';
ALTER TABLE devices ENCRYPTION='Y';
```

### 3. 连接安全
```sql
-- 要求SSL连接
GRANT ALL PRIVILEGES ON data_glove_db.* TO 'dataglove_app'@'%' REQUIRE SSL;
```

---

**注意事项**：
1. 定期备份数据库
2. 监控数据库性能
3. 及时清理历史数据
4. 保持数据库版本更新
5. 配置合适的字符集和排序规则