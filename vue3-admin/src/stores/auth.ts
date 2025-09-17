import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { authAPI, type LoginRequest, type UserInfoResponse } from '@/api/auth'
import { tokenManager, type TokenInfo } from '@/utils/token'

// 用户信息接口
export interface User {
  userId: number        // 对应User实体的userId字段
  nickname: string      // 对应User实体的nickname字段
  email: string         // 对应User实体的email字段
  avatarUrl?: string    // 对应User实体的avatarUrl字段
  phone?: string        // 对应User实体的phone字段
  gender?: number       // 对应User实体的gender字段：0未知，1男，2女
  status: number        // 对应User实体的status字段：0禁用，1正常
  role?: string         // 角色信息（业务扩展字段）
  permissions?: string[] // 权限信息（业务扩展字段）
}

// 登录表单接口
export interface LoginForm {
  nickname: string      // 对应User实体的nickname字段
  password: string      // 对应User实体的password字段
}



export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref<string>(tokenManager.getToken() || '')
  const user = ref<User | null>(null)
  const permissions = ref<string[]>([])
  const loading = ref<boolean>(false)

  // 计算属性
  const isLoggedIn = computed(() => {
    return tokenManager.isTokenValid() && !!user.value && !!token.value
  })
  const userRole = computed(() => user.value?.role || '')

  // 登录
  const login = async (loginForm: LoginForm) => {
    try {
      loading.value = true
      
      // 调用登录API
      const response = await authAPI.login(loginForm as LoginRequest)
      
      // 保存token信息
      const tokenInfo: TokenInfo = {
        accessToken: response.token,
        refreshToken: response.refreshToken,
        expiresIn: response.expiresIn,
        tokenType: response.tokenType
      }
      tokenManager.setToken(tokenInfo)
      
      // 更新状态
      token.value = response.token
      user.value = response.user
      permissions.value = response.user.permissions || []
      
      // 保存用户信息到本地存储
      localStorage.setItem('user', JSON.stringify(response.user))
      
      ElMessage.success('登录成功')
      return { success: true }
    } catch (error: any) {
      const message = error.response?.data?.message || error.message || '登录失败'
      ElMessage.error(message)
      return { success: false, message }
    } finally {
      loading.value = false
    }
  }

  // 登出
  const logout = async () => {
    try {
      // 调用登出API
      if (tokenManager.hasToken()) {
        await authAPI.logout()
      }
    } catch (error) {
      console.error('登出请求失败:', error)
    } finally {
      // 清除状态
      token.value = ''
      user.value = null
      permissions.value = []
      
      // 清除token和用户信息
      tokenManager.clearToken()
      localStorage.removeItem('user')
    }
  }

  // 获取用户信息
  const getUserInfo = async () => {
    try {
      // 如果token无效，抛出错误
      if (!tokenManager.isTokenValid()) {
        throw new Error('用户未登录或token已过期')
      }
      
      // 先尝试从token中解析用户信息
      const userInfoFromToken = tokenManager.getUserInfoFromToken()
      if (userInfoFromToken && user.value) {
        return user.value
      }
      
      // 从API获取最新用户信息
      const userInfo = await authAPI.getUserInfo()
      user.value = userInfo
      permissions.value = userInfo.permissions || []
      
      // 更新本地存储
      localStorage.setItem('user', JSON.stringify(userInfo))
      
      return userInfo
    } catch (error) {
      console.error('获取用户信息失败:', error)
      // 如果获取失败，清除登录状态
      await logout()
      throw error
    }
  }

  // 检查权限
  const hasPermission = (permission: string) => {
    if (userRole.value === 'admin') return true
    return permissions.value.includes(permission) || permissions.value.includes('*')
  }

  // 检查角色
  const hasRole = (role: string) => {
    return userRole.value === role
  }

  // 刷新token
  const refreshToken = async () => {
    try {
      const refreshTokenValue = tokenManager.getRefreshToken()
      if (!refreshTokenValue) {
        throw new Error('刷新令牌不存在')
      }
      
      const response = await authAPI.refreshToken(refreshTokenValue)
      
      // 更新token信息
      const tokenInfo: TokenInfo = {
        accessToken: response.token,
        expiresIn: response.expiresIn
      }
      tokenManager.setToken(tokenInfo)
      token.value = response.token
      
      return true
    } catch (error) {
      console.error('刷新token失败:', error)
      // 刷新失败，清除登录状态
      await logout()
      return false
    }
  }
  
  // 检查并自动刷新token
  const checkAndRefreshToken = async () => {
    if (tokenManager.isTokenExpiringSoon() && tokenManager.getRefreshToken()) {
      return await refreshToken()
    }
    return tokenManager.isTokenValid()
  }

  // 初始化用户信息
  const initUserInfo = () => {
    const savedUser = localStorage.getItem('user')
    if (savedUser && tokenManager.hasToken()) {
      try {
        user.value = JSON.parse(savedUser)
        permissions.value = user.value?.permissions || []
        token.value = tokenManager.getToken() || ''
      } catch (error) {
        console.error('解析用户信息失败:', error)
        logout()
      }
    }
  }

  return {
    token,
    user,
    permissions,
    loading,
    isLoggedIn,
    userRole,
    login,
    logout,
    getUserInfo,
    refreshToken,
    checkAndRefreshToken,
    hasPermission,
    hasRole,
    initUserInfo
  }
})