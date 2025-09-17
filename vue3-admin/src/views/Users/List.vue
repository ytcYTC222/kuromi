<template>
  <div class="user-list">
    <!-- 搜索和筛选区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入用户昵称、手机号或邮箱"
            clearable
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item label="用户状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="正常" :value="UserStatus.ACTIVE" />
            <el-option label="禁用" :value="UserStatus.DISABLED" />
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

    <!-- 用户列表 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              添加用户
            </el-button>
            <el-button type="success" @click="handleExport">
              <el-icon><Download /></el-icon>
              导出
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="userList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :size="40" :src="row.avatarUrl || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" />
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="用户昵称" width="120" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            <el-tag :type="getGenderType(row.gender)" size="small">
              {{ getGenderText(row.gender) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalOrders" label="订单数" width="100" />
        <el-table-column prop="lastLoginTime" label="最后登录" width="180">
          <template #default="{ row }">
            {{ row.lastLoginTime ? formatDate(row.lastLoginTime) : '从未登录' }}
          </template>
        </el-table-column>
        <el-table-column prop="registerTime" label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.registerTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text class="action-edit-btn" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button 
              :type="row.status === UserStatus.ACTIVE ? 'warning' : 'success'" 
              size="small" 
              text 
              :class="row.status === UserStatus.ACTIVE ? 'action-disable-btn' : 'action-enable-btn'"
              @click="toggleStatus(row)"
            >
              {{ row.status === UserStatus.ACTIVE ? '禁用' : '启用' }}
            </el-button>
            <el-button type="info" size="small" text class="action-reset-btn" @click="resetPassword(row)">
              重置密码
            </el-button>
            <el-button type="danger" size="small" text class="action-delete-btn" @click="handleDelete(row)">
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
import { userAPI, type UserResponse, type UserQueryParams, UserStatus, UserGender, userStatusMap, userGenderMap, userStatusColorMap, userGenderColorMap } from '@/api/user'
import {
  Search,
  Refresh,
  Plus,
  Download
} from '@element-plus/icons-vue'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: undefined as number | undefined
})

// 加载状态
const loading = ref(false)

// 选中的用户
const selectedUsers = ref<UserResponse[]>([])

// 分页信息
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 用户列表数据
const userList = ref<UserResponse[]>([])

// 获取性别类型
const getGenderType = (gender?: number) => {
  return userGenderColorMap[gender as keyof typeof userGenderColorMap] || 'info'
}

// 获取性别文本
const getGenderText = (gender?: number) => {
  return userGenderMap[gender as keyof typeof userGenderMap] || '未知'
}

// 获取状态类型
const getStatusType = (status: number) => {
  return userStatusColorMap[status as keyof typeof userStatusColorMap] || 'info'
}

// 获取状态文本
const getStatusText = (status: number) => {
  return userStatusMap[status as keyof typeof userStatusMap] || '未知'
}

// 处理搜索
const handleSearch = () => {
  pagination.page = 1
  loadUserList()
}

// 处理重置
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    status: undefined
  })
  pagination.page = 1
  loadUserList()
}

// 处理选择变化
const handleSelectionChange = (selection: UserResponse[]) => {
  selectedUsers.value = selection
}

// 处理分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.page = 1
  loadUserList()
}

// 处理页码变化
const handleCurrentChange = (page: number) => {
  pagination.page = page
  loadUserList()
}

// 添加用户
const handleAdd = () => {
  router.push('/users/add')
}

// 编辑用户
const handleEdit = (user: UserResponse) => {
  router.push(`/users/edit/${user.userId}`)
}

// 切换状态
const toggleStatus = async (user: UserResponse) => {
  const action = user.status === UserStatus.ACTIVE ? '禁用' : '启用'
  const newStatus = user.status === UserStatus.ACTIVE ? UserStatus.DISABLED : UserStatus.ACTIVE
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}用户 "${user.nickname}" 吗？`,
      `确认${action}`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用API更新状态
    await userAPI.updateUserStatus(user.userId, { status: newStatus })
    
    // 更新本地数据
    user.status = newStatus
    user.statusName = getStatusText(newStatus)
    
    ElMessage.success(`${action}成功`)
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败：${error.message || '系统错误'}`)
    }
  }
}

// 重置密码
const resetPassword = async (user: UserResponse) => {
  try {
    const { value: newPassword } = await ElMessageBox.prompt(
      `请输入用户 "${user.nickname}" 的新密码`,
      '重置密码',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'password',
        inputPlaceholder: '请输入新密码（6-20位）',
        inputValidator: (value: string) => {
          if (!value || value.length < 6 || value.length > 20) {
            return '密码长度必须在6-20位之间'
          }
          return true
        }
      }
    )
    
    // 调用API重置密码
    await userAPI.resetPassword(user.userId, { newPassword })
    
    ElMessage.success('密码重置成功')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`重置密码失败：${error.message || '系统错误'}`)
    }
  }
}

// 删除用户
const handleDelete = async (user: UserResponse) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${user.nickname}" 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用API删除用户
    await userAPI.deleteUser(user.userId)
    
    // 重新加载数据
    await loadUserList()
    
    ElMessage.success('删除成功')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`删除失败：${error.message || '系统错误'}`)
    }
  }
}

// 导出用户
const handleExport = () => {
  ElMessage.info('导出功能开发中...')
}

// 加载用户列表
const loadUserList = async () => {
  loading.value = true
  
  try {
    const params: UserQueryParams = {
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
      status: searchForm.status
    }
    
    const result = await userAPI.getUserList(params)
    
    userList.value = result.records
    pagination.total = result.total
    pagination.page = result.current
    pagination.size = result.size
    
  } catch (error: any) {
    ElMessage.error(`加载用户列表失败：${error.message || '系统错误'}`)
    userList.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadUserList()
})
</script>

<style lang="scss" scoped>
.user-list {
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
    
    .pagination-wrapper {
      margin-top: 20px;
      display: flex;
      justify-content: center;
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
      background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%) !important;
      color: #333 !important;
      border: 1px solid #84fab0 !important;
      border-radius: 20px !important;
      padding: 6px 12px !important;
      margin: 0 2px !important;
      transition: all 0.3s ease !important;
      box-shadow: 0 3px 10px rgba(132, 250, 176, 0.4) !important;
      
      &:hover {
        background: linear-gradient(135deg, #56ab2f 0%, #a8e6cf 100%) !important;
        color: #fff !important;
        transform: translateY(-3px) !important;
        box-shadow: 0 6px 20px rgba(86, 171, 47, 0.5) !important;
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
  }
}

// 响应式设计
@media (max-width: 768px) {
  .user-list {
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