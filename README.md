# 数据手套系统 - Web管理平台

## 项目简介

数据手套系统是一个基于Spring Boot和Vue.js的现代化Web管理平台，专门为数据手套手语翻译系统设计。系统提供了完整的用户管理、设备管理、数据采集和分析功能，支持多用户、多设备的协同工作。

## 技术栈

### 后端
- **框架**: Spring Boot 3.2.0
- **安全**: Spring Security + JWT
- **数据库**: MySQL 8.0 + JPA/Hibernate
- **缓存**: Redis 6.0
- **文档**: Swagger/OpenAPI 3.0
- **构建工具**: Maven

### 前端
- **框架**: Vue 3.3.8 + TypeScript
- **UI库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP客户端**: Axios
- **图表**: ECharts
- **构建工具**: Vite

## 功能特性

### 🔐 用户管理
- 用户注册/登录
- 角色权限管理（管理员/普通用户）
- 个人信息管理
- 密码修改和重置

### 📱 设备管理
- 设备注册和认证
- 设备状态监控
- 用户设备绑定/解绑
- 设备心跳检测

### 📊 数据管理
- 传感器数据采集
- 手势识别结果
- 学习记录跟踪
- 数据统计分析

### 🎨 用户界面
- 响应式设计
- 明暗主题切换
- 数据可视化
- 实时状态更新

### 🔧 系统特性
- RESTful API设计
- JWT认证机制
- 数据分页和搜索
- 国际化支持预留
- 系统监控和日志

## 项目结构

```
data-glove-web/
├── backend/                    # 后端Spring Boot项目
│   ├── src/main/java/com/dataglove/web/
│   │   ├── controller/         # 控制器层
│   │   ├── service/           # 业务服务层
│   │   ├── repository/        # 数据访问层
│   │   ├── entity/            # 实体类
│   │   ├── dto/               # 数据传输对象
│   │   ├── security/          # 安全配置
│   │   ├── config/            # 配置类
│   │   ├── common/            # 通用工具
│   │   └── integration/       # 外部集成
│   ├── src/main/resources/
│   │   ├── application.yml    # 应用配置
│   │   └── db/migration/      # 数据库迁移脚本
│   └── pom.xml                # Maven依赖配置
├── frontend/                   # 前端Vue项目
│   ├── src/
│   │   ├── api/               # API接口
│   │   ├── components/        # 组件
│   │   ├── views/             # 页面视图
│   │   ├── store/             # 状态管理
│   │   ├── router/            # 路由配置
│   │   ├── utils/             # 工具函数
│   │   └── types/             # TypeScript类型
│   ├── package.json           # 依赖配置
│   └── vite.config.ts         # 构建配置
└── docs/                      # 项目文档
    ├── API.md                 # API文档
    ├── DATABASE.md            # 数据库文档
    └── DEPLOYMENT.md          # 部署文档
```

## 快速开始

### 环境要求
- Java 17+
- Node.js 16+
- MySQL 8.0+
- Redis 6.0+

### 后端部署

1. **克隆项目**
```bash
git clone <repository-url>
cd data-glove-web
```

2. **配置数据库**
```sql
CREATE DATABASE data_glove_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **导入数据库结构**
```bash
mysql -u root -p data_glove_db < backend/src/main/resources/db/migration/V1__init_database.sql
```

4. **配置应用**
```yaml
# backend/src/main/resources/application-dev.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/data_glove_db
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379
```

5. **启动后端服务**
```bash
cd backend
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 前端部署

1. **安装依赖**
```bash
cd frontend
npm install
```

2. **启动开发服务器**
```bash
npm run dev
```

前端应用将在 `http://localhost:3000` 启动

## API文档

后端提供完整的RESTful API，支持：

- **认证授权**: 用户登录、注册、权限验证
- **用户管理**: 用户CRUD操作、角色管理
- **设备管理**: 设备注册、绑定、状态监控
- **数据管理**: 传感器数据、手势识别、学习记录
- **外部集成**: IoT Hub、AI服务、移动端接口

详细API文档请参考 [API.md](docs/API.md)

## 数据库设计

系统采用关系型数据库设计，包含：

- **用户系统**: 用户表、角色表、权限关联
- **设备系统**: 设备表、绑定关系表
- **数据系统**: 传感器数据、手势识别、学习记录
- **系统表**: 操作日志、系统配置

详细数据库文档请参考 [DATABASE.md](docs/DATABASE.md)

## 部署指南

系统支持多种部署方式：

- **传统部署**: JAR包 + MySQL + Redis
- **容器化部署**: Docker + Docker Compose
- **云平台部署**: 支持主流云服务

详细部署文档请参考 [DEPLOYMENT.md](docs/DEPLOYMENT.md)

## 系统架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   用户界面      │    │   API网关       │    │   业务服务      │
│   (Vue.js)      │◄──►│   (Spring Boot) │◄──►│   (微服务)      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │                       │
                                ▼                       ▼
                    ┌─────────────────┐    ┌─────────────────┐
                    │   缓存层        │    │   数据库        │
                    │   (Redis)       │    │   (MySQL)       │
                    └─────────────────┘    └─────────────────┘
```

## 开发规范

### 代码风格
- 后端遵循阿里巴巴Java开发规范
- 前端使用ESLint + Prettier格式化
- 统一的命名约定和注释规范

### 提交规范
```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
test: 测试相关
chore: 构建/工具相关
```

### 分支管理
- `main`: 主分支，用于生产环境
- `develop`: 开发分支，用于集成测试
- `feature/*`: 功能分支
- `hotfix/*`: 热修复分支

## 测试

### 后端测试
```bash
cd backend
mvn test
```

### 前端测试
```bash
cd frontend
npm run test
```

### 集成测试
```bash
# 启动测试环境
docker-compose -f docker-compose.test.yml up -d

# 运行集成测试
npm run test:integration
```

## 监控和日志

### 应用监控
- Spring Boot Actuator健康检查
- Prometheus指标收集
- Grafana数据可视化

### 日志管理
- 结构化日志输出
- 日志级别控制
- 日志文件滚动

## 安全考虑

### 认证授权
- JWT令牌认证
- 基于角色的访问控制
- 接口权限验证

### 数据安全
- 密码加密存储
- 敏感数据脱敏
- SQL注入防护

### 网络安全
- HTTPS通信
- CORS策略配置
- 请求频率限制

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系我们

- 项目主页: [GitHub Repository]
- 问题反馈: [GitHub Issues]
- 技术支持: [Email]

---

**注意**: 本项目为数据手套手语翻译系统的Web管理平台，请确保在生产环境中使用安全的配置和密码。