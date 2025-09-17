<template>
  <div class="app-header">
    <!-- 左侧区域 -->
    <div class="header-left">
      <!-- 折叠按钮 -->
      <el-button 
        type="text" 
        class="sidebar-toggle"
        @click="appStore.toggleSidebar"
      >
        <el-icon size="18">
          <Fold v-if="!appStore.sidebarCollapsed" />
          <Expand v-else />
        </el-icon>
      </el-button>
      
      <!-- 面包屑导航 -->
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item 
          v-for="item in appStore.breadcrumbs" 
          :key="item.title"
          :to="item.path"
        >
          {{ item.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    
    <!-- 右侧区域 -->
    <div class="header-right">
      <!-- 全屏按钮 -->
      <el-tooltip content="全屏" placement="bottom">
        <el-button type="text" class="header-btn" @click="toggleFullscreen">
          <el-icon size="18">
            <FullScreen v-if="!isFullscreen" />
            <Aim v-else />
          </el-icon>
        </el-button>
      </el-tooltip>
      
      <!-- 主题切换 -->
      <el-tooltip content="切换主题" placement="bottom">
        <el-button type="text" class="header-btn" @click="appStore.toggleTheme">
          <el-icon size="18">
            <Sunny v-if="appStore.theme === 'light'" />
            <Moon v-else />
          </el-icon>
        </el-button>
      </el-tooltip>
      
      <!-- 消息通知 -->
      <el-dropdown trigger="click" class="notification-dropdown">
        <el-button type="text" class="header-btn">
          <el-badge :value="unreadCount" :hidden="unreadCount === 0">
            <el-icon size="18"><Bell /></el-icon>
          </el-badge>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu class="notification-menu">
            <div class="notification-header">
              <span>消息通知</span>
              <el-button type="text" size="small" @click="markAllAsRead">全部已读</el-button>
            </div>
            <el-scrollbar max-height="300px">
              <div v-if="notifications.length === 0" class="no-notifications">
                暂无消息
              </div>
              <div 
                v-for="notification in notifications" 
                :key="notification.id"
                class="notification-item"
                :class="{ 'is-unread': !notification.read }"
                @click="markAsRead(notification.id)"
              >
                <div class="notification-content">
                  <div class="notification-title">{{ notification.title }}</div>
                  <div class="notification-desc">{{ notification.content }}</div>
                  <div class="notification-time">{{ formatDate(notification.time) }}</div>
                </div>
              </div>
            </el-scrollbar>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      
      <!-- 用户信息 -->
      <el-dropdown trigger="click" class="user-dropdown">
        <div class="user-info">
          <el-avatar 
            :src="authStore.user?.avatarUrl" 
            :size="32"
            class="user-avatar"
          >
            <el-icon><User /></el-icon>
          </el-avatar>
          <span class="username">{{ authStore.user?.nickname }}</span>
          <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="goToProfile">
              <el-icon><User /></el-icon>
              个人中心
            </el-dropdown-item>
            <!-- <el-dropdown-item @click="showSettings">
              <el-icon><Setting /></el-icon>
              系统设置
            </el-dropdown-item> -->
            <el-dropdown-item divided @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useAppStore } from '@/stores/app'
import { useAuthStore } from '@/stores/auth'
import { formatDate } from '@/utils'
import {
  Fold,
  Expand,
  FullScreen,
  Aim,
  Sunny,
  Moon,
  Bell,
  User,
  ArrowDown,
  Setting,
  SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const appStore = useAppStore()
const authStore = useAuthStore()

// 全屏状态
const isFullscreen = ref(false)

// 通知数据
const notifications = ref([
  {
    id: 1,
    title: '新订单提醒',
    content: '您有一个新的订单需要处理',
    time: new Date(),
    read: false
  },
  {
    id: 2,
    title: '系统更新',
    content: '系统将在今晚进行维护更新',
    time: new Date(Date.now() - 3600000),
    read: true
  }
])

// 未读消息数量
const unreadCount = computed(() => {
  return notifications.value.filter(n => !n.read).length
})

// 切换全屏
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    document.exitFullscreen()
    isFullscreen.value = false
  }
}

// 监听全屏状态变化
const handleFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement
}

// 标记消息为已读
const markAsRead = (id: number) => {
  const notification = notifications.value.find(n => n.id === id)
  if (notification) {
    notification.read = true
  }
}

// 标记所有消息为已读
const markAllAsRead = () => {
  notifications.value.forEach(n => n.read = true)
}

// 跳转到个人中心
const goToProfile = () => {
  router.push('/profile')
}

// 显示设置
const showSettings = () => {
  router.push('/system/settings')
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要退出登录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await authStore.logout()
    ElMessage.success('退出登录成功')
    // 跳转到登录页并刷新页面
    window.location.href = '/login'
  } catch {
    // 用户取消
  }
}

onMounted(() => {
  document.addEventListener('fullscreenchange', handleFullscreenChange)
})

onUnmounted(() => {
  document.removeEventListener('fullscreenchange', handleFullscreenChange)
})
</script>

<style scoped>
.app-header {
  height: 60px;
  background: linear-gradient(90deg, #fff8e1 0%, #fffde7 50%, #fff9c4 100%);
  border-bottom: 1px solid rgba(255, 215, 61, 0.3);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(255, 215, 0, 0.15);
}

.header-left {
  display: flex;
  align-items: center;
}

.header-left .sidebar-toggle {
  margin-right: 20px;
  color: #8d6e63;
}

.header-left .sidebar-toggle:hover {
  color: #5d4037;
  background-color: rgba(255, 215, 61, 0.2);
}

.header-left .breadcrumb {
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-right .header-btn {
  color: #8d6e63;
}

.header-right .header-btn:hover {
  color: #5d4037;
  background-color: rgba(255, 215, 61, 0.2);
}

.notification-dropdown .notification-menu {
  width: 320px;
}

.notification-dropdown .notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
  font-weight: 600;
}

.notification-dropdown .no-notifications {
  padding: 40px 20px;
  text-align: center;
  color: #909399;
}

.notification-dropdown .notification-item {
  padding: 12px 16px;
  border-bottom: 1px solid #f5f7fa;
  cursor: pointer;
  transition: background-color 0.3s;
}

.notification-dropdown .notification-item:hover {
  background-color: #f5f7fa;
}

.notification-dropdown .notification-item.is-unread {
  background-color: #ecf5ff;
}

.notification-dropdown .notification-item.is-unread::before {
  content: '';
  position: absolute;
  left: 8px;
  top: 50%;
  transform: translateY(-50%);
  width: 6px;
  height: 6px;
  background-color: #409eff;
  border-radius: 50%;
}

.notification-dropdown .notification-title {
  font-weight: 600;
  margin-bottom: 4px;
}

.notification-dropdown .notification-desc {
  color: #909399;
  font-size: 12px;
  margin-bottom: 4px;
}

.notification-dropdown .notification-time {
  color: #c0c4cc;
  font-size: 11px;
}

.user-dropdown .user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-dropdown .user-info:hover {
  background-color: rgba(255, 215, 61, 0.2);
}

.user-dropdown .user-avatar {
  margin-right: 8px;
}

.user-dropdown .username {
  margin-right: 8px;
  font-size: 14px;
  color: #5d4037;
}

.user-dropdown .dropdown-icon {
  color: #909399;
  font-size: 12px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .app-header {
    padding: 0 10px;
  }
  
  .header-left .breadcrumb {
    display: none;
  }
  
  .user-info .username {
    display: none;
  }
}
</style>