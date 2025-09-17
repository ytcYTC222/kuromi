<template>
  <div class="dashboard kuromi-theme">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <el-card class="welcome-card kuromi-card">
        <div class="welcome-content">
          <div class="welcome-text">
            <h2 class="kuromi-title">欢迎回来，{{ userStore.user?.nickname }}！</h2>
            <p>今天是 {{ currentDate }}，祝您工作愉快</p>
          </div>
          <div class="welcome-avatar">
            <el-avatar :size="60" :src="userStore.user?.avatarUrl" />
          </div>
        </div>
      </el-card>
    </div>

    <!-- 数据统计卡片 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :xs="12" :sm="6" v-for="stat in statsData" :key="stat.key">
          <el-card class="stat-card kuromi-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon" :style="{ backgroundColor: stat.color }">
                <el-icon :size="24">
                  <component :is="stat.icon" />
                </el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stat.value }}</div>
                <div class="stat-label">{{ stat.label }}</div>
                <div class="stat-trend" :class="stat.trend > 0 ? 'positive' : 'negative'">
                  <el-icon :size="12">
                    <ArrowUp v-if="stat.trend > 0" />
                    <ArrowDown v-else />
                  </el-icon>
                  {{ Math.abs(stat.trend) }}%
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <!-- 销售趋势图 -->
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>销售趋势</span>
                <el-select v-model="salesPeriod" size="small" style="width: 100px" @change="loadSalesTrend">
                  <el-option label="7天" value="7" />
                  <el-option label="30天" value="30" />
                  <el-option label="90天" value="90" />
                </el-select>
              </div>
            </template>
            <div ref="salesChartRef" class="chart-container"></div>
          </el-card>
        </el-col>

        <!-- 订单状态分布 -->
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card">
            <template #header>
              <span>订单状态分布</span>
            </template>
            <div ref="orderChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 最新订单 -->
    <div class="recent-section">
      <el-card class="recent-card">
        <template #header>
          <div class="card-header">
            <span>最新订单</span>
            <el-button type="primary" size="small" class="view-all-btn" @click="$router.push('/orders')" v-kuromi-click>
              查看全部
            </el-button>
          </div>
        </template>
        <el-table :data="recentOrders" style="width: 100%" v-loading="ordersLoading">
          <el-table-column prop="orderNumber" label="订单号" width="180" />
          <el-table-column prop="receiverName" label="客户" width="120" />
          <el-table-column prop="actualAmount" label="金额" width="120">
            <template #default="{ row }">
              <span class="amount">¥{{ row.actualAmount.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间">
            <template #default="{ row }">
              {{ formatDate(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="primary" size="small" text class="action-view-btn" @click="viewOrder(row)" v-kuromi-click>
                查看
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { formatDate } from '@/utils'
import { dashboardAPI } from '@/api/dashboard'
import { orderAPI } from '@/api/order'
import type { OrderResponse } from '@/api/order'
import {
  User,
  ShoppingBag,
  Money,
  TrendCharts,
  ArrowUp,
  ArrowDown
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const userStore = useAuthStore()
const router = useRouter()

// 当前日期
const currentDate = ref(new Date().toLocaleDateString('zh-CN', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
  weekday: 'long'
}))

// 销售周期
const salesPeriod = ref('7')

// 图表引用
const salesChartRef = ref<HTMLDivElement>()
const orderChartRef = ref<HTMLDivElement>()

// 统计数据
const statsData = ref([
  {
    key: 'users',
    label: '用户总数',
    value: '0',
    icon: User,
    color: '#409EFF',
    trend: 0
  },
  {
    key: 'orders',
    label: '订单总数',
    value: '0',
    icon: ShoppingBag,
    color: '#67C23A',
    trend: 0
  },
  {
    key: 'revenue',
    label: '总收入',
    value: '¥0',
    icon: Money,
    color: '#E6A23C',
    trend: 0
  },
  {
    key: 'products',
    label: '商品总数',
    value: '0',
    icon: TrendCharts,
    color: '#F56C6C',
    trend: 0
  }
])

// 最新订单数据
const recentOrders = ref<OrderResponse[]>([])
const ordersLoading = ref(false)

// 获取状态类型
const getStatusType = (status: number) => {
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

// 获取状态文本
const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    1: '待付款',
    2: '已付款',
    3: '已发货',
    4: '已收货',
    5: '已完成',
    6: '已取消'
  }
  return statusMap[status] || '未知'
}

// 查看订单
const viewOrder = (order: OrderResponse) => {
  router.push(`/orders/detail/${order.orderId}`)
}

// 加载仪表板数据
const loadDashboardData = async () => {
  try {
    const data = await dashboardAPI.getOverview()
    
    // 更新统计数据
    statsData.value[0].value = data.totalUsers.toLocaleString()
    statsData.value[1].value = data.totalOrders.toLocaleString()
    statsData.value[2].value = `¥${data.totalSales.toLocaleString()}`
    statsData.value[3].value = data.totalProducts.toLocaleString()
    
    // 更新趋势数据（这里简化处理，实际应该从API获取）
    statsData.value[0].trend = 5.2
    statsData.value[1].trend = 8.7
    statsData.value[2].trend = 12.3
    statsData.value[3].trend = 2.1
    
    // 更新图表
    await nextTick()
    updateSalesChart(data.salesTrend)
    updateOrderChart(data.categoryStats)
  } catch (error) {
    console.error('加载仪表板数据失败:', error)
  }
}

// 加载销售趋势数据
const loadSalesTrend = async () => {
  try {
    const data = await dashboardAPI.getSalesTrend({ days: parseInt(salesPeriod.value) })
    await nextTick()
    updateSalesChart(data)
  } catch (error) {
    console.error('加载销售趋势数据失败:', error)
  }
}

// 加载最新订单
const loadRecentOrders = async () => {
  ordersLoading.value = true
  try {
    const response = await orderAPI.getOrderList({ page: 1, size: 5 })
    recentOrders.value = response.records || []
  } catch (error) {
    console.error('加载最新订单失败:', error)
  } finally {
    ordersLoading.value = false
  }
}

// 更新销售趋势图
const updateSalesChart = (data: { date: string; amount: number }[]) => {
  if (!salesChartRef.value) return
  
  const chart = echarts.getInstanceByDom(salesChartRef.value) || echarts.init(salesChartRef.value)
  
  const dates = data.map(item => item.date)
  const amounts = data.map(item => item.amount)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      },
      formatter: (params: any) => {
        const [series] = params
        return `${series.name}<br/>${series.marker} 销售额: ¥${series.value.toLocaleString()}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: (value: number) => `¥${(value / 1000).toFixed(0)}k`
      }
    },
    series: [
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        data: amounts,
        itemStyle: {
          color: '#409EFF'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ])
        }
      }
    ]
  }
  
  chart.setOption(option)
}

// 更新订单状态图
const updateOrderChart = (data: { categoryName: string; orderCount: number; percentage: number }[]) => {
  if (!orderChartRef.value) return
  
  const chart = echarts.getInstanceByDom(orderChartRef.value) || echarts.init(orderChartRef.value)
  
  const chartData = data.map(item => ({
    value: item.orderCount,
    name: item.categoryName
  }))
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '订单分类',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '18',
            fontWeight: 'bold',
            formatter: '{b}\n{d}%'
          }
        },
        labelLine: {
          show: false
        },
        data: chartData
      }
    ]
  }
  
  chart.setOption(option)
}

// 组件挂载后初始化数据
onMounted(async () => {
  await Promise.all([
    loadDashboardData(),
    loadRecentOrders()
  ])
  
  // 初始化图表
  await nextTick()
  const salesChart = echarts.getInstanceByDom(salesChartRef.value!) || echarts.init(salesChartRef.value!)
  const orderChart = echarts.getInstanceByDom(orderChartRef.value!) || echarts.init(orderChartRef.value!)
  
  // 响应式
  window.addEventListener('resize', () => {
    salesChart.resize()
    orderChart.resize()
  })
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';

.dashboard {
  padding: 20px;
  
  .welcome-section {
    margin-bottom: 20px;
    
    .welcome-card {
      .welcome-content {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .welcome-text {
          h2 {
            margin: 0 0 8px 0;
            color: #303133;
            font-size: 24px;
            font-weight: 600;
          }
          
          p {
            margin: 0;
            color: #606266;
            font-size: 14px;
          }
        }
        
        .welcome-avatar {
          flex-shrink: 0;
        }
      }
    }
  }
  
  .stats-section {
    margin-bottom: 20px;
    
    .stat-card {
      .stat-content {
        display: flex;
        align-items: center;
        
        .stat-icon {
          width: 60px;
          height: 60px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 16px;
          color: $text-primary;
        }
        
        .stat-info {
          flex: 1;
          
          .stat-value {
            font-size: 24px;
            font-weight: 600;
            color: #303133;
            margin-bottom: 4px;
          }
          
          .stat-label {
            font-size: 14px;
            color: #606266;
            margin-bottom: 4px;
          }
          
          .stat-trend {
            font-size: 12px;
            display: flex;
            align-items: center;
            gap: 2px;
            
            &.positive {
              color: #67C23A;
            }
            
            &.negative {
              color: #F56C6C;
            }
          }
        }
      }
    }
  }
  
  .charts-section {
    margin-bottom: 20px;
    
    .chart-card {
      .chart-container {
        height: 300px;
        width: 100%;
      }
    }
  }
  
  .recent-section {
    .recent-card {
      .amount {
        font-weight: 600;
        color: #E6A23C;
      }
    }
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  // 查看全部按钮样式 - 少女心渐变色彩
  .view-all-btn {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
    border: none !important;
    border-radius: 20px !important;
    color: white !important;
    font-weight: 500 !important;
    padding: 8px 16px !important;
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4) !important;
    transition: all 0.3s ease !important;
  }
  
  .view-all-btn:hover {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%) !important;
    transform: translateY(-2px) !important;
    box-shadow: 0 6px 20px rgba(240, 147, 251, 0.6) !important;
  }
  
  // 操作列查看按钮样式 - 少女心渐变色彩
  .action-view-btn {
    background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%) !important;
    border: none !important;
    border-radius: 15px !important;
    color: #333 !important;
    font-weight: 500 !important;
    box-shadow: 0 3px 10px rgba(168, 237, 234, 0.4) !important;
    transition: all 0.3s ease !important;
  }
  
  .action-view-btn:hover {
    background: linear-gradient(135deg, #96e6e1 0%, #fcc5d8 100%) !important;
    transform: translateY(-1px) !important;
    box-shadow: 0 5px 15px rgba(168, 237, 234, 0.6) !important;
    color: #333 !important;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .dashboard {
    padding: 10px;
    
    .welcome-section {
      .welcome-card {
        .welcome-content {
          flex-direction: column;
          text-align: center;
          gap: 16px;
        }
      }
    }
    
    .charts-section {
      .chart-card {
        .chart-container {
          height: 250px;
        }
      }
    }
  }
}
</style>