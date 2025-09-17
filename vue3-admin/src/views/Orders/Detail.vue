<template>
  <div class="order-detail">
    <el-card 
      v-loading="loading"
      shadow="never"
    >
      <template #header>
        <div class="card-header">
          <span>订单详情</span>
          <div class="header-actions">
            <el-button @click="goBack">
              返回列表
            </el-button>
            <el-button type="primary" @click="printOrder">
              <el-icon><Printer /></el-icon>
              打印订单
            </el-button>
          </div>
        </div>
      </template>
      
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
                <el-tag type="primary">{{ orderInfo.orderNumber || orderInfo.orderNo }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="订单状态">
                <el-tag :type="getStatusType(orderInfo.status)">
                  {{ orderInfo.statusName || getStatusText(orderInfo.status) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="下单时间">
                {{ orderInfo.createTime || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="支付时间">
                {{ orderInfo.paymentTime || orderInfo.payTime || '未支付' }}
              </el-descriptions-item>
              <el-descriptions-item label="发货时间">
                {{ orderInfo.shippingTime || orderInfo.shipTime || '未发货' }}
              </el-descriptions-item>
              <el-descriptions-item label="完成时间">
                {{ orderInfo.deliveryTime || orderInfo.finishTime || '未完成' }}
              </el-descriptions-item>
              <el-descriptions-item label="支付方式">
                {{ orderInfo.paymentMethodName || getPaymentText(orderInfo.paymentMethod) || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="配送方式">
                {{ orderInfo.shippingCompany || orderInfo.shippingMethod || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="订单备注" :span="2">
                {{ orderInfo.remark || '无' }}
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
                {{ orderInfo.user?.nickname || orderInfo.customer?.name || orderInfo.receiverName || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="联系电话">
                {{ orderInfo.user?.phone || orderInfo.customer?.phone || orderInfo.receiverPhone || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="客户邮箱">
                {{ orderInfo.user?.email || orderInfo.customer?.email || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="会员等级">
                <el-tag size="small">{{ orderInfo.customer?.level || '普通会员' }}</el-tag>
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
                {{ orderInfo.address?.receiverName || orderInfo.receiverName || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="联系电话">
                {{ orderInfo.address?.receiverPhone || orderInfo.receiverPhone || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="邮政编码">
                {{ orderInfo.address?.zipCode || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="详细地址" :span="3">
                {{ orderInfo.receiverAddress || 
                   (orderInfo.address ? 
                     `${orderInfo.address.province} ${orderInfo.address.city} ${orderInfo.address.district} ${orderInfo.address.detailAddress}` : 
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
            
            <el-table :data="orderInfo.orderItems || []" border>
              <el-table-column label="商品图片" width="80">
                <template #default="{ row }">
                  <el-image 
                    :src="row.productImage || row.image || 'https://via.placeholder.com/50x50'" 
                    :preview-src-list="[row.productImage || row.image || 'https://via.placeholder.com/50x50']"
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
                <span class="price">¥{{ (orderInfo.totalAmount || orderInfo.cost?.subtotal || 0).toFixed(2) }}</span>
              </div>
              <div class="cost-item">
                <span>运费：</span>
                <span class="price">¥{{ (orderInfo.shippingFee || orderInfo.cost?.shipping || 0).toFixed(2) }}</span>
              </div>
              <div class="cost-item">
                <span>优惠金额：</span>
                <span class="price discount">-¥{{ (orderInfo.discountAmount || orderInfo.cost?.discount || 0).toFixed(2) }}</span>
              </div>
              <div class="cost-item total">
                <span>订单总额：</span>
                <span class="price total-price">¥{{ (orderInfo.actualAmount || orderInfo.cost?.total || 0).toFixed(2) }}</span>
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
                v-if="orderInfo.status === 1"
                type="success"
                @click="confirmPayment"
              >
                确认付款
              </el-button>
              <el-button 
                v-if="orderInfo.status === 2"
                type="primary"
                @click="shipOrder"
              >
                发货
              </el-button>
              <el-button 
                v-if="orderInfo.status === 3"
                type="success"
                @click="completeOrder"
              >
                确认收货
              </el-button>
              <el-button 
                v-if="[1, 2].includes(orderInfo.status)"
                type="danger"
                @click="cancelOrder"
              >
                取消订单
              </el-button>
              <el-button @click="editOrder">
                编辑订单
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 物流信息 -->
      <el-row v-if="orderInfo.trackingNumber" style="margin-top: 20px">
        <el-col :span="24">
          <el-card class="info-card" shadow="never">
            <template #header>
              <div class="section-header">
                <el-icon><Truck /></el-icon>
                <span>物流信息</span>
              </div>
            </template>
            
            <el-descriptions :column="3" border>
              <el-descriptions-item label="物流公司">
                {{ orderInfo.shippingCompany || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="运单号">
                {{ orderInfo.trackingNumber || '-' }}
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
                  v-if="orderInfo.createTime"
                  :timestamp="orderInfo.createTime"
                  placement="top"
                >
                  订单已创建
                </el-timeline-item>
                <el-timeline-item 
                  v-if="orderInfo.paymentTime"
                  :timestamp="orderInfo.paymentTime"
                  placement="top"
                >
                  支付成功
                </el-timeline-item>
                <el-timeline-item 
                  v-if="orderInfo.shippingTime"
                  :timestamp="orderInfo.shippingTime"
                  placement="top"
                >
                  商品已发货
                </el-timeline-item>
                <el-timeline-item 
                  v-if="orderInfo.deliveryTime"
                  :timestamp="orderInfo.deliveryTime"
                  placement="top"
                >
                  商品已送达
                </el-timeline-item>
              </el-timeline>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderAPI, type OrderResponse } from '@/api/order'
import {
  Document,
  User,
  Location,
  ShoppingBag,
  Money,
  Setting,
  Truck,
  Printer
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 订单信息 - 初始化为空，等待API加载
const orderInfo = ref<OrderResponse>({} as OrderResponse)
const loading = ref(false)

// 获取订单状态类型
const getStatusType = (status: number | string) => {
  // 如果是数字状态（API返回）
  if (typeof status === 'number') {
    const statusMap: Record<number, string> = {
      1: 'warning',  // 待付款
      2: 'primary',  // 已付款
      3: 'info',     // 已发货
      4: 'success',  // 已收货
      5: 'success',  // 已完成
      6: 'danger'    // 已取消
    }
    return statusMap[status] || 'info'
  }
  
  // 如果是字符串状态（模拟数据）
  const statusMap: Record<string, string> = {
    pending: 'warning',
    paid: 'primary',
    shipped: 'info',
    completed: 'success',
    cancelled: 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取订单状态文本
const getStatusText = (status: number | string) => {
  // 如果是数字状态（API返回）
  if (typeof status === 'number') {
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
  
  // 如果是字符串状态（模拟数据）
  const statusMap: Record<string, string> = {
    pending: '待付款',
    paid: '已付款',
    shipped: '已发货',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status] || '未知状态'
}

// 获取支付方式文本
const getPaymentText = (method: number | string | undefined) => {
  if (!method) return '-'
  
  // 如果是数字类型（API返回）
  if (typeof method === 'number') {
    const methodMap: Record<number, string> = {
      1: '微信支付',
      2: '支付宝',
      3: '银行卡',
      4: '货到付款'
    }
    return methodMap[method] || '未知支付方式'
  }
  
  // 如果是字符串类型（模拟数据）
  const methodMap: Record<string, string> = {
    wechat: '微信支付',
    alipay: '支付宝',
    bank: '银行卡',
    cash: '货到付款'
  }
  return methodMap[method] || '未知支付方式'
}


// 确认付款
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
    
    loading.value = true
    await orderAPI.confirmOrder(orderInfo.value.orderId)
    
    // 重新加载订单数据
    await loadOrderData()
    
    ElMessage.success('付款确认成功')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`付款确认失败：${error.message || '未知错误'}`)
    }
  } finally {
    loading.value = false
  }
}

// 发货
const shipOrder = async () => {
  try {
    const { value: data } = await ElMessageBox.prompt(
      '请输入快递公司和运单号：',
      '确认发货',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '格式：顺丰速运,SF1234567890',
        inputValidator: (value) => {
          if (!value || !value.includes(',')) {
            return '请按格式输入：快递公司,运单号'
          }
          return true
        }
      }
    )
    
    const [shippingCompany, trackingNumber] = data.split(',')
    
    loading.value = true
    await orderAPI.shipOrder(orderInfo.value.orderId, {
      shippingCompany: shippingCompany.trim(),
      trackingNumber: trackingNumber.trim()
    })
    
    // 重新加载订单数据
    await loadOrderData()
    
    ElMessage.success('发货成功')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`发货失败：${error.message || '未知错误'}`)
    }
  } finally {
    loading.value = false
  }
}

// 完成订单
const completeOrder = async () => {
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
    
    loading.value = true
    await orderAPI.completeOrder(orderInfo.value.orderId)
    
    // 重新加载订单数据
    await loadOrderData()
    
    ElMessage.success('订单完成')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`完成订单失败：${error.message || '未知错误'}`)
    }
  } finally {
    loading.value = false
  }
}

// 取消订单
const cancelOrder = async () => {
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
    
    loading.value = true
    await orderAPI.cancelOrder(orderInfo.value.orderId, { reason })
    
    // 重新加载订单数据
    await loadOrderData()
    
    ElMessage.success('订单已取消')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`取消订单失败：${error.message || '未知错误'}`)
    }
  } finally {
    loading.value = false
  }
}

// 编辑订单
const editOrder = () => {
  ElMessage.info('编辑订单功能开发中')
}

// 打印订单
const printOrder = () => {
  window.print()
}

// 返回列表
const goBack = () => {
  router.push('/orders')
}

// 加载订单数据
const loadOrderData = async () => {
  try {
    const orderId = route.params.id as string
    console.log('从路由获取的订单ID:', orderId)
    if (!orderId) {
      ElMessage.error('订单ID不能为空')
      router.push('/orders')
      return
    }
    
    loading.value = true
    console.log('加载订单数据:', orderId)
    
    // 调用API获取订单详情
    const response = await orderAPI.getOrderById(Number(orderId))
    console.log('API返回的订单详情:', response)
    orderInfo.value = response
    console.log('设置后的orderInfo:', orderInfo.value)
  } catch (error: any) {
    console.error('加载订单数据失败:', error)
    ElMessage.error(`加载订单数据失败：${error.message || '未知错误'}`)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadOrderData()
})
</script>

<style lang="scss" scoped>
.order-detail {
  padding: 20px;
  
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

// 响应式设计
@media (max-width: 768px) {
  .order-detail {
    padding: 10px;
    
    .card-header {
      flex-direction: column;
      gap: 10px;
      align-items: flex-start;
      
      .header-actions {
        width: 100%;
        
        .el-button {
          flex: 1;
        }
      }
    }
    
    .order-actions {
      .el-button {
        margin-bottom: 8px;
      }
    }
  }
}

// 打印样式
@media print {
  .order-detail {
    .header-actions,
    .order-actions {
      display: none !important;
    }
  }
}
</style>