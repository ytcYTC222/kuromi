# API接口详细文档

## 🌐 API概览

本项目包含两套API系统：
1. **Node.js API** (222项目) - 面向C端用户的微信小程序API
2. **Spring Boot API** (mall-admin-backend) - 面向B端管理员的后台管理API

## 📱 Node.js API (微信小程序端)

### 基础信息
- **基础URL**: `http://192.168.8.157:3000/api`
- **认证方式**: JWT Bearer Token
- **数据格式**: JSON
- **字符编码**: UTF-8

### 统一响应格式
```json
{
  "success": true,
  "message": "操作成功",
  "data": {},
  "timestamp": "2024-09-10T10:30:00.000Z"
}
```

### 1. 首页数据API

#### GET /api/home/hero
获取首页主要数据（轮播图、促销商品、分类、热门商品）

**请求参数**: 无

**响应示例**:
```json
{
  "success": true,
  "message": "获取首页数据成功",
  "data": {
    "banners": [
      {
        "banner_id": 1,
        "title": "春季家具大促销",
        "image_url": "https://example.com/banner1.jpg",
        "link_type": 2,
        "link_value": "1"
      }
    ],
    "promotions": [
      {
        "product_id": 1,
        "product_name": "现代简约沙发",
        "original_price": "2999.00",
        "current_price": "2199.00",
        "main_image": "https://example.com/sofa1.jpg"
      }
    ],
    "categories": [
      {
        "category_id": 1,
        "category_name": "客厅",
        "category_image": "https://example.com/living-room.jpg",
        "product_count": 156
      }
    ],
    "hotProducts": [
      {
        "product_id": 1,
        "product_name": "北欧风餐桌",
        "current_price": "1299.00",
        "main_image": "https://example.com/table1.jpg",
        "rating_average": "4.5",
        "sales_count": 89
      }
    ]
  }
}
```

### 2. 商品管理API

#### GET /api/products
获取商品列表

**请求参数**:
- `page` (number, optional): 页码，默认1
- `limit` (number, optional): 每页数量，默认10
- `categoryId` (number, optional): 分类ID
- `keyword` (string, optional): 搜索关键词
- `sortBy` (string, optional): 排序方式 (price_asc, price_desc, sales_desc, rating_desc)
- `minPrice` (number, optional): 最低价格
- `maxPrice` (number, optional): 最高价格

**响应示例**:
```json
{
  "success": true,
  "message": "获取商品列表成功",
  "data": {
    "products": [
      {
        "product_id": 1,
        "product_name": "现代简约沙发",
        "current_price": "2199.00",
        "original_price": "2999.00",
        "main_image": "https://example.com/sofa1.jpg",
        "rating_average": "4.5",
        "sales_count": 156,
        "category_name": "客厅",
        "is_promotion": 1
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 10,
      "total": 156,
      "totalPages": 16
    }
  }
}
```

#### GET /api/products/:id
获取商品详情

**路径参数**:
- `id` (number, required): 商品ID

**响应示例**:
```json
{
  "success": true,
  "message": "获取商品详情成功",
  "data": {
    "product": {
      "product_id": 1,
      "product_name": "现代简约沙发",
      "description": "舒适的现代简约风格沙发...",
      "current_price": "2199.00",
      "original_price": "2999.00",
      "stock_quantity": 25,
      "material": "布艺",
      "color": "浅灰色",
      "size": "200*90*80cm",
      "weight": "45.5",
      "rating_average": "4.5",
      "rating_count": 89,
      "sales_count": 156
    },
    "images": [
      {
        "image_id": 1,
        "image_url": "https://example.com/sofa1-1.jpg",
        "sort_order": 1
      }
    ],
    "specifications": [
      {
        "spec_name": "材质",
        "spec_value": "布艺"
      }
    ],
    "reviews": [
      {
        "review_id": 1,
        "user_nickname": "张***",
        "rating": 5,
        "comment": "质量很好，款式喜欢",
        "create_time": "2024-09-01T10:30:00.000Z"
      }
    ]
  }
}
```

### 3. 购物车API

#### POST /api/cart/add
添加商品到购物车

**请求体**:
```json
{
  "productId": 1,
  "quantity": 2
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "添加到购物车成功",
  "data": {
    "cart_id": 123,
    "total_quantity": 5
  }
}
```

#### GET /api/cart
获取购物车列表

**请求头**:
- `Authorization`: Bearer {token}

**响应示例**:
```json
{
  "success": true,
  "message": "获取购物车成功",
  "data": {
    "items": [
      {
        "cart_id": 1,
        "product_id": 1,
        "product_name": "现代简约沙发",
        "current_price": "2199.00",
        "quantity": 2,
        "is_selected": 1,
        "main_image": "https://example.com/sofa1.jpg",
        "stock_quantity": 25,
        "subtotal": "4398.00"
      }
    ],
    "summary": {
      "total_quantity": 5,
      "selected_quantity": 3,
      "total_amount": "6597.00",
      "selected_amount": "4398.00"
    }
  }
}
```

### 4. 用户认证API

#### POST /api/auth/login
用户登录

**请求体**:
```json
{
  "phone": "13800138000",
  "password": "123456"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "user_id": 1,
      "phone": "13800138000",
      "nickname": "张三",
      "avatar_url": "https://example.com/avatar1.jpg"
    }
  }
}
```

#### POST /api/auth/register
用户注册

**请求体**:
```json
{
  "phone": "13800138000",
  "password": "123456",
  "nickname": "张三"
}
```

### 5. 订单管理API

#### POST /api/orders/create
创建订单

**请求体**:
```json
{
  "items": [
    {
      "product_id": 1,
      "quantity": 2,
      "unit_price": "2199.00"
    }
  ],
  "address_id": 1,
  "shipping_method": "standard",
  "order_notes": "请及时发货"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "订单创建成功",
  "data": {
    "order_id": 100001,
    "order_number": "ORD20240910001",
    "final_amount": "4398.00"
  }
}
```

#### GET /api/orders
获取订单列表

**请求参数**:
- `page` (number, optional): 页码
- `status` (number, optional): 订单状态

**响应示例**:
```json
{
  "success": true,
  "message": "获取订单列表成功",
  "data": {
    "orders": [
      {
        "order_id": 100001,
        "order_number": "ORD20240910001",
        "order_status": 2,
        "status_text": "已付款",
        "final_amount": "4398.00",
        "create_time": "2024-09-10T10:30:00.000Z",
        "items_count": 2,
        "first_product_image": "https://example.com/sofa1.jpg"
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 10,
      "total": 25
    }
  }
}
```

### 6. 搜索API

#### GET /api/search/products
商品搜索

**请求参数**:
- `keyword` (string, required): 搜索关键词
- `page` (number, optional): 页码
- `categoryId` (number, optional): 分类筛选

#### GET /api/search/hot-keywords
获取热门搜索关键词

**响应示例**:
```json
{
  "success": true,
  "message": "获取热门搜索成功",
  "data": {
    "keywords": [
      { "keyword": "沙发", "search_count": 1250 },
      { "keyword": "床", "search_count": 980 },
      { "keyword": "餐桌", "search_count": 756 }
    ]
  }
}
```

## 🏢 Spring Boot API (后台管理端)

### 基础信息
- **基础URL**: `http://localhost:8080/api`
- **API文档**: `http://localhost:8080/api/swagger-ui.html`
- **认证方式**: JWT Bearer Token
- **数据格式**: JSON

### 统一响应格式
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1694332200000
}
```

### 分页响应格式
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [],
    "total": 100,
    "current": 1,
    "size": 10,
    "pages": 10
  }
}
```

### 1. 商品管理API

#### GET /api/products
获取商品列表（后台管理）

**请求参数**:
- `page` (number): 页码，默认1
- `size` (number): 每页大小，默认10
- `keyword` (string): 搜索关键词
- `categoryId` (number): 分类ID
- `status` (number): 商品状态

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [
      {
        "productId": 1,
        "productName": "现代简约沙发",
        "productCode": "SF001",
        "categoryName": "客厅",
        "currentPrice": 2199.00,
        "stockQuantity": 25,
        "salesCount": 156,
        "status": 1,
        "createTime": "2024-09-01T10:30:00"
      }
    ],
    "total": 156,
    "current": 1,
    "size": 10,
    "pages": 16
  }
}
```

#### POST /api/products
创建商品

**请求体**:
```json
{
  "productName": "现代简约沙发",
  "productCode": "SF001",
  "categoryId": 1,
  "brand": "宜家",
  "description": "舒适的现代简约风格沙发",
  "originalPrice": 2999.00,
  "currentPrice": 2199.00,
  "stockQuantity": 50,
  "material": "布艺",
  "color": "浅灰色",
  "size": "200*90*80cm",
  "weight": 45.5
}
```

### 2. 订单管理API

#### GET /api/orders
获取订单列表（后台管理）

**请求参数**:
- `page` (number): 页码
- `size` (number): 每页大小
- `status` (number): 订单状态
- `startDate` (string): 开始日期
- `endDate` (string): 结束日期

#### PUT /api/orders/{id}/status
更新订单状态

**路径参数**:
- `id` (number): 订单ID

**请求体**:
```json
{
  "status": 3,
  "shippingCode": "YT202409100001",
  "shippingCompany": "圆通快递"
}
```

### 3. 用户管理API

#### GET /api/users
获取用户列表

#### GET /api/users/{id}
获取用户详情

#### PUT /api/users/{id}/status
更新用户状态

### 4. 数据统计API

#### GET /api/dashboard/overview
获取仪表板概览数据

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "totalUsers": 1256,
    "totalOrders": 3456,
    "totalProducts": 456,
    "totalSales": 156789.50,
    "todayOrders": 23,
    "todaySales": 8900.00,
    "salesTrend": [
      { "date": "2024-09-01", "amount": 8900.00 },
      { "date": "2024-09-02", "amount": 9200.00 }
    ],
    "categoryStats": [
      { "categoryName": "客厅", "orderCount": 156, "percentage": 35.2 },
      { "categoryName": "卧室", "orderCount": 123, "percentage": 27.8 }
    ]
  }
}
```

## 🔒 认证授权

### JWT Token格式
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdCIsImlhdCI6MTYzNDU2NzgwMCwiZXhwIjoxNjM0NjU0MjAwfQ.signature
```

### Token有效期
- **Node.js API**: 7天
- **Spring Boot API**: 24小时

### 权限控制
不同角色具有不同的API访问权限：
- **普通用户**: 只能访问自己的数据
- **管理员**: 可以访问所有管理API
- **超级管理员**: 具有系统配置权限

## 📈 API性能优化

### 1. 缓存策略
- **热门数据缓存**: 首页数据、热门商品等
- **用户会话缓存**: 登录状态、购物车等
- **查询结果缓存**: 分类树、商品列表等

### 2. 分页设计
- **默认分页**: 所有列表接口都支持分页
- **最大限制**: 单次查询最多100条记录
- **性能优化**: 使用LIMIT OFFSET进行分页

### 3. 查询优化
- **索引优化**: 关键查询字段都建立了索引
- **SQL优化**: 避免N+1查询，使用JOIN优化
- **数据预加载**: 关联数据一次性加载

## 🛡️ 安全措施

### 1. 输入验证
- **参数校验**: 所有输入参数都进行格式验证
- **SQL注入防护**: 使用参数化查询
- **XSS防护**: 输出数据HTML转义

### 2. 频率限制
- **登录限制**: 同一IP 5分钟内最多登录5次
- **API限制**: 普通用户每分钟最多100次请求
- **验证码**: 敏感操作需要验证码验证

### 3. 数据安全
- **敏感数据加密**: 密码使用bcrypt加密
- **HTTPS传输**: 生产环境强制HTTPS
- **日志脱敏**: 日志中不记录敏感信息

## 📝 错误码定义

### 通用错误码
- `200`: 成功
- `400`: 参数错误
- `401`: 未授权
- `403`: 权限不足
- `404`: 资源不存在
- `500`: 服务器内部错误

### 业务错误码
- `1001`: 用户不存在
- `1002`: 密码错误
- `2001`: 商品不存在
- `2002`: 库存不足
- `3001`: 订单不存在
- `3002`: 订单状态错误

这套API设计遵循RESTful规范，提供了完整的电商功能支持，同时注重性能和安全性。