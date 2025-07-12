#!/bin/bash

echo "正在启动数据手套系统后端服务..."

# 设置环境变量
export DB_USERNAME=root
export DB_PASSWORD=xhf2021963
export JWT_SECRET=dataglove_jwt_secret_key_2024_very_secure_key_for_production

# 进入后端目录
cd "$(dirname "$0")/backend"

# 检查数据库连接
echo "检查数据库连接..."
mysql -u root -pxhf2021963 -e "SELECT 1;" 2>/dev/null
if [ $? -ne 0 ]; then
    echo "❌ 数据库连接失败"
    exit 1
fi
echo "✅ 数据库连接成功"

# 启动后端服务
echo "启动Spring Boot应用..."
mvn spring-boot:run &
SPRING_PID=$!

echo "Spring Boot应用启动中，进程ID: $SPRING_PID"
echo "等待服务启动..."

# 等待服务启动
for i in {1..30}; do
    sleep 2
    if curl -s http://localhost:8080/api/v1/auth/login >/dev/null 2>&1; then
        echo "✅ 后端服务启动成功！"
        echo "API地址: http://localhost:8080/api"
        break
    fi
    echo "等待中... ($i/30)"
done

# 检查服务状态
if curl -s http://localhost:8080/api/v1/auth/login >/dev/null 2>&1; then
    echo "🎉 后端服务已就绪"
    echo "现在可以访问前端: http://localhost:3000"
    echo "默认登录: admin / admin123"
else
    echo "❌ 后端服务启动失败"
    kill $SPRING_PID 2>/dev/null
    exit 1
fi

# 保持脚本运行
wait $SPRING_PID