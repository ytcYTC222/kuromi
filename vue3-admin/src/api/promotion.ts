import request from '@/utils/request'

/**
 * 促销活动接口类型定义
 */

// 促销类型枚举
export enum PromotionType {
  PRODUCT = 1,    // 商品促销
  CATEGORY = 2,   // 分类促销
  GLOBAL = 3      // 全场促销
}

// 折扣类型枚举
export enum DiscountType {
  PERCENTAGE = 1, // 百分比折扣
  FIXED = 2       // 固定金额减免
}

// 促销活动状态枚举
export enum PromotionStatus {
  DISABLED = 0,   // 禁用
  ENABLED = 1     // 启用
}

// 促销类型映射
export const promotionTypeMap: Record<number, string> = {
  [PromotionType.PRODUCT]: '商品促销',
  [PromotionType.CATEGORY]: '分类促销',
  [PromotionType.GLOBAL]: '全场促销'
}

// 折扣类型映射
export const discountTypeMap: Record<number, string> = {
  [DiscountType.PERCENTAGE]: '百分比折扣',
  [DiscountType.FIXED]: '固定金额减免'
}

// 促销活动请求参数
export interface PromotionRequest {
  promotionName: string
  promotionType: PromotionType
  targetId?: number
  discountType: DiscountType
  discountValue: number
  minAmount?: number
  maxDiscount?: number
  startTime: string
  endTime: string
  isActive?: PromotionStatus
  sortOrder?: number
  bannerImage?: string
  description?: string
}

// 促销活动响应数据
export interface PromotionResponse {
  promotionId: number
  promotionName: string
  promotionType: PromotionType
  promotionTypeName: string
  targetId?: number
  targetName?: string
  discountType: DiscountType
  discountTypeName: string
  discountValue: number
  minAmount: number
  maxDiscount?: number
  startTime: string
  endTime: string
  isActive: PromotionStatus
  statusName: string
  sortOrder: number
  bannerImage?: string
  description?: string
  createTime: string
  updateTime: string
}

// 促销活动列表查询参数
export interface PromotionListQuery {
  page?: number
  size?: number
  promotionName?: string
  promotionType?: PromotionType
  isActive?: PromotionStatus
}

// 分页结果
export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
}

// API响应包装
export interface ApiResponse<T> {
  data: T
  message: string
}

/**
 * 促销活动管理API
 */
export const promotionAPI = {
  /**
   * 获取促销活动列表
   */
  getPromotionList(params: PromotionListQuery): Promise<ApiResponse<PageResult<PromotionResponse>>> {
    return request({
      url: '/promotions',
      method: 'get',
      params
    })
  },

  /**
   * 根据ID获取促销活动详情
   */
  getPromotionById(id: number): Promise<ApiResponse<PromotionResponse>> {
    return request({
      url: `/promotions/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建促销活动
   */
  createPromotion(data: PromotionRequest): Promise<ApiResponse<number>> {
    return request({
      url: '/promotions',
      method: 'post',
      data
    })
  },

  /**
   * 更新促销活动
   */
  updatePromotion(id: number, data: PromotionRequest): Promise<ApiResponse<void>> {
    return request({
      url: `/promotions/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除促销活动
   */
  deletePromotion(id: number): Promise<ApiResponse<void>> {
    return request({
      url: `/promotions/${id}`,
      method: 'delete'
    })
  },

  /**
   * 更新促销活动状态
   */
  updatePromotionStatus(id: number, isActive: PromotionStatus): Promise<ApiResponse<void>> {
    return request({
      url: `/promotions/${id}/status`,
      method: 'patch',
      data: { isActive }
    })
  }
}

export default promotionAPI