<template>
  <div class="sidebar" :class="{ 'is-collapsed': appStore.sidebarCollapsed }">
    <!-- Logo区域 -->
    <div class="sidebar-logo">
      <router-link to="/dashboard" class="logo-link">
        <img src="/logo.svg" alt="Logo" class="logo-img" v-if="!appStore.sidebarCollapsed" />
        <img src="/logo.svg" alt="Logo" class="logo-img-mini" v-else />
        <h1 v-if="!appStore.sidebarCollapsed" class="logo-title">商城管理</h1>
      </router-link>
    </div>
    
    <!-- 导航菜单 -->
    <el-scrollbar class="sidebar-scrollbar">
      <el-menu
        :default-active="$route.path"
        :collapse="appStore.sidebarCollapsed"
        :unique-opened="true"
        router
        class="sidebar-menu"
      >
        <!-- 仪表板 -->
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>仪表板</template>
        </el-menu-item>
        
        <!-- 商品管理 -->
        <el-sub-menu index="/products">
          <template #title>
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </template>
          <el-menu-item index="/products/list">
            <el-icon><List /></el-icon>
            <template #title>商品列表</template>
          </el-menu-item>
          <!-- <el-menu-item index="/products/add">
            <el-icon><Plus /></el-icon>
            <template #title>添加商品</template>
          </el-menu-item> -->
          <el-menu-item index="/products/categories">
            <el-icon><Menu /></el-icon>
            <template #title>商品分类</template>
          </el-menu-item>
        </el-sub-menu>
        
        <!-- 订单管理 -->
        <el-sub-menu index="/orders">
          <template #title>
            <el-icon><ShoppingCart /></el-icon>
            <span>订单管理</span>
          </template>
          <el-menu-item index="/orders/list">
            <el-icon><List /></el-icon>
            <template #title>订单列表</template>
          </el-menu-item>
          <!-- <el-menu-item index="/orders/refunds">
            <el-icon><RefreshLeft /></el-icon>
            <template #title>退款管理</template>
          </el-menu-item> -->
        </el-sub-menu>
        
        <!-- 用户管理 -->
        <el-sub-menu index="/users">
          <template #title>
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/users/list">
            <el-icon><List /></el-icon>
            <template #title>用户列表</template>
          </el-menu-item>
          <!-- <el-menu-item index="/users/roles">
            <el-icon><UserFilled /></el-icon>
            <template #title>角色管理</template>
          </el-menu-item> -->
        </el-sub-menu>
        
        <!-- 营销管理 -->
        <el-sub-menu index="/marketing">
          <template #title>
            <el-icon><Promotion /></el-icon>
            <span>营销管理</span>
          </template>
          <el-menu-item index="/marketing/coupons/list">
            <el-icon><Ticket /></el-icon>
            <template #title>优惠券</template>
          </el-menu-item>
          <el-menu-item index="/marketing/activities">
            <el-icon><Star /></el-icon>
            <template #title>活动管理</template>
          </el-menu-item>
        </el-sub-menu>
        
        <!-- 内容管理 -->
        <el-sub-menu index="/content">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>内容管理</span>
          </template>
          <el-menu-item index="/content/banners">
            <el-icon><Picture /></el-icon>
            <template #title>轮播图</template>
          </el-menu-item>
          <el-menu-item index="/content/articles">
            <el-icon><EditPen /></el-icon>
            <template #title>文章管理</template>
          </el-menu-item>
        </el-sub-menu>
        
        <!-- 系统管理 -->
        <el-sub-menu index="/system" v-if="authStore.hasRole('admin')">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/settings">
            <el-icon><Tools /></el-icon>
            <template #title>系统设置</template>
          </el-menu-item>
          <el-menu-item index="/system/logs">
            <el-icon><Document /></el-icon>
            <template #title>操作日志</template>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { useAppStore } from '@/stores/app'
import { useAuthStore } from '@/stores/auth'
import {
  Odometer,
  Goods,
  List,
  Plus,
  Menu,
  ShoppingCart,
  RefreshLeft,
  User,
  UserFilled,
  Promotion,
  Ticket,
  Star,
  Document,
  Picture,
  EditPen,
  Setting,
  Tools
} from '@element-plus/icons-vue'

const appStore = useAppStore()
const authStore = useAuthStore()
</script>

<style scoped>
.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  width: 250px;
  background: linear-gradient(180deg, #fff8e1 0%, #fffde7 50%, #fff9c4 100%);
  box-shadow: 2px 0 8px rgba(255, 215, 0, 0.1);
  transition: width 0.3s ease;
  z-index: 1001;
}

.sidebar.is-collapsed {
  width: 64px;
}

.sidebar-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(255, 215, 61, 0.3);
  background: rgba(255, 249, 196, 0.5);
}

.sidebar-logo .logo-link {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: #5d4037;
}

.sidebar-logo .logo-img {
  width: 32px;
  height: 32px;
  margin-right: 12px;
}

.sidebar-logo .logo-img-mini {
  width: 32px;
  height: 32px;
}

.sidebar-logo .logo-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.sidebar-scrollbar {
  height: calc(100vh - 60px);
}

.sidebar-menu {
  border: none;
  background-color: transparent;
}

.sidebar-menu :deep(.el-menu-item) {
  color: #8d6e63;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: rgba(255, 215, 61, 0.2);
  color: #5d4037;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: #ffd93d;
  color: #5d4037;
  box-shadow: inset 0 0 10px rgba(255, 215, 61, 0.3);
}

.sidebar-menu :deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background-color: #ff6b9d;
}

.sidebar-menu :deep(.el-sub-menu .el-sub-menu__title) {
  color: #8d6e63;
}

.sidebar-menu :deep(.el-sub-menu .el-sub-menu__title:hover) {
  background-color: rgba(255, 215, 61, 0.2);
  color: #5d4037;
}

.sidebar-menu :deep(.el-sub-menu .el-menu) {
  background-color: rgba(255, 249, 196, 0.3);
}

.sidebar-menu :deep(.el-sub-menu .el-menu .el-menu-item) {
  padding-left: 50px !important;
}

.sidebar-menu :deep(.el-sub-menu .el-menu .el-menu-item:hover) {
  background-color: rgba(255, 215, 61, 0.3);
}

.sidebar-menu :deep(.el-sub-menu .el-menu .el-menu-item.is-active) {
  background-color: #ffd93d;
  color: #5d4037;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
    transition: transform 0.3s ease;
  }
  
  .sidebar:not(.is-collapsed) {
    transform: translateX(0);
  }
}
</style>