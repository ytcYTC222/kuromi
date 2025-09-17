/**
 * 订单相关API接口
 */
import request from '@/utils/request'

/**
 * 订单查询参数
 */
export interface OrderQueryParams {
  page?: number
  size?: number
  keyword?: string
  status?: number
  userId?: number
}

/**
 * 订单状态枚举
 */
export enum OrderStatus {
  PENDING_PAYMENT = 1,  // 待付款
  PAID = 2,            // 已付款
  SHIPPED = 3,         // 已发货
  DELIVERED = 4,       // 已收货
  COMPLETED = 5,       // 已完成
  CANCELLED = 6        // 已取消
}

/**
 * 支付状态枚举
 */
export enum PaymentStatus {
  UNPAID = 0,          // 未支付
  PAID = 1,            // 已支付
  REFUNDED = 2         // 已退款
}

/**
 * 订单商品项响应数据
 */
export interface OrderItemResponse {
  orderItemId: number
  orderId: number
  productId: number
  product?: ProductResponse
  productName: string
  productCode?: string
  productImage?: string
  productSpec?: string
  unitPrice: number  // 商品单价
  quantity: number
  subtotal: number   // 小计金额
  discountAmount?: number
  actualAmount: number  // 实际支付金额
  weight?: number
  volume?: number
  isReviewed?: number
  reviewId?: number
  createTime: string
  updateTime?: string
}

/**
 * 商品响应数据（简化）
 */
export interface ProductResponse {
  productId: number
  productName: string
  productCode?: string
  productImage?: string
  price: number
  originalPrice?: number
  categoryId?: number
  categoryName?: string
  description?: string
  status?: number
}

/**
 * 用户响应数据（简化）
 */
export interface UserResponse {
  userId: number
  username: string
  nickname?: string
  phone?: string
  email?: string
}

/**
 * 地址响应数据（简化）
 */
export interface UserAddressResponse {
  addressId: number
  receiverName: string
  receiverPhone: string
  province: string
  city: string
  district: string
  detailAddress: string
  zipCode?: string
  isDefault: number
}

/**
 * 优惠券响应数据（简化）
 */
export interface CouponResponse {
  couponId: number
  couponName: string
  couponType: number
  discountAmount: number
  minOrderAmount: number
}

/**
 * 订单响应数据
 */
export interface OrderResponse {
  orderId: number
  orderNumber: string
  userId: number
  user?: UserResponse
  addressId?: number
  address?: UserAddressResponse
  status: number
  statusName: string
  paymentStatus: number
  paymentStatusName: string
  paymentMethod?: number
  paymentMethodName?: string
  paymentTime?: string
  paymentTransactionId?: string
  totalAmount: number
  shippingFee: number
  discountAmount: number
  actualAmount: number
  couponId?: number
  coupon?: CouponResponse
  shippingCompany?: string
  trackingNumber?: string
  shippingTime?: string
  deliveryTime?: string
  receiverName?: string
  receiverPhone?: string
  receiverAddress?: string
  remark?: string
  cancelReason?: string
  cancelTime?: string
  refundReason?: string
  refundTime?: string
  refundAmount?: number
  orderItems: OrderItemResponse[]
  createTime: string
  updateTime?: string
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
 * 订单商品项请求数据
 */
export interface OrderItemRequest {
  productId: number
  quantity: number
  specification?: string
}

/**
 * 订单请求数据
 */
export interface OrderRequest {
  userId: number
  addressId: number
  orderItems: OrderItemRequest[]
  couponId?: number
  remark?: string
  deliveryType?: number
  paymentMethod?: number
  invoiceType?: number
  invoiceTitle?: string
  invoiceContent?: string
  expectedDeliveryTime?: string
}

/**
 * 发货请求数据
 */
export interface ShipOrderRequest {
  trackingNumber: string
  shippingCompany: string
}

/**
 * 取消订单请求数据
 */
export interface CancelOrderRequest {
  reason: string
}

/**
 * 状态更新请求数据
 */
export interface StatusUpdateRequest {
  status: number
}

/**
 * 批量状态更新请求数据
 */
export interface BatchStatusUpdateRequest {
  ids: number[]
  status: number
}

/**
 * 订单统计响应数据
 */
export interface OrderStatisticsResponse {
  totalOrders: number
  status1Count: number  // 待付款
  status2Count: number  // 已付款
  status3Count: number  // 已发货
  status4Count: number  // 已收货
  status5Count: number  // 已完成
  status6Count: number  // 已取消
  todayOrders: number
}

/**
 * 销售统计响应数据
 */
export interface SalesStatisticsResponse {
  totalSales: number
  orderCount: number
}

/**
 * 订单API类
 */
export class OrderAPI {
  /**
   * 获取订单列表（分页）
   */
  static async getOrderList(params: OrderQueryParams): Promise<PageResponse<OrderResponse>> {
    const response = await request.get('/orders', { params })
    console.log('获取订单列表（分页）', response)
    return response.data
  }

  /**
   * 根据ID获取订单详情
   */
  static async getOrderById(id: number): Promise<OrderResponse> {
    const response = await request.get(`/orders/${id}`)
    return response.data
  }

  /**
   * 根据订单号获取订单
   */
  static async getOrderByNumber(orderNumber: string): Promise<OrderResponse> {
    const response = await request.get(`/orders/number/${orderNumber}`)
    return response.data
  }

  /**
   * 创建订单
   */
  static async createOrder(data: OrderRequest): Promise<string> {
    const response = await request.post('/orders', data)
    return response.data
  }

  /**
   * 更新订单
   */
  static async updateOrder(id: number, data: OrderRequest): Promise<void> {
    await request.put(`/orders/${id}`, data)
  }

  /**
   * 取消订单
   */
  static async cancelOrder(id: number, data: CancelOrderRequest): Promise<void> {
    await request.patch(`/orders/${id}/cancel`, data)
  }

  /**
   * 确认订单
   */
  static async confirmOrder(id: number): Promise<void> {
    await request.patch(`/orders/${id}/confirm`)
  }

  /**
   * 订单发货
   */
  static async shipOrder(id: number, data: ShipOrderRequest): Promise<void> {
    await request.patch(`/orders/${id}/ship`, data)
  }

  /**
   * 完成订单
   */
  static async completeOrder(id: number): Promise<void> {
    await request.patch(`/orders/${id}/complete`)
  }

  /**
   * 更新订单状态
   */
  static async updateOrderStatus(id: number, data: StatusUpdateRequest): Promise<void> {
    await request.patch(`/orders/${id}/status`, data)
  }

  /**
   * 根据用户ID获取订单列表
   */
  static async getOrdersByUserId(userId: number): Promise<OrderResponse[]> {
    const response = await request.get(`/orders/user/${userId}`)
    return response.data
  }

  /**
   * 根据状态获取订单列表
   */
  static async getOrdersByStatus(status: number): Promise<OrderResponse[]> {
    const response = await request.get(`/orders/status/${status}`)
    return response.data
  }

  /**
   * 获取订单统计信息
   */
  static async getOrderStatistics(): Promise<OrderStatisticsResponse> {
    const response = await request.get('/orders/statistics')
    return response.data
  }

  /**
   * 获取销售统计
   */
  static async getSalesStatistics(startDate?: string, endDate?: string): Promise<SalesStatisticsResponse> {
    const params: any = {}
    if (startDate) params.startDate = startDate
    if (endDate) params.endDate = endDate
    
    const response = await request.get('/orders/sales-statistics', { params })
    return response.data
  }

  /**
   * 批量更新订单状态
   */
  static async batchUpdateStatus(data: BatchStatusUpdateRequest): Promise<void> {
    await request.patch('/orders/batch/status', data)
  }
}

// 导出默认实例方法
export const orderAPI = {
  getOrderList: OrderAPI.getOrderList.bind(OrderAPI),
  getOrderById: OrderAPI.getOrderById.bind(OrderAPI),
  getOrderByNumber: OrderAPI.getOrderByNumber.bind(OrderAPI),
  createOrder: OrderAPI.createOrder.bind(OrderAPI),
  updateOrder: OrderAPI.updateOrder.bind(OrderAPI),
  cancelOrder: OrderAPI.cancelOrder.bind(OrderAPI),
  confirmOrder: OrderAPI.confirmOrder.bind(OrderAPI),
  shipOrder: OrderAPI.shipOrder.bind(OrderAPI),
  completeOrder: OrderAPI.completeOrder.bind(OrderAPI),
  updateOrderStatus: OrderAPI.updateOrderStatus.bind(OrderAPI),
  getOrdersByUserId: OrderAPI.getOrdersByUserId.bind(OrderAPI),
  getOrdersByStatus: OrderAPI.getOrdersByStatus.bind(OrderAPI),
  getOrderStatistics: OrderAPI.getOrderStatistics.bind(OrderAPI),
  getSalesStatistics: OrderAPI.getSalesStatistics.bind(OrderAPI),
  batchUpdateStatus: OrderAPI.batchUpdateStatus.bind(OrderAPI)
}

// 订单状态映射
export const orderStatusMap = {
  [OrderStatus.PENDING_PAYMENT]: '待付款',
  [OrderStatus.PAID]: '已付款',
  [OrderStatus.SHIPPED]: '已发货',
  [OrderStatus.DELIVERED]: '已收货',
  [OrderStatus.COMPLETED]: '已完成',
  [OrderStatus.CANCELLED]: '已取消'
}

// 支付状态映射
export const paymentStatusMap = {
  [PaymentStatus.UNPAID]: '未支付',
  [PaymentStatus.PAID]: '已支付',
  [PaymentStatus.REFUNDED]: '已退款'
}

// 订单状态颜色映射
export const orderStatusColorMap = {
  [OrderStatus.PENDING_PAYMENT]: 'warning',
  [OrderStatus.PAID]: 'info',
  [OrderStatus.SHIPPED]: 'primary',
  [OrderStatus.DELIVERED]: 'success',
  [OrderStatus.COMPLETED]: 'success',
  [OrderStatus.CANCELLED]: 'danger'
}