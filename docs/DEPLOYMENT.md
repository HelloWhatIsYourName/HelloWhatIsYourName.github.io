# 数据手套系统部署文档

## 系统要求

### 硬件要求
- **CPU**: 2核心以上
- **内存**: 4GB以上
- **存储**: 20GB以上可用空间
- **网络**: 稳定的网络连接

### 软件要求
- **操作系统**: Linux (Ubuntu 20.04+) / Windows Server 2019+ / macOS 10.15+
- **Java**: OpenJDK 17+
- **Node.js**: 16.0+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Nginx**: 1.18+（可选）

## 部署架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   前端 (Vue3)   │    │   后端 (Spring) │    │   数据库 (MySQL)│
│   Port: 3000    │────│   Port: 8080    │────│   Port: 3306    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
          │                       │                       │
          │                       │                       │
          │                       │            ┌─────────────────┐
          │                       │            │   缓存 (Redis)  │
          │                       └────────────│   Port: 6379    │
          │                                    └─────────────────┘
          │
┌─────────────────┐
│   反向代理      │
│   (Nginx)       │
│   Port: 80/443  │
└─────────────────┘
```

## 数据库部署

### 1. 安装 MySQL 8.0

#### Ubuntu/Debian:
```bash
sudo apt update
sudo apt install mysql-server
sudo mysql_secure_installation
```

#### CentOS/RHEL:
```bash
sudo yum install mysql-server
sudo systemctl start mysqld
sudo systemctl enable mysqld
sudo mysql_secure_installation
```

#### Docker:
```bash
docker run -d \
  --name mysql-dataglove \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=your_password \
  -e MYSQL_DATABASE=data_glove_db \
  -v mysql_data:/var/lib/mysql \
  mysql:8.0
```

### 2. 创建数据库

```sql
-- 登录MySQL
mysql -u root -p

-- 创建数据库
CREATE DATABASE data_glove_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户
CREATE USER 'dataglove'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON data_glove_db.* TO 'dataglove'@'%';
FLUSH PRIVILEGES;
```

### 3. 导入数据库结构

```bash
mysql -u dataglove -p data_glove_db < backend/src/main/resources/db/migration/V1__init_database.sql
```

## Redis 部署

### 1. 安装 Redis

#### Ubuntu/Debian:
```bash
sudo apt update
sudo apt install redis-server
sudo systemctl start redis-server
sudo systemctl enable redis-server
```

#### CentOS/RHEL:
```bash
sudo yum install redis
sudo systemctl start redis
sudo systemctl enable redis
```

#### Docker:
```bash
docker run -d \
  --name redis-dataglove \
  -p 6379:6379 \
  -v redis_data:/data \
  redis:6.2-alpine
```

### 2. 配置 Redis

编辑 `/etc/redis/redis.conf`：

```conf
# 绑定地址
bind 127.0.0.1 0.0.0.0

# 设置密码
requirepass your_redis_password

# 持久化
save 900 1
save 300 10
save 60 10000

# 内存策略
maxmemory 1gb
maxmemory-policy allkeys-lru
```

重启 Redis：
```bash
sudo systemctl restart redis-server
```

## 后端部署

### 1. 环境准备

#### 安装 Java 17:
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# CentOS/RHEL
sudo yum install java-17-openjdk-devel

# 验证安装
java -version
```

#### 安装 Maven:
```bash
# Ubuntu/Debian
sudo apt install maven

# CentOS/RHEL
sudo yum install maven

# 验证安装
mvn -version
```

### 2. 构建后端项目

```bash
cd backend

# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包项目
mvn clean package -DskipTests
```

### 3. 配置应用

创建生产环境配置文件 `application-prod.yml`：

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/data_glove_db?useSSL=true&serverTimezone=UTC
    username: dataglove
    password: ${DB_PASSWORD}
    
  redis:
    host: localhost
    port: 6379
    password: ${REDIS_PASSWORD}
    
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000

logging:
  level:
    com.dataglove.web: INFO
  file:
    name: /var/log/dataglove/application.log
```

### 4. 部署方式

#### 方式一：直接运行JAR包

```bash
# 创建应用目录
sudo mkdir -p /opt/dataglove
sudo chown $USER:$USER /opt/dataglove

# 复制JAR包
cp target/data-glove-web-1.0.0.jar /opt/dataglove/

# 创建启动脚本
cat > /opt/dataglove/start.sh << 'EOF'
#!/bin/bash
export SPRING_PROFILES_ACTIVE=prod
export DB_PASSWORD=your_db_password
export REDIS_PASSWORD=your_redis_password
export JWT_SECRET=your_jwt_secret

java -jar \
  -Xmx2g \
  -Xms1g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -Dspring.profiles.active=prod \
  /opt/dataglove/data-glove-web-1.0.0.jar
EOF

chmod +x /opt/dataglove/start.sh

# 启动应用
/opt/dataglove/start.sh
```

#### 方式二：使用 systemd 服务

创建服务文件 `/etc/systemd/system/dataglove.service`：

```ini
[Unit]
Description=Data Glove Web Service
After=network.target mysql.service redis.service

[Service]
Type=simple
User=dataglove
Group=dataglove
WorkingDirectory=/opt/dataglove
ExecStart=/usr/bin/java -jar -Xmx2g -Xms1g /opt/dataglove/data-glove-web-1.0.0.jar
ExecStop=/bin/kill -15 $MAINPID
Restart=always
RestartSec=10

Environment=SPRING_PROFILES_ACTIVE=prod
Environment=DB_PASSWORD=your_db_password
Environment=REDIS_PASSWORD=your_redis_password
Environment=JWT_SECRET=your_jwt_secret

[Install]
WantedBy=multi-user.target
```

启动服务：
```bash
sudo systemctl daemon-reload
sudo systemctl enable dataglove
sudo systemctl start dataglove
sudo systemctl status dataglove
```

#### 方式三：使用 Docker

创建 `Dockerfile`：

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/data-glove-web-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

创建 `docker-compose.yml`：

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root_password
      MYSQL_DATABASE: data_glove_db
      MYSQL_USER: dataglove
      MYSQL_PASSWORD: db_password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./backend/src/main/resources/db/migration:/docker-entrypoint-initdb.d
    
  redis:
    image: redis:6.2-alpine
    command: redis-server --requirepass redis_password
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
      
  app:
    build: ./backend
    depends_on:
      - mysql
      - redis
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_PASSWORD: db_password
      REDIS_PASSWORD: redis_password
      JWT_SECRET: jwt_secret

volumes:
  mysql_data:
  redis_data:
```

运行：
```bash
docker-compose up -d
```

## 前端部署

### 1. 环境准备

#### 安装 Node.js:
```bash
# 使用 NodeSource 仓库
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# 验证安装
node -v
npm -v
```

### 2. 构建前端项目

```bash
cd frontend

# 安装依赖
npm install

# 构建生产版本
npm run build
```

### 3. 部署方式

#### 方式一：使用 Nginx

##### 安装 Nginx:
```bash
sudo apt update
sudo apt install nginx
```

##### 配置 Nginx:

创建配置文件 `/etc/nginx/sites-available/dataglove`：

```nginx
server {
    listen 80;
    server_name your_domain.com;
    
    # 前端静态文件
    location / {
        root /var/www/dataglove;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
    
    # API 代理
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
    
    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

##### 部署前端文件:
```bash
# 创建网站目录
sudo mkdir -p /var/www/dataglove
sudo chown $USER:$USER /var/www/dataglove

# 复制构建文件
cp -r frontend/dist/* /var/www/dataglove/

# 启用站点
sudo ln -s /etc/nginx/sites-available/dataglove /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

#### 方式二：使用 Docker

创建 `frontend/Dockerfile`：

```dockerfile
FROM node:18-alpine AS builder

WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

创建 `frontend/nginx.conf`：

```nginx
events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;
    
    server {
        listen 80;
        server_name localhost;
        
        location / {
            root /usr/share/nginx/html;
            index index.html;
            try_files $uri $uri/ /index.html;
        }
        
        location /api/ {
            proxy_pass http://backend:8080;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }
    }
}
```

## SSL 配置

### 使用 Let's Encrypt

```bash
# 安装 Certbot
sudo apt install certbot python3-certbot-nginx

# 获取SSL证书
sudo certbot --nginx -d your_domain.com

# 自动续期
sudo crontab -e
# 添加以下行：
0 12 * * * /usr/bin/certbot renew --quiet
```

### 手动配置SSL

```nginx
server {
    listen 443 ssl http2;
    server_name your_domain.com;
    
    ssl_certificate /path/to/certificate.crt;
    ssl_certificate_key /path/to/private.key;
    
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;
    
    # 其他配置...
}

server {
    listen 80;
    server_name your_domain.com;
    return 301 https://$server_name$request_uri;
}
```

## 监控和日志

### 1. 应用监控

#### 使用 Spring Boot Actuator

在 `application-prod.yml` 中配置：

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true
```

#### 健康检查端点:
```bash
curl http://localhost:8080/actuator/health
```

### 2. 日志管理

#### 配置 logback-spring.xml:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <springProfile name="prod">
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>/var/log/dataglove/application.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>/var/log/dataglove/application.%d{yyyy-MM-dd}.log</fileNamePattern>
                <maxHistory>30</maxHistory>
                <totalSizeCap>1GB</totalSizeCap>
            </rollingPolicy>
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        
        <root level="INFO">
            <appender-ref ref="FILE"/>
        </root>
    </springProfile>
</configuration>
```

### 3. 系统监控

#### 使用 Prometheus + Grafana

创建 `prometheus.yml`：

```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'dataglove-app'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
```

## 备份策略

### 1. 数据库备份

```bash
#!/bin/bash
# 数据库备份脚本

BACKUP_DIR="/backup/mysql"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="dataglove_backup_${DATE}.sql"

# 创建备份目录
mkdir -p $BACKUP_DIR

# 备份数据库
mysqldump -u dataglove -p'your_password' data_glove_db > $BACKUP_DIR/$BACKUP_FILE

# 压缩备份文件
gzip $BACKUP_DIR/$BACKUP_FILE

# 删除30天前的备份
find $BACKUP_DIR -name "*.sql.gz" -mtime +30 -delete

echo "Database backup completed: $BACKUP_FILE.gz"
```

### 2. 定时备份

```bash
# 添加到 crontab
0 2 * * * /path/to/backup_script.sh
```

## 安全配置

### 1. 防火墙配置

```bash
# UFW (Ubuntu)
sudo ufw enable
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow from 10.0.0.0/8 to any port 8080
sudo ufw allow from 10.0.0.0/8 to any port 3306
sudo ufw allow from 10.0.0.0/8 to any port 6379
```

### 2. 应用安全

- 使用强密码
- 定期更新 JWT 密钥
- 启用 HTTPS
- 配置 CORS 策略
- 限制文件上传大小
- 启用 SQL 注入防护

### 3. 系统安全

- 定期更新系统
- 禁用不必要的服务
- 配置 SSH 密钥登录
- 启用自动安全更新

## 故障排除

### 常见问题

1. **应用无法启动**
   - 检查端口是否被占用
   - 验证数据库连接
   - 查看应用日志

2. **数据库连接失败**
   - 检查数据库服务状态
   - 验证用户名密码
   - 检查防火墙设置

3. **前端页面无法加载**
   - 检查 Nginx 配置
   - 验证静态文件路径
   - 查看 Nginx 错误日志

4. **API 调用失败**
   - 检查后端服务状态
   - 验证 JWT 令牌
   - 查看网络连接

### 日志查看

```bash
# 应用日志
tail -f /var/log/dataglove/application.log

# Nginx 日志
tail -f /var/log/nginx/error.log
tail -f /var/log/nginx/access.log

# 系统日志
journalctl -u dataglove -f
```

---

**注意事项**：
1. 在生产环境中，请确保使用强密码和安全配置
2. 定期备份数据库和配置文件
3. 监控系统资源使用情况
4. 保持系统和应用程序更新