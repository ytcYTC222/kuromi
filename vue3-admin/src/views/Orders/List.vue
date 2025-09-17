<template>
  <div class="order-list">
    <!-- 搜索和筛选区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="订单号">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入订单号、客户姓名或电话"
            clearable
            style="width: 250px"
          />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="待付款" :value="1" />
            <el-option label="已付款" :value="2" />
            <el-option label="已发货" :value="3" />
            <el-option label="已收货" :value="4" />
            <el-option label="已完成" :value="5" />
            <el-option label="已取消" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input
            v-model="searchForm.userId"
            placeholder="请输入用户ID"
            clearable
            style="width: 120px"
            type="number"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="search-button" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button class="reset-button" @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 订单统计卡片 -->
    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-number">{{ statistics.totalOrders || 0 }}</div>
              <div class="stat-label">总订单</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-number">{{ statistics.status1Count || 0 }}</div>
              <div class="stat-label">待付款</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-number">{{ statistics.status2Count || 0 }}</div>
              <div class="stat-label">已付款</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-number">{{ statistics.todayOrders || 0 }}</div>
              <div class="stat-label">今日订单</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 订单列表 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>订单列表</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleExport">
              <el-icon><Download /></el-icon>
              导出
            </el-button>
            <el-button type="success" @click="refreshData">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="orderList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="orderNumber" label="订单号" width="180" fixed="left">
          <template #default="{ row }">
            <el-link type="primary" @click="viewOrder(row)">
              {{ row.orderNumber }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="receiverName" label="收货人" width="100" />
        <el-table-column prop="receiverPhone" label="联系电话" width="130" />
        <el-table-column prop="orderItems" label="商品" width="170">
          <template #default="{ row }">
            <div class="product-info">
              <div v-for="(item, index) in row.orderItems.slice(0, 2)" :key="index" class="product-item">
                <span class="product-name">{{ item.productName }}</span>
                <span class="product-price">¥{{ item.unitPrice || item.productPrice || 0 }}</span>
                <span class="product-quantity">x{{ item.quantity }}</span>
                <span class="product-subtotal">= ¥{{ item.subtotal || item.totalPrice || 0 }}</span>
              </div>
              <div v-if="row.orderItems.length > 2" class="more-products">
                +{{ row.orderItems.length - 2 }}个商品
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="actualAmount" label="实付金额" width="120">
          <template #default="{ row }">
            <span class="amount">¥{{ row.actualAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paymentStatusName" label="支付状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getPaymentStatusType(row.paymentStatus)" size="small">
              {{ row.paymentStatusName }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column> -->
        <el-table-column prop="paymentTime" label="支付时间" width="180">
          <template #default="{ row }">
            {{ row.paymentTime ? formatDate(row.paymentTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="shippingTime" label="发货时间" width="160">
          <template #default="{ row }">
            {{ row.shippingTime ? formatDate(row.shippingTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text class="action-view-btn" @click="viewOrder(row)">
              查看
            </el-button>
            <el-button 
              v-if="row.status === 1" 
              type="success" 
              size="small" 
              text 
              class="action-confirm-btn"
              @click="confirmOrder(row)"
            >
              确认付款
            </el-button>
            <el-button 
              v-if="row.status === 2" 
              type="success" 
              size="small" 
              text 
              class="action-ship-btn"
              @click="shipOrder(row)"
            >
              发货
            </el-button>
            <el-button 
              v-if="row.status === 3" 
              type="success" 
              size="small" 
              text 
              class="action-complete-btn"
              @click="completeOrder(row)"
            >
              完成
            </el-button>
            <el-button 
              v-if="row.status === 1" 
              type="danger" 
              size="small" 
              text 
              class="action-cancel-btn"
              @click="cancelOrder(row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 发货对话框 -->
    <el-dialog v-model="shipDialogVisible" title="订单发货" width="500px">
      <el-form ref="shipFormRef" :model="shipForm" :rules="shipRules" label-width="100px">
        <el-form-item label="快递公司" prop="shippingCompany">
          <el-select v-model="shipForm.shippingCompany" placeholder="请选择快递公司" style="width: 100%">
            <el-option label="顺丰速运" value="顺丰速运" />
            <el-option label="圆通快递" value="圆通快递" />
            <el-option label="中通快递" value="中通快递" />
            <el-option label="申通快递" value="申通快递" />
            <el-option label="韵达快递" value="韵达快递" />
            <el-option label="京东物流" value="京东物流" />
          </el-select>
        </el-form-item>
        <el-form-item label="快递单号" prop="trackingNumber">
          <el-input v-model="shipForm.trackingNumber" placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="shipDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleShip" :loading="shipLoading">
            确认发货
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="80%"
      max-width="1200px"
      :before-close="handleDetailDialogClose"
    >
      <div v-loading="detailLoading" class="order-detail-dialog">
        <!-- 订单基本信息 -->
        <el-row :gutter="20">
          <el-col :xs="24" :lg="16">
            <el-card class="info-card" shadow="never">
              <template #header>
                <div class="section-header">
                  <el-icon><Document /></el-icon>
                  <span>订单信息</span>
                </div>
              </template>
              
              <el-descriptions :column="2" border>
                <el-descriptions-item label="订单编号">
                  <el-tag type="primary">{{ currentOrder.orderNumber }}</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="订单状态">
                  <el-tag :type="getStatusType(currentOrder.status)">
                    {{ currentOrder.statusName || getStatusText(currentOrder.status) }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="下单时间">
                  {{ currentOrder.createTime || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="支付时间">
                  {{ currentOrder.paymentTime || '未支付' }}
                </el-descriptions-item>
                <el-descriptions-item label="发货时间">
                  {{ currentOrder.shippingTime || '未发货' }}
                </el-descriptions-item>
                <el-descriptions-item label="完成时间">
                  {{ currentOrder.deliveryTime || '未完成' }}
                </el-descriptions-item>
                <el-descriptions-item label="支付方式">
                  {{ currentOrder.paymentMethodName || getPaymentText(currentOrder.paymentMethod) || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="配送方式">
                  {{ currentOrder.shippingCompany || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="订单备注" :span="2">
                  {{ currentOrder.remark || '无' }}
                </el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-col>
          
          <el-col :xs="24" :lg="8">
            <el-card class="info-card" shadow="never">
              <template #header>
                <div class="section-header">
                  <el-icon><User /></el-icon>
                  <span>客户信息</span>
                </div>
              </template>
              
              <el-descriptions :column="1" border>
                <el-descriptions-item label="客户姓名">
                  {{ currentOrder.user?.nickname || currentOrder.receiverName || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="联系电话">
                  {{ currentOrder.user?.phone || currentOrder.receiverPhone || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="客户邮箱">
                  {{ currentOrder.user?.email || '-' }}
                </el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-col>
        </el-row>
        
        <!-- 收货地址 -->
        <el-row style="margin-top: 20px">
          <el-col :span="24">
            <el-card class="info-card" shadow="never">
              <template #header>
                <div class="section-header">
                  <el-icon><Location /></el-icon>
                  <span>收货地址</span>
                </div>
              </template>
              
              <el-descriptions :column="3" border>
                <el-descriptions-item label="收货人">
                  {{ currentOrder.address?.receiverName || currentOrder.receiverName || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="联系电话">
                  {{ currentOrder.address?.receiverPhone || currentOrder.receiverPhone || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="邮政编码">
                  {{ currentOrder.address?.zipCode || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="详细地址" :span="3">
                  {{ currentOrder.receiverAddress || 
                     (currentOrder.address ? 
                       `${currentOrder.address.province} ${currentOrder.address.city} ${currentOrder.address.district} ${currentOrder.address.detailAddress}` : 
                       '-') }}
                </el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-col>
        </el-row>
        
        <!-- 商品信息 -->
        <el-row style="margin-top: 20px">
          <el-col :span="24">
            <el-card class="info-card" shadow="never">
              <template #header>
                <div class="section-header">
                  <el-icon><ShoppingBag /></el-icon>
                  <span>商品信息</span>
                </div>
              </template>
              
              <el-table :data="currentOrder.orderItems || []" border>
                <el-table-column label="商品图片" width="80">
                  <template #default="{ row }">
                    <el-image 
                      :src="row.productImage || 'https://via.placeholder.com/50x50'" 
                      :preview-src-list="[row.productImage || 'https://via.placeholder.com/50x50']"
                      fit="cover"
                      style="width: 50px; height: 50px; border-radius: 4px;"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="商品名称" prop="productName" min-width="200" />
                <el-table-column label="商品编码" prop="productCode" width="120" />
                <el-table-column label="规格" prop="productSpec" width="120" />
                <el-table-column label="单价" width="100">
                  <template #default="{ row }">
                    <span class="price">¥{{ (row.unitPrice || row.price || 0).toFixed(2) }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="数量" prop="quantity" width="80" align="center" />
                <el-table-column label="小计" width="120">
                  <template #default="{ row }">
                    <span class="price">¥{{ (row.subtotal || row.totalPrice || (row.unitPrice || row.price || 0) * row.quantity).toFixed(2) }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>
        </el-row>
        
        <!-- 费用明细 -->
        <el-row :gutter="20" style="margin-top: 20px">
          <el-col :xs="24" :lg="12">
            <el-card class="info-card" shadow="never">
              <template #header>
                <div class="section-header">
                  <el-icon><Money /></el-icon>
                  <span>费用明细</span>
                </div>
              </template>
              
              <div class="cost-detail">
                <div class="cost-item">
                  <span>商品总额：</span>
                  <span class="price">¥{{ (currentOrder.totalAmount || 0).toFixed(2) }}</span>
                </div>
                <div class="cost-item">
                  <span>运费：</span>
                  <span class="price">¥{{ (currentOrder.shippingFee || 0).toFixed(2) }}</span>
                </div>
                <div class="cost-item">
                  <span>优惠金额：</span>
                  <span class="price discount">-¥{{ (currentOrder.discountAmount || 0).toFixed(2) }}</span>
                </div>
                <div class="cost-item total">
                  <span>订单总额：</span>
                  <span class="price total-price">¥{{ (currentOrder.actualAmount || 0).toFixed(2) }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
          
          <el-col :xs="24" :lg="12">
            <el-card class="info-card" shadow="never">
              <template #header>
                <div class="section-header">
                  <el-icon><Setting /></el-icon>
                  <span>订单操作</span>
                </div>
              </template>
              
              <div class="order-actions">
                <el-button 
                  v-if="currentOrder.status === 1"
                  type="success"
                  @click="confirmPayment"
                >
                  确认付款
                </el-button>
                <el-button 
                  v-if="currentOrder.status === 2"
                  type="primary"
                  @click="shipOrderFromDetail"
                >
                  发货
                </el-button>
                <el-button 
                  v-if="currentOrder.status === 3"
                  type="success"
                  @click="completeOrderFromDetail"
                >
                  确认收货
                </el-button>
                <el-button 
                  v-if="[1, 2].includes(currentOrder.status)"
                  type="danger"
                  @click="cancelOrderFromDetail"
                >
                  取消订单
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <!-- 物流信息 -->
        <el-row v-if="currentOrder.trackingNumber" style="margin-top: 20px">
          <el-col :span="24">
            <el-card class="info-card" shadow="never">
              <template #header>
                <div class="section-header">
                  <el-icon><Box /></el-icon>
                  <span>物流信息</span>
                </div>
              </template>
              
              <el-descriptions :column="3" border>
                <el-descriptions-item label="物流公司">
                  {{ currentOrder.shippingCompany || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="运单号">
                  {{ currentOrder.trackingNumber || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="物流状态">
                  <el-tag type="info">
                    已发货
                  </el-tag>
                </el-descriptions-item>
              </el-descriptions>
              
              <div class="logistics-timeline" style="margin-top: 20px">
                <el-timeline>
                  <el-timeline-item 
                    v-if="currentOrder.createTime"
                    :timestamp="currentOrder.createTime"
                    placement="top"
                  >
                    订单已创建
                  </el-timeline-item>
                  <el-timeline-item 
                    v-if="currentOrder.paymentTime"
                    :timestamp="currentOrder.paymentTime"
                    placement="top"
                  >
                    支付成功
                  </el-timeline-item>
                  <el-timeline-item 
                    v-if="currentOrder.shippingTime"
                    :timestamp="currentOrder.shippingTime"
                    placement="top"
                  >
                    商品已发货
                  </el-timeline-item>
                  <el-timeline-item 
                    v-if="currentOrder.deliveryTime"
                    :timestamp="currentOrder.deliveryTime"
                    placement="top"
                  >
                    商品已送达
                  </el-timeline-item>
                </el-timeline>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useRouter } from 'vue-router'
import {
  orderAPI,
  type OrderResponse,
  type OrderQueryParams,
  type OrderStatisticsResponse,
  OrderStatus,
  PaymentStatus,
  orderStatusColorMap
} from '@/api/order'
import {
  Search,
  Refresh,
  Download,
  ArrowDown,
  Document,
  User,
  Location,
  ShoppingBag,
  Money,
  Setting,
  Box
} from '@element-plus/icons-vue'

const router = useRouter()

// 搜索表单
const searchForm = reactive<OrderQueryParams>({
  keyword: '',
  status: undefined,
  userId: undefined
})

// 加载状态
const loading = ref(false)
const shipLoading = ref(false)
const detailLoading = ref(false)

// 选中的订单
const selectedOrders = ref<OrderResponse[]>([])

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 订单列表
const orderList = ref<OrderResponse[]>([])

// 订单统计
const statistics = ref<OrderStatisticsResponse>({
  totalOrders: 0,
  status1Count: 0,
  status2Count: 0,
  status3Count: 0,
  status4Count: 0,
  status5Count: 0,
  status6Count: 0,
  todayOrders: 0
})

// 发货对话框
const shipDialogVisible = ref(false)
const shipFormRef = ref<FormInstance>()
const currentShipOrder = ref<OrderResponse | null>(null)

// 发货表单
const shipForm = reactive({
  shippingCompany: '',
  trackingNumber: ''
})

// 发货表单验证规则
const shipRules: FormRules = {
  shippingCompany: [
    { required: true, message: '请选择快递公司', trigger: 'change' }
  ],
  trackingNumber: [
    { required: true, message: '请输入快递单号', trigger: 'blur' },
    { min: 8, max: 30, message: '快递单号长度在8到30个字符', trigger: 'blur' }
  ]
}

// 订单详情对话框
const detailDialogVisible = ref(false)
const currentOrder = ref<OrderResponse>({} as OrderResponse)

// 加载订单列表
const loadOrderList = async () => {
  loading.value = true
  try {
    const params: OrderQueryParams = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    
    const response = await orderAPI.getOrderList(params)
    orderList.value = response.records || []
    pagination.total = response.total
  } catch (error: any) {
    console.error('加载订单列表失败:', error)
    ElMessage.error(`加载订单列表失败：${error.message || '未知错误'}`)
  } finally {
    loading.value = false
  }
}

// 加载订单统计
const loadOrderStatistics = async () => {
  try {
    const response = await orderAPI.getOrderStatistics()
    statistics.value = response
  } catch (error: any) {
    console.error('加载订单统计失败:', error)
    ElMessage.error(`加载订单统计失败：${error.message || '未知错误'}`)
  }
}

// 刷新数据
const refreshData = async () => {
  await Promise.all([
    loadOrderList(),
    loadOrderStatistics()
  ])
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadOrderList()
}

// 重置搜索
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = undefined
  searchForm.userId = undefined
  pagination.page = 1
  loadOrderList()
}

// 分页大小改变
const handleSizeChange = (val: number) => {
  pagination.size = val
  pagination.page = 1
  loadOrderList()
}

// 当前页改变
const handleCurrentChange = (val: number) => {
  pagination.page = val
  loadOrderList()
}

// 选择改变
const handleSelectionChange = (val: OrderResponse[]) => {
  selectedOrders.value = val
}

// 查看订单详情
const viewOrder = async (order: OrderResponse) => {
  detailLoading.value = true
  detailDialogVisible.value = true
  try {
    // 获取完整的订单详情
    const response = await orderAPI.getOrderById(order.orderId)
    currentOrder.value = response
  } catch (error: any) {
    console.error('加载订单详情失败:', error)
    ElMessage.error(`加载订单详情失败：${error.message || '未知错误'}`)
  } finally {
    detailLoading.value = false
  }
}

// 关闭详情对话框
const handleDetailDialogClose = (done: () => void) => {
  done()
}

// 获取订单状态文本
const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    1: '待付款',
    2: '已付款',
    3: '已发货',
    4: '已收货',
    5: '已完成',
    6: '已取消'
  }
  return statusMap[status] || '未知状态'
}

// 获取支付方式文本
const getPaymentText = (method: number | undefined) => {
  if (!method) return '-'
  
  const methodMap: Record<number, string> = {
    1: '微信支付',
    2: '支付宝',
    3: '银行卡',
    4: '货到付款'
  }
  return methodMap[method] || '未知支付方式'
}

// 确认订单付款
const confirmOrder = async (order: OrderResponse) => {
  try {
    await ElMessageBox.confirm(
      `确定要确认订单"${order.orderNumber}"的付款状态吗？`,
      '确认付款',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await orderAPI.confirmOrder(order.orderId)
    ElMessage.success('订单确认成功')
    await refreshData()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`确认订单失败：${error.message || '未知错误'}`)
    }
  }
}

// 从详情对话框确认付款
const confirmPayment = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要确认此订单已付款吗？',
      '确认付款',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    detailLoading.value = true
    await orderAPI.confirmOrder(currentOrder.value.orderId)
    
    // 更新当前订单状态
    currentOrder.value.status = 2
    currentOrder.value.statusName = '已付款'
    
    // 刷新列表数据
    await refreshData()
    
    ElMessage.success('付款确认成功')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`付款确认失败：${error.message || '未知错误'}`)
    }
  } finally {
    detailLoading.value = false
  }
}

// 发货
const shipOrder = (order: OrderResponse) => {
  currentShipOrder.value = order
  shipForm.shippingCompany = ''
  shipForm.trackingNumber = ''
  shipDialogVisible.value = true
}

// 从详情对话框发货
const shipOrderFromDetail = async () => {
  // 关闭详情对话框，打开发货对话框
  detailDialogVisible.value = false
  currentShipOrder.value = currentOrder.value
  shipForm.shippingCompany = ''
  shipForm.trackingNumber = ''
  shipDialogVisible.value = true
}

// 确认发货
const handleShip = async () => {
  if (!shipFormRef.value || !currentShipOrder.value) return
  
  try {
    await shipFormRef.value.validate()
    shipLoading.value = true
    
    await orderAPI.shipOrder(currentShipOrder.value.orderId, {
      trackingNumber: shipForm.trackingNumber,
      shippingCompany: shipForm.shippingCompany
    })
    
    ElMessage.success('订单发货成功')
    shipDialogVisible.value = false
    
    // 如果在详情对话框中发货，更新详情中的订单状态
    if (currentOrder.value.orderId === currentShipOrder.value.orderId) {
      currentOrder.value.status = 3
      currentOrder.value.statusName = '已发货'
      currentOrder.value.shippingCompany = shipForm.shippingCompany
      currentOrder.value.trackingNumber = shipForm.trackingNumber
      currentOrder.value.shippingTime = new Date().toISOString()
    }
    
    await refreshData()
  } catch (error: any) {
    if (error.errorFields) {
      console.error('表单验证失败:', error)
    } else {
      ElMessage.error(`订单发货失败：${error.message || '未知错误'}`)
    }
  } finally {
    shipLoading.value = false
  }
}

// 完成订单
const completeOrder = async (order: OrderResponse) => {
  try {
    await ElMessageBox.confirm(
      `确定要将订单"${order.orderNumber}"标记为已完成吗？`,
      '完成订单',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await orderAPI.completeOrder(order.orderId)
    ElMessage.success('订单完成成功')
    await refreshData()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`完成订单失败：${error.message || '未知错误'}`)
    }
  }
}

// 从详情对话框完成订单
const completeOrderFromDetail = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要完成此订单吗？',
      '确认完成',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    detailLoading.value = true
    await orderAPI.completeOrder(currentOrder.value.orderId)
    
    // 更新当前订单状态
    currentOrder.value.status = 5
    currentOrder.value.statusName = '已完成'
    currentOrder.value.deliveryTime = new Date().toISOString()
    
    // 刷新列表数据
    await refreshData()
    
    ElMessage.success('订单完成')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`完成订单失败：${error.message || '未知错误'}`)
    }
  } finally {
    detailLoading.value = false
  }
}

// 取消订单
const cancelOrder = async (order: OrderResponse) => {
  try {
    const { value: reason } = await ElMessageBox.prompt(
      `请输入取消订单"${order.orderNumber}"的原因：`,
      '取消订单',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入取消原因',
        inputValidator: (value) => {
          if (!value || value.trim().length === 0) {
            return '取消原因不能为空'
          }
          return true
        }
      }
    )
    
    await orderAPI.cancelOrder(order.orderId, { reason })
    ElMessage.success('订单取消成功')
    await refreshData()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`取消订单失败：${error.message || '未知错误'}`)
    }
  }
}

// 从详情对话框取消订单
const cancelOrderFromDetail = async () => {
  try {
    const { value: reason } = await ElMessageBox.prompt(
      '请输入取消订单的原因：',
      '确认取消',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入取消原因',
        inputValidator: (value) => {
          if (!value || value.trim().length === 0) {
            return '取消原因不能为空'
          }
          return true
        }
      }
    )
    
    detailLoading.value = true
    await orderAPI.cancelOrder(currentOrder.value.orderId, { reason })
    
    // 更新当前订单状态
    currentOrder.value.status = 6
    currentOrder.value.statusName = '已取消'
    currentOrder.value.cancelReason = reason
    currentOrder.value.cancelTime = new Date().toISOString()
    
    // 刷新列表数据
    await refreshData()
    
    ElMessage.success('订单已取消')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`取消订单失败：${error.message || '未知错误'}`)
    }
  } finally {
    detailLoading.value = false
  }
}

// 导出订单
const handleExport = () => {
  ElMessage.info('导出功能开发中...')
}

// 获取状态类型
const getStatusType = (status: number) => {
  return orderStatusColorMap[status] || 'info'
}

// 获取支付状态类型
const getPaymentStatusType = (status: number) => {
  switch (status) {
    case PaymentStatus.UNPAID:
      return 'warning'
    case PaymentStatus.PAID:
      return 'success'
    case PaymentStatus.REFUNDED:
      return 'info'
    default:
      return 'info'
  }
}

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 组件挂载时加载数据
onMounted(() => {
  refreshData()
})
</script>

<style lang="scss" scoped>
.order-list {
  padding: 20px;
  
  .search-card {
    margin-bottom: 20px;
    
    .search-form {
      .el-form-item {
        margin-bottom: 0;
      }
    }
    
    // 搜索按钮样式 - 少女心渐变色彩
    .search-button {
      background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 50%, #fecfef 100%) !important;
      border: 1px solid #ff9a9e !important;
      color: #fff !important;
      font-weight: 600;
      border-radius: 25px;
      padding: 10px 20px;
      transition: all 0.3s ease;
      box-shadow: 0 4px 15px rgba(255, 154, 158, 0.4) !important;
      
      &:hover {
        background: linear-gradient(135deg, #ff6b6b 0%, #ff8a95 50%, #ffa8cc 100%) !important;
        transform: translateY(-3px);
        box-shadow: 0 8px 25px rgba(255, 107, 107, 0.5) !important;
        color: #fff !important;
      }
    }
    
    // 重置按钮样式 - 少女心渐变色彩
    .reset-button {
      background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%) !important;
      border: 1px solid #a8edea !important;
      color: #fff !important;
      font-weight: 500;
      border-radius: 25px;
      padding: 10px 20px;
      transition: all 0.3s ease;
      box-shadow: 0 4px 15px rgba(168, 237, 234, 0.4) !important;
      
      &:hover {
        background: linear-gradient(135deg, #79f1a4 0%, #0e4b99 100%) !important;
        transform: translateY(-3px);
        box-shadow: 0 8px 25px rgba(121, 241, 164, 0.5) !important;
        color: #fff !important;
      }
    }
  }
  
  .stats-cards {
    margin-bottom: 20px;
    
    .stat-card {
      text-align: center;
      padding: 20px;
      
      .stat-number {
        font-size: 24px;
        font-weight: bold;
        color: #409eff;
        margin-bottom: 8px;
      }
      
      .stat-label {
        color: #909399;
        font-size: 14px;
      }
    }
  }
  
  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-weight: 600;
      
      .header-actions {
        display: flex;
        gap: 10px;
      }
    }
    
    // 操作列按钮样式 - 少女心渐变色彩
    .action-view-btn {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
      color: #fff !important;
      border: 1px solid #667eea !important;
      border-radius: 20px !important;
      padding: 6px 12px !important;
      margin: 0 2px !important;
      transition: all 0.3s ease !important;
      box-shadow: 0 3px 10px rgba(102, 126, 234, 0.4) !important;
      
      &:hover {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%) !important;
        transform: translateY(-3px) !important;
        box-shadow: 0 6px 20px rgba(240, 147, 251, 0.5) !important;
      }
    }
    
    .action-confirm-btn {
      background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%) !important;
      color: #fff !important;
      border: 1px solid #84fab0 !important;
      border-radius: 20px !important;
      padding: 6px 12px !important;
      margin: 0 2px !important;
      transition: all 0.3s ease !important;
      box-shadow: 0 3px 10px rgba(132, 250, 176, 0.4) !important;
      
      &:hover {
        background: linear-gradient(135deg, #56ab2f 0%, #a8e6cf 100%) !important;
        transform: translateY(-3px) !important;
        box-shadow: 0 6px 20px rgba(86, 171, 47, 0.5) !important;
      }
    }
    
    .action-ship-btn {
      background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%) !important;
      color: #fff !important;
      border: 1px solid #ffecd2 !important;
      border-radius: 20px !important;
      padding: 6px 12px !important;
      margin: 0 2px !important;
      transition: all 0.3s ease !important;
      box-shadow: 0 3px 10px rgba(255, 236, 210, 0.4) !important;
      
      &:hover {
        background: linear-gradient(135deg, #ff9a56 0%, #ff6a88 100%) !important;
        transform: translateY(-3px) !important;
        box-shadow: 0 6px 20px rgba(255, 154, 86, 0.5) !important;
      }
    }
    
    .action-complete-btn {
      background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%) !important;
      color: #fff !important;
      border: 1px solid #a8edea !important;
      border-radius: 20px !important;
      padding: 6px 12px !important;
      margin: 0 2px !important;
      transition: all 0.3s ease !important;
      box-shadow: 0 3px 10px rgba(168, 237, 234, 0.4) !important;
      
      &:hover {
        background: linear-gradient(135deg, #79f1a4 0%, #0e4b99 100%) !important;
        transform: translateY(-3px) !important;
        box-shadow: 0 6px 20px rgba(121, 241, 164, 0.5) !important;
      }
    }
    
    .action-cancel-btn {
      background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%) !important;
      color: #fff !important;
      border: 1px solid #ff9a9e !important;
      border-radius: 20px !important;
      padding: 6px 12px !important;
      margin: 0 2px !important;
      transition: all 0.3s ease !important;
      box-shadow: 0 3px 10px rgba(255, 154, 158, 0.4) !important;
      
      &:hover {
        background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%) !important;
        transform: translateY(-3px) !important;
        box-shadow: 0 6px 20px rgba(255, 107, 107, 0.5) !important;
      }
    }
    
    .product-info {
      .product-item {
        margin-bottom: 4px;
        font-size: 12px;
        color: #606266;
      }
      
      .more-products {
        font-size: 12px;
        color: #909399;
      }
    }
    
    .amount {
      font-weight: 600;
      color: #e6a23c;
    }
    
    .pagination-wrapper {
      margin-top: 20px;
      display: flex;
      justify-content: center;
    }
  }
  
  .order-detail-dialog {
    max-height: 70vh;
    overflow-y: auto;
    padding-right: 10px;
    
    .info-card {
      margin-bottom: 20px;
      border: 1px solid #ebeef5;
      
      .section-header {
        display: flex;
        align-items: center;
        gap: 8px;
        font-weight: 600;
        color: #303133;
      }
    }
    
    .price {
      color: #f56c6c;
      font-weight: 600;
      
      &.discount {
        color: #67c23a;
      }
      
      &.total-price {
        font-size: 18px;
        color: #e6a23c;
      }
    }
    
    .cost-detail {
      .cost-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 8px 0;
        border-bottom: 1px solid #f0f0f0;
        
        &:last-child {
          border-bottom: none;
        }
        
        &.total {
          margin-top: 10px;
          padding-top: 15px;
          border-top: 2px solid #409eff;
          font-size: 16px;
          font-weight: 600;
        }
      }
    }
    
    .order-actions {
      display: flex;
      flex-direction: column;
      gap: 10px;
      
      .el-button {
        width: 100%;
      }
    }
    
    .logistics-timeline {
      max-height: 300px;
      overflow-y: auto;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .order-list {
    padding: 10px;
    
    .search-form {
      .el-form-item {
        width: 100%;
        margin-bottom: 10px;
        
        .el-input,
        .el-select {
          width: 100% !important;
        }
      }
    }
    
    .stats-cards {
      .el-col {
        margin-bottom: 15px;
      }
    }
    
    .table-card {
      overflow-x: auto;
    }
    
    .order-detail-dialog {
      .order-actions {
        .el-button {
          margin-bottom: 8px;
        }
      }
    }
  }
}
</style>