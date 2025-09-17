/**
 * 分类相关API接口
 */
import request from '@/utils/request'

/**
 * 分类查询参数
 */
export interface CategoryQueryParams {
  page?: number
  size?: number
  keyword?: string
  parentId?: number
  level?: number
  status?: number
}

/**
 * 分类响应数据
 */
export interface CategoryResponse {
  categoryId: number
  categoryName: string
  parentId: number
  parentName?: string
  level: number
  path?: string
  icon?: string
  image?: string
  description?: string
  sortOrder: number
  status: number // 0-禁用, 1-启用
  isNav?: number
  seoKeywords?: string
  seoDescription?: string
  productCount?: number
  children?: CategoryResponse[]
  createTime: string
  updateTime: string
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
 * 分类请求数据
 */
export interface CategoryRequest {
  categoryName: string
  parentId?: number
  description?: string
  icon?: string
  image?: string
  sortOrder?: number
  isActive?: number // 0-禁用, 1-启用（后端使用isActive字段）
  level?: number
  path?: string
  seoKeywords?: string
  seoDescription?: string
  tags?: string
}

/**
 * 分类API类
 */
export class CategoryAPI {
  /**
   * 获取分类列表（分页）
   */
  static async getCategoryList(params: CategoryQueryParams): Promise<PageResponse<CategoryResponse>> {
    const response = await request.get('/categories', { params })
    return response.data
  }

  /**
   * 根据ID获取分类详情
   */
  static async getCategoryById(id: number): Promise<CategoryResponse> {
    const response = await request.get(`/categories/${id}`)
    return response.data
  }

  /**
   * 创建分类
   */
  static async createCategory(data: CategoryRequest): Promise<CategoryResponse> {
    const response = await request.post('/categories', data)
    return response.data
  }

  /**
   * 更新分类
   */
  static async updateCategory(id: number, data: CategoryRequest): Promise<CategoryResponse> {
    const response = await request.put(`/categories/${id}`, data)
    return response.data
  }

  /**
   * 删除分类
   */
  static async deleteCategory(id: number): Promise<void> {
    await request.delete(`/categories/${id}`)
  }

  /**
   * 获取所有顶级分类
   */
  static async getTopCategories(): Promise<CategoryResponse[]> {
    const response = await request.get('/categories/top')
    return response.data
  }

  /**
   * 根据父分类ID获取子分类
   */
  static async getCategoriesByParentId(parentId: number): Promise<CategoryResponse[]> {
    const response = await request.get(`/categories/parent/${parentId}`)
    return response.data
  }

  /**
   * 获取分类树结构
   */
  static async getCategoryTree(): Promise<CategoryResponse[]> {
    const response = await request.get('/categories/tree')
    return response.data
  }

  /**
   * 批量更新分类状态
   */
  static async batchUpdateStatus(ids: number[], isActive: boolean): Promise<void> {
    await request.patch('/categories/batch/status', { ids, isActive })
  }
}

// 导出默认实例方法
export const categoryAPI = {
  getCategoryList: CategoryAPI.getCategoryList.bind(CategoryAPI),
  getCategoryById: CategoryAPI.getCategoryById.bind(CategoryAPI),
  createCategory: CategoryAPI.createCategory.bind(CategoryAPI),
  updateCategory: CategoryAPI.updateCategory.bind(CategoryAPI),
  deleteCategory: CategoryAPI.deleteCategory.bind(CategoryAPI),
  getTopCategories: CategoryAPI.getTopCategories.bind(CategoryAPI),
  getCategoriesByParentId: CategoryAPI.getCategoriesByParentId.bind(CategoryAPI),
  getCategoryTree: CategoryAPI.getCategoryTree.bind(CategoryAPI),
  batchUpdateStatus: CategoryAPI.batchUpdateStatus.bind(CategoryAPI)
}

export default categoryAPI