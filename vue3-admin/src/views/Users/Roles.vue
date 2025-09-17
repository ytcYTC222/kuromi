<template>
  <div class="roles-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="角色名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入角色名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="角色状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="启用" value="active" />
            <el-option label="禁用" value="inactive" />
          </el-select>
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
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 角色列表 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>角色列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增角色
          </el-button>
        </div>
      </template>

      <el-table
        :data="roleList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="角色名称" min-width="150" />
        <el-table-column prop="code" label="角色编码" width="150" />
        <el-table-column prop="description" label="角色描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="userCount" label="用户数量" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
              {{ row.status === 'active' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="info" size="small" @click="handlePermission(row)">
              权限
            </el-button>
            <el-button 
              :type="row.status === 'active' ? 'warning' : 'success'" 
              size="small" 
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 'active' ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
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

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :before-close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="formData.code" placeholder="请输入角色编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
        </el-form-item>
        <el-form-item label="角色状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio value="active">启用</el-radio>
            <el-radio value="inactive">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 权限配置对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="权限配置"
      width="800px"
      :before-close="handlePermissionDialogClose"
    >
      <div class="permission-container">
        <div class="permission-header">
          <span>为角色 "{{ currentRole.name }}" 配置权限</span>
        </div>
        <el-tree
          ref="permissionTreeRef"
          :data="permissionTree"
          :props="treeProps"
          show-checkbox
          node-key="id"
          :default-checked-keys="checkedPermissions"
          class="permission-tree"
        />
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handlePermissionDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSavePermissions">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const dialogVisible = ref(false)
const permissionDialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()
const permissionTreeRef = ref()
const currentRole = ref({})
const checkedPermissions = ref([])

// 搜索表单
const searchForm = reactive({
  name: '',
  status: ''
})

// 分页数据
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 角色列表
const roleList = ref([
  {
    id: 1,
    name: '超级管理员',
    code: 'super_admin',
    description: '拥有系统所有权限',
    userCount: 1,
    status: 'active',
    createdAt: '2024-01-01 00:00:00'
  },
  {
    id: 2,
    name: '管理员',
    code: 'admin',
    description: '拥有大部分管理权限',
    userCount: 3,
    status: 'active',
    createdAt: '2024-01-02 10:30:00'
  },
  {
    id: 3,
    name: '运营人员',
    code: 'operator',
    description: '负责日常运营管理',
    userCount: 5,
    status: 'active',
    createdAt: '2024-01-03 14:20:00'
  },
  {
    id: 4,
    name: '客服人员',
    code: 'service',
    description: '负责客户服务',
    userCount: 8,
    status: 'inactive',
    createdAt: '2024-01-04 09:15:00'
  }
])

// 表单数据
const formData = reactive({
  id: null,
  name: '',
  code: '',
  description: '',
  status: 'active'
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { pattern: /^[a-zA-Z_][a-zA-Z0-9_]*$/, message: '角色编码只能包含字母、数字和下划线，且不能以数字开头', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择角色状态', trigger: 'change' }
  ]
}

// 权限树配置
const treeProps = {
  children: 'children',
  label: 'name'
}

// 权限树数据
const permissionTree = ref([
  {
    id: 1,
    name: '系统管理',
    children: [
      { id: 11, name: '用户管理' },
      { id: 12, name: '角色管理' },
      { id: 13, name: '权限管理' },
      { id: 14, name: '系统设置' }
    ]
  },
  {
    id: 2,
    name: '商品管理',
    children: [
      { id: 21, name: '商品列表' },
      { id: 22, name: '商品分类' },
      { id: 23, name: '品牌管理' },
      { id: 24, name: '库存管理' }
    ]
  },
  {
    id: 3,
    name: '订单管理',
    children: [
      { id: 31, name: '订单列表' },
      { id: 32, name: '退款管理' },
      { id: 33, name: '物流管理' }
    ]
  },
  {
    id: 4,
    name: '营销管理',
    children: [
      { id: 41, name: '优惠券' },
      { id: 42, name: '促销活动' },
      { id: 43, name: '会员管理' }
    ]
  },
  {
    id: 5,
    name: '内容管理',
    children: [
      { id: 51, name: '文章管理' },
      { id: 52, name: '广告管理' },
      { id: 53, name: '帮助中心' }
    ]
  },
  {
    id: 6,
    name: '财务管理',
    children: [
      { id: 61, name: '收支明细' },
      { id: 62, name: '提现管理' },
      { id: 63, name: '财务报表' }
    ]
  }
])

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchRoleList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    status: ''
  })
  pagination.page = 1
  fetchRoleList()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增角色'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑角色'
  Object.assign(formData, { ...row })
  dialogVisible.value = true
}

// 权限配置
const handlePermission = (row) => {
  currentRole.value = { ...row }
  // 模拟获取角色已有权限
  checkedPermissions.value = [11, 12, 21, 22, 31]
  permissionDialogVisible.value = true
}

// 切换状态
const handleToggleStatus = async (row) => {
  const action = row.status === 'active' ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(
      `确定要${action}角色"${row.name}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 这里应该调用切换状态API
    row.status = row.status === 'active' ? 'inactive' : 'active'
    ElMessage.success(`${action}成功`)
  } catch {
    ElMessage.info(`已取消${action}`)
  }
}

// 删除
const handleDelete = async (row) => {
  if (row.userCount > 0) {
    ElMessage.warning('该角色下还有用户，无法删除')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除角色"${row.name}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 这里应该调用删除API
    ElMessage.success('删除成功')
    fetchRoleList()
  } catch {
    ElMessage.info('已取消删除')
  }
}

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  fetchRoleList()
}

// 当前页改变
const handleCurrentChange = (page) => {
  pagination.page = page
  fetchRoleList()
}

// 关闭对话框
const handleDialogClose = () => {
  dialogVisible.value = false
  resetForm()
}

// 关闭权限对话框
const handlePermissionDialogClose = () => {
  permissionDialogVisible.value = false
  currentRole.value = {}
  checkedPermissions.value = []
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    // 这里应该调用保存API
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    fetchRoleList()
  } catch {
    ElMessage.error('请检查表单数据')
  }
}

// 保存权限
const handleSavePermissions = () => {
  const checkedKeys = permissionTreeRef.value.getCheckedKeys()
  const halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys()
  const allCheckedKeys = [...checkedKeys, ...halfCheckedKeys]
  
  // 这里应该调用保存权限API
  console.log('保存权限:', allCheckedKeys)
  ElMessage.success('权限配置保存成功')
  permissionDialogVisible.value = false
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    id: null,
    name: '',
    code: '',
    description: '',
    status: 'active'
  })
  formRef.value?.clearValidate()
}

// 获取角色列表
const fetchRoleList = async () => {
  loading.value = true
  try {
    // 这里应该调用API获取数据
    // const response = await roleApi.getList({
    //   ...searchForm,
    //   page: pagination.page,
    //   size: pagination.size
    // })
    // roleList.value = response.data.list
    // pagination.total = response.data.total
    
    // 模拟API延迟
    await new Promise(resolve => setTimeout(resolve, 500))
    pagination.total = 20
  } catch (error) {
    ElMessage.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchRoleList()
})
</script>

<style scoped>
.roles-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.permission-container {
  max-height: 400px;
  overflow-y: auto;
}

.permission-header {
  margin-bottom: 15px;
  font-weight: bold;
  color: #303133;
}

.permission-tree {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
}

@media (max-width: 768px) {
  .roles-container {
    padding: 10px;
  }
  
  .el-table {
    font-size: 12px;
  }
  
  .el-button {
    padding: 5px 10px;
    font-size: 12px;
  }
}
</style>