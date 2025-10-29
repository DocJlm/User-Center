# 用户导航 - 2025

一个基于 Vue 3 + Spring Boot 构建的现代化用户管理系统。

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Vue](https://img.shields.io/badge/vue-3.4.0-brightgreen.svg)
![Spring Boot](https://img.shields.io/badge/spring%20boot-3.5.7-green.svg)
![TypeScript](https://img.shields.io/badge/typescript-5.0.0-blue.svg)

## 📋 项目概述

用户导航是一个功能完整的用户管理系统，提供用户注册、登录、个人信息管理、数据统计等功能。项目采用前后端分离架构，前端使用 Vue 3 + TypeScript + Ant Design Vue，后端使用 Spring Boot + MyBatis Plus。

### ✨ 主要特性

- 🔐 **用户认证**: 注册、登录、注销功能
- 👤 **个人中心**: 完整的用户信息管理
- 🖼️ **头像上传**: 支持图片上传和预览
- 📊 **数据统计**: 实时系统统计信息
- 🔒 **权限管理**: 基于角色的访问控制
- 📱 **响应式设计**: 适配各种设备屏幕
- 🎨 **现代化UI**: 基于 Ant Design Vue 的美观界面

## 🏗️ 技术架构

### 前端技术栈
- **框架**: Vue 3.4.0 + TypeScript 5.0
- **构建工具**: Vite 5.0
- **UI组件库**: Ant Design Vue 4.0
- **状态管理**: Pinia 2.0
- **路由管理**: Vue Router 4.0
- **HTTP客户端**: Axios 1.6
- **样式处理**: CSS3 + 响应式设计

### 后端技术栈
- **框架**: Spring Boot 3.5.7
- **数据库**: MySQL 8.x
- **ORM框架**: MyBatis Plus 3.5.14
- **构建工具**: Maven 3.9
- **Java版本**: JDK 17+
- **安全认证**: Session-based Authentication

## 🚀 快速开始

### 环境要求
- Node.js 16+
- JDK 17+
- MySQL 8.0+
- Maven 3.6+

### 安装步骤

#### 1. 克隆项目
```bash
git clone https://github.com/DocJlm/User-Center.git
cd user-center
```

#### 2. 数据库配置
```sql
-- 创建数据库
CREATE DATABASE zqc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户表
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    userAccount VARCHAR(50) NOT NULL UNIQUE COMMENT '用户账号',
    userPassword VARCHAR(100) NOT NULL COMMENT '用户密码',
    username VARCHAR(50) COMMENT '用户名',
    avatarUrl VARCHAR(500) COMMENT '头像URL',
    gender TINYINT DEFAULT 0 COMMENT '性别 0-女 1-男',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(50) COMMENT '邮箱',
    userStatus TINYINT DEFAULT 0 COMMENT '用户状态 0-正常 1-封禁',
    planetCode VARCHAR(20) UNIQUE COMMENT '星球编号',
    userRole TINYINT DEFAULT 0 COMMENT '用户角色 0-普通用户 1-管理员',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete TINYINT DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除'
) COMMENT '用户表';
```

#### 3. 后端配置
```bash
cd user-center-backend

# 修改数据库配置
# 编辑 src/main/resources/application.properties
# 设置数据库连接信息

# 启动后端服务
mvn spring-boot:run -Dmaven.test.skip=true
```

#### 4. 前端配置
```bash
cd user-center-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 或者使用自定义开发服务器
node start-dev.js
```

#### 5. 访问应用
- 前端演示: http://localhost:3000/demo.html
- 后端API: http://localhost:8080/api/

## 📁 项目结构

```
user-center/
├── user-center-frontend/          # 前端项目
│   ├── src/
│   │   ├── views/                 # 页面组件
│   │   │   ├── Welcome.vue        # 首页
│   │   │   ├── Login.vue          # 登录页
│   │   │   ├── Register.vue       # 注册页
│   │   │   ├── Profile.vue        # 个人中心
│   │   │   ├── Stats.vue          # 数据统计
│   │   │   ├── Settings.vue       # 设置页面
│   │   │   ├── Messages.vue       # 消息中心
│   │   │   ├── UserManage.vue     # 用户管理
│   │   │   ├── TableList.vue      # 数据表格
│   │   │   └── admin/
│   │   │       └── SystemSettings.vue  # 系统设置
│   │   ├── services/              # API服务
│   │   ├── stores/                # Pinia状态管理
│   │   ├── types/                 # TypeScript类型定义
│   │   └── utils/                 # 工具函数
│   ├── demo.html                  # 演示页面
│   └── package.json
├── user-center-backend/           # 后端项目
│   ├── src/main/java/com/zqc/usercenter/
│   │   ├── controller/            # REST API控制器
│   │   ├── service/               # 业务逻辑层
│   │   ├── mapper/                # 数据访问层
│   │   ├── model/                 # 实体类
│   │   ├── common/                # 通用组件
│   │   ├── config/                # 配置类
│   │   └── exception/             # 异常处理
│   └── src/main/resources/
│       └── application.properties # 配置文件
└── README.md                      # 项目说明
```

## 🎯 功能模块

### 用户管理
- 用户注册和登录
- 个人信息修改
- 头像上传和更换
- 密码修改
- 账户注销

### 数据统计
- 用户总数统计
- 活跃用户分析
- 用户增长趋势
- 系统使用统计

### 管理功能
- 用户列表管理（管理员）
- 系统设置配置
- 消息通知中心
- 系统日志查看

## 🔧 配置说明

### 后端配置 (application.properties)
```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/zqc
spring.datasource.username=root
spring.datasource.password=123456

# 服务器配置
server.port=8080
server.servlet.context-path=/api

# 文件上传配置
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### 前端配置
```typescript
// src/services/request.ts
const request = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
})
```

## 📖 API文档

### 用户认证
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `POST /api/user/logout` - 用户注销
- `GET /api/user/current` - 获取当前用户

### 用户管理
- `PUT /api/user/update` - 更新用户信息
- `POST /api/user/upload-avatar` - 上传头像
- `POST /api/user/change-password` - 修改密码
- `GET /api/user/search` - 搜索用户（管理员）

### 数据统计
- `GET /api/system/stats` - 获取系统统计
- `GET /api/user/activities/{userId}` - 获取用户活动

## 🎨 界面预览

### 主要页面
- **首页**: 现代化的欢迎页面，展示系统概览
- **个人中心**: 完整的用户信息管理界面
- **数据统计**: 直观的数据可视化展示
- **用户管理**: 管理员用户管理界面
- **系统设置**: 系统配置和设置

### 设计特色
- 响应式布局，支持移动端访问
- 现代化的卡片式设计
- 流畅的动画和交互效果
- 统一的设计语言和色彩体系

## 🚀 部署说明

### 开发环境
```bash
# 前端
cd user-center-frontend
npm run dev

# 后端
cd user-center-backend
mvn spring-boot:run
```

### 生产环境
```bash
# 前端构建
cd user-center-frontend
npm run build

# 后端打包
cd user-center-backend
mvn clean package

# 运行
java -jar target/user-center-0.0.1-SNAPSHOT.jar
```

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 👨‍💻 作者

**ZQC** - [我的博客](https://www.zqcblog.com/)

## 📞 联系方式

- 邮箱: [**[zqc0914@gmail.com](mailto:zqc0914@gmail.com)**]
- 博客: [https://www.zqcblog.com/](https://www.zqcblog.com/)

## 🙏 致谢

感谢以下开源项目的支持：
- [Vue.js](https://vuejs.org/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Ant Design Vue](https://antdv.com/)
- [MyBatis Plus](https://baomidou.com/)

---

⭐ 如果这个项目对你有帮助，请给它一个星标！