/**
 * 认证相关API接口
 */
import request from '@/utils/request'
import type { TokenInfo } from '@/utils/token'

/**
 * 登录请求参数
 */
export interface LoginRequest {
  nickname: string  // 使用昵称登录，对应User实体的nickname字段
  password: string
}

/**
 * 登录响应数据
 */
export interface LoginResponse {
  token: string
  refreshToken?: string
  expiresIn?: number
  tokenType?: string
  user: {
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
}

/**
 * 用户信息响应
 */
export interface UserInfoResponse {
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

/**
 * 刷新token响应
 */
export interface RefreshTokenResponse {
  token: string
  expiresIn?: number
}

/**
 * 用户注册请求参数
 */
export interface RegisterRequest {
  nickname: string      // 对应User实体的nickname字段
  email: string         // 对应User实体的email字段
  password: string      // 对应User实体的password字段
  confirmPassword: string // 确认密码（前端验证用）
  phone?: string        // 对应User实体的phone字段（可选）
  gender?: number       // 对应User实体的gender字段：0未知，1男，2女（可选）
}

/**
 * 用户注册响应数据
 */
export interface RegisterResponse {
  userId: number        // 对应User实体的userId字段
  nickname: string      // 对应User实体的nickname字段
  email: string         // 对应User实体的email字段
  phone?: string        // 对应User实体的phone字段
  message: string       // 注册成功消息
}

/**
 * 认证API类
 */
export class AuthAPI {
  /**
   * 用户登录
   */
  static async login(loginData: LoginRequest): Promise<LoginResponse> {
    const response = await request.post('/auth/login', loginData)
    return response.data
  }
  
  /**
   * 用户登出
   */
  static async logout(): Promise<void> {
    await request.post('/auth/logout')
  }
  
  /**
   * 获取当前用户信息
   */
  static async getUserInfo(): Promise<UserInfoResponse> {
    const response = await request.get('/auth/user')
    return response.data
  }
  
  /**
   * 刷新访问令牌
   */
  static async refreshToken(refreshToken: string): Promise<RefreshTokenResponse> {
    const response = await request.post('/auth/refresh', {
      refreshToken
    })
    return response.data
  }
  
  /**
   * 验证token有效性
   */
  static async validateToken(): Promise<boolean> {
    try {
      await request.get('/auth/validate')
      return true
    } catch (error) {
      return false
    }
  }
  
  /**
   * 修改密码
   */
  static async changePassword(data: {
    oldPassword: string
    newPassword: string
  }): Promise<void> {
    await request.post('/auth/change-password', data)
  }
  
  /**
   * 用户注册
   */
  static async register(registerData: RegisterRequest): Promise<RegisterResponse> {
    const response = await request.post('/auth/register', registerData)
    return response.data
  }
}

// 导出默认实例方法
export const authAPI = {
  login: AuthAPI.login.bind(AuthAPI),
  logout: AuthAPI.logout.bind(AuthAPI),
  getUserInfo: AuthAPI.getUserInfo.bind(AuthAPI),
  refreshToken: AuthAPI.refreshToken.bind(AuthAPI),
  validateToken: AuthAPI.validateToken.bind(AuthAPI),
  changePassword: AuthAPI.changePassword.bind(AuthAPI),
  register: AuthAPI.register.bind(AuthAPI)
}

export default authAPI