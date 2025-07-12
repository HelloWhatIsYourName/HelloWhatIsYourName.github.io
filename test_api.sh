#!/bin/bash

BASE_URL="http://localhost:8080/api"
TOKEN=""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 测试结果统计
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# 测试函数
test_api() {
    local method=$1
    local url=$2
    local data=$3
    local expected_code=$4
    local description=$5
    
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    
    if [ "$method" = "GET" ]; then
        if [ -n "$TOKEN" ]; then
            response=$(curl -s -w "%{http_code}" -H "Authorization: Bearer $TOKEN" -o /tmp/api_response.json "$url")
        else
            response=$(curl -s -w "%{http_code}" -o /tmp/api_response.json "$url")
        fi
    else
        if [ -n "$TOKEN" ]; then
            response=$(curl -s -w "%{http_code}" -X "$method" -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d "$data" "$url" -o /tmp/api_response.json)
        else
            response=$(curl -s -w "%{http_code}" -X "$method" -H "Content-Type: application/json" -d "$data" "$url" -o /tmp/api_response.json)
        fi
    fi
    
    if [ "$response" = "$expected_code" ]; then
        echo -e "${GREEN}✓${NC} $description"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}✗${NC} $description (Expected: $expected_code, Got: $response)"
        FAILED_TESTS=$((FAILED_TESTS + 1))
        if [ -f /tmp/api_response.json ]; then
            echo "Response: $(cat /tmp/api_response.json)"
        fi
    fi
}

# 检查服务是否启动
check_service() {
    echo -e "${YELLOW}检查服务状态...${NC}"
    if curl -s http://localhost:8080/actuator/health > /dev/null; then
        echo -e "${GREEN}✓${NC} 后端服务正常运行"
    else
        echo -e "${RED}✗${NC} 后端服务未启动，请先启动后端服务"
        exit 1
    fi
}

# 开始测试
echo -e "${YELLOW}数据手套系统 API 测试${NC}"
echo "=================================="

# 检查服务状态
check_service

# 测试用户认证
echo -e "\n${YELLOW}1. 测试用户认证${NC}"
test_api "POST" "$BASE_URL/v1/auth/login" '{"username": "admin", "password": "admin123"}' "200" "管理员登录"

# 获取token
echo -e "\n${YELLOW}获取访问令牌...${NC}"
TOKEN=$(curl -s -X POST "$BASE_URL/v1/auth/login" -H "Content-Type: application/json" -d '{"username": "admin", "password": "admin123"}' | jq -r '.data.accessToken' 2>/dev/null)

if [ "$TOKEN" = "null" ] || [ -z "$TOKEN" ]; then
    echo -e "${RED}✗${NC} 无法获取访问令牌"
    exit 1
else
    echo -e "${GREEN}✓${NC} 成功获取访问令牌"
fi

# 测试错误的登录
test_api "POST" "$BASE_URL/v1/auth/login" '{"username": "admin", "password": "wrong"}' "400" "错误密码登录"

# 测试用户管理
echo -e "\n${YELLOW}2. 测试用户管理${NC}"
test_api "GET" "$BASE_URL/v1/users?page=0&size=10" "" "200" "获取用户列表"
test_api "GET" "$BASE_URL/v1/users/1" "" "200" "获取用户详情"

# 测试创建用户
test_api "POST" "$BASE_URL/v1/users" '{
    "username": "testuser_' $(date +%s) '",
    "email": "test' $(date +%s) '@example.com",
    "password": "password123",
    "confirmPassword": "password123",
    "phone": "13800138000",
    "realName": "测试用户"
}' "201" "创建新用户"

# 测试设备管理
echo -e "\n${YELLOW}3. 测试设备管理${NC}"
test_api "GET" "$BASE_URL/v1/devices?page=0&size=10" "" "200" "获取设备列表"

# 测试创建设备
test_api "POST" "$BASE_URL/v1/devices" '{
    "deviceId": "TEST_DEVICE_' $(date +%s) '",
    "deviceName": "测试数据手套",
    "deviceType": "DATA_GLOVE",
    "hardwareVersion": "1.0.0",
    "firmwareVersion": "1.0.0",
    "macAddress": "00:11:22:33:44:55",
    "location": "测试实验室",
    "description": "用于测试的数据手套设备"
}' "201" "创建新设备"

# 测试数据管理
echo -e "\n${YELLOW}4. 测试数据管理${NC}"
test_api "GET" "$BASE_URL/v1/data/sensor-data?page=0&size=10" "" "200" "获取传感器数据"
test_api "GET" "$BASE_URL/v1/data/gesture-results?page=0&size=10" "" "200" "获取手势识别结果"
test_api "GET" "$BASE_URL/v1/data/learning-records?page=0&size=10" "" "200" "获取学习记录"

# 测试外部集成接口
echo -e "\n${YELLOW}5. 测试外部集成接口${NC}"
test_api "POST" "$BASE_URL/v1/integration/iot/device-data" '{
    "deviceId": "TEST_DEVICE_001",
    "sensorType": "FLEX",
    "sensorPosition": "thumb",
    "dataValue": {
        "value": 0.75,
        "unit": "voltage",
        "timestamp": "2023-12-01T10:00:00"
    },
    "timestamp": "2023-12-01T10:00:00"
}' "200" "接收设备数据"

test_api "POST" "$BASE_URL/v1/integration/iot/device-status" '{
    "deviceId": "TEST_DEVICE_001",
    "status": "ONLINE",
    "timestamp": "2023-12-01T10:00:00"
}' "200" "更新设备状态"

# 测试健康检查
echo -e "\n${YELLOW}6. 测试系统健康检查${NC}"
test_api "GET" "http://localhost:8080/actuator/health" "" "200" "系统健康检查"

# 输出测试结果
echo -e "\n${YELLOW}=================================="
echo -e "测试结果统计${NC}"
echo "总测试数: $TOTAL_TESTS"
echo -e "通过: ${GREEN}$PASSED_TESTS${NC}"
echo -e "失败: ${RED}$FAILED_TESTS${NC}"

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "\n${GREEN}🎉 所有测试通过！${NC}"
    exit 0
else
    echo -e "\n${RED}❌ 有测试失败，请检查！${NC}"
    exit 1
fi