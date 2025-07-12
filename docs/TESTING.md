# 数据手套系统测试指南

## 测试环境准备

### 1. 环境要求
- **Java**: OpenJDK 17+
- **Node.js**: 16.0+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Maven**: 3.6+

### 2. 启动测试环境

#### 启动数据库服务
```bash
# 启动MySQL
sudo systemctl start mysql

# 启动Redis
sudo systemctl start redis-server

# 或使用Docker
docker run -d --name mysql-test -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=data_glove_db mysql:8.0
docker run -d --name redis-test -p 6379:6379 redis:6.2-alpine
```

#### 初始化数据库
```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE data_glove_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 导入数据库结构
mysql -u root -p data_glove_db < backend/src/main/resources/db/migration/V1__init_database.sql
```

## 后端测试

### 1. 单元测试
```bash
cd backend

# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=UserServiceTest

# 运行测试并生成报告
mvn test jacoco:report
```

### 2. 集成测试
```bash
# 启动应用
mvn spring-boot:run

# 检查应用状态
curl http://localhost:8080/actuator/health
```

### 3. API测试

#### 使用curl测试

**1. 用户注册**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "confirmPassword": "password123",
    "phone": "13800138000",
    "realName": "测试用户"
  }'
```

**2. 用户登录**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**3. 获取用户列表（需要token）**
```bash
# 先获取token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}' \
  | jq -r '.data.accessToken')

# 使用token访问API
curl -X GET "http://localhost:8080/api/v1/users?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

**4. 创建设备**
```bash
curl -X POST http://localhost:8080/api/v1/devices \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "deviceId": "TEST_DEVICE_001",
    "deviceName": "测试数据手套",
    "deviceType": "DATA_GLOVE",
    "hardwareVersion": "1.0.0",
    "firmwareVersion": "1.0.0",
    "macAddress": "00:11:22:33:44:55",
    "location": "测试实验室",
    "description": "用于测试的数据手套设备"
  }'
```

#### 使用Postman测试

1. **导入API集合**
   - 创建新的Collection
   - 设置Base URL: `http://localhost:8080/api`
   - 配置Authorization: Bearer Token

2. **测试用例**
   - 用户注册/登录
   - 用户管理CRUD
   - 设备管理CRUD
   - 数据查询接口

### 4. 性能测试

#### 使用Apache Bench
```bash
# 测试登录接口
ab -n 1000 -c 10 -T application/json -p login_data.json http://localhost:8080/api/v1/auth/login

# login_data.json内容
echo '{"username": "admin", "password": "admin123"}' > login_data.json
```

#### 使用JMeter
1. 创建测试计划
2. 添加HTTP请求采样器
3. 配置线程组（用户数、循环次数）
4. 添加监听器查看结果

## 前端测试

### 1. 安装依赖
```bash
cd frontend
npm install
```

### 2. 单元测试
```bash
# 运行单元测试
npm run test

# 运行测试并生成覆盖率报告
npm run test:coverage

# 监听模式运行测试
npm run test:watch
```

### 3. 端到端测试
```bash
# 安装Cypress
npm install cypress --save-dev

# 运行Cypress测试
npx cypress open

# 或命令行运行
npx cypress run
```

### 4. 手动测试

#### 启动开发服务器
```bash
npm run dev
```

#### 测试功能点

**1. 用户认证**
- 访问 `http://localhost:3000/login`
- 测试登录功能（用户名: admin, 密码: admin123）
- 测试登录失败情况
- 测试注册功能
- 测试退出登录

**2. 用户管理**
- 访问用户管理页面
- 测试用户列表显示
- 测试用户搜索功能
- 测试用户创建/编辑/删除

**3. 设备管理**
- 访问设备管理页面
- 测试设备列表显示
- 测试设备状态更新
- 测试设备绑定/解绑

**4. 数据管理**
- 访问数据管理页面
- 测试数据查询功能
- 测试数据可视化
- 测试数据导出功能

### 5. 浏览器兼容性测试
测试以下浏览器：
- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## 自动化测试

### 1. 创建测试脚本

**backend/test_api.sh**
```bash
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
        response=$(curl -s -w "%{http_code}" -o /tmp/api_response.json "$url")
    else
        response=$(curl -s -w "%{http_code}" -X "$method" -H "Content-Type: application/json" -d "$data" "$url" -o /tmp/api_response.json)
    fi
    
    if [ "$response" = "$expected_code" ]; then
        echo -e "${GREEN}✓${NC} $description"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}✗${NC} $description (Expected: $expected_code, Got: $response)"
        FAILED_TESTS=$((FAILED_TESTS + 1))
        cat /tmp/api_response.json
    fi
}

# 开始测试
echo -e "${YELLOW}开始API测试...${NC}"

# 测试用户认证
echo -e "\n${YELLOW}测试用户认证${NC}"
test_api "POST" "$BASE_URL/v1/auth/login" '{"username": "admin", "password": "admin123"}' "200" "管理员登录"

# 获取token
TOKEN=$(curl -s -X POST "$BASE_URL/v1/auth/login" -H "Content-Type: application/json" -d '{"username": "admin", "password": "admin123"}' | jq -r '.data.accessToken')

# 测试用户管理
echo -e "\n${YELLOW}测试用户管理${NC}"
test_api "GET" "$BASE_URL/v1/users?page=0&size=10" "" "200" "获取用户列表"

# 测试设备管理
echo -e "\n${YELLOW}测试设备管理${NC}"
test_api "GET" "$BASE_URL/v1/devices?page=0&size=10" "" "200" "获取设备列表"

# 输出测试结果
echo -e "\n${YELLOW}测试结果统计${NC}"
echo "总测试数: $TOTAL_TESTS"
echo -e "通过: ${GREEN}$PASSED_TESTS${NC}"
echo -e "失败: ${RED}$FAILED_TESTS${NC}"

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "\n${GREEN}所有测试通过！${NC}"
    exit 0
else
    echo -e "\n${RED}有测试失败，请检查！${NC}"
    exit 1
fi
```

### 2. 运行自动化测试
```bash
# 给脚本执行权限
chmod +x backend/test_api.sh

# 运行测试
./backend/test_api.sh
```

## 集成测试

### 1. 使用Docker Compose
```yaml
# docker-compose.test.yml
version: '3.8'
services:
  mysql-test:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: data_glove_db
    ports:
      - "3307:3306"
    
  redis-test:
    image: redis:6.2-alpine
    ports:
      - "6380:6379"
    
  backend-test:
    build: ./backend
    depends_on:
      - mysql-test
      - redis-test
    environment:
      - SPRING_PROFILES_ACTIVE=test
      - DB_HOST=mysql-test
      - REDIS_HOST=redis-test
    ports:
      - "8081:8080"
```

### 2. 运行集成测试
```bash
# 启动测试环境
docker-compose -f docker-compose.test.yml up -d

# 等待服务启动
sleep 30

# 运行测试
./backend/test_api.sh

# 清理环境
docker-compose -f docker-compose.test.yml down
```

## 压力测试

### 1. 使用K6
```javascript
// load_test.js
import http from 'k6/http';
import { check } from 'k6';

export let options = {
  stages: [
    { duration: '2m', target: 100 },
    { duration: '5m', target: 100 },
    { duration: '2m', target: 200 },
    { duration: '5m', target: 200 },
    { duration: '2m', target: 0 },
  ],
};

export default function () {
  let response = http.post('http://localhost:8080/api/v1/auth/login', {
    username: 'admin',
    password: 'admin123',
  });
  
  check(response, {
    'status is 200': (r) => r.status === 200,
    'response time < 500ms': (r) => r.timings.duration < 500,
  });
}
```

### 2. 运行压力测试
```bash
# 安装K6
curl https://github.com/grafana/k6/releases/download/v0.47.0/k6-v0.47.0-linux-amd64.tar.gz -L | tar xvz --strip-components 1

# 运行测试
./k6 run load_test.js
```

## 测试报告

### 1. 生成测试报告
```bash
# 后端测试报告
mvn clean test jacoco:report

# 前端测试报告
npm run test:coverage
```

### 2. 查看报告
- 后端报告: `backend/target/site/jacoco/index.html`
- 前端报告: `frontend/coverage/index.html`

## 常见问题排查

### 1. 连接数据库失败
```bash
# 检查数据库服务状态
sudo systemctl status mysql

# 检查数据库连接
mysql -u root -p -e "SELECT 1"

# 检查数据库配置
cat backend/src/main/resources/application-dev.yml
```

### 2. Redis连接失败
```bash
# 检查Redis服务状态
sudo systemctl status redis-server

# 检查Redis连接
redis-cli ping
```

### 3. 前端无法访问后端API
```bash
# 检查后端服务状态
curl http://localhost:8080/actuator/health

# 检查CORS配置
curl -H "Origin: http://localhost:3000" -v http://localhost:8080/api/v1/auth/login
```

## 测试最佳实践

1. **测试驱动开发**: 先写测试，再写代码
2. **持续集成**: 每次代码提交都运行测试
3. **测试覆盖率**: 保持代码覆盖率在80%以上
4. **性能测试**: 定期进行性能测试，监控系统性能
5. **安全测试**: 定期进行安全性测试，检查漏洞

---

**注意事项**：
1. 测试环境应该与生产环境尽可能相似
2. 测试数据应该与生产数据隔离
3. 定期清理测试数据
4. 保持测试用例的维护和更新