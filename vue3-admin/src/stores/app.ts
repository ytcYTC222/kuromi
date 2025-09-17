import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 侧边栏状态
  const sidebarCollapsed = ref<boolean>(false)
  
  // 设备类型
  const device = ref<'desktop' | 'tablet' | 'mobile'>('desktop')
  
  // 主题模式
  const theme = ref<'light' | 'dark'>('light')
  
  // 语言设置
  const language = ref<'zh-CN' | 'en-US'>('zh-CN')
  
  // 页面加载状态
  const loading = ref<boolean>(false)
  
  // 面包屑导航
  const breadcrumbs = ref<Array<{ title: string; path?: string }>>([])
  
  // 标签页
  const tabs = ref<Array<{ title: string; path: string; closable?: boolean }>>([
    { title: '首页', path: '/dashboard', closable: false }
  ])
  
  // 当前激活的标签页
  const activeTab = ref<string>('/dashboard')

  // 切换侧边栏
  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
    localStorage.setItem('sidebarCollapsed', String(sidebarCollapsed.value))
  }

  // 设置设备类型
  const setDevice = (deviceType: 'desktop' | 'tablet' | 'mobile') => {
    device.value = deviceType
    
    // 移动端自动收起侧边栏
    if (deviceType === 'mobile') {
      sidebarCollapsed.value = true
    }
  }

  // 切换主题
  const toggleTheme = () => {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
    localStorage.setItem('theme', theme.value)
    
    // 更新HTML类名
    document.documentElement.className = theme.value
  }

  // 设置语言
  const setLanguage = (lang: 'zh-CN' | 'en-US') => {
    language.value = lang
    localStorage.setItem('language', lang)
  }

  // 设置加载状态
  const setLoading = (status: boolean) => {
    loading.value = status
  }

  // 设置面包屑
  const setBreadcrumbs = (crumbs: Array<{ title: string; path?: string }>) => {
    breadcrumbs.value = crumbs
  }

  // 添加标签页
  const addTab = (tab: { title: string; path: string; closable?: boolean }) => {
    const existingTab = tabs.value.find(t => t.path === tab.path)
    if (!existingTab) {
      tabs.value.push({ closable: true, ...tab })
    }
    activeTab.value = tab.path
  }

  // 移除标签页
  const removeTab = (path: string) => {
    const index = tabs.value.findIndex(tab => tab.path === path)
    if (index > -1 && tabs.value[index].closable !== false) {
      tabs.value.splice(index, 1)
      
      // 如果移除的是当前激活的标签页，切换到前一个标签页
      if (activeTab.value === path) {
        const newIndex = Math.min(index, tabs.value.length - 1)
        activeTab.value = tabs.value[newIndex]?.path || '/dashboard'
      }
    }
  }

  // 移除其他标签页
  const removeOtherTabs = (currentPath: string) => {
    tabs.value = tabs.value.filter(tab => 
      tab.path === currentPath || tab.closable === false
    )
    activeTab.value = currentPath
  }

  // 移除所有标签页
  const removeAllTabs = () => {
    tabs.value = tabs.value.filter(tab => tab.closable === false)
    activeTab.value = tabs.value[0]?.path || '/dashboard'
  }

  // 设置激活的标签页
  const setActiveTab = (path: string) => {
    activeTab.value = path
  }

  // 初始化应用设置
  const initAppSettings = () => {
    // 恢复侧边栏状态
    const savedSidebarState = localStorage.getItem('sidebarCollapsed')
    if (savedSidebarState !== null) {
      sidebarCollapsed.value = savedSidebarState === 'true'
    }

    // 恢复主题设置
    const savedTheme = localStorage.getItem('theme') as 'light' | 'dark'
    if (savedTheme) {
      theme.value = savedTheme
      document.documentElement.className = savedTheme
    }

    // 恢复语言设置
    const savedLanguage = localStorage.getItem('language') as 'zh-CN' | 'en-US'
    if (savedLanguage) {
      language.value = savedLanguage
    }

    // 检测设备类型
    const checkDevice = () => {
      const width = window.innerWidth
      if (width < 768) {
        setDevice('mobile')
      } else if (width < 1024) {
        setDevice('tablet')
      } else {
        setDevice('desktop')
      }
    }

    checkDevice()
    window.addEventListener('resize', checkDevice)
  }

  return {
    // 状态
    sidebarCollapsed,
    device,
    theme,
    language,
    loading,
    breadcrumbs,
    tabs,
    activeTab,
    
    // 方法
    toggleSidebar,
    setDevice,
    toggleTheme,
    setLanguage,
    setLoading,
    setBreadcrumbs,
    addTab,
    removeTab,
    removeOtherTabs,
    removeAllTabs,
    setActiveTab,
    initAppSettings
  }
})