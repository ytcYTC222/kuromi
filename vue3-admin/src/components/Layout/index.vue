<template>
  <div class="app-layout" :class="{ 'is-mobile': appStore.device === 'mobile' }">
    <!-- 少女心装饰背景 -->
    <div class="cute-decorations"></div>
    
    <!-- 库洛米装饰元素 -->
    <KuromiDecorations />
    
    <!-- 移动端遮罩层 -->
    <div 
      v-if="appStore.device === 'mobile' && !appStore.sidebarCollapsed"
      class="mobile-mask"
      @click="appStore.toggleSidebar"
    ></div>
    
    <!-- 侧边栏 -->
    <Sidebar />
    
    <!-- 主内容区域 -->
    <div class="main-container" :class="{ 'sidebar-collapsed': appStore.sidebarCollapsed }">
      <!-- 顶部导航 -->
      <Header />
      
      <!-- 标签页导航 -->
      <TabsView />
      
      <!-- 页面内容 -->
      <div class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useAppStore } from '@/stores/app'
import { useAuthStore } from '@/stores/auth'
import Sidebar from './Sidebar.vue'
import Header from './Header.vue'
import TabsView from './TabsView.vue'
import KuromiDecorations from '@/components/KuromiDecorations.vue'

const appStore = useAppStore()
const authStore = useAuthStore()

onMounted(() => {
  // 初始化应用设置
  appStore.initAppSettings()
  
  // 初始化用户信息
  authStore.initUserInfo()
})
</script>

<style scoped>
.app-layout {
  position: relative;
  height: 100vh;
  width: 100%;
  display: flex;
}

.app-layout.is-mobile .main-container {
  margin-left: 0;
}

.mobile-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.3);
  z-index: 998;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-left: 250px;
  transition: margin-left 0.3s ease;
}

.main-container.sidebar-collapsed {
  margin-left: 64px;
}

.app-main {
  flex: 1;
  padding: 24px;
  background: transparent;
  overflow-y: auto;
  position: relative;
}

@media (max-width: 768px) {
  .app-main {
    padding: 10px;
  }
}

/* 页面切换动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}
</style>