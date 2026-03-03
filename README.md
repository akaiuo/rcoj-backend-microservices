

# Whoj Backend - 在线评测系统后端

Whoj Backend 是一个基于微服务架构的在线评测系统（Online Judge）后端服务，支持用户管理、题目管理、代码提交与评测、帖子讨论等功能。

## 系统架构

本项目采用 Spring Cloud 微服务架构，主要包含以下服务模块：

| 服务模块 | 端口 | 功能描述 |
|---------|------|---------|
| whoj-backend-gateway | 8101 | API 网关，统一入口 |
| whoj-backend-user-service | 8102 | 用户服务（注册、登录、管理） |
| whoj-backend-question-service | 8102 | 题目服务（题目管理、提交记录） |
| whoj-backend-judge-service | 8102 | 评测服务（代码执行、结果判定） |
| whoj-backend-post-service | 8102 | 帖子服务（论坛、评论、互动） |
| whoj-backend-validation-service | 8102 | 验证服务（邮箱验证码、Redis） |

## 技术栈

- **核心框架**: Spring Boot 2.x / Spring Cloud
- **数据库**: MySQL + MyBatis-Plus
- **缓存**: Redis
- **消息队列**: RabbitMQ
- **服务注册**: Nacos
- **API 网关**: Spring Cloud Gateway
- **远程调用**: OpenFeign
- **容器化**: Docker

## 主要功能

### 用户模块
- 用户注册（邮箱验证码）
- 用户登录/登出
- 用户信息管理
- 管理员权限控制

### 题目模块
- 题目 CRUD 操作
- 题目分类标签
- 题目难度配置（时间限制、内存限制）
- 题目提交与状态追踪

### 评测模块
- 多语言支持（Java、Python、C++ 等）
- 代码沙箱执行
- 多测试用例批量评测
- 运行结果分析（时间、内存、输出）

### 帖子模块
- 发布帖子
- 帖子评论与回复
- 点赞、收藏、关注功能
- 用户互动

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6+
- RabbitMQ 3.8+
- Nacos 2.x

### 数据库初始化

```sql
-- 创建数据库
CREATE DATABASE whoj_backend;

-- 导入 SQL 脚本（位于 sql/ 目录下）
```

### 配置修改

修改各服务模块的 `application.yml` 或 `application-prod.yml` 配置文件：

```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/whoj_backend
    username: your_username
    password: your_password

# Redis 配置
  redis:
    host: localhost
    port: 6379

# Nacos 配置
  cloud:
    nacos:
      server-addr: localhost:8848
```

### 启动服务

1. 启动 Nacos 注册中心
2. 启动 Redis
3. 启动 RabbitMQ
4. 按顺序启动各微服务：

```bash
# 编译项目
mvn clean install -DskipTests

# 启动网关
cd whoj-backend-gateway
mvn spring-boot:run

# 启动用户服务
cd whoj-backend-user-service
mvn spring-boot:run

# 启动其他服务...
```

### Docker 部署

```bash
# 使用 docker-compose 启动
docker-compose -f docker-compose.service.yml up -d
```

## API 文档

### 用户接口

| 方法 | 路径 | 说明 |
|-----|------|-----|
| POST | /api/user/register | 用户注册 |
| POST | /api/user/login | 用户登录 |
| POST | /api/user/logout | 退出登录 |
| GET | /api/user/get/login | 获取当前登录用户 |
| PUT | /api/user/update/my | 修改个人信息 |

### 题目接口

| 方法 | 路径 | 说明 |
|-----|------|-----|
| POST | /api/question/add | 新增题目 |
| POST | /api/question/submit/do | 提交代码 |
| POST | /api/question/list/page/vo | 分页查询题目 |
| GET | /api/question/get/vo | 获取题目详情 |

### 帖子接口

| 方法 | 路径 | 说明 |
|-----|------|-----|
| POST | /api/post/add | 发布帖子 |
| GET | /api/post/get | 获取帖子详情 |
| POST | /api/post/list/page/vo | 分页查询帖子 |
| POST | /api/post/comment/add | 发表评论 |

## 项目结构

```
whoj-backend-microservice/
├── whoj-backend-common/          # 公共模块
│   ├── annotation/               # 自定义注解
│   ├── common/                   # 通用响应类
│   ├── config/                   # 配置类
│   ├── constant/                 # 常量定义
│   ├── exception/                # 异常处理
│   └── utils/                    # 工具类
├── whoj-backend-gateway/         # 网关服务
├── whoj-backend-judge-service/  # 评测服务
│   ├── judge/                    # 评测核心逻辑
│   │   ├── strategy/             # 评测策略
│   │   └── codeSandbox/          # 代码沙箱
│   └── message/                  # 消息处理
├── whoj-backend-model/           # 数据模型
│   ├── model/entity/             # 数据库实体
│   ├── model/dto/                # 数据传输对象
│   ├── model/vo/                 # 视图对象
│   └── model/enums/              # 枚举类
├── whoj-backend-post-service/    # 帖子服务
├── whoj-backend-question-service/ # 题目服务
├── whoj-backend-service-client/  # Feign 客户端
├── whoj-backend-user-service/    # 用户服务
└── whoj-backend-validation-service/ # 验证服务
```

## License

本项目基于 MIT License 开源。