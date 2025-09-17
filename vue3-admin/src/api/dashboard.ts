/**
 * 仪表板相关API接口
 */
import request from '@/utils/request'

/**
 * 仪表板概览数据响应
 */
export interface DashboardOverviewResponse {
  totalUsers: number
  totalOrders: number
  totalProducts: number
  totalSales: number
  todayOrders: number
  todaySales: number
  salesTrend: {
    date: string
    amount: number
  }[]
  categoryStats: {
    categoryName: string
    orderCount: number
    percentage: number
  }[]
}

/**
 * 销售趋势查询参数
 */
export interface SalesTrendQueryParams {
  days?: number
}

/**
 * 仪表板API类
 */
export class DashboardAPI {
  /**
   * 获取仪表板概览数据
   */
  static async getOverview(): Promise<DashboardOverviewResponse> {
    const response = await request.get('/dashboard/overview')
    return response.data
  }

  /**
   * 获取销售趋势数据
   */
  static async getSalesTrend(params?: SalesTrendQueryParams): Promise<{
    date: string
    amount: number
  }[]> {
    const response = await request.get('/dashboard/sales-trend', { params })
    return response.data
  }

  /**
   * 获取分类统计数据
   */
  static async getCategoryStats(): Promise<{
    categoryName: string
    orderCount: number
    percentage: number
  }[]> {
    const response = await request.get('/dashboard/category-stats')
    return response.data
  }
}

// 导出默认实例方法
export const dashboardAPI = {
  getOverview: DashboardAPI.getOverview.bind(DashboardAPI),
  getSalesTrend: DashboardAPI.getSalesTrend.bind(DashboardAPI),
  getCategoryStats: DashboardAPI.getCategoryStats.bind(DashboardAPI)
}

export default dashboardAPI