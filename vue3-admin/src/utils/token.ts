/**
 * Token管理工具类
 * 提供token的存储、获取、验证和解析功能
 */

// Token存储键名
const TOKEN_KEY = 'access_token'
const REFRESH_TOKEN_KEY = 'refresh_token'
const TOKEN_EXPIRE_KEY = 'token_expire_time'

/**
 * Token信息接口
 */
export interface TokenInfo {
  accessToken: string
  refreshToken?: string
  expiresIn?: number
  tokenType?: string
}

/**
 * JWT Payload接口
 */
export interface JwtPayload {
  sub: string // 用户ID
  username: string
  role: string
  permissions: string[]
  iat: number // 签发时间
  exp: number // 过期时间
}

/**
 * Token工具类
 */
export class TokenManager {
  /**
   * 设置token信息
   */
  static setToken(tokenInfo: TokenInfo): void {
    const { accessToken, refreshToken, expiresIn } = tokenInfo
    
    // 存储访问令牌
    localStorage.setItem(TOKEN_KEY, accessToken)
    
    // 存储刷新令牌
    if (refreshToken) {
      localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken)
    }
    
    // 计算并存储过期时间
    if (expiresIn) {
      const expireTime = Date.now() + expiresIn * 1000
      localStorage.setItem(TOKEN_EXPIRE_KEY, expireTime.toString())
    }
  }
  
  /**
   * 获取访问令牌
   */
  static getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY)
  }
  
  /**
   * 获取刷新令牌
   */
  static getRefreshToken(): string | null {
    return localStorage.getItem(REFRESH_TOKEN_KEY)
  }
  
  /**
   * 获取token过期时间
   */
  static getTokenExpireTime(): number | null {
    const expireTime = localStorage.getItem(TOKEN_EXPIRE_KEY)
    return expireTime ? parseInt(expireTime) : null
  }
  
  /**
   * 检查token是否存在
   */
  static hasToken(): boolean {
    return !!this.getToken()
  }
  
  /**
   * 检查token是否过期
   */
  static isTokenExpired(): boolean {
    const expireTime = this.getTokenExpireTime()
    if (!expireTime) return false
    
    // 提前5分钟判断为过期，用于刷新token
    return Date.now() > (expireTime - 5 * 60 * 1000)
  }
  
  /**
   * 检查token是否有效（存在且未过期）
   */
  static isTokenValid(): boolean {
    return this.hasToken() && !this.isTokenExpired()
  }
  
  /**
   * 解析JWT token获取payload
   */
  static parseToken(token?: string): JwtPayload | null {
    const targetToken = token || this.getToken()
    if (!targetToken) return null
    
    try {
      // JWT token格式: header.payload.signature
      const parts = targetToken.split('.')
      if (parts.length !== 3) return null
      
      // 解码payload部分
      const payload = parts[1]
      const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'))
      return JSON.parse(decoded) as JwtPayload
    } catch (error) {
      console.error('解析token失败:', error)
      return null
    }
  }
  
  /**
   * 获取token中的用户信息
   */
  static getUserInfoFromToken(): Partial<JwtPayload> | null {
    const payload = this.parseToken()
    if (!payload) return null
    
    return {
      sub: payload.sub,
      username: payload.username,
      role: payload.role,
      permissions: payload.permissions
    }
  }
  
  /**
   * 清除所有token信息
   */
  static clearToken(): void {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(REFRESH_TOKEN_KEY)
    localStorage.removeItem(TOKEN_EXPIRE_KEY)
  }
  
  /**
   * 获取Authorization header值
   */
  static getAuthHeader(): string | null {
    const token = this.getToken()
    return token ? `Bearer ${token}` : null
  }
  
  /**
   * 检查token是否即将过期（30分钟内）
   */
  static isTokenExpiringSoon(): boolean {
    const expireTime = this.getTokenExpireTime()
    if (!expireTime) return false
    
    // 30分钟内过期
    return Date.now() > (expireTime - 30 * 60 * 1000)
  }
}

// 导出默认实例方法
export const tokenManager = {
  setToken: TokenManager.setToken.bind(TokenManager),
  getToken: TokenManager.getToken.bind(TokenManager),
  getRefreshToken: TokenManager.getRefreshToken.bind(TokenManager),
  hasToken: TokenManager.hasToken.bind(TokenManager),
  isTokenValid: TokenManager.isTokenValid.bind(TokenManager),
  isTokenExpired: TokenManager.isTokenExpired.bind(TokenManager),
  parseToken: TokenManager.parseToken.bind(TokenManager),
  getUserInfoFromToken: TokenManager.getUserInfoFromToken.bind(TokenManager),
  clearToken: TokenManager.clearToken.bind(TokenManager),
  getAuthHeader: TokenManager.getAuthHeader.bind(TokenManager),
  isTokenExpiringSoon: TokenManager.isTokenExpiringSoon.bind(TokenManager)
}

export default tokenManager