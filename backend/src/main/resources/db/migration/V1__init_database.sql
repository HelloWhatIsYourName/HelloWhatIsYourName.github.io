-- 数据手套系统数据库创建脚本
CREATE DATABASE IF NOT EXISTS data_glove_db 
    DEFAULT CHARACTER SET utf8mb4 
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE data_glove_db;

-- 用户基础信息表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    email VARCHAR(100) UNIQUE NOT NULL COMMENT '邮箱',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    real_name VARCHAR(100) COMMENT '真实姓名',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    status ENUM('ACTIVE', 'INACTIVE', 'BANNED') DEFAULT 'ACTIVE' COMMENT '账户状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_at TIMESTAMP NULL COMMENT '最后登录时间',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户基础信息表';

-- 角色表
CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) UNIQUE NOT NULL COMMENT '角色名称',
    description VARCHAR(200) COMMENT '角色描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_role_name (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 设备信息表
CREATE TABLE devices (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '设备ID',
    device_id VARCHAR(100) UNIQUE NOT NULL COMMENT '设备唯一标识',
    device_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    device_type ENUM('DATA_GLOVE') DEFAULT 'DATA_GLOVE' COMMENT '设备类型',
    hardware_version VARCHAR(50) COMMENT '硬件版本',
    firmware_version VARCHAR(50) COMMENT '固件版本',
    mac_address VARCHAR(17) COMMENT 'MAC地址',
    status ENUM('ONLINE', 'OFFLINE', 'MAINTENANCE') DEFAULT 'OFFLINE' COMMENT '设备状态',
    location VARCHAR(200) COMMENT '设备位置',
    description TEXT COMMENT '设备描述',
    last_heartbeat TIMESTAMP NULL COMMENT '最后心跳时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_device_id (device_id),
    INDEX idx_status (status),
    INDEX idx_device_type (device_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备信息表';

-- 用户设备绑定表
CREATE TABLE user_devices (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '绑定ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    device_id BIGINT NOT NULL COMMENT '设备ID',
    bind_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
    unbind_time TIMESTAMP NULL COMMENT '解绑时间',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否活跃',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_device_active (user_id, device_id, is_active),
    INDEX idx_user_id (user_id),
    INDEX idx_device_id (device_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户设备绑定表';

-- 传感器数据表
CREATE TABLE sensor_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '数据ID',
    device_id BIGINT NOT NULL COMMENT '设备ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    sensor_type ENUM('FLEX', 'STRAIN', 'IMU') NOT NULL COMMENT '传感器类型',
    sensor_position VARCHAR(50) COMMENT '传感器位置',
    data_value JSON NOT NULL COMMENT '传感器数据(JSON格式)',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '数据时间戳',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_device_timestamp (device_id, timestamp),
    INDEX idx_user_timestamp (user_id, timestamp),
    INDEX idx_sensor_type (sensor_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='传感器数据表';

-- 手势识别结果表
CREATE TABLE gesture_results (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '识别结果ID',
    device_id BIGINT NOT NULL COMMENT '设备ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    gesture_name VARCHAR(100) NOT NULL COMMENT '手势名称',
    confidence DECIMAL(5,4) NOT NULL COMMENT '置信度',
    raw_data JSON COMMENT '原始数据',
    processed_data JSON COMMENT '处理后数据',
    recognition_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '识别时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_device_recognition (device_id, recognition_time),
    INDEX idx_user_recognition (user_id, recognition_time),
    INDEX idx_gesture_name (gesture_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='手势识别结果表';

-- 学习记录表
CREATE TABLE learning_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学习记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    gesture_name VARCHAR(100) NOT NULL COMMENT '手势名称',
    practice_count INT DEFAULT 0 COMMENT '练习次数',
    success_count INT DEFAULT 0 COMMENT '成功次数',
    average_confidence DECIMAL(5,4) COMMENT '平均置信度',
    last_practice_time TIMESTAMP NULL COMMENT '最后练习时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_gesture (user_id, gesture_name),
    INDEX idx_user_id (user_id),
    INDEX idx_gesture_name (gesture_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习记录表';

-- 系统日志表
CREATE TABLE system_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    action VARCHAR(100) NOT NULL COMMENT '操作类型',
    resource VARCHAR(200) COMMENT '资源',
    details TEXT COMMENT '详细信息',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';

-- 初始化角色数据
INSERT INTO roles (role_name, description) VALUES
('ADMIN', '系统管理员'),
('USER', '普通用户');

-- 初始化管理员用户 (密码: admin123)
INSERT INTO users (username, email, password_hash, real_name, status) VALUES
('admin', 'admin@dataglove.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P6rN7nwTCWKzAu', '系统管理员', 'ACTIVE');

-- 分配管理员角色
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1);