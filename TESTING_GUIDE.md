# 🚀 数据手套系统测试指南

## 系统启动完成状态

✅ **前端服务**: 已启动在 http://localhost:3000  
⏳ **后端服务**: 需要启动（下面有说明）  
⏳ **数据库**: 需要配置（下面有说明）

---

## 1. 💾 数据库设置

### 启动MySQL服务
```bash
# macOS
brew services start mysql

# 或者如果已经安装了MySQL
sudo /usr/local/mysql/support-files/mysql.server start
```

### 创建数据库
```bash
# 登录MySQL（密码：xhf2021963）
mysql -u root -p

# 创建数据库
CREATE DATABASE data_glove_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 退出MySQL
exit
```

### 导入数据库结构
```bash
# 在项目根目录执行
mysql -u root -pxhf2021963 data_glove_db < backend/src/main/resources/db/migration/V1__init_database.sql
```

---

## 2. 🔧 启动后端服务

### 方式一：使用启动脚本（推荐）
```bash
# 给脚本执行权限
chmod +x start_system.sh

# 运行启动脚本
./start_system.sh
```

### 方式二：手动启动
```bash
# 进入后端目录
cd backend

# 设置环境变量
export DB_USERNAME=root
export DB_PASSWORD=xhf2021963
export JWT_SECRET=dataglove_jwt_secret_key_2024

# 启动后端服务
mvn spring-boot:run
```

后端服务启动后，可以访问：
- **API接口**: http://localhost:8080/api
- **健康检查**: http://localhost:8080/actuator/health

---

## 3. 🌐 访问前端系统

打开浏览器，访问：http://localhost:3000

### 默认登录信息
- **用户名**: admin
- **密码**: admin123

### 系统功能页面
- **仪表板**: 系统概览和统计信息
- **用户管理**: 用户列表、角色管理
- **设备管理**: 设备注册、状态监控
- **数据管理**: 传感器数据、手势识别结果、学习记录

---

## 4. 🧪 API测试

### 自动化测试
```bash
# 确保后端服务已启动
chmod +x test_api.sh
./test_api.sh
```

### 手动测试关键接口

#### 1. 用户登录
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

#### 2. 获取用户列表（需要token）
```bash
# 先获取token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}' \
  | jq -r '.data.accessToken')

# 使用token获取用户列表
curl -X GET "http://localhost:8080/api/v1/users?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 5. 📊 功能测试清单

### 前端功能测试
- [ ] 用户登录/登出
- [ ] 用户管理（增删改查）
- [ ] 设备管理（增删改查）
- [ ] 数据查询和显示
- [ ] 响应式布局（移动端适配）

### 后端API测试
- [ ] 用户认证API
- [ ] 用户管理API
- [ ] 设备管理API
- [ ] 数据查询API
- [ ] 外部集成API

### 数据库测试
- [ ] 用户数据CRUD
- [ ] 设备数据CRUD
- [ ] 传感器数据存储
- [ ] 手势识别结果存储
- [ ] 学习记录更新

---

## 6. 🔍 故障排除

### 常见问题

#### 后端启动失败
```bash
# 检查数据库连接
mysql -u root -pxhf2021963 -e "SELECT 1;"

# 检查端口占用
lsof -i :8080
```

#### 前端无法访问后端
- 检查后端服务是否启动：`curl http://localhost:8080/actuator/health`
- 检查防火墙设置
- 确认代理配置正确

#### 数据库连接失败
- 确认MySQL服务运行状态
- 检查用户名密码是否正确
- 确认数据库名称正确

---

## 7. 📱 移动端测试

系统支持响应式设计，可以在移动设备上测试：

1. 手机浏览器访问：`http://[你的IP地址]:3000`
2. 测试触摸操作
3. 验证布局自适应

---

## 8. 🚀 性能测试

### 并发测试
```bash
# 安装Apache Bench
brew install httpie

# 测试登录接口性能
ab -n 1000 -c 10 -T application/json -p login_data.json http://localhost:8080/api/v1/auth/login
```

### 负载测试
```bash
# 创建测试数据
echo '{"username": "admin", "password": "admin123"}' > login_data.json

# 运行负载测试
ab -n 1000 -c 10 -T application/json -p login_data.json http://localhost:8080/api/v1/auth/login
```

---

## 9. 📝 测试报告

测试完成后，请记录：
- ✅ 成功的功能
- ❌ 发现的问题
- ⚠️ 需要改进的地方
- 💡 建议和反馈

---

## 10. 📞 获取帮助

如果遇到问题，请：
1. 检查日志文件
2. 查看控制台错误信息
3. 参考项目文档：`docs/` 目录
4. 查看API文档：`docs/API.md`

---

**🎉 现在您可以开始测试数据手套系统了！**