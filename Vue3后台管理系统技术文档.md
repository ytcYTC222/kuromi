# 现代家居商城后台管理系统 - Vue3技术文档

## 项目概述

本项目是基于微信小程序 `d:\商城后台\222` 和数据库设计 `database_design.sql` 开发的Vue3后台管理系统。采用简洁的技术栈，实现完整的商城后台管理功能，包括商品管理、订单管理、用户管理、数据统计等核心模块。

## 技术栈选择

### 前端技术栈（简洁高效）
- **Vue 3** - 渐进式JavaScript框架，使用Composition API
- **Element Plus** - 基于Vue 3的组件库，提供丰富的UI组件
- **Vue Router 4** - 官方路由管理器
- **Pinia** - 轻量级状态管理库
- **Axios** - HTTP客户端库
- **ECharts** - 数据可视化图表库
- **Vite** - 快速构建工具
- **SCSS** - CSS预处理器

### 后端技术栈（复用现有）
- **Node.js + Express** - 复用小程序后端API
- **MySQL** - 使用现有数据库设计
- **RESTful API** - 标准化接口设计

## 项目结构

```
vue3-admin/
├── public/                 # 静态资源
│   ├── favicon.ico
│   └── index.html
├── src/
│   ├── api/               # API接口
│   │   ├── auth.js        # 认证相关
│   │   ├── dashboard.js   # 仪表板
│   │   ├── products.js    # 商品管理
│   │   ├── orders.js      # 订单管理
│   │   ├── users.js       # 用户管理
│   │   ├── categories.js  # 分类管理
│   │   ├── banners.js     # 轮播图管理
│   │   └── index.js       # API配置
│   ├── assets/            # 静态资源
│   │   ├── images/
│   │   └── styles/
│   │       ├── global.scss
│   │       └── variables.scss
│   ├── components/        # 公共组件
│   │   ├── Layout/        # 布局组件
│   │   │   ├── Header.vue
│   │   │   ├── Sidebar.vue
│   │   │   └── Layout.vue
│   │   ├── Charts/        # 图表组件
│   │   │   ├── LineChart.vue
│   │   │   ├── PieChart.vue
│   │   │   └── BarChart.vue
│   │   └── Common/        # 通用组件
│   │       ├── ImageUpload.vue
│   │       ├── RichEditor.vue
│   │       └── SearchForm.vue
│   ├── router/            # 路由配置
│   │   └── index.js
│   ├── stores/            # 状态管理
│   │   ├── auth.js        # 认证状态
│   │   ├── user.js        # 用户状态
│   │   └── app.js         # 应用状态
│   ├── utils/             # 工具函数
│   │   ├── request.js     # HTTP请求封装
│   │   ├── auth.js        # 认证工具
│   │   ├── format.js      # 格式化工具
│   │   └── validate.js    # 验证工具
│   ├── views/             # 页面组件
│   │   ├── Dashboard/     # 仪表板
│   │   │   └── index.vue
│   │   ├── Products/      # 商品管理
│   │   │   ├── ProductList.vue
│   │   │   ├── ProductForm.vue
│   │   │   └── CategoryManage.vue
│   │   ├── Orders/        # 订单管理
│   │   │   ├── OrderList.vue
│   │   │   └── OrderDetail.vue
│   │   ├── Users/         # 用户管理
│   │   │   ├── UserList.vue
│   │   │   └── UserDetail.vue
│   │   ├── Marketing/     # 营销管理
│   │   │   ├── BannerList.vue
│   │   │   ├── CouponList.vue
│   │   │   └── PromotionList.vue
│   │   ├── Content/       # 内容管理
│   │   │   ├── ArticleList.vue
│   │   │   └── HotSearches.vue
│   │   ├── System/        # 系统管理
│   │   │   ├── OperationLogs.vue
│   │   │   └── SystemConfig.vue
│   │   └── Auth/          # 认证页面
│   │       ├── Login.vue
│   │       └── Register.vue
│   ├── App.vue            # 根组件
│   └── main.js            # 入口文件
├── package.json           # 依赖配置
├── vite.config.js         # Vite配置
└── README.md              # 项目说明
```

## 核心功能模块

### 1. 仪表板模块

**功能特点：**
- 数据概览：总销售额、订单数、商品数、用户数
- 销售趋势图表：近30天销售数据可视化
- 商品分类占比：饼图展示各分类销售情况
- 最新订单列表：实时订单状态展示
- 快捷操作：常用功能快速入口

**技术实现：**
```javascript
// Dashboard/index.vue
<template>
  <div class="dashboard">
    <!-- 数据概览卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6" v-for="stat in stats" :key="stat.key">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
            <div class="stat-trend" :class="stat.trend">
              <el-icon><ArrowUp v-if="stat.trend === 'up'" /><ArrowDown v-else /></el-icon>
              {{ stat.change }}%
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts">
      <el-col :span="16">
        <el-card title="销售趋势">
          <LineChart :data="salesData" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card title="分类占比">
          <PieChart :data="categoryData" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDashboardData } from '@/api/dashboard'
import LineChart from '@/components/Charts/LineChart.vue'
import PieChart from '@/components/Charts/PieChart.vue'

const stats = ref([])
const salesData = ref([])
const categoryData = ref([])

const loadDashboardData = async () => {
  try {
    const { data } = await getDashboardData()
    stats.value = data.stats
    salesData.value = data.salesTrend
    categoryData.value = data.categoryStats
  } catch (error) {
    console.error('加载仪表板数据失败:', error)
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>
```

### 2. 商品管理模块

**功能特点：**
- 商品列表：支持搜索、筛选、分页
- 商品添加/编辑：富文本编辑器、图片上传
- 批量操作：批量上架、下架、删除
- 分类管理：树形结构分类管理
- 库存管理：库存预警、库存日志

**数据库表对应：**
- `products` - 商品主表
- `product_images` - 商品图片
- `product_specifications` - 商品规格
- `categories` - 商品分类
- `product_stock_logs` - 库存日志

**核心代码示例：**
```javascript
// Products/ProductList.vue
<template>
  <div class="product-list">
    <!-- 搜索表单 -->
    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="商品名称">
        <el-input v-model="searchForm.name" placeholder="请输入商品名称" />
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="searchForm.categoryId" placeholder="请选择分类">
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
    
    <!-- 操作按钮 -->
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">添加商品</el-button>
      <el-button type="success" :disabled="!selectedRows.length" @click="handleBatchOnShelf">批量上架</el-button>
      <el-button type="warning" :disabled="!selectedRows.length" @click="handleBatchOffShelf">批量下架</el-button>
    </div>
    
    <!-- 商品表格 -->
    <el-table :data="products" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="商品图片" width="100">
        <template #default="{ row }">
          <el-image :src="row.mainImage" style="width: 60px; height: 60px" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" />
      <el-table-column prop="categoryName" label="分类" />
      <el-table-column prop="price" label="价格" />
      <el-table-column prop="stock" label="库存" />
      <el-table-column label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 'on_shelf' ? 'success' : 'warning'">
            {{ row.status === 'on_shelf' ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button type="text" @click="handleEdit(row)">编辑</el-button>
          <el-button type="text" @click="handleToggleStatus(row)">
            {{ row.status === 'on_shelf' ? '下架' : '上架' }}
          </el-button>
          <el-button type="text" class="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页 -->
    <el-pagination
      v-model:current-page="pagination.current"
      v-model:page-size="pagination.pageSize"
      :total="pagination.total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>
```

### 3. 订单管理模块

**功能特点：**
- 订单列表：多状态筛选、时间范围查询
- 订单详情：完整订单信息展示
- 状态管理：订单状态流转
- 物流跟踪：发货信息管理
- 退款处理：退款申请审核

**数据库表对应：**
- `orders` - 订单主表
- `order_items` - 订单商品明细

### 4. 用户管理模块

**功能特点：**
- 用户列表：用户信息查看、搜索
- 用户详情：完整用户档案
- 行为分析：用户行为统计
- 地址管理：收货地址管理

**数据库表对应：**
- `users` - 用户主表
- `user_addresses` - 用户地址
- `user_behavior_stats` - 用户行为统计
- `user_browse_history` - 浏览历史

### 5. 营销管理模块

**功能特点：**
- 轮播图管理：首页轮播图配置
- 优惠券管理：优惠券创建、发放
- 促销活动：活动配置、效果统计

**数据库表对应：**
- `banners` - 轮播图
- `coupons` - 优惠券
- `promotions` - 促销活动

## API接口设计

### 接口规范

**基础URL：** `http://localhost:3000/api`

**响应格式：**
```javascript
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1640995200000
}
```

### 核心接口列表

#### 仪表板接口
```javascript
// 获取仪表板数据
GET /api/dashboard/overview

// 获取销售趋势
GET /api/dashboard/sales-trend?days=30

// 获取分类统计
GET /api/dashboard/category-stats
```

#### 商品管理接口
```javascript
// 商品列表
GET /api/products?page=1&pageSize=10&keyword=&categoryId=

// 商品详情
GET /api/products/:id

// 创建商品
POST /api/products

// 更新商品
PUT /api/products/:id

// 删除商品
DELETE /api/products/:id

// 批量操作
POST /api/products/batch
```

#### 订单管理接口
```javascript
// 订单列表
GET /api/orders?page=1&pageSize=10&status=&startDate=&endDate=

// 订单详情
GET /api/orders/:id

// 更新订单状态
PUT /api/orders/:id/status

// 发货
POST /api/orders/:id/ship
```

## 状态管理设计

### Pinia Store结构

```javascript
// stores/auth.js - 认证状态
export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token'))
  const userInfo = ref(null)
  const permissions = ref([])
  
  const login = async (credentials) => {
    const { data } = await authAPI.login(credentials)
    token.value = data.token
    userInfo.value = data.userInfo
    localStorage.setItem('token', data.token)
  }
  
  const logout = () => {
    token.value = null
    userInfo.value = null
    permissions.value = []
    localStorage.removeItem('token')
  }
  
  return { token, userInfo, permissions, login, logout }
})

// stores/app.js - 应用状态
export const useAppStore = defineStore('app', () => {
  const sidebarCollapsed = ref(false)
  const theme = ref('light')
  const loading = ref(false)
  
  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }
  
  return { sidebarCollapsed, theme, loading, toggleSidebar }
})
```

## 路由配置

```javascript
// router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/components/Layout/Layout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Auth/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard/index.vue'),
        meta: { title: '仪表板', icon: 'dashboard' }
      },
      {
        path: 'products',
        name: 'Products',
        meta: { title: '商品管理', icon: 'goods' },
        children: [
          {
            path: 'list',
            name: 'ProductList',
            component: () => import('@/views/Products/ProductList.vue'),
            meta: { title: '商品列表' }
          },
          {
            path: 'add',
            name: 'ProductAdd',
            component: () => import('@/views/Products/ProductForm.vue'),
            meta: { title: '添加商品' }
          },
          {
            path: 'edit/:id',
            name: 'ProductEdit',
            component: () => import('@/views/Products/ProductForm.vue'),
            meta: { title: '编辑商品' }
          },
          {
            path: 'categories',
            name: 'Categories',
            component: () => import('@/views/Products/CategoryManage.vue'),
            meta: { title: '分类管理' }
          }
        ]
      },
      {
        path: 'orders',
        name: 'Orders',
        meta: { title: '订单管理', icon: 'order' },
        children: [
          {
            path: 'list',
            name: 'OrderList',
            component: () => import('@/views/Orders/OrderList.vue'),
            meta: { title: '订单列表' }
          },
          {
            path: 'detail/:id',
            name: 'OrderDetail',
            component: () => import('@/views/Orders/OrderDetail.vue'),
            meta: { title: '订单详情' }
          }
        ]
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/Users/UserList.vue'),
        meta: { title: '用户管理', icon: 'user' }
      },
      {
        path: 'marketing',
        name: 'Marketing',
        meta: { title: '营销管理', icon: 'marketing' },
        children: [
          {
            path: 'banners',
            name: 'Banners',
            component: () => import('@/views/Marketing/BannerList.vue'),
            meta: { title: '轮播图管理' }
          },
          {
            path: 'coupons',
            name: 'Coupons',
            component: () => import('@/views/Marketing/CouponList.vue'),
            meta: { title: '优惠券管理' }
          }
        ]
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth !== false && !authStore.token) {
    next('/login')
  } else {
    next()
  }
})

export default router
```

## 工具函数封装

### HTTP请求封装

```javascript
// utils/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:3000/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const { code, message, data } = response.data
    
    if (code === 200) {
      return { data, message }
    } else {
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message))
    }
  },
  (error) => {
    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      authStore.logout()
      window.location.href = '/login'
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
```

### 格式化工具

```javascript
// utils/format.js
import dayjs from 'dayjs'

// 格式化日期
export const formatDate = (date, format = 'YYYY-MM-DD HH:mm:ss') => {
  return dayjs(date).format(format)
}

// 格式化金额
export const formatMoney = (amount) => {
  return `¥${Number(amount).toFixed(2)}`
}

// 格式化文件大小
export const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 订单状态映射
export const orderStatusMap = {
  1: { text: '待付款', type: 'warning' },
  2: { text: '已付款', type: 'success' },
  3: { text: '已发货', type: 'primary' },
  4: { text: '已收货', type: 'success' },
  5: { text: '已完成', type: 'success' },
  6: { text: '已取消', type: 'danger' }
}
```

## 部署配置

### 环境配置

```javascript
// .env.development
VITE_API_BASE_URL=http://localhost:3000/api
VITE_UPLOAD_URL=http://localhost:3000/upload

// .env.production
VITE_API_BASE_URL=https://api.yourdomain.com/api
VITE_UPLOAD_URL=https://api.yourdomain.com/upload
```

### Vite配置

```javascript
// vite.config.js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3001,
    proxy: {
      '/api': {
        target: 'http://localhost:3000',
        changeOrigin: true
      }
    }
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    rollupOptions: {
      output: {
        chunkFileNames: 'js/[name]-[hash].js',
        entryFileNames: 'js/[name]-[hash].js',
        assetFileNames: '[ext]/[name]-[hash].[ext]'
      }
    }
  }
})
```

## 开发指南

### 1. 项目初始化

```bash
# 创建项目
npm create vue@latest vue3-admin
cd vue3-admin

# 安装依赖
npm install

# 安装Element Plus
npm install element-plus

# 安装其他依赖
npm install axios pinia vue-router@4 echarts dayjs
npm install sass -D

# 启动开发服务器
npm run dev
```

### 2. 开发规范

**组件命名：**
- 使用PascalCase命名组件文件
- 组件名称应该具有描述性

**API调用：**
- 统一使用async/await语法
- 错误处理使用try-catch
- 加载状态管理

**样式规范：**
- 使用SCSS预处理器
- 遵循BEM命名规范
- 响应式设计

### 3. 性能优化

**代码分割：**
```javascript
// 路由懒加载
const ProductList = () => import('@/views/Products/ProductList.vue')

// 组件懒加载
const LazyComponent = defineAsyncComponent(() => import('./LazyComponent.vue'))
```

**图片优化：**
```javascript
// 图片懒加载
<el-image :src="imageUrl" lazy />

// 图片压缩
const compressImage = (file, quality = 0.8) => {
  // 图片压缩逻辑
}
```

## 总结

本Vue3后台管理系统采用简洁的技术栈，充分复用现有的微信小程序后端API和数据库设计。通过模块化的架构设计，实现了完整的商城后台管理功能。系统具有以下特点：

1. **技术栈简洁**：使用Vue3 + Element Plus + Pinia等主流技术
2. **功能完整**：覆盖商品、订单、用户、营销等核心业务
3. **易于维护**：清晰的项目结构和代码规范
4. **性能优化**：代码分割、懒加载等优化策略
5. **响应式设计**：适配不同屏幕尺寸

该系统可以快速部署上线，为现代家居商城提供完整的后台管理解决方案。