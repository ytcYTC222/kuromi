import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import { useAuthStore } from '@/stores/auth'
import './assets/styles/global.scss'
import { KuromiSoundsPlugin, kuromiSoundDirectives } from '@/utils/kuromiSounds'

const app = createApp(App)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

const pinia = createPinia()
app.use(pinia)
app.use(router)
app.use(ElementPlus)
app.use(KuromiSoundsPlugin)

// 注册库洛米音效指令
for (const [name, directive] of Object.entries(kuromiSoundDirectives)) {
  app.directive(name, directive)
}

// 初始化认证状态
const authStore = useAuthStore()
authStore.initUserInfo()

app.mount('#app')
