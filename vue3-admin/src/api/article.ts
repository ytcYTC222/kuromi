import request from '@/utils/request'

// 文章状态枚举
export enum ArticleStatus {
  DRAFT = 0,     // 草稿
  PUBLISHED = 1  // 已发布
}

// 文章请求类型
export interface ArticleRequest {
  title: string
  excerpt?: string
  content: string
  coverImage?: string
  category?: string
  tags?: string
  author?: string
  status?: ArticleStatus
  publishTime?: string
}

// 文章响应类型
export interface ArticleResponse {
  articleId: number
  title: string
  excerpt?: string
  content: string
  coverImage?: string
  category?: string
  tags?: string
  author?: string
  viewCount: number
  likeCount: number
  status: ArticleStatus
  statusName: string
  createTime: string
  updateTime: string
  publishTime?: string
}

// 分页查询参数
export interface ArticleListParams {
  page?: number
  size?: number
  title?: string
  category?: string
  status?: ArticleStatus
}

// 分页结果类型
export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
}

// 文章API
export const articleAPI = {
  /**
   * 获取文章列表
   */
  getArticleList: (params: ArticleListParams = {}) => {
    return request<PageResult<ArticleResponse>>({
      url: '/articles',
      method: 'GET',
      params
    })
  },

  /**
   * 根据ID获取文章详情
   */
  getArticleById: (id: number) => {
    return request<ArticleResponse>({
      url: `/articles/${id}`,
      method: 'GET'
    })
  },

  /**
   * 创建文章
   */
  createArticle: (data: ArticleRequest) => {
    return request<number>({
      url: '/articles',
      method: 'POST',
      data
    })
  },

  /**
   * 更新文章
   */
  updateArticle: (id: number, data: ArticleRequest) => {
    return request<void>({
      url: `/articles/${id}`,
      method: 'PUT',
      data
    })
  },

  /**
   * 删除文章
   */
  deleteArticle: (id: number) => {
    return request<void>({
      url: `/articles/${id}`,
      method: 'DELETE'
    })
  },

  /**
   * 更新文章状态
   */
  updateArticleStatus: (id: number, status: ArticleStatus) => {
    return request<void>({
      url: `/articles/${id}/status`,
      method: 'PATCH',
      data: { status }
    })
  },

  /**
   * 增加文章浏览量
   */
  incrementViewCount: (id: number) => {
    return request<void>({
      url: `/articles/${id}/view`,
      method: 'POST'
    })
  }
}

export default articleAPI