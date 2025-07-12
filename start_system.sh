#!/bin/bash

# 数据手套系统启动脚本
echo "=================================="
echo "数据手套系统启动脚本"
echo "=================================="

# 设置环境变量
export DB_USERNAME=root
export DB_PASSWORD=xhf2021963
export JWT_SECRET=dataglove_jwt_secret_key_2024_very_secure_key_for_production

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查MySQL服务
echo -e "\n${YELLOW}1. 检查MySQL服务...${NC}"
if pgrep -x "mysqld" > /dev/null; then
    echo -e "${GREEN}✓${NC} MySQL服务正在运行"
else
    echo -e "${RED}✗${NC} MySQL服务未运行"
    echo "请启动MySQL服务："
    echo "  macOS: brew services start mysql"
    echo "  Ubuntu: sudo systemctl start mysql"
    exit 1
fi

# 测试数据库连接
echo -e "\n${YELLOW}2. 测试数据库连接...${NC}"
mysql -u root -pxhf2021963 -e "SELECT 1;" 2>/dev/null
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓${NC} 数据库连接成功"
else
    echo -e "${RED}✗${NC} 数据库连接失败"
    echo "请检查MySQL服务状态和密码"
    exit 1
fi

# 检查数据库是否存在
echo -e "\n${YELLOW}3. 检查数据库...${NC}"
DB_EXISTS=$(mysql -u root -pxhf2021963 -e "SHOW DATABASES LIKE 'data_glove_db';" 2>/dev/null | wc -l)
if [ $DB_EXISTS -gt 1 ]; then
    echo -e "${GREEN}✓${NC} 数据库 data_glove_db 已存在"
else
    echo -e "${YELLOW}×${NC} 数据库 data_glove_db 不存在，正在创建..."
    mysql -u root -pxhf2021963 -e "CREATE DATABASE data_glove_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓${NC} 数据库创建成功"
        
        # 导入数据库结构
        echo -e "${YELLOW}导入数据库结构...${NC}"
        mysql -u root -pxhf2021963 data_glove_db < backend/src/main/resources/db/migration/V1__init_database.sql 2>/dev/null
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}✓${NC} 数据库结构导入成功"
        else
            echo -e "${RED}✗${NC} 数据库结构导入失败"
            exit 1
        fi
    else
        echo -e "${RED}✗${NC} 数据库创建失败"
        exit 1
    fi
fi

# 检查Redis服务
echo -e "\n${YELLOW}4. 检查Redis服务...${NC}"
if pgrep -x "redis-server" > /dev/null; then
    echo -e "${GREEN}✓${NC} Redis服务正在运行"
else
    echo -e "${RED}✗${NC} Redis服务未运行"
    echo "请启动Redis服务："
    echo "  macOS: brew services start redis"
    echo "  Ubuntu: sudo systemctl start redis-server"
    exit 1
fi

echo -e "\n${GREEN}所有服务检查完成！${NC}"
echo -e "\n${YELLOW}现在可以启动应用：${NC}"
echo "1. 启动后端服务："
echo "   cd backend && mvn spring-boot:run"
echo ""
echo "2. 启动前端服务（新终端）："
echo "   cd frontend && npm install && npm run dev"
echo ""
echo "3. 运行API测试（新终端）："
echo "   ./test_api.sh"

# 询问是否立即启动后端
echo -e "\n${YELLOW}是否立即启动后端服务？ (y/n)${NC}"
read -r response
if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
    echo -e "${YELLOW}正在启动后端服务...${NC}"
    cd backend
    mvn spring-boot:run
fi