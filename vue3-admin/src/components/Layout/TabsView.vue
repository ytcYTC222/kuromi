<template>
  <div class="tabs-view" v-if="appStore.tabs.length > 1">
    <div class="tabs-container">
      <el-scrollbar class="tabs-scrollbar">
        <div class="tabs-content">
          <div 
            v-for="tab in appStore.tabs" 
            :key="tab.path"
            class="tab-item"
            :class="{ 'is-active': tab.path === appStore.activeTab }"
            @click="handleTabClick(tab)"
            @contextmenu.prevent="handleContextMenu($event, tab)"
          >
            <span class="tab-title">{{ tab.title }}</span>
            <el-icon 
              v-if="tab.closable !== false" 
              class="tab-close"
              @click.stop="handleTabClose(tab.path)"
            >
              <Close />
            </el-icon>
          </div>
        </div>
      </el-scrollbar>
      
      <!-- 标签页操作按钮 -->
      <div class="tabs-actions">
        <el-dropdown trigger="click" @command="handleCommand">
          <el-button type="text" class="action-btn">
            <el-icon><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="refresh">
                <el-icon><Refresh /></el-icon>
                刷新当前页
              </el-dropdown-item>
              <el-dropdown-item command="closeOthers">
                <el-icon><Close /></el-icon>
                关闭其他
              </el-dropdown-item>
              <el-dropdown-item command="closeAll">
                <el-icon><CircleClose /></el-icon>
                关闭所有
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
    
    <!-- 右键菜单 -->
    <div 
      v-show="contextMenuVisible"
      class="context-menu"
      :style="{ left: contextMenuLeft + 'px', top: contextMenuTop + 'px' }"
      @click="hideContextMenu"
    >
      <div class="context-menu-item" @click="refreshTab">
        <el-icon><Refresh /></el-icon>
        刷新
      </div>
      <div 
        v-if="selectedTab?.closable !== false"
        class="context-menu-item" 
        @click="closeTab"
      >
        <el-icon><Close /></el-icon>
        关闭
      </div>
      <div class="context-menu-item" @click="closeOtherTabs">
        <el-icon><Close /></el-icon>
        关闭其他
      </div>
      <div class="context-menu-item" @click="closeAllTabs">
        <el-icon><CircleClose /></el-icon>
        关闭所有
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { Close, ArrowDown, Refresh, CircleClose } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const appStore = useAppStore()

// 右键菜单相关
const contextMenuVisible = ref(false)
const contextMenuLeft = ref(0)
const contextMenuTop = ref(0)
const selectedTab = ref<{ title: string; path: string; closable?: boolean } | null>(null)

// 监听路由变化，更新标签页
watch(
  () => route.path,
  (newPath) => {
    const title = route.meta?.title as string || '未知页面'
    appStore.addTab({ title, path: newPath })
    appStore.setActiveTab(newPath)
  },
  { immediate: true }
)

// 点击标签页
const handleTabClick = (tab: { title: string; path: string; closable?: boolean }) => {
  if (tab.path !== route.path) {
    router.push(tab.path)
  }
}

// 关闭标签页
const handleTabClose = (path: string) => {
  appStore.removeTab(path)
  
  // 如果关闭的是当前标签页，跳转到新的激活标签页
  if (path === route.path) {
    router.push(appStore.activeTab)
  }
}

// 处理下拉菜单命令
const handleCommand = (command: string) => {
  switch (command) {
    case 'refresh':
      refreshCurrentTab()
      break
    case 'closeOthers':
      appStore.removeOtherTabs(route.path)
      break
    case 'closeAll':
      appStore.removeAllTabs()
      router.push(appStore.activeTab)
      break
  }
}

// 右键菜单
const handleContextMenu = (event: MouseEvent, tab: { title: string; path: string; closable?: boolean }) => {
  event.preventDefault()
  selectedTab.value = tab
  contextMenuLeft.value = event.clientX
  contextMenuTop.value = event.clientY
  contextMenuVisible.value = true
}

// 隐藏右键菜单
const hideContextMenu = () => {
  contextMenuVisible.value = false
  selectedTab.value = null
}

// 刷新标签页
const refreshTab = () => {
  if (selectedTab.value) {
    if (selectedTab.value.path === route.path) {
      refreshCurrentTab()
    } else {
      router.push(selectedTab.value.path).then(() => {
        refreshCurrentTab()
      })
    }
  }
  hideContextMenu()
}

// 刷新当前标签页
const refreshCurrentTab = () => {
  // 通过重新加载组件来刷新页面
  const currentPath = route.path
  router.replace('/redirect' + currentPath)
}

// 关闭标签页（右键菜单）
const closeTab = () => {
  if (selectedTab.value && selectedTab.value.closable !== false) {
    handleTabClose(selectedTab.value.path)
  }
  hideContextMenu()
}

// 关闭其他标签页
const closeOtherTabs = () => {
  if (selectedTab.value) {
    appStore.removeOtherTabs(selectedTab.value.path)
    if (selectedTab.value.path !== route.path) {
      router.push(selectedTab.value.path)
    }
  }
  hideContextMenu()
}

// 关闭所有标签页
const closeAllTabs = () => {
  appStore.removeAllTabs()
  router.push(appStore.activeTab)
  hideContextMenu()
}

// 点击其他地方隐藏右键菜单
const handleClickOutside = (event: MouseEvent) => {
  if (contextMenuVisible.value) {
    hideContextMenu()
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';

.tabs-view {
  height: 40px;
  background: linear-gradient(90deg, rgba(255, 249, 196, 0.9) 0%, rgba(255, 248, 225, 0.95) 50%, rgba(255, 249, 196, 0.9) 100%);
  border-bottom: 1px solid rgba(255, 215, 61, 0.3);
  position: relative;
  box-shadow: 0 1px 4px rgba(255, 215, 61, 0.1);
}

.tabs-container {
  display: flex;
  height: 100%;
}

.tabs-scrollbar {
  flex: 1;
}

.tabs-scrollbar :deep(.el-scrollbar__bar) {
  display: none;
}

.tabs-content {
  display: flex;
  height: 100%;
  align-items: center;
  padding: 0 10px;
}

.tab-item {
  display: flex;
  align-items: center;
  height: 32px;
  padding: 0 12px;
  margin-right: 4px;
  background: linear-gradient(135deg, rgba(255, 249, 196, 0.8) 0%, rgba(255, 248, 225, 0.9) 100%);
  border: 1px solid rgba(255, 215, 61, 0.3);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
  color: #8d6e63;
}

.tab-item:hover {
  background: linear-gradient(135deg, rgba(255, 215, 61, 0.3) 0%, rgba(255, 204, 153, 0.4) 100%);
  border-color: rgba(255, 215, 61, 0.5);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(255, 215, 61, 0.2);
}

.tab-item.is-active {
  background: linear-gradient(135deg, #ffd73d 0%, rgba(255, 204, 153, 0.9) 100%);
  border-color: #ffd73d;
  color: #5d4037;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(255, 215, 61, 0.3);
}

.tab-item.is-active .tab-close {
  color: white;
}

.tab-item.is-active .tab-close:hover {
  background-color: rgba(93, 64, 55, 0.2);
}

.tab-item .tab-title {
  font-size: 12px;
  margin-right: 6px;
}

.tab-item .tab-close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  border-radius: 2px;
  font-size: 12px;
  color: #909399;
  transition: all 0.3s;
}

.tab-item .tab-close:hover {
  background-color: rgba(255, 215, 61, 0.3);
  color: #5d4037;
}

.tabs-actions {
  display: flex;
  align-items: center;
  padding: 0 10px;
  border-left: 1px solid rgba(255, 215, 61, 0.3);
}

.tabs-actions .action-btn {
  color: #8d6e63;
}

.tabs-actions .action-btn:hover {
  color: #5d4037;
  background-color: rgba(255, 215, 61, 0.2);
}

.context-menu {
  position: fixed;
  z-index: 9999;
  background: linear-gradient(145deg, rgba(255, 249, 196, 0.95) 0%, rgba(255, 248, 225, 0.98) 100%);
  border: 1px solid rgba(255, 215, 61, 0.3);
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(255, 215, 61, 0.2);
  padding: 4px 0;
  min-width: 120px;
}

.context-menu .context-menu-item {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  font-size: 12px;
  cursor: pointer;
  transition: background-color 0.3s;
  color: #8d6e63;
}

.context-menu .context-menu-item:hover {
  background-color: rgba(255, 215, 61, 0.2);
}

.context-menu .context-menu-item .el-icon {
  margin-right: 8px;
  font-size: 14px;
}

/* 移动端隐藏标签页 */
@media (max-width: 768px) {
  .tabs-view {
    display: none;
  }
}
</style>