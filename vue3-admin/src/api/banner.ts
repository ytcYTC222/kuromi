import request from '@/utils/request'

/**
 * 轮播图接口类型定义
 */

// 链接类型枚举
export enum LinkType {
  PRODUCT = 1,   // 商品
  CATEGORY = 2,  // 分类
  ARTICLE = 3,   // 文章
  EXTERNAL = 4   // 外链
}

// 轮播图状态枚举
export enum BannerStatus {
  DISABLED = 0,  // 禁用
  ENABLED = 1    // 启用
}

// 链接类型映射
export const linkTypeMap: Record<number, string> = {
  [LinkType.PRODUCT]: '商品',
  [LinkType.CATEGORY]: '分类',
  [LinkType.ARTICLE]: '文章',
  [LinkType.EXTERNAL]: '外链'
}

// 轮播图请求参数
export interface BannerRequest {
  title: string
  imageUrl: string
  linkType: LinkType
  linkValue?: string
  sortOrder?: number
  isActive?: BannerStatus
  startTime?: string
  endTime?: string
}

// 轮播图响应数据
export interface BannerResponse {
  bannerId: number
  title: string
  imageUrl: string
  linkType: LinkType
  linkTypeName: string
  linkValue?: string
  sortOrder: number
  isActive: BannerStatus
  statusName: string
  startTime?: string
  endTime?: string
  createTime: string
}

// 轮播图列表查询参数
export interface BannerListQuery {
  page?: number
  size?: number
  title?: string
  linkType?: LinkType
  isActive?: BannerStatus
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
 * 轮播图管理API
 */
export const bannerAPI = {
  /**
   * 获取轮播图列表
   */
  getBannerList(params: BannerListQuery): Promise<ApiResponse<PageResult<BannerResponse>>> {
    return request({
      url: '/banners',
      method: 'get',
      params
    })
  },

  /**
   * 根据ID获取轮播图详情
   */
  getBannerById(id: number): Promise<ApiResponse<BannerResponse>> {
    return request({
      url: `/banners/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建轮播图
   */
  createBanner(data: BannerRequest): Promise<ApiResponse<number>> {
    return request({
      url: '/banners',
      method: 'post',
      data
    })
  },

  /**
   * 更新轮播图
   */
  updateBanner(id: number, data: BannerRequest): Promise<ApiResponse<void>> {
    return request({
      url: `/banners/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除轮播图
   */
  deleteBanner(id: number): Promise<ApiResponse<void>> {
    return request({
      url: `/banners/${id}`,
      method: 'delete'
    })
  },

  /**
   * 更新轮播图状态
   */
  updateBannerStatus(id: number, isActive: BannerStatus): Promise<ApiResponse<void>> {
    return request({
      url: `/banners/${id}/status`,
      method: 'patch',
      data: { isActive }
    })
  }
}

export default bannerAPI