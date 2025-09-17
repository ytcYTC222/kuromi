<template>
  <div class="register-container">
    <div class="register-box">
      <!-- Logo区域 -->
      <div class="register-header">
        <img src="/logo.svg" alt="Logo" class="logo" />
        <h1 class="title">商城管理系统</h1>
        <p class="subtitle">创建您的管理员账户</p>
      </div>
      
      <!-- 注册表单 -->
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        @keyup.enter="handleRegister"
      >
        <el-form-item prop="nickname">
          <el-input
            v-model="registerForm.nickname"
            placeholder="请输入昵称"
            size="large"
            prefix-icon="User"
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱地址"
            size="large"
            prefix-icon="Message"
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            size="large"
            prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号（可选）"
            size="large"
            prefix-icon="Phone"
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="gender">
          <el-radio-group v-model="registerForm.gender" size="large">
            <el-radio :label="0">未知</el-radio>
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item prop="agreement">
          <el-checkbox v-model="registerForm.agreement">
            我已阅读并同意
            <el-link type="primary" :underline="false">《用户协议》</el-link>
            和
            <el-link type="primary" :underline="false">《隐私政策》</el-link>
          </el-checkbox>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-btn"
            :loading="loading"
            @click="handleRegister"
          >
            {{ loading ? '注册中...' : '立即注册' }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <!-- 登录链接 -->
      <div class="login-link">
        <el-divider>已有账户？</el-divider>
        <el-button 
          type="info" 
          plain 
          size="default"
          @click="goToLogin"
        >
          返回登录
        </el-button>
      </div>
    </div>
    
    <!-- 背景装饰 -->
    <div class="register-bg">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
      <div class="bg-shape shape-3"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { authAPI } from '@/api/auth'
import type { RegisterRequest } from '@/api/auth'

const router = useRouter()

// 表单引用
const registerFormRef = ref<FormInstance>()

// 注册表单数据
interface RegisterForm {
  nickname: string
  email: string
  password: string
  confirmPassword: string
  phone: string
  gender: number
  agreement: boolean
}

const registerForm = reactive<RegisterForm>({
  nickname: '',
  email: '',
  password: '',
  confirmPassword: '',
  phone: '',
  gender: 0,
  agreement: false
})

// 加载状态
const loading = ref(false)

// 邮箱验证正则
const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/

// 密码强度验证
const validatePassword = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'))
  } else if (value.length > 20) {
    callback(new Error('密码长度不能超过20位'))
  } else {
    // 如果确认密码已填写，重新验证确认密码
    if (registerForm.confirmPassword) {
      registerFormRef.value?.validateField('confirmPassword')
    }
    callback()
  }
}

// 确认密码验证
const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请确认密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 邮箱验证
const validateEmail = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请输入邮箱地址'))
  } else if (!emailRegex.test(value)) {
    callback(new Error('请输入正确的邮箱格式'))
  } else {
    callback()
  }
}

// 协议验证
const validateAgreement = (rule: any, value: boolean, callback: any) => {
  if (!value) {
    callback(new Error('请阅读并同意用户协议和隐私政策'))
  } else {
    callback()
  }
}

// 表单验证规则
const registerRules: FormRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { validator: validateEmail, trigger: 'blur' }
  ],
  password: [
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  gender: [
    { type: 'number', message: '请选择性别', trigger: 'change' }
  ],
  agreement: [
    { validator: validateAgreement, trigger: 'change' }
  ]
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    await registerFormRef.value.validate()
    
    loading.value = true
    
    // 准备注册数据
    const registerData: RegisterRequest = {
      nickname: registerForm.nickname,
      email: registerForm.email,
      password: registerForm.password,
      confirmPassword: registerForm.confirmPassword,
      phone: registerForm.phone,
      gender: registerForm.gender
    }
    
    // 调用注册API
    const result = await authAPI.register(registerData)
    
    ElMessage.success(`注册成功！欢迎您，${result.nickname}！请登录您的账户`)
    
    // 注册成功后跳转到登录页
    router.push('/login')
    
  } catch (error: any) {
    console.error('注册失败:', error)
    
    // 处理不同类型的错误
    let errorMessage = '注册失败，请重试'
    
    if (error.response?.data?.message) {
      errorMessage = error.response.data.message
    } else if (error.message) {
      errorMessage = error.message
    }
    
    ElMessage.error(errorMessage)
  } finally {
    loading.value = false
  }
}

// 跳转到登录页
const goToLogin = () => {
  router.push('/login')
}
</script>

<style lang="scss" scoped>
@use 'sass:color';
@import '@/assets/styles/variables.scss';

.register-container {
  min-height: 100vh;
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

.register-box {
  position: relative;
  width: 420px;
  padding: 40px;
  background: rgb(255, 254, 247);
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  z-index: 10;
}

.register-header {
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

.register-form {
  .register-btn {
    width: 100%;
    height: 44px;
    font-size: 16px;
    font-weight: 600;
    background: linear-gradient(135deg, $primary-color 0%, color.adjust($primary-color, $lightness: 10%) 100%) !important;
    border-color: $primary-color !important;
    color: #333 !important;
    
    &:hover {
      background: linear-gradient(135deg, color.adjust($primary-color, $lightness: 5%) 0%, color.adjust($primary-color, $lightness: 15%) 100%) !important;
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba($primary-color, 0.3);
    }
  }
  
  :deep(.el-form-item__content) {
    .el-checkbox {
      font-size: 13px;
      line-height: 1.4;
    }
  }
}

.login-link {
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

.register-bg {
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

/* 响应式设计 */
@media (max-width: 480px) {
  .register-box {
    width: 90%;
    padding: 30px 20px;
  }
  
  .register-header {
    .title {
      font-size: 20px;
    }
  }
  
  .register-form {
    :deep(.el-form-item__content) {
      .el-checkbox {
        font-size: 12px;
      }
    }
  }
}

@media (max-width: 360px) {
  .register-box {
    width: 95%;
    padding: 25px 15px;
  }
}

/* 深色主题适配 */
@media (prefers-color-scheme: dark) {
  .register-container {
    background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
  }
  
  .register-box {
    background: rgba(30, 30, 30, 0.95);
    color: white;
    
    .register-header {
      .title {
        color: white;
      }
      
      .subtitle {
        color: #ccc;
      }
    }
  }
}
</style>