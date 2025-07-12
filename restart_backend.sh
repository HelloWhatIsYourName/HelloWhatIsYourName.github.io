#!/bin/bash

echo "🚀 重新启动数据手套系统后端服务..."

# 杀掉可能存在的旧进程
echo "清理旧进程..."
pkill -f "DataGloveWebApplication" 2>/dev/null || true
pkill -f "spring-boot:run" 2>/dev/null || true

# 设置环境变量
export DB_USERNAME=root
export DB_PASSWORD=xhf2021963
export JWT_SECRET=dataglove_jwt_secret_key_2024_very_secure_key_for_production

# 进入后端目录
cd "$(dirname "$0")/backend"

echo "📡 检查数据库连接..."
mysql -u root -pxhf2021963 -e "SELECT 1;" 2>/dev/null
if [ $? -ne 0 ]; then
    echo "❌ 数据库连接失败，请检查MySQL服务"
    exit 1
fi
echo "✅ 数据库连接成功"

echo "🔄 启动Spring Boot应用..."
nohup mvn spring-boot:run > spring-boot.log 2>&1 &
SPRING_PID=$!

echo "⏱️  等待服务启动（进程ID: $SPRING_PID）..."

# 等待服务启动，最多等待60秒
for i in {1..60}; do
    sleep 1
    if curl -s http://localhost:8080/api/v1/auth/login >/dev/null 2>&1; then
        echo "✅ 后端服务启动成功！"
        echo "🌐 API地址: http://localhost:8080/api"
        echo "📱 前端地址: http://localhost:3000"
        echo "👤 默认登录: admin / admin123"
        echo ""
        echo "🎉 系统已就绪！您可以开始测试了。"
        exit 0
    fi
    if [ $((i % 10)) -eq 0 ]; then
        echo "等待中... (${i}s/60s)"
    fi
done

echo "❌ 后端服务启动超时"
echo "📋 检查日志文件: backend/spring-boot.log"
tail -20 spring-boot.log
exit 1