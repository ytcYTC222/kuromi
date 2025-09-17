/**
 * 商品相关API接口
 */
import request from '@/utils/request'

/**
 * 商品查询参数
 */
export interface ProductQueryParams {
  page?: number
  size?: number
  productName?: string
  productCode?: string
  categoryId?: number
  status?: number
  isHot?: number
  isNew?: number
  minPrice?: number
  maxPrice?: number
  brand?: string
  tags?: string
  keyword?: string
  createTimeStart?: string
  createTimeEnd?: string
  stockWarning?: number
}

/**
 * 商品图片信息
 */
export interface ProductImage {
  imageId: number
  productId: number
  imageUrl: string
  imageType: number // 1主图，2详情图
  sortOrder: number
  createTime: string
}

/**
 * 商品响应数据
 */
export interface ProductResponse {
  productId: number
  productName: string
  productCode: string
  categoryId: number
  categoryName?: string
  brand: string
  description: string
  material: string
  color: string
  size: string
  weight: number
  originalPrice: number
  currentPrice: number
  costPrice: number
  promotionPrice: number
  stockQuantity: number
  salesCount: number
  viewCount: number
  ratingAverage: number
  ratingCount: number
  // 图片相关字段
  imageUrl?: string // 主图URL
  images?: ProductImage[] // 所有图片列表
  mainImage?: string // 主图URL（兼容字段）
  isHot: number
  isNew: number
  isPromotion: number
  promotionStartTime?: string
  promotionEndTime?: string
  status: number
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
 * 商品请求数据
 */
export interface ProductRequest {
  productName: string
  productCode?: string
  categoryId: number
  brand?: string
  description?: string
  material?: string
  color?: string
  size?: string
  weight?: number
  originalPrice: number
  currentPrice: number
  costPrice?: number
  promotionPrice?: number
  stockQuantity: number
  isHot?: number
  isNew?: number
  isPromotion?: number
  promotionStartTime?: string
  promotionEndTime?: string
  status: number
}

/**
 * 商品API类
 */
export class ProductAPI {
  /**
   * 获取商品列表（分页）
   */
  static async getProductList(params: ProductQueryParams): Promise<PageResponse<ProductResponse>> {
    const response = await request.get('/products', { params })
    console.log('获取商品列表（分页）', response)
    return response.data
  }

  /**
   * 根据ID获取商品详情
   */
  static async getProductById(id: number): Promise<ProductResponse> {
    const response = await request.get(`/products/${id}`)
    return response.data
  }

  /**
   * 创建商品
   */
  static async createProduct(data: ProductRequest): Promise<ProductResponse> {
    const response = await request.post('/products', data)
    return response.data
  }

  /**
   * 更新商品
   */
  static async updateProduct(id: number, data: ProductRequest): Promise<ProductResponse> {
    const response = await request.put(`/products/${id}`, data)
    return response.data
  }

  /**
   * 删除商品
   */
  static async deleteProduct(id: number): Promise<void> {
    await request.delete(`/products/${id}`)
  }

  /**
   * 批量删除商品
   */
  static async batchDeleteProducts(ids: number[]): Promise<void> {
    await request.delete('/products/batch', { data: { ids } })
  }

  /**
   * 更新商品状态
   */
  static async updateProductStatus(id: number, status: number): Promise<void> {
    await request.patch(`/products/${id}/status`, { status })
  }

  /**
   * 批量更新商品状态
   */
  static async batchUpdateProductStatus(ids: number[], status: number): Promise<void> {
    await request.patch('/products/batch/status', { ids, status })
  }

  /**
   * 获取热门商品
   */
  static async getHotProducts(limit?: number): Promise<ProductResponse[]> {
    const response = await request.get('/products/hot', { params: { limit } })
    return response.data
  }

  /**
   * 获取推荐商品
   */
  static async getRecommendedProducts(limit?: number): Promise<ProductResponse[]> {
    const response = await request.get('/products/recommended', { params: { limit } })
    return response.data
  }

  /**
   * 搜索商品
   */
  static async searchProducts(keyword: string, params?: ProductQueryParams): Promise<PageResponse<ProductResponse>> {
    const response = await request.get('/products/search', { 
      params: { keyword, ...params } 
    })
    return response.data
  }

  /**
   * 获取库存预警商品
   */
  static async getLowStockProducts(threshold?: number): Promise<ProductResponse[]> {
    const response = await request.get('/products/low-stock', { params: { threshold } })
    return response.data
  }
}

// 导出默认实例方法
export const productAPI = {
  getProductList: ProductAPI.getProductList.bind(ProductAPI),
  getProductById: ProductAPI.getProductById.bind(ProductAPI),
  createProduct: ProductAPI.createProduct.bind(ProductAPI),
  updateProduct: ProductAPI.updateProduct.bind(ProductAPI),
  deleteProduct: ProductAPI.deleteProduct.bind(ProductAPI),
  batchDeleteProducts: ProductAPI.batchDeleteProducts.bind(ProductAPI),
  updateProductStatus: ProductAPI.updateProductStatus.bind(ProductAPI),
  batchUpdateProductStatus: ProductAPI.batchUpdateProductStatus.bind(ProductAPI),
  getHotProducts: ProductAPI.getHotProducts.bind(ProductAPI),
  getRecommendedProducts: ProductAPI.getRecommendedProducts.bind(ProductAPI),
  searchProducts: ProductAPI.searchProducts.bind(ProductAPI),
  getLowStockProducts: ProductAPI.getLowStockProducts.bind(ProductAPI)
}

export default productAPI