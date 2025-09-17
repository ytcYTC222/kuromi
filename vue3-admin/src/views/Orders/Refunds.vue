<template>
  <div class="refunds-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="订单号">
          <el-input
            v-model="searchForm.orderNo"
            placeholder="请输入订单号"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="退款状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="待审核" value="pending" />
            <el-option label="已同意" value="approved" />
            <el-option label="已拒绝" value="rejected" />
            <el-option label="已退款" value="refunded" />
          </el-select>
        </el-form-item>
        <el-form-item label="退款类型">
          <el-select
            v-model="searchForm.type"
            placeholder="请选择类型"
            clearable
            style="width: 150px"
          >
            <el-option label="仅退款" value="refund_only" />
            <el-option label="退货退款" value="return_refund" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px"
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

    <!-- 退款列表 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>退款列表</span>
          <div class="header-actions">
            <el-button type="success" class="batch-approve-btn" @click="handleBatchApprove" :disabled="!selectedRefunds.length">
              <el-icon><Check /></el-icon>
              批量同意
            </el-button>
            <el-button type="danger" class="batch-reject-btn" @click="handleBatchReject" :disabled="!selectedRefunds.length">
              <el-icon><Close /></el-icon>
              批量拒绝
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="refundList"
        v-loading="loading"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="退款ID" width="100" />
        <el-table-column prop="orderNo" label="订单号" width="150" />
        <el-table-column prop="productInfo" label="商品信息" min-width="200">
          <template #default="{ row }">
            <div class="product-info">
              <img :src="row.productImage" alt="" class="product-image" />
              <div class="product-details">
                <div class="product-name">{{ row.productName }}</div>
                <div class="product-spec">{{ row.productSpec }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="退款类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.type === 'refund_only' ? 'info' : 'warning'">
              {{ row.type === 'refund_only' ? '仅退款' : '退货退款' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="退款金额" width="120">
          <template #default="{ row }">
            <span class="amount">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="退款原因" min-width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="申请时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" class="action-view-btn" @click="handleView(row)">
              查看
            </el-button>
            <el-button 
              v-if="row.status === 'pending'"
              type="success" 
              size="small" 
              class="action-approve-btn"
              @click="handleApprove(row)"
            >
              同意
            </el-button>
            <el-button 
              v-if="row.status === 'pending'"
              type="danger" 
              size="small" 
              class="action-reject-btn"
              @click="handleReject(row)"
            >
              拒绝
            </el-button>
            <el-button 
              v-if="row.status === 'approved'"
              type="warning" 
              size="small" 
              class="action-refund-btn"
              @click="handleRefund(row)"
            >
              退款
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 退款详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="退款详情"
      width="800px"
      :before-close="handleDetailDialogClose"
    >
      <div v-if="currentRefund" class="refund-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="退款ID">{{ currentRefund.id }}</el-descriptions-item>
          <el-descriptions-item label="订单号">{{ currentRefund.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="退款类型">
            <el-tag :type="currentRefund.type === 'refund_only' ? 'info' : 'warning'">
              {{ currentRefund.type === 'refund_only' ? '仅退款' : '退货退款' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="退款金额">
            <span class="amount">¥{{ currentRefund.amount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="退款状态">
            <el-tag :type="getStatusType(currentRefund.status)">{{ getStatusLabel(currentRefund.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ currentRefund.createdAt }}</el-descriptions-item>
          <el-descriptions-item label="退款原因" :span="2">{{ currentRefund.reason }}</el-descriptions-item>
          <el-descriptions-item label="用户说明" :span="2">{{ currentRefund.description || '无' }}</el-descriptions-item>
        </el-descriptions>

        <div class="product-section">
          <h4>商品信息</h4>
          <div class="product-info-detail">
            <img :src="currentRefund.productImage" alt="" class="product-image-large" />
            <div class="product-details-large">
              <div class="product-name">{{ currentRefund.productName }}</div>
              <div class="product-spec">规格: {{ currentRefund.productSpec }}</div>
              <div class="product-price">单价: ¥{{ currentRefund.productPrice }}</div>
              <div class="product-quantity">数量: {{ currentRefund.quantity }}</div>
            </div>
          </div>
        </div>

        <div v-if="currentRefund.images && currentRefund.images.length" class="images-section">
          <h4>凭证图片</h4>
          <div class="images-grid">
            <img 
              v-for="(image, index) in currentRefund.images" 
              :key="index"
              :src="image" 
              alt="" 
              class="evidence-image"
              @click="previewImage(image)"
            />
          </div>
        </div>

        <div v-if="currentRefund.adminNote" class="admin-note-section">
          <h4>管理员备注</h4>
          <p>{{ currentRefund.adminNote }}</p>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button class="cancel-button" @click="handleDetailDialogClose">关闭</el-button>
          <el-button 
            v-if="currentRefund && currentRefund.status === 'pending'"
            type="success" 
            class="confirm-button"
            @click="handleApprove(currentRefund)"
          >
            同意退款
          </el-button>
          <el-button 
            v-if="currentRefund && currentRefund.status === 'pending'"
            type="danger" 
            class="reject-button"
            @click="handleReject(currentRefund)"
          >
            拒绝退款
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 处理退款对话框 -->
    <el-dialog
      v-model="processDialogVisible"
      :title="processTitle"
      width="500px"
      :before-close="handleProcessDialogClose"
    >
      <el-form
        ref="processFormRef"
        :model="processForm"
        :rules="processFormRules"
        label-width="100px"
      >
        <el-form-item label="处理备注" prop="note">
          <el-input
            v-model="processForm.note"
            type="textarea"
            :rows="4"
            placeholder="请输入处理备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleProcessDialogClose">取消</el-button>
          <el-button type="primary" @click="handleProcessSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Check, Close } from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const detailDialogVisible = ref(false)
const processDialogVisible = ref(false)
const processTitle = ref('')
const processType = ref('')
const currentRefund = ref(null)
const selectedRefunds = ref([])
const processFormRef = ref()

// 搜索表单
const searchForm = reactive({
  orderNo: '',
  status: '',
  type: '',
  dateRange: []
})

// 分页数据
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 处理表单
const processForm = reactive({
  note: ''
})

// 处理表单验证规则
const processFormRules = {
  note: [
    { required: true, message: '请输入处理备注', trigger: 'blur' }
  ]
}

// 退款列表
const refundList = ref([
  {
    id: 'RF001',
    orderNo: 'ORD20240115001',
    productName: 'iPhone 15 Pro Max',
    productSpec: '深空黑色 256GB',
    productImage: 'https://via.placeholder.com/60x60',
    productPrice: 9999,
    quantity: 1,
    type: 'refund_only',
    amount: 9999,
    reason: '商品质量问题',
    description: '收到商品后发现屏幕有划痕',
    status: 'pending',
    createdAt: '2024-01-15 10:30:00',
    images: ['https://via.placeholder.com/200x200', 'https://via.placeholder.com/200x200']
  },
  {
    id: 'RF002',
    orderNo: 'ORD20240114002',
    productName: 'MacBook Pro 14英寸',
    productSpec: '银色 M3 16GB 512GB',
    productImage: 'https://via.placeholder.com/60x60',
    productPrice: 15999,
    quantity: 1,
    type: 'return_refund',
    amount: 15999,
    reason: '不喜欢',
    description: '颜色不符合预期',
    status: 'approved',
    createdAt: '2024-01-14 14:20:00',
    adminNote: '同意退款，请用户寄回商品'
  },
  {
    id: 'RF003',
    orderNo: 'ORD20240113003',
    productName: 'AirPods Pro 2',
    productSpec: '白色',
    productImage: 'https://via.placeholder.com/60x60',
    productPrice: 1899,
    quantity: 2,
    type: 'refund_only',
    amount: 3798,
    reason: '商品损坏',
    description: '包装破损，耳机有划痕',
    status: 'refunded',
    createdAt: '2024-01-13 09:15:00',
    adminNote: '已处理退款'
  }
])

// 获取状态标签
const getStatusLabel = (status) => {
  const map = {
    pending: '待审核',
    approved: '已同意',
    rejected: '已拒绝',
    refunded: '已退款'
  }
  return map[status] || status
}

// 获取状态类型
const getStatusType = (status) => {
  const map = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger',
    refunded: 'info'
  }
  return map[status] || ''
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchRefundList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    orderNo: '',
    status: '',
    type: '',
    dateRange: []
  })
  pagination.page = 1
  fetchRefundList()
}

// 查看详情
const handleView = (row) => {
  currentRefund.value = { ...row }
  detailDialogVisible.value = true
}

// 同意退款
const handleApprove = (row) => {
  processType.value = 'approve'
  processTitle.value = '同意退款'
  currentRefund.value = { ...row }
  processForm.note = ''
  processDialogVisible.value = true
}

// 拒绝退款
const handleReject = (row) => {
  processType.value = 'reject'
  processTitle.value = '拒绝退款'
  currentRefund.value = { ...row }
  processForm.note = ''
  processDialogVisible.value = true
}

// 执行退款
const handleRefund = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要为订单"${row.orderNo}"执行退款操作吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 这里应该调用退款API
    ElMessage.success('退款成功')
    fetchRefundList()
  } catch {
    ElMessage.info('已取消退款')
  }
}

// 批量同意
const handleBatchApprove = async () => {
  const pendingRefunds = selectedRefunds.value.filter(item => item.status === 'pending')
  if (pendingRefunds.length === 0) {
    ElMessage.warning('请选择待审核的退款申请')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要批量同意${pendingRefunds.length}个退款申请吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 这里应该调用批量同意API
    ElMessage.success('批量同意成功')
    fetchRefundList()
  } catch {
    ElMessage.info('已取消操作')
  }
}

// 批量拒绝
const handleBatchReject = async () => {
  const pendingRefunds = selectedRefunds.value.filter(item => item.status === 'pending')
  if (pendingRefunds.length === 0) {
    ElMessage.warning('请选择待审核的退款申请')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要批量拒绝${pendingRefunds.length}个退款申请吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 这里应该调用批量拒绝API
    ElMessage.success('批量拒绝成功')
    fetchRefundList()
  } catch {
    ElMessage.info('已取消操作')
  }
}

// 选择改变
const handleSelectionChange = (selection) => {
  selectedRefunds.value = selection
}

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  fetchRefundList()
}

// 当前页改变
const handleCurrentChange = (page) => {
  pagination.page = page
  fetchRefundList()
}

// 关闭详情对话框
const handleDetailDialogClose = () => {
  detailDialogVisible.value = false
  currentRefund.value = null
}

// 关闭处理对话框
const handleProcessDialogClose = () => {
  processDialogVisible.value = false
  processForm.note = ''
  currentRefund.value = null
}

// 提交处理
const handleProcessSubmit = async () => {
  try {
    await processFormRef.value.validate()
    
    // 这里应该调用处理API
    const action = processType.value === 'approve' ? '同意' : '拒绝'
    ElMessage.success(`${action}退款成功`)
    processDialogVisible.value = false
    detailDialogVisible.value = false
    fetchRefundList()
  } catch {
    ElMessage.error('请检查表单数据')
  }
}

// 预览图片
const previewImage = (image) => {
  // 这里可以实现图片预览功能
  window.open(image, '_blank')
}

// 获取退款列表
const fetchRefundList = async () => {
  loading.value = true
  try {
    // 这里应该调用API获取数据
    // const response = await refundApi.getList({
    //   ...searchForm,
    //   page: pagination.page,
    //   size: pagination.size
    // })
    // refundList.value = response.data.list
    // pagination.total = response.data.total
    
    // 模拟API延迟
    await new Promise(resolve => setTimeout(resolve, 500))
    pagination.total = 50
  } catch (error) {
    ElMessage.error('获取退款列表失败')
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchRefundList()
})
</script>

<style scoped>
.refunds-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-card .search-button {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 50%, #fecfef 100%);
  border: none;
  border-radius: 20px;
  color: white;
  font-weight: 500;
  padding: 8px 20px;
  box-shadow: 0 4px 15px rgba(255, 154, 158, 0.4);
  transition: all 0.3s ease;
}

.search-card .search-button:hover {
  background: linear-gradient(135deg, #ff8a95 0%, #fdb5e8 50%, #fdb5e8 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 154, 158, 0.6);
}

.search-card .reset-button {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  border: none;
  border-radius: 20px;
  color: #666;
  font-weight: 500;
  padding: 8px 20px;
  box-shadow: 0 4px 15px rgba(168, 237, 234, 0.4);
  transition: all 0.3s ease;
}

.search-card .reset-button:hover {
  background: linear-gradient(135deg, #96e6e1 0%, #fcc5d8 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(168, 237, 234, 0.6);
}

.table-card {
  margin-bottom: 20px;
}

.table-card .batch-approve-btn {
  background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
  border: none;
  border-radius: 20px;
  color: white;
  font-weight: 500;
  padding: 8px 16px;
  box-shadow: 0 4px 15px rgba(132, 250, 176, 0.4);
  transition: all 0.3s ease;
}

.table-card .batch-approve-btn:hover {
  background: linear-gradient(135deg, #6ee89a 0%, #7bc8ee 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(132, 250, 176, 0.6);
}

.table-card .batch-reject-btn {
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
  border: none;
  border-radius: 20px;
  color: white;
  font-weight: 500;
  padding: 8px 16px;
  box-shadow: 0 4px 15px rgba(255, 154, 158, 0.4);
  transition: all 0.3s ease;
}

.table-card .batch-reject-btn:hover {
  background: linear-gradient(135deg, #ff8a95 0%, #f8c1b5 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 154, 158, 0.6);
}

.table-card .action-view-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 15px;
  color: white;
  font-weight: 500;
  box-shadow: 0 3px 10px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.table-card .action-view-btn:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  transform: translateY(-1px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.6);
}

.table-card .action-approve-btn {
  background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
  border: none;
  border-radius: 15px;
  color: white;
  font-weight: 500;
  box-shadow: 0 3px 10px rgba(132, 250, 176, 0.4);
  transition: all 0.3s ease;
}

.table-card .action-approve-btn:hover {
  background: linear-gradient(135deg, #6ee89a 0%, #7bc8ee 100%);
  transform: translateY(-1px);
  box-shadow: 0 5px 15px rgba(132, 250, 176, 0.6);
}

.table-card .action-reject-btn {
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
  border: none;
  border-radius: 15px;
  color: white;
  font-weight: 500;
  box-shadow: 0 3px 10px rgba(255, 154, 158, 0.4);
  transition: all 0.3s ease;
}

.table-card .action-reject-btn:hover {
  background: linear-gradient(135deg, #ff8a95 0%, #f8c1b5 100%);
  transform: translateY(-1px);
  box-shadow: 0 5px 15px rgba(255, 154, 158, 0.6);
}

.table-card .action-refund-btn {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
  border: none;
  border-radius: 15px;
  color: #8b4513;
  font-weight: 500;
  box-shadow: 0 3px 10px rgba(255, 236, 210, 0.4);
  transition: all 0.3s ease;
}

.table-card .action-refund-btn:hover {
  background: linear-gradient(135deg, #ffe0b8 0%, #fa9f85 100%);
  transform: translateY(-1px);
  box-shadow: 0 5px 15px rgba(255, 236, 210, 0.6);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.product-image {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
}

.product-details {
  flex: 1;
}

.product-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.product-spec {
  font-size: 12px;
  color: #909399;
}

.amount {
  color: #f56c6c;
  font-weight: 500;
}

.refund-detail {
  max-height: 600px;
  overflow-y: auto;
}

.product-section {
  margin-top: 20px;
}

.product-section h4 {
  margin-bottom: 10px;
  color: #303133;
}

.product-info-detail {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.product-image-large {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.product-details-large {
  flex: 1;
}

.product-details-large > div {
  margin-bottom: 5px;
}

.product-name {
  font-size: 16px;
  font-weight: 500;
}

.product-spec,
.product-price,
.product-quantity {
  color: #606266;
}

.images-section {
  margin-top: 20px;
}

.images-section h4 {
  margin-bottom: 10px;
  color: #303133;
}

.images-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 10px;
}

.evidence-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
  transition: transform 0.2s;
}

.evidence-image:hover {
  transform: scale(1.05);
}

.admin-note-section {
  margin-top: 20px;
}

.admin-note-section h4 {
  margin-bottom: 10px;
  color: #303133;
}

.admin-note-section p {
  padding: 10px;
  background: #f0f9ff;
  border-left: 4px solid #409eff;
  margin: 0;
}

.dialog-footer .cancel-button {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border: none;
  border-radius: 20px;
  color: white;
  font-weight: 500;
  padding: 8px 20px;
  box-shadow: 0 4px 15px rgba(240, 147, 251, 0.4);
  transition: all 0.3s ease;
}

.dialog-footer .cancel-button:hover {
  background: linear-gradient(135deg, #ee82f0 0%, #f3435e 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(240, 147, 251, 0.6);
}

.dialog-footer .confirm-button {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  border: none;
  border-radius: 20px;
  color: #666;
  font-weight: 500;
  padding: 8px 20px;
  box-shadow: 0 4px 15px rgba(168, 237, 234, 0.4);
  transition: all 0.3s ease;
}

.dialog-footer .confirm-button:hover {
  background: linear-gradient(135deg, #96e6e1 0%, #fcc5d8 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(168, 237, 234, 0.6);
}

.dialog-footer .reject-button {
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
  border: none;
  border-radius: 20px;
  color: white;
  font-weight: 500;
  padding: 8px 20px;
  box-shadow: 0 4px 15px rgba(255, 154, 158, 0.4);
  transition: all 0.3s ease;
}

.dialog-footer .reject-button:hover {
  background: linear-gradient(135deg, #ff8a95 0%, #f8c1b5 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 154, 158, 0.6);
}

@media (max-width: 768px) {
  .refunds-container {
    padding: 10px;
  }
  
  .el-table {
    font-size: 12px;
  }
  
  .el-button {
    padding: 5px 10px;
    font-size: 12px;
  }
  
  .product-info {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .images-grid {
    grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  }
  
  .evidence-image {
    width: 80px;
    height: 80px;
  }
}
</style>