<template>
  <div class="coupon-detail">
    <el-card shadow="never" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>优惠卷详情</span>
          <div class="header-actions">
            <el-button 
              type="primary" 
              @click="handleEdit"
              :disabled="!couponDetail"
            >
              编辑优惠卷
            </el-button>
            <el-button @click="handleBack">返回列表</el-button>
          </div>
        </div>
      </template>

      <div v-if="couponDetail" class="detail-content">
        <!-- 基本信息 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card shadow="never" class="info-card">
              <template #header>
                <span class="section-title">基本信息</span>
              </template>
              
              <div class="info-item">
                <label>优惠卷ID：</label>
                <span>{{ couponDetail.couponId }}</span>
              </div>
              
              <div class="info-item">
                <label>优惠卷名称：</label>
                <span>{{ couponDetail.couponName }}</span>
              </div>
              
              <div class="info-item">
                <label>优惠卷类型：</label>
                <el-tag :type="getCouponTypeColor(couponDetail.couponType)" size="small">
                  {{ couponDetail.couponTypeName }}
                </el-tag>
              </div>
              
              <div class="info-item">
                <label>优惠值：</label>
                <span class="highlight">
                  <span v-if="couponDetail.couponType === CouponType.FULL_REDUCTION">
                    减{{ couponDetail.discountValue }}元
                  </span>
                  <span v-else-if="couponDetail.couponType === CouponType.DISCOUNT">
                    {{ (couponDetail.discountValue * 10).toFixed(1) }}折
                  </span>
                  <span v-else>
                    免邮
                  </span>
                </span>
              </div>
              
              <div class="info-item" v-if="couponDetail.minAmount">
                <label>使用门槛：</label>
                <span>满{{ couponDetail.minAmount }}元</span>
              </div>
              
              <div class="info-item" v-if="couponDetail.maxDiscount">
                <label>最大优惠：</label>
                <span>{{ couponDetail.maxDiscount }}元</span>
              </div>
            </el-card>
          </el-col>
          
          <el-col :span="12">
            <el-card shadow="never" class="info-card">
              <template #header>
                <span class="section-title">状态与时间</span>
              </template>
              
              <div class="info-item">
                <label>当前状态：</label>
                <el-tag :type="getStatusColor(couponDetail.status)" size="small">
                  {{ couponDetail.statusName }}
                </el-tag>
              </div>
              
              <div class="info-item">
                <label>优惠卷状态：</label>
                <el-tag :type="getCouponActualStatusColor(couponDetail.couponStatus)" size="small">
                  {{ couponDetail.couponStatusName }}
                </el-tag>
              </div>
              
              <div class="info-item">
                <label>开始时间：</label>
                <span>{{ formatDate(couponDetail.startTime) }}</span>
              </div>
              
              <div class="info-item">
                <label>结束时间：</label>
                <span>{{ formatDate(couponDetail.endTime) }}</span>
              </div>
              
              <div class="info-item">
                <label>创建时间：</label>
                <span>{{ formatDate(couponDetail.createTime) }}</span>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 使用情况统计 -->
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="24">
            <el-card shadow="never" class="info-card">
              <template #header>
                <span class="section-title">使用情况统计</span>
              </template>
              
              <el-row :gutter="20">
                <el-col :span="6">
                  <div class="stat-item">
                    <div class="stat-value total">{{ couponDetail.totalQuantity }}</div>
                    <div class="stat-label">发放总数</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="stat-item">
                    <div class="stat-value used">{{ couponDetail.usedQuantity }}</div>
                    <div class="stat-label">已使用</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="stat-item">
                    <div class="stat-value remaining">{{ couponDetail.remainingQuantity }}</div>
                    <div class="stat-label">剩余数量</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="stat-item">
                    <div class="stat-value rate">{{ usageRate }}%</div>
                    <div class="stat-label">使用率</div>
                  </div>
                </el-col>
              </el-row>
              
              <!-- 使用率进度条 -->
              <div style="margin-top: 20px;">
                <el-progress 
                  :percentage="usageRate" 
                  :color="getProgressColor(usageRate)"
                  :stroke-width="20"
                  text-inside
                />
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button 
            v-if="couponDetail.status === CouponStatus.ENABLED" 
            type="warning" 
            @click="handleToggleStatus"
          >
            禁用优惠卷
          </el-button>
          <el-button 
            v-else 
            type="success" 
            @click="handleToggleStatus"
          >
            启用优惠卷
          </el-button>
          
          <el-button 
            type="danger" 
            @click="handleDelete"
            :disabled="couponDetail.usedQuantity > 0"
          >
            删除优惠卷
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDate } from '@/utils'
import { 
  couponAPI, 
  type CouponResponse,
  CouponType,
  CouponStatus,
  CouponActualStatus,
  couponTypeColorMap,
  couponStatusColorMap,
  couponActualStatusColorMap
} from '@/api/coupon'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const couponDetail = ref<CouponResponse | null>(null)

// 计算属性
const couponId = computed(() => Number(route.params.id))

const usageRate = computed(() => {
  if (!couponDetail.value || couponDetail.value.totalQuantity === 0) return 0
  return Math.round((couponDetail.value.usedQuantity / couponDetail.value.totalQuantity) * 100)
})

// 获取优惠卷类型颜色
const getCouponTypeColor = (type: CouponType) => {
  return couponTypeColorMap[type] || 'info'
}

// 获取状态颜色
const getStatusColor = (status: CouponStatus) => {
  return couponStatusColorMap[status] || 'info'
}

// 获取优惠卷实际状态颜色
const getCouponActualStatusColor = (status: CouponActualStatus) => {
  return couponActualStatusColorMap[status] || 'info'
}

// 获取进度条颜色
const getProgressColor = (percentage: number) => {
  if (percentage < 30) return '#5cb87a'
  if (percentage < 70) return '#e6a23c'
  return '#f56c6c'
}

// 返回列表
const handleBack = () => {
  router.push('/marketing/coupons/list')
}

// 编辑优惠卷
const handleEdit = () => {
  if (couponDetail.value) {
    router.push(`/marketing/coupons/edit/${couponDetail.value.couponId}`)
  }
}

// 切换状态
const handleToggleStatus = async () => {
  if (!couponDetail.value) return

  const action = couponDetail.value.status === CouponStatus.ENABLED ? '禁用' : '启用'
  const newStatus = couponDetail.value.status === CouponStatus.ENABLED ? CouponStatus.DISABLED : CouponStatus.ENABLED
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}优惠卷 "${couponDetail.value.couponName}" 吗？`,
      `确认${action}`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await couponAPI.updateCouponStatus(couponDetail.value.couponId, { status: newStatus })
    
    ElMessage.success(`${action}成功`)
    loadCouponDetail()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败：${error.message || '系统错误'}`)
    }
  }
}

// 删除优惠卷
const handleDelete = async () => {
  if (!couponDetail.value) return

  if (couponDetail.value.usedQuantity > 0) {
    ElMessage.warning('该优惠卷已有用户使用，无法删除')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除优惠卷 "${couponDetail.value.couponName}" 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await couponAPI.deleteCoupon(couponDetail.value.couponId)
    
    ElMessage.success('删除成功')
    router.push('/marketing/coupons/list')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`删除失败：${error.message || '系统错误'}`)
    }
  }
}

// 加载优惠卷详情
const loadCouponDetail = async () => {
  try {
    loading.value = true
    
    const response = await couponAPI.getCouponById(couponId.value)
    couponDetail.value = response
    
  } catch (error: any) {
    ElMessage.error(`加载优惠卷详情失败：${error.message || '系统错误'}`)
    router.push('/marketing/coupons/list')
  } finally {
    loading.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  if (couponId.value) {
    loadCouponDetail()
  } else {
    ElMessage.error('无效的优惠卷ID')
    router.push('/marketing/coupons/list')
  }
})
</script>

<style lang="scss" scoped>
.coupon-detail {
  padding: 20px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .header-actions {
      display: flex;
      gap: 10px;
    }
  }
  
  .detail-content {
    .info-card {
      margin-bottom: 20px;
      
      .section-title {
        font-weight: 600;
        font-size: 16px;
      }
      
      .info-item {
        display: flex;
        align-items: center;
        padding: 8px 0;
        border-bottom: 1px solid #f0f0f0;
        
        &:last-child {
          border-bottom: none;
        }
        
        label {
          font-weight: 500;
          color: #666;
          width: 120px;
          flex-shrink: 0;
        }
        
        span {
          color: #333;
          
          &.highlight {
            color: #409eff;
            font-weight: 600;
          }
        }
      }
    }
    
    .stat-item {
      text-align: center;
      padding: 20px;
      border: 1px solid #ebeef5;
      border-radius: 8px;
      
      .stat-value {
        font-size: 28px;
        font-weight: bold;
        margin-bottom: 8px;
        
        &.total { color: #409eff; }
        &.used { color: #f56c6c; }
        &.remaining { color: #67c23a; }
        &.rate { color: #e6a23c; }
      }
      
      .stat-label {
        font-size: 14px;
        color: #666;
      }
    }
    
    .action-buttons {
      margin-top: 30px;
      text-align: center;
      
      .el-button {
        margin: 0 10px;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .coupon-detail {
    padding: 10px;
    
    .card-header {
      flex-direction: column;
      gap: 10px;
      align-items: stretch;
      
      .header-actions {
        justify-content: center;
      }
    }
    
    .info-item {
      flex-direction: column;
      align-items: flex-start !important;
      
      label {
        width: auto !important;
        margin-bottom: 4px;
      }
    }
    
    .action-buttons {
      .el-button {
        width: 100%;
        margin: 5px 0;
      }
    }
  }
}
</style>