<template>
  <div class="logs-container">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="日志类型">
          <el-select
            v-model="searchForm.type"
            placeholder="请选择日志类型"
            clearable
            style="width: 150px"
          >
            <el-option label="操作日志" value="operation" />
            <el-option label="登录日志" value="login" />
            <el-option label="错误日志" value="error" />
            <el-option label="系统日志" value="system" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作人">
          <el-input
            v-model="searchForm.operator"
            placeholder="请输入操作人"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="IP地址">
          <el-input
            v-model="searchForm.ip"
            placeholder="请输入IP地址"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 350px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          <el-button type="danger" @click="handleClearLogs">
            <el-icon><Delete /></el-icon>
            清空日志
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 日志列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>系统日志</span>
          <div class="header-actions">
            <el-button type="success" @click="handleExport">
              <el-icon><Download /></el-icon>
              导出日志
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="tableData"
        style="width: 100%"
        v-loading="loading"
        row-key="id"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">{{ getTypeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operator" label="操作人" width="120" />
        <el-table-column prop="action" label="操作" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP地址" width="140" />
        <el-table-column prop="userAgent" label="用户代理" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="操作时间" width="180" />
        <el-table-column prop="duration" label="耗时" width="80">
          <template #default="{ row }">
            <span>{{ row.duration }}ms</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 日志详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="日志详情"
      width="800px"
    >
      <div class="log-detail" v-if="currentLog">
        <div class="detail-section">
          <h4>基本信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <label>日志ID：</label>
              <span>{{ currentLog.id }}</span>
            </div>
            <div class="detail-item">
              <label>日志类型：</label>
              <el-tag :type="getTypeTagType(currentLog.type)">{{ getTypeText(currentLog.type) }}</el-tag>
            </div>
            <div class="detail-item">
              <label>操作人：</label>
              <span>{{ currentLog.operator }}</span>
            </div>
            <div class="detail-item">
              <label>操作时间：</label>
              <span>{{ currentLog.createTime }}</span>
            </div>
            <div class="detail-item">
              <label>IP地址：</label>
              <span>{{ currentLog.ip }}</span>
            </div>
            <div class="detail-item">
              <label>耗时：</label>
              <span>{{ currentLog.duration }}ms</span>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h4>操作详情</h4>
          <div class="detail-item full-width">
            <label>操作描述：</label>
            <span>{{ currentLog.action }}</span>
          </div>
          <div class="detail-item full-width">
            <label>请求路径：</label>
            <span>{{ currentLog.path }}</span>
          </div>
          <div class="detail-item full-width">
            <label>请求方法：</label>
            <el-tag :type="getMethodTagType(currentLog.method)">{{ currentLog.method }}</el-tag>
          </div>
        </div>
        
        <div class="detail-section">
          <h4>环境信息</h4>
          <div class="detail-item full-width">
            <label>用户代理：</label>
            <span>{{ currentLog.userAgent }}</span>
          </div>
          <div class="detail-item full-width">
            <label>请求参数：</label>
            <pre class="json-content">{{ formatJson(currentLog.params) }}</pre>
          </div>
          <div class="detail-item full-width" v-if="currentLog.response">
            <label>响应结果：</label>
            <pre class="json-content">{{ formatJson(currentLog.response) }}</pre>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Delete, Download } from '@element-plus/icons-vue'

const loading = ref(false)
const detailDialogVisible = ref(false)
const currentLog = ref<any>(null)

// 搜索表单
const searchForm = reactive({
  type: '',
  operator: '',
  ip: '',
  dateRange: []
})

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 表格数据
const tableData = ref([
  {
    id: 1,
    type: 'login',
    operator: 'admin',
    action: '用户登录',
    ip: '192.168.1.100',
    userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
    createTime: '2024-01-15 09:30:25',
    duration: 156,
    path: '/api/auth/login',
    method: 'POST',
    params: { username: 'admin', password: '******' },
    response: { code: 200, message: '登录成功' }
  },
  {
    id: 2,
    type: 'operation',
    operator: 'admin',
    action: '创建商品',
    ip: '192.168.1.100',
    userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
    createTime: '2024-01-15 10:15:42',
    duration: 234,
    path: '/api/products',
    method: 'POST',
    params: { name: '测试商品', price: 99.99, category: '电子产品' },
    response: { code: 200, message: '创建成功', data: { id: 123 } }
  },
  {
    id: 3,
    type: 'error',
    operator: 'user001',
    action: '访问权限不足',
    ip: '192.168.1.101',
    userAgent: 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36',
    createTime: '2024-01-15 11:22:18',
    duration: 45,
    path: '/api/admin/users',
    method: 'GET',
    params: {},
    response: { code: 403, message: '权限不足' }
  },
  {
    id: 4,
    type: 'system',
    operator: 'system',
    action: '系统定时任务执行',
    ip: '127.0.0.1',
    userAgent: 'System Task',
    createTime: '2024-01-15 12:00:00',
    duration: 1250,
    path: '/task/cleanup',
    method: 'SYSTEM',
    params: { task: 'cleanup_logs', schedule: 'daily' },
    response: { code: 200, message: '任务执行成功', processed: 1500 }
  }
])

// 获取类型标签类型
const getTypeTagType = (type: string) => {
  const typeMap: Record<string, string> = {
    operation: 'success',
    login: 'primary',
    error: 'danger',
    system: 'info'
  }
  return typeMap[type] || ''
}

// 获取类型文本
const getTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    operation: '操作日志',
    login: '登录日志',
    error: '错误日志',
    system: '系统日志'
  }
  return typeMap[type] || type
}

// 获取请求方法标签类型
const getMethodTagType = (method: string) => {
  const methodMap: Record<string, string> = {
    GET: 'info',
    POST: 'success',
    PUT: 'warning',
    DELETE: 'danger',
    SYSTEM: 'primary'
  }
  return methodMap[method] || ''
}

// 格式化JSON
const formatJson = (obj: any) => {
  if (!obj) return ''
  try {
    return JSON.stringify(obj, null, 2)
  } catch {
    return String(obj)
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  fetchData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    type: '',
    operator: '',
    ip: '',
    dateRange: []
  })
  handleSearch()
}

// 查看详情
const handleView = (row: any) => {
  currentLog.value = row
  detailDialogVisible.value = true
}

// 清空日志
const handleClearLogs = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清空所有日志吗？此操作不可恢复！',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('日志清空成功')
    fetchData()
  } catch {
    // 用户取消
  }
}

// 导出日志
const handleExport = async () => {
  try {
    loading.value = true
    
    // 模拟导出API调用
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    ElMessage.success('日志导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  } finally {
    loading.value = false
  }
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  fetchData()
}

// 当前页改变
const handleCurrentChange = (page: number) => {
  pagination.currentPage = page
  fetchData()
}

// 获取数据
const fetchData = async () => {
  try {
    loading.value = true
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    pagination.total = tableData.value.length
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.logs-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 0;
}

.table-card {
  margin-bottom: 20px;
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
  margin-top: 20px;
  text-align: right;
}

.log-detail {
  max-height: 600px;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.detail-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.detail-section h4 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.detail-item.full-width {
  grid-column: 1 / -1;
  flex-direction: column;
  gap: 5px;
}

.detail-item label {
  font-weight: 600;
  min-width: 80px;
  color: #606266;
  flex-shrink: 0;
}

.detail-item.full-width label {
  min-width: auto;
}

.json-content {
  background-color: #f5f7fa;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  line-height: 1.4;
  max-height: 200px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .logs-container {
    padding: 10px;
  }
  
  .search-form {
    display: block;
  }
  
  .search-form .el-form-item {
    display: block;
    margin-bottom: 10px;
  }
  
  .detail-grid {
    grid-template-columns: 1fr;
  }
  
  .card-header {
    flex-direction: column;
    gap: 10px;
    align-items: stretch;
  }
}
</style>