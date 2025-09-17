<template>
  <div class="login-container">
    <div class="login-box">
      <!-- Logo区域 -->
      <div class="login-header">
        <img src="/logo.svg" alt="Logo" class="logo" />
        <h1 class="title">商城管理系统</h1>
        <p class="subtitle">欢迎登录后台管理系统</p>
      </div>
      
      <!-- 登录表单 -->
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="nickname">
          <el-input
            v-model="loginForm.nickname"
            placeholder="请输入昵称"
            size="large"
            prefix-icon="User"
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item>
          <div class="login-options">
            <el-checkbox v-model="rememberMe">记住密码</el-checkbox>
            <el-link type="primary" :underline="false">忘记密码？</el-link>
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <!-- 快速登录 -->
      <!-- <div class="quick-login">
        <el-divider>快速登录</el-divider>
        <div class="quick-login-buttons">
          <el-button 
            type="info" 
            plain 
            size="small"
            @click="quickLogin('admin')"
          >
            管理员登录
          </el-button>
          <el-button 
            type="success" 
            plain 
            size="small"
            @click="quickLogin('user')"
          >
            普通用户登录
          </el-button>
        </div>
      </div> -->
      
      <!-- 注册链接 -->
      <div class="register-link">
        <el-divider>还没有账户？</el-divider>
        <el-button 
          type="primary" 
          plain 
          size="default"
          @click="goToRegister"
        >
          立即注册
        </el-button>
      </div>
    </div>
    
    <!-- 背景装饰 -->
    <div class="login-bg">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
      <div class="bg-shape shape-3"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElLoading } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import type { LoginForm } from '@/stores/auth'

// interface LoginForm {
//   nickname: string
//   password: string
// }

const router = useRouter()
const authStore = useAuthStore()

// 表单引用
const loginFormRef = ref<FormInstance>()

// 登录表单数据
const loginForm = reactive<LoginForm>({
  nickname: '',
  password: ''
})

// 记住密码
const rememberMe = ref(false)

// 加载状态
const loading = ref(false)

// 表单验证规则
const loginRules: FormRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    
    loading.value = true
    const result = await authStore.login(loginForm)
    
    if (result.success) {
      // 记住密码
      if (rememberMe.value) {
        localStorage.setItem('rememberedNickname', loginForm.nickname)
        localStorage.setItem('rememberedPassword', loginForm.password)
      } else {
        localStorage.removeItem('rememberedNickname')
        localStorage.removeItem('rememberedPassword')
      }
      
      // 跳转到首页并刷新页面
      await nextTick()
      window.location.href = '/dashboard'
    } else {
      ElMessage.error(result.message || '登录失败')
    }
  } catch (error) {
    console.error('登录验证失败:', error)
  } finally {
    loading.value = false
  }
}

// 快速登录
const quickLogin = async (type: 'admin' | 'user') => {
  const accounts = {
    admin: { nickname: 'admin', password: '123456' },
    user: { nickname: 'user', password: '123456' }
  }
  
  const account = accounts[type]
  loginForm.nickname = account.nickname
  loginForm.password = account.password
  
  await handleLogin()
}

// 跳转到注册页
const goToRegister = () => {
  router.push('/register')
}

// 初始化
onMounted(() => {
  // 恢复记住的密码
  const rememberedNickname = localStorage.getItem('rememberedNickname')
  const rememberedPassword = localStorage.getItem('rememberedPassword')
  
  if (rememberedNickname && rememberedPassword) {
    loginForm.nickname = rememberedNickname
    loginForm.password = rememberedPassword
    rememberMe.value = true
  }
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';

.login-container {
  position: relative;
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background-image: url('@/assets/20210827012725_20430.jpg'); /* 设置背景图 */
  background-size: cover; /* 背景图覆盖整个容器 */
  background-position: center; /* 背景图居中 */
  height: 100vh; /* 容器高度为视口高度 */
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-box {
  position: relative;
  width: 400px;
  padding: 40px;
  background: rgb(255, 254, 247);
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  z-index: 10;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
  
  .logo {
    width: 64px;
    height: 64px;
    margin-bottom: 16px;
  }
  
  .title {
    font-size: 24px;
    font-weight: 600;
    color: #333;
    margin: 0 0 8px 0;
  }
  
  .subtitle {
    font-size: 14px;
    color: #666;
    margin: 0;
  }
}

.login-form {
  .login-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
  }
  
  .login-btn {
    width: 100%;
    height: 44px;
    font-size: 16px;
    font-weight: 600;
    background: linear-gradient(45deg, #ffd93d, #ffeb3b) !important;
    border-color: #ffd93d !important;
    color: #5d4037 !important;
    
    &:hover {
      background: linear-gradient(45deg, #ffe066, #ffee58) !important;
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(255, 217, 61, 0.3);
    }
  }
}

.quick-login {
  margin-top: 20px;
  
  .el-divider {
    margin: 20px 0;
    
    :deep(.el-divider__text) {
      font-size: 12px;
      color: #999;
    }
  }
  
  .quick-login-buttons {
    display: flex;
    gap: 10px;
    justify-content: center;
  }
}

.register-link {
  margin-top: 20px;
  text-align: center;
  
  .el-divider {
    margin: 20px 0;
    
    :deep(.el-divider__text) {
      font-size: 12px;
      color: #999;
    }
  }
}

.login-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  
  .bg-shape {
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
    animation: float 6s ease-in-out infinite;
    
    &.shape-1 {
      width: 200px;
      height: 200px;
      top: 10%;
      left: 10%;
      animation-delay: 0s;
    }
    
    &.shape-2 {
      width: 150px;
      height: 150px;
      top: 60%;
      right: 10%;
      animation-delay: 2s;
    }
    
    &.shape-3 {
      width: 100px;
      height: 100px;
      bottom: 20%;
      left: 20%;
      animation-delay: 4s;
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

// 响应式设计
@media (max-width: 480px) {
  .login-box {
    width: 90%;
    padding: 30px 20px;
  }
  
  .login-header {
    .title {
      font-size: 20px;
    }
  }
}

// 深色主题适配
@media (prefers-color-scheme: dark) {
  .login-container {
    background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
  }
  
  .login-box {
    background: rgba(30, 30, 30, 0.95);
    color: $text-primary;
    
    .login-header {
      .title {
        color: $text-primary;
      }
      
      .subtitle {
        color: #ccc;
      }
    }
  }
}
</style>