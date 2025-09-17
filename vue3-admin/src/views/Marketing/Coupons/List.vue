<template>
  <div class="coupon-list">
    <!-- 搜索和筛选区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入优惠卷名称"
            clearable
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item label="优惠卷类型">
          <el-select
            v-model="searchForm.couponType"
            placeholder="请选择类型"
            clearable
            style="width: 140px"
          >
            <el-option 
              v-for="(label, value) in couponTypeMap" 
              :key="value" 
              :label="label" 
              :value="Number(value)" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option 
              v-for="(label, value) in couponStatusMap" 
              :key="value" 
              :label="label" 
              :value="Number(value)" 
            />
          </el-select>
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

    <!-- 优惠卷列表 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>优惠卷列表</span>
          <div class="header-actions">
            <el-button type="primary" class="add-button" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              添加优惠卷
            </el-button>
            <el-button 
              type="danger" 
              class="batch-delete-btn"
              :disabled="selectedCoupons.length === 0"
              @click="handleBatchDelete"
            >
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
            <el-button 
              type="success" 
              class="batch-enable-btn"
              :disabled="selectedCoupons.length === 0"
              @click="handleBatchEnable"
            >
              <el-icon><Check /></el-icon>
              批量启用
            </el-button>
            <el-button 
              type="warning" 
              class="batch-disable-btn"
              :disabled="selectedCoupons.length === 0"
              @click="handleBatchDisable"
            >
              <el-icon><Close /></el-icon>
              批量禁用
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="couponList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="couponName" label="优惠卷名称" width="160" />
        <el-table-column prop="couponType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getCouponTypeColor(row.couponType)" size="small">
              {{ row.couponTypeName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="discountValue" label="优惠值" width="120">
          <template #default="{ row }">
            <span v-if="row.couponType === CouponType.FULL_REDUCTION">
              减{{ row.discountValue }}元
            </span>
            <span v-else-if="row.couponType === CouponType.DISCOUNT">
              {{ (row.discountValue * 10).toFixed(1) }}折
            </span>
            <span v-else>
              免邮
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="minAmount" label="使用条件" width="120">
          <template #default="{ row }">
            <span v-if="row.minAmount && row.minAmount > 0">
              满{{ row.minAmount }}元
            </span>
            <span v-else>无门槛</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalQuantity" label="发放数量" width="100" />
        <el-table-column prop="usedQuantity" label="已使用" width="80" />
        <el-table-column prop="remainingQuantity" label="剩余" width="80">
          <template #default="{ row }">
            <span :class="{ 'text-danger': row.remainingQuantity <= 0 }">
              {{ row.remainingQuantity }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="couponStatus" label="券状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getCouponActualStatusColor(row.couponStatus)" size="small">
              {{ row.couponStatusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="启用状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusColor(row.status)" size="small">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="230" fixed="right">
          <template #default="{ row }">
            <el-button size="small" class="action-edit-btn" text @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button 
              size="small" 
              :class="row.status === CouponStatus.ENABLED ? 'action-disable-btn' : 'action-enable-btn'"
              text 
              @click="toggleStatus(row)"
            >
              {{ row.status === CouponStatus.ENABLED ? '禁用' : '启用' }}
            </el-button>
            <!-- <el-button type="info" size="small" text @click="handleView(row)">
              详情
            </el-button> -->
            <el-button 
              size="small" 
              class="action-delete-btn"
              text 
              @click="handleDelete(row)"
              :disabled="row.usedQuantity > 0"
            >
              删除
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDate } from '@/utils'
import { 
  couponAPI, 
  type CouponResponse, 
  type CouponQueryParams,
  CouponType,
  CouponStatus,
  CouponActualStatus,
  couponTypeMap,
  couponStatusMap,
  couponTypeColorMap,
  couponStatusColorMap,
  couponActualStatusColorMap
} from '@/api/coupon'
import {
  Search,
  Refresh,
  Plus,
  Delete,
  Check,
  Close
} from '@element-plus/icons-vue'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  keyword: '',
  couponType: undefined as number | undefined,
  status: undefined as number | undefined
})

// 加载状态
const loading = ref(false)

// 选中的优惠卷
const selectedCoupons = ref<CouponResponse[]>([])

// 分页信息
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 优惠卷列表数据
const couponList = ref<CouponResponse[]>([])

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

// 处理搜索
const handleSearch = () => {
  pagination.page = 1
  loadCouponList()
}

// 处理重置
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    couponType: undefined,
    status: undefined
  })
  pagination.page = 1
  loadCouponList()
}

// 处理选择变化
const handleSelectionChange = (selection: CouponResponse[]) => {
  selectedCoupons.value = selection
}

// 处理分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.page = 1
  loadCouponList()
}

// 处理页码变化
const handleCurrentChange = (page: number) => {
  pagination.page = page
  loadCouponList()
}

// 添加优惠卷
const handleAdd = () => {
  router.push('/marketing/coupons/add')
}

// 编辑优惠卷
const handleEdit = (coupon: CouponResponse) => {
  router.push(`/marketing/coupons/edit/${coupon.couponId}`)
}

// 查看详情
const handleView = (coupon: CouponResponse) => {
  router.push(`/marketing/coupons/detail/${coupon.couponId}`)
}

// 切换状态
const toggleStatus = async (coupon: CouponResponse) => {
  const action = coupon.status === CouponStatus.ENABLED ? '禁用' : '启用'
  const newStatus = coupon.status === CouponStatus.ENABLED ? CouponStatus.DISABLED : CouponStatus.ENABLED
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}优惠卷 "${coupon.couponName}" 吗？`,
      `确认${action}`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用API更新状态
    await couponAPI.updateCouponStatus(coupon.couponId, { status: newStatus })
    
    ElMessage.success(`${action}成功`)
    loadCouponList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败：${error.message || '系统错误'}`)
    }
  }
}

// 删除优惠卷
const handleDelete = async (coupon: CouponResponse) => {
  if (coupon.usedQuantity > 0) {
    ElMessage.warning('该优惠卷已有用户使用，无法删除')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除优惠卷 "${coupon.couponName}" 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用API删除优惠卷
    await couponAPI.deleteCoupon(coupon.couponId)
    
    ElMessage.success('删除成功')
    loadCouponList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`删除失败：${error.message || '系统错误'}`)
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  const canDeleteCoupons = selectedCoupons.value.filter(c => c.usedQuantity === 0)
  const cannotDeleteCoupons = selectedCoupons.value.filter(c => c.usedQuantity > 0)
  
  if (cannotDeleteCoupons.length > 0) {
    ElMessage.warning(`${cannotDeleteCoupons.length}个优惠卷已有用户使用，无法删除`)
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedCoupons.value.length} 个优惠卷吗？此操作不可恢复！`,
      '确认批量删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const couponIds = canDeleteCoupons.map(c => c.couponId)
    await couponAPI.deleteCoupons(couponIds)
    
    ElMessage.success('批量删除成功')
    loadCouponList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`批量删除失败：${error.message || '系统错误'}`)
    }
  }
}

// 批量启用
const handleBatchEnable = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要启用选中的 ${selectedCoupons.value.length} 个优惠卷吗？`,
      '确认批量启用',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const couponIds = selectedCoupons.value.map(c => c.couponId)
    await couponAPI.updateCouponStatusBatch({ couponIds, status: CouponStatus.ENABLED })
    
    ElMessage.success('批量启用成功')
    loadCouponList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`批量启用失败：${error.message || '系统错误'}`)
    }
  }
}

// 批量禁用
const handleBatchDisable = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要禁用选中的 ${selectedCoupons.value.length} 个优惠卷吗？`,
      '确认批量禁用',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const couponIds = selectedCoupons.value.map(c => c.couponId)
    await couponAPI.updateCouponStatusBatch({ couponIds, status: CouponStatus.DISABLED })
    
    ElMessage.success('批量禁用成功')
    loadCouponList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`批量禁用失败：${error.message || '系统错误'}`)
    }
  }
}

// 加载优惠卷列表
const loadCouponList = async () => {
  loading.value = true
  
  try {
    const params: CouponQueryParams = {
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
      couponType: searchForm.couponType,
      status: searchForm.status
    }
    
    console.log('发送API请求，参数：', params)
    const result = await couponAPI.getCouponList(params)
    console.log('API响应结果：', result)
    console.log('响应数据结构：', JSON.stringify(result, null, 2))
    
    // 检查响应结构
    if (result && result.data && result.data.records) {
      couponList.value = result.data.records
      pagination.total = result.data.total
      pagination.page = result.data.current
      pagination.size = result.data.size
      console.log('设置列表数据：', couponList.value)
    } else {
      console.error('响应数据结构不正确：', result)
      ElMessage.error('响应数据格式错误')
      couponList.value = []
      pagination.total = 0
    }
    
  } catch (error: any) {
    console.error('加载优惠卷列表失败：', error)
    console.error('错误详情：', error.response || error.message || error)
    ElMessage.error(`加载优惠卷列表失败：${error.message || '系统错误'}`)
    couponList.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  console.log('优惠卷管理页面挂载')
  // 直接测试API连接
  testAPIConnection()
  loadCouponList()
})

// 测试API连接
const testAPIConnection = async () => {
  try {
    console.log('测试API连接...')
    const response = await fetch('http://localhost:8080/api/coupons?page=1&size=10')
    const data = await response.json()
    console.log('直接API调用结果：', data)
  } catch (error) {
    console.error('API连接测试失败：', error)
  }
}
</script>

<style lang="scss" scoped>
.coupon-list {
  padding: 20px;
  
  .search-card {
    margin-bottom: 20px;
    
    .search-form {
      .el-form-item {
        margin-bottom: 0;
      }
    }
    
    // // 搜索按钮样式
    // .search-button {
    //   background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 50%, #fecfef 100%);
    //   border: 1px solid #ff9a9e;
    //   color: white;
    //   border-radius: 20px;
    //   padding: 8px 20px;
    //   font-weight: 500;
    //   box-shadow: 0 2px 8px rgba(255, 154, 158, 0.3);
    //   transition: all 0.3s ease;
      
    //   &:hover {
    //     background: linear-gradient(135deg, #ff8a8e 0%, #fdbfdf 50%, #fdbfdf 100%);
    //     transform: translateY(-2px);
    //     box-shadow: 0 4px 12px rgba(255, 154, 158, 0.4);
    //   }
    // }
    
    // // 重置按钮样式
    // .reset-button {
    //   background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
    //   border: 1px solid #a8edea;
    //   color: #333;
    //   border-radius: 20px;
    //   padding: 8px 20px;
    //   font-weight: 500;
    //   box-shadow: 0 2px 8px rgba(168, 237, 234, 0.3);
    //   transition: all 0.3s ease;
      
    //   &:hover {
    //     background: linear-gradient(135deg, #98ddd8 0%, #edc6d3 100%);
    //     transform: translateY(-2px);
    //     box-shadow: 0 4px 12px rgba(168, 237, 234, 0.4);
    //     color: #333;
    //   }
    // }
    // 搜索按钮样式 - 少女心渐变色彩
    .search-button {
      background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 50%, #fecfef 100%) !important;
      color: #fff !important;
      border: 1px solid #ff9a9e !important;
      border-radius: 25px !important;
      padding: 8px 20px !important;
      transition: all 0.3s ease !important;
      box-shadow: 0 4px 15px rgba(255, 154, 158, 0.4) !important;
      
      &:hover {
        background: linear-gradient(135deg, #ff6b6b 0%, #ff8a95 50%, #ffa8cc 100%) !important;
        transform: translateY(-3px) !important;
        box-shadow: 0 8px 25px rgba(255, 107, 107, 0.5) !important;
      }
    }
    
    // 重置按钮样式 - 少女心渐变色彩
    .reset-button {
      background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%) !important;
      color: #333 !important;
      border: 1px solid #a8edea !important;
      border-radius: 25px !important;
      padding: 8px 20px !important;
      transition: all 0.3s ease !important;
      box-shadow: 0 4px 15px rgba(168, 237, 234, 0.4) !important;
      
      &:hover {
        background: linear-gradient(135deg, #79f1a4 0%, #0e4b99 100%) !important;
        color: #fff !important;
        transform: translateY(-3px) !important;
        box-shadow: 0 8px 25px rgba(121, 241, 164, 0.5) !important;
      }
    }

  }
  
  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .header-actions {
        display: flex;
        gap: 10px;
      }
    }
    
    // 添加按钮样式
    .add-button {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: 1px solid #667eea;
      color: white;
      border-radius: 20px;
      padding: 8px 20px;
      font-weight: 500;
      box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
      transition: all 0.3s ease;
      
      &:hover {
        background: linear-gradient(135deg, #566dda 0%, #664a92 100%);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
      }
    }
    
    // 批量删除按钮样式
    .batch-delete-btn {
      background: linear-gradient(135deg, #ff6b6b 0%, #ffa8a8 100%);
      border: 1px solid #ff6b6b;
      color: white;
      border-radius: 20px;
      padding: 8px 16px;
      font-weight: 500;
      box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
      transition: all 0.3s ease;
      
      &:hover:not(:disabled) {
        background: linear-gradient(135deg, #ff5b5b 0%, #ff9898 100%);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
      }
      
      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    }
    
    // 批量启用按钮样式
    .batch-enable-btn {
      background: linear-gradient(135deg, #51cf66 0%, #8ce99a 100%);
      border: 1px solid #51cf66;
      color: #333;
      border-radius: 20px;
      padding: 8px 16px;
      font-weight: 500;
      box-shadow: 0 2px 8px rgba(81, 207, 102, 0.3);
      transition: all 0.3s ease;
      
      &:hover:not(:disabled) {
        background: linear-gradient(135deg, #41bf56 0%, #7cd98a 100%);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(81, 207, 102, 0.4);
        color: #333;
      }
      
      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    }
    
    // 批量禁用按钮样式
    .batch-disable-btn {
      background: linear-gradient(135deg, #ffd43b 0%, #fab005 100%);
      border: 1px solid #ffd43b;
      color: #333;
      border-radius: 20px;
      padding: 8px 16px;
      font-weight: 500;
      box-shadow: 0 2px 8px rgba(255, 212, 59, 0.3);
      transition: all 0.3s ease;
      
      &:hover:not(:disabled) {
        background: linear-gradient(135deg, #ffc42b 0%, #ea9f05 100%);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(255, 212, 59, 0.4);
        color: #333;
      }
      
      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    }
    

    // 操作列按钮样式 - 少女心渐变色彩
    .action-edit-btn {
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
    
    .action-enable-btn {
      background: linear-gradient(135deg, #51cf66 0%, #8ce99a 100%);
      border: 1px solid #51cf66;
      color: #333;
      border-radius: 20px;
      padding: 6px 16px;
      font-weight: 500;
      box-shadow: 0 2px 8px rgba(81, 207, 102, 0.3);
      transition: all 0.3s ease;
      
      &:hover {
        background: linear-gradient(135deg, #41bf56 0%, #7cd98a 100%);
        transform: translateY(-3px);
        box-shadow: 0 6px 16px rgba(81, 207, 102, 0.4);
        color: #333;
      }
    }
    
    .action-disable-btn {
      background: linear-gradient(135deg, #fdcb6e 0%, #e17055 100%);
      border: 1px solid #fdcb6e;
      color: #333;
      border-radius: 20px;
      padding: 6px 16px;
      font-weight: 500;
      box-shadow: 0 2px 8px rgba(253, 203, 110, 0.3);
      transition: all 0.3s ease;
      
      &:hover {
        background: linear-gradient(135deg, #fcbb5e 0%, #d16045 100%);
        color: #fff;
        transform: translateY(-3px);
        box-shadow: 0 6px 16px rgba(253, 203, 110, 0.4);
      }
    }
    
    .action-disable-btn {
      background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%) !important;
      color: #333 !important;
      border: 1px solid #ffecd2 !important;
      border-radius: 20px !important;
      padding: 6px 12px !important;
      margin: 0 2px !important;
      transition: all 0.3s ease !important;
      box-shadow: 0 3px 10px rgba(255, 236, 210, 0.4) !important;
      
      &:hover {
        background: linear-gradient(135deg, #ff9a56 0%, #ff6a88 100%) !important;
        color: #fff !important;
        transform: translateY(-3px) !important;
        box-shadow: 0 6px 20px rgba(255, 154, 86, 0.5) !important;
      }
    }
    
    .action-reset-btn {
      background: linear-gradient(135deg, #d299c2 0%, #fef9d7 100%) !important;
      color: #333 !important;
      border: 1px solid #d299c2 !important;
      border-radius: 20px !important;
      padding: 6px 12px !important;
      margin: 0 2px !important;
      transition: all 0.3s ease !important;
      box-shadow: 0 3px 10px rgba(210, 153, 194, 0.4) !important;
      
      &:hover {
        background: linear-gradient(135deg, #89f7fe 0%, #66a6ff 100%) !important;
        color: #fff !important;
        transform: translateY(-3px) !important;
        box-shadow: 0 6px 20px rgba(137, 247, 254, 0.5) !important;
      }
    }
    
    .action-delete-btn {
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
    
    .pagination-wrapper {
      margin-top: 20px;
      display: flex;
      justify-content: center;
    }
  }
  
  .text-danger {
    color: #f56c6c;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .coupon-list {
    padding: 10px;
    
    .search-form {
      .el-form-item {
        width: 100%;
        margin-bottom: 10px;
      }
    }
  }
}
</style>