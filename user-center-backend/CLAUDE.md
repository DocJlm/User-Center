# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个基于 Spring Boot 3.5.7 + MyBatis Plus 的用户中心后端服务，提供用户注册、登录、管理等功能。

## 常用开发命令

### 构建和运行
```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run

# 打包项目
mvn clean package

# 运行测试
mvn test

# 运行单个测试类
mvn test -Dtest=UserServiceTest

# 运行单个测试方法
mvn test -Dtest=UserServiceTest#testAddUser
```

### 数据库
```bash
# 创建数据库并导入表结构
mysql -u root -p < sql/create_table.sql
```

## 项目架构

### 技术栈
- **框架**: Spring Boot 3.5.7
- **数据库**: MySQL 8.x
- **ORM**: MyBatis Plus 3.5.14
- **构建工具**: Maven
- **Java版本**: 17

### 核心包结构
- `com.zqc.usercenter.controller`: REST API 控制器层
- `com.zqc.usercenter.service`: 业务逻辑层
- `com.zqc.usercenter.mapper`: 数据访问层
- `com.zqc.usercenter.model.domain`: 领域模型
- `com.zqc.usercenter.model.domain.request`: 请求 DTO
- `com.zqc.usercenter.common`: 通用响应类、错误码等
- `com.zqc.usercenter.exception`: 异常处理

### 关键设计

#### 用户认证和授权
- 使用 Session 管理用户登录状态
- 用户角色分为：普通用户(0)和管理员(1)
- 管理员权限通过 `isAdmin()` 方法检查

#### 数据安全
- 密码加密存储（用户注册时进行加密）
- 用户信息脱敏处理（`getSafetyUser()` 方法）
- 逻辑删除机制（isDelete 字段）

#### 响应格式
- 统一使用 `BaseResponse<T>` 包装返回结果
- 使用 `ResultUtils` 工具类创建成功/失败响应

## 配置说明

### 应用配置 (application.yml)
- 服务端口: 8080
- 上下文路径: /api
- 数据库: MySQL (localhost:3306/zqc)
- Session 超时: 86400秒
- MyBatis Plus: 启用逻辑删除，关闭驼峰命名转换

### 依赖管理
- Spring Boot Web
- MyBatis Plus Boot Starter
- MySQL Connector
- Lombok (代码简化)
- Apache Commons Lang3

## API 端点

基础路径: `/api/user`
- `POST /register` - 用户注册
- `POST /login` - 用户登录
- `POST /logout` - 用户注销
- `GET /current` - 获取当前登录用户
- `GET /search` - 搜索用户（需管理员权限）
- `POST /delete` - 删除用户（需管理员权限）

## 开发注意事项

### 数据库操作
- 使用 MyBatis Plus 提供的 CRUD 方法
- 自定义 SQL 在 `mapper/UserMapper.xml` 中定义
- 支持逻辑删除

### 测试
- 测试类在 `src/test/java` 目录
- 包含完整的用户 CRUD 操作测试
- 用户注册逻辑的边界条件测试

### 异常处理
- 自定义 `BusinessException` 业务异常
- `GlobalExceptionHandler` 全局异常处理
- 统一错误码定义在 `ErrorCode` 中