/**
 * 用户相关API接口
 */
import request from '@/utils/request'

/**
 * 用户查询参数
 */
export interface UserQueryParams {
  page?: number
  size?: number
  keyword?: string
  status?: number
}

/**
 * 用户状态枚举
 */
export enum UserStatus {
  DISABLED = 0,        // 禁用
  ACTIVE = 1          // 正常
}

/**
 * 用户性别枚举
 */
export enum UserGender {
  UNKNOWN = 0,         // 未知
  MALE = 1,           // 男
  FEMALE = 2          // 女
}

/**
 * 用户响应数据
 */
export interface UserResponse {
  userId: number
  openid?: string
  unionid?: string
  nickname: string
  avatarUrl?: string
  gender?: number
  genderName?: string
  phone?: string
  email?: string
  birthday?: string
  registerTime: string
  lastLoginTime?: string
  status: number
  statusName: string
  totalOrders?: number
  totalConsumption?: number
}

/**
 * 用户请求数据
 */
export interface UserRequest {
  openid?: string
  unionid?: string
  nickname: string
  avatarUrl?: string
  phone?: string
  email?: string
  password?: string
  gender?: number
  birthday?: string
  status?: number
}

/**
 * 用户注册请求数据
 */
export interface UserRegisterRequest {
  nickname: string
  email: string
  password: string
  confirmPassword: string
  phone?: string
  gender?: number
  birthday?: string
  avatarUrl?: string
}

/**
 * 用户注册响应数据
 */
export interface UserRegisterResponse {
  userId: number
  email: string
  phone?: string
  nickname: string
  gender?: number
  status: number
  registerTime: string
  message: string
}

/**
 * 分页响应数据
 */
export interface PageResponse<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

/**
 * 用户状态更新请求数据
 */
export interface UserStatusUpdateRequest {
  status: number
}

/**
 * 批量状态更新请求数据
 */
export interface BatchUserStatusUpdateRequest {
  ids: number[]
  status: number
}

/**
 * 重置密码请求数据
 */
export interface ResetPasswordRequest {
  newPassword: string
}

/**
 * 用户统计响应数据
 */
export interface UserStatisticsResponse {
  totalUsers: number
  activeUsers: number
  disabledUsers: number
  todayNewUsers: number
  monthNewUsers: number
}

/**
 * 用户API类
 */
export class UserAPI {
  /**
   * 获取用户列表（分页）
   */
  static async getUserList(params: UserQueryParams): Promise<PageResponse<UserResponse>> {
    const response = await request.get('/users', { params })
    console.log('获取用户列表（分页）', response)
    return response.data
  }

  /**
   * 根据ID获取用户详情
   */
  static async getUserById(id: number): Promise<UserResponse> {
    const response = await request.get(`/users/${id}`)
    return response.data
  }

  /**
   * 创建用户
   */
  static async createUser(data: UserRequest): Promise<number> {
    const response = await request.post('/users', data)
    return response.data
  }

  /**
   * 更新用户
   */
  static async updateUser(id: number, data: UserRequest): Promise<void> {
    await request.put(`/users/${id}`, data)
  }

  /**
   * 删除用户
   */
  static async deleteUser(id: number): Promise<void> {
    await request.delete(`/users/${id}`)
  }

  /**
   * 更新用户状态
   */
  static async updateUserStatus(id: number, data: UserStatusUpdateRequest): Promise<void> {
    await request.patch(`/users/${id}/status`, data)
  }

  /**
   * 批量更新用户状态
   */
  static async batchUpdateUserStatus(data: BatchUserStatusUpdateRequest): Promise<void> {
    await request.patch('/users/batch/status', data)
  }

  /**
   * 重置用户密码
   */
  static async resetPassword(id: number, data: ResetPasswordRequest): Promise<void> {
    await request.patch(`/users/${id}/password`, {
      password: data.newPassword
    })
  }

  /**
   * 获取用户统计信息
   */
  static async getUserStatistics(): Promise<UserStatisticsResponse> {
    const response = await request.get('/users/statistics')
    return response.data
  }

  /**
   * 用户注册
   */
  static async register(data: UserRegisterRequest): Promise<UserRegisterResponse> {
    const response = await request.post('/users/register', data)
    return response.data
  }

  /**
   * 检查昵称是否存在
   */
  static async checkNicknameExists(nickname: string): Promise<boolean> {
    const response = await request.get(`/users/check-nickname/${nickname}`)
    return response.data
  }

  /**
   * 检查邮箱是否存在
   */
  static async checkEmailExists(email: string): Promise<boolean> {
    const response = await request.get(`/users/check-email/${email}`)
    return response.data
  }

  /**
   * 检查手机号是否存在
   */
  static async checkPhoneExists(phone: string): Promise<boolean> {
    const response = await request.get(`/users/check-phone/${phone}`)
    return response.data
  }
}

// 导出默认实例方法
export const userAPI = {
  getUserList: UserAPI.getUserList.bind(UserAPI),
  getUserById: UserAPI.getUserById.bind(UserAPI),
  createUser: UserAPI.createUser.bind(UserAPI),
  updateUser: UserAPI.updateUser.bind(UserAPI),
  deleteUser: UserAPI.deleteUser.bind(UserAPI),
  updateUserStatus: UserAPI.updateUserStatus.bind(UserAPI),
  batchUpdateUserStatus: UserAPI.batchUpdateUserStatus.bind(UserAPI),
  resetPassword: UserAPI.resetPassword.bind(UserAPI),
  getUserStatistics: UserAPI.getUserStatistics.bind(UserAPI),
  register: UserAPI.register.bind(UserAPI),
  checkNicknameExists: UserAPI.checkNicknameExists.bind(UserAPI),
  checkEmailExists: UserAPI.checkEmailExists.bind(UserAPI),
  checkPhoneExists: UserAPI.checkPhoneExists.bind(UserAPI)
}

// 用户状态映射
export const userStatusMap = {
  [UserStatus.DISABLED]: '禁用',
  [UserStatus.ACTIVE]: '正常'
}

// 用户性别映射
export const userGenderMap = {
  [UserGender.UNKNOWN]: '未知',
  [UserGender.MALE]: '男',
  [UserGender.FEMALE]: '女'
}

// 用户状态颜色映射
export const userStatusColorMap = {
  [UserStatus.DISABLED]: 'danger',
  [UserStatus.ACTIVE]: 'success'
}

// 用户性别颜色映射
export const userGenderColorMap = {
  [UserGender.UNKNOWN]: 'info',
  [UserGender.MALE]: 'primary',
  [UserGender.FEMALE]: 'warning'
}