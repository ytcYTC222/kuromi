# 商城后台管理系统

## 项目简介

这是一个完整的商城生态系统，包含三个核心子项目，为用户提供从前端购物到后台管理的完整解决方案：

1. **微信小程序 (222目录)** - C端用户购物平台
2. **Spring Boot后端 (mall-admin-backend目录)** - B端后台管理API服务
3. **Vue3前端 (vue3-admin目录)** - B端后台管理界面

## 项目结构

```
.
├── 222/                    # 微信小程序项目（C端用户购物）
├── mall-admin-backend/     # Spring Boot后端（B端后台管理API）
├── vue3-admin/             # Vue3前端（B端后台管理界面）
├── 数据库设计详细说明.md    # 数据库设计文档
├── API接口详细文档.md      # API接口文档
└── 项目详细文档.md          # 项目整体说明文档
```

## 技术栈

### 微信小程序端
- 前端：微信小程序原生 + WXML/WXSS + JavaScript ES6+
- 后端：Node.js + Express.js + MySQL2
- 认证：JWT + bcryptjs

### Spring Boot后端
- 框架：Spring Boot 3.1.5 + Spring Web + Spring Validation
- 数据层：MySQL 8.0 + MyBatis-Plus 3.5.4.1 + HikariCP
- 认证：JWT 0.12.3
- 文档：SpringDoc OpenAPI 2.2.0 + Swagger UI

### Vue3前端
- 框架：Vue.js 3.5.18 + TypeScript 5.8.0 + Vite
- UI组件：Element Plus 2.11.2 + Element Plus Icons
- 状态管理：Pinia 3.0.3 + Vue Router 4.5.1
- 工具库：Axios + Day.js + ECharts + Sass

## 快速开始

### 环境要求
- Node.js 16.14.0+
- JDK 17+
- MySQL 8.0+
- 微信开发者工具

### 项目启动

**启动Node.js后端**:
```bash
cd 222/server
npm install
npm start
```

**启动Spring Boot后端**:
```bash
cd mall-admin-backend
mvn spring-boot:run
```

**启动Vue3前端**:
```bash
cd vue3-admin
npm install
npm run dev
```

## 文档资源

- [家具购物小程序毕业设计说明书](222/家具购物小程序毕业设计说明书.md)
- [Spring Boot后端项目规范说明书](Spring%20Boot后端项目规范说明书.md)
- [Vue3后台管理系统技术文档](Vue3后台管理系统技术文档.md)
- [数据库设计详细说明](数据库设计详细说明.md)
- [API接口详细文档](API接口详细文档.md)

## 项目特色

1. **三端一体**: 小程序端 + 管理后台 + API服务完整生态
2. **技术先进**: 使用当前最新稳定版技术栈
3. **架构清晰**: 前后端分离 + 三层架构设计
4. **功能完整**: 覆盖电商核心业务流程
5. **文档完善**: 详细的开发和部署文档

## 贡献指南

欢迎提交Issue和Pull Request来改进本项目。

## 许可证

本项目仅供学习和参考使用。