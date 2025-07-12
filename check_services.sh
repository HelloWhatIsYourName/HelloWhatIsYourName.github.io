#!/bin/bash

# 测试数据库连接
echo "正在测试数据库连接..."

# 检查MySQL服务是否运行
if ! pgrep -x "mysqld" > /dev/null; then
    echo "MySQL服务未运行，请先启动MySQL服务"
    echo "macOS: brew services start mysql"
    echo "Ubuntu: sudo systemctl start mysql"
    exit 1
fi

# 测试数据库连接
mysql -u root -p -e "SELECT 1;" 2>/dev/null
if [ $? -eq 0 ]; then
    echo "✓ 数据库连接成功"
else
    echo "✗ 数据库连接失败"
    echo "请检查MySQL服务状态和用户密码"
    exit 1
fi

# 检查数据库是否存在
DB_EXISTS=$(mysql -u root -p -e "SHOW DATABASES LIKE 'data_glove_db';" 2>/dev/null | wc -l)
if [ $DB_EXISTS -gt 1 ]; then
    echo "✓ 数据库 data_glove_db 已存在"
else
    echo "× 数据库 data_glove_db 不存在"
    echo "正在创建数据库..."
    mysql -u root -p -e "CREATE DATABASE data_glove_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null
    if [ $? -eq 0 ]; then
        echo "✓ 数据库创建成功"
    else
        echo "✗ 数据库创建失败"
        exit 1
    fi
fi

# 检查Redis服务
if ! pgrep -x "redis-server" > /dev/null; then
    echo "Redis服务未运行，请先启动Redis服务"
    echo "macOS: brew services start redis"
    echo "Ubuntu: sudo systemctl start redis-server"
    exit 1
else
    echo "✓ Redis服务正在运行"
fi

echo "所有服务检查完成！"