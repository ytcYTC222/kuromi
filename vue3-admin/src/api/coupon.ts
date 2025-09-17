import request from '@/utils/request'

// 优惠卷类型枚举
export enum CouponType {
  FULL_REDUCTION = 1, // 满减
  DISCOUNT = 2,       // 折扣
  FREE_SHIPPING = 3   // 免邮
}

// 优惠卷状态枚举
export enum CouponStatus {
  DISABLED = 0,  // 禁用
  ENABLED = 1    // 启用
}

// 优惠卷实际状态枚举
export enum CouponActualStatus {
  NOT_STARTED = 0, // 未开始
  IN_PROGRESS = 1, // 进行中
  ENDED = 2,       // 已结束
  DISABLED = 3     // 已禁用
}

// 优惠卷类型映射
export const couponTypeMap = {
  [CouponType.FULL_REDUCTION]: '满减',
  [CouponType.DISCOUNT]: '折扣',
  [CouponType.FREE_SHIPPING]: '免邮'
} as const

// 优惠卷状态映射
export const couponStatusMap = {
  [CouponStatus.DISABLED]: '禁用',
  [CouponStatus.ENABLED]: '启用'
} as const

// 优惠卷实际状态映射
export const couponActualStatusMap = {
  [CouponActualStatus.NOT_STARTED]: '未开始',
  [CouponActualStatus.IN_PROGRESS]: '进行中',
  [CouponActualStatus.ENDED]: '已结束',
  [CouponActualStatus.DISABLED]: '已禁用'
} as const

// 优惠卷类型颜色映射
export const couponTypeColorMap = {
  [CouponType.FULL_REDUCTION]: 'success',
  [CouponType.DISCOUNT]: 'warning',
  [CouponType.FREE_SHIPPING]: 'info'
} as const

// 优惠卷状态颜色映射
export const couponStatusColorMap = {
  [CouponStatus.DISABLED]: 'danger',
  [CouponStatus.ENABLED]: 'success'
} as const

// 优惠卷实际状态颜色映射
export const couponActualStatusColorMap = {
  [CouponActualStatus.NOT_STARTED]: 'info',
  [CouponActualStatus.IN_PROGRESS]: 'success',
  [CouponActualStatus.ENDED]: 'warning',
  [CouponActualStatus.DISABLED]: 'danger'
} as const

// API响应包装类型
export interface ApiResponse<T> {
  data: T
  message: string
}

// 优惠卷查询参数
export interface CouponQueryParams {
  page?: number
  size?: number
  keyword?: string
  couponType?: CouponType
  status?: CouponStatus
}

// 优惠卷请求对象
export interface CouponRequest {
  couponName: string
  couponType: CouponType
  discountValue: number
  minAmount?: number
  maxDiscount?: number
  totalQuantity: number
  startTime: string
  endTime: string
  status?: CouponStatus
}

// 优惠卷响应对象
export interface CouponResponse {
  couponId: number
  couponName: string
  couponType: CouponType
  couponTypeName: string
  discountValue: number
  minAmount?: number
  maxDiscount?: number
  totalQuantity: number
  usedQuantity: number
  remainingQuantity: number
  startTime: string
  endTime: string
  status: CouponStatus
  statusName: string
  couponStatus: CouponActualStatus
  couponStatusName: string
  createTime: string
}

// 分页结果
export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
}

// 状态更新请求
export interface StatusUpdateRequest {
  status: CouponStatus
}

// 批量状态更新请求
export interface BatchStatusUpdateRequest {
  couponIds: number[]
  status: CouponStatus
}

// 优惠卷API
export const couponAPI = {
  // 获取优惠卷列表
  getCouponList(params: CouponQueryParams = {}) {
    console.log('获取优惠卷列表（分页）', params)
    return request.get<ApiResponse<PageResult<CouponResponse>>>('/coupons', { params })
  },

  // 获取优惠卷详情
  getCouponById(id: number) {
    return request.get<ApiResponse<CouponResponse>>(`/coupons/${id}`)
  },

  // 创建优惠卷
  createCoupon(data: CouponRequest) {
    return request.post<ApiResponse<CouponResponse>>('/coupons', data)
  },

  // 更新优惠卷
  updateCoupon(id: number, data: CouponRequest) {
    return request.put<ApiResponse<CouponResponse>>(`/coupons/${id}`, data)
  },

  // 删除优惠卷
  deleteCoupon(id: number) {
    return request.delete(`/coupons/${id}`)
  },

  // 批量删除优惠卷
  deleteCoupons(couponIds: number[]) {
    return request.delete('/coupons/batch', { data: couponIds })
  },

  // 更新优惠卷状态
  updateCouponStatus(id: number, data: StatusUpdateRequest) {
    return request.patch(`/coupons/${id}/status`, data)
  },

  // 批量更新优惠卷状态
  updateCouponStatusBatch(data: BatchStatusUpdateRequest) {
    return request.patch('/coupons/batch/status', data)
  },

  // 获取所有启用的优惠卷
  getActiveCoupons() {
    return request.get<CouponResponse[]>('/coupons/active')
  }
}

export default couponAPI