# 商城后台管理系统

## 项目简介

基于Spring Boot 3.2.0开发的商城后台管理系统，提供商品管理、订单管理、用户管理等功能。

## 技术栈

- **框架**: Spring Boot 3.2.0
- **数据库**: MySQL 8.0+
- **ORM**: MyBatis-Plus 3.5.4.1
- **安全**: JWT
- **文档**: SpringDoc OpenAPI 3
- **构建工具**: Maven
- **JDK版本**: 17+

## 项目结构

```
mall-admin-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── mall/
│   │   │           └── admin/
│   │   │               ├── MallAdminApplication.java    # 启动类
│   │   │               ├── common/                     # 通用类
│   │   │               │   ├── BusinessException.java  # 业务异常
│   │   │               │   ├── GlobalExceptionHandler.java # 全局异常处理
│   │   │               │   ├── PageResult.java         # 分页结果
│   │   │               │   ├── Result.java             # 统一响应
│   │   │               │   └── ResultCode.java         # 响应码
│   │   │               ├── config/                     # 配置类
│   │   │               │   ├── CorsConfig.java         # 跨域配置
│   │   │               │   ├── MybatisPlusConfig.java  # MyBatis配置
│   │   │               │   └── SwaggerConfig.java      # Swagger配置
│   │   │               ├── controller/                 # 控制器
│   │   │               ├── dto/                        # 数据传输对象
│   │   │               │   └── PageQuery.java          # 分页查询
│   │   │               ├── entity/                     # 实体类
│   │   │               │   └── BaseEntity.java         # 基础实体
│   │   │               ├── mapper/                     # 数据访问层
│   │   │               └── service/                    # 业务逻辑层
│   │   └── resources/
│   │       └── application.yml                         # 配置文件
│   └── test/
│       └── java/                                       # 测试代码
├── pom.xml                                             # Maven配置
└── README.md                                           # 项目说明
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 数据库配置

1. 创建数据库 `mall_admin`
2. 修改 `application.yml` 中的数据库连接信息

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mall_admin?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 运行项目

1. 克隆项目到本地
2. 进入项目目录
3. 执行以下命令：

```bash
# 安装依赖
mvn clean install

# 运行项目
mvn spring-boot:run
```

### 访问地址

- 应用地址: http://localhost:8080/api
- API文档: http://localhost:8080/api/swagger-ui.html

## 开发规范

### 代码规范

1. 使用Lombok简化代码
2. 统一使用Result包装响应结果
3. 统一异常处理
4. 使用Swagger注解生成API文档

### 数据库规范

1. 表名使用下划线命名
2. 字段名使用下划线命名
3. 必须包含create_time、update_time、deleted字段

### API规范

1. RESTful API设计
2. 统一响应格式
3. 统一错误码

## 部署说明

### 打包

```bash
mvn clean package -Dmaven.test.skip=true
```

### 运行

```bash
java -jar target/mall-admin-1.0.0.jar
```

## 联系方式

- 邮箱: admin@mall.com
- 项目地址: https://github.com/mall-admin

## 许可证

MIT License