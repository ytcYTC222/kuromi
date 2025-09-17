<template>
  <div class="product-categories kuromi-theme">
    <el-card class="card kuromi-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title kuromi-title">商品分类管理</span>
          <el-button type="primary" class="cute-button kuromi-btn" @click="handleAdd" v-kuromi-click>
            <el-icon><Plus /></el-icon>
            添加分类
          </el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" inline>
          <el-form-item label="分类名称">
            <el-input 
              v-model="searchForm.name" 
              placeholder="请输入分类名称"
              clearable
              class="kuromi-input"
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select 
              v-model="searchForm.status" 
              placeholder="请选择状态"
              clearable
              style="width: 120px"
            >
              <el-option label="启用" value="1" />
              <el-option label="禁用" value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="search-button" @click="handleSearch" v-kuromi-click>
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button class="reset-button" @click="handleReset" v-kuromi-click>
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 分类树 -->
      <div class="category-tree">
        <el-tree
          ref="treeRef"
          :data="categoryTree"
          :props="treeProps"
          node-key="categoryId"
          default-expand-all
          :expand-on-click-node="false"
          class="category-tree-component"
          v-loading="categoriesLoading"
        >
          <template #default="{ node, data }">
            <div class="tree-node">
              <div class="node-content">
                <el-icon class="node-icon">
                  <Folder v-if="data.children && data.children.length > 0" />
                  <Document v-else />
                </el-icon>
                <span class="node-label">{{ data.categoryName }}</span>
                <el-tag 
                  :type="data.status === 1 ? 'success' : 'danger'"
                  size="small"
                  class="node-status"
                >
                  {{ data.status === 1 ? '启用' : '禁用' }}
                </el-tag>
                <span class="node-sort">排序: {{ data.sortOrder }}</span>
              </div>
              <div class="node-actions">
                <el-button 
                  type="primary" 
                  size="small" 
                  text
                  @click="handleAddChild(data)"
                  v-kuromi-click
                >
                  添加子分类
                </el-button>
                <el-button 
                  type="primary" 
                  size="small" 
                  text
                  @click="handleEdit(data)"
                  v-kuromi-click
                >
                  编辑
                </el-button>
                <el-button 
                  type="danger" 
                  size="small" 
                  text
                  @click="handleDelete(data)"
                  v-kuromi-click
                >
                  删除
                </el-button>
              </div>
            </div>
          </template>
        </el-tree>
      </div>
    </el-card>
    
    <!-- 添加/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="categoryName">
          <el-input 
            v-model="form.categoryName" 
            placeholder="请输入分类名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="父级分类" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="categoryOptions"
            :props="{ label: 'categoryName', value: 'categoryId' }"
            placeholder="请选择父级分类（不选则为顶级分类）"
            clearable
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="分类图标" prop="icon">
          <el-input 
            v-model="form.icon" 
            placeholder="请输入图标类名"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number 
            v-model="form.sortOrder" 
            :min="0" 
            :max="9999"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="分类描述" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入分类描述"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" v-kuromi-click>取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="loading" v-kuromi-click>
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Search,
  Refresh,
  Folder,
  Document
} from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { categoryAPI, type CategoryResponse, type CategoryRequest } from '@/api/category'

interface Category {
  categoryId: number
  categoryName: string
  parentId: number | null
  icon?: string
  sortOrder: number
  status: number // 0-禁用, 1-启用
  description?: string
  children?: Category[]
}

const formRef = ref<FormInstance>()
const treeRef = ref()
const loading = ref(false)
const categoriesLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentCategory = ref<Category | null>(null)

// 搜索表单
const searchForm = reactive({
  name: '',
  status: ''
})

// 分类表单
const form = reactive({
  categoryId: 0,
  categoryName: '',
  parentId: null as number | null,
  icon: '',
  sortOrder: 0,
  status: 1 as number, // 0-禁用, 1-启用
  description: ''
})

// 分类数据
const categories = ref<Category[]>([])

// 树形组件配置
const treeProps = {
  children: 'children',
  label: 'categoryName'
}

// 对话框标题
const dialogTitle = computed(() => {
  return isEdit.value ? '编辑分类' : '添加分类'
})

// 分类树数据
const categoryTree = computed(() => {
  return filterCategories(categories.value)
})

// 分类选项（用于父级分类选择）
const categoryOptions = computed(() => {
  const options = JSON.parse(JSON.stringify(categories.value))
  // 如果是编辑模式，需要排除当前分类及其子分类
  if (isEdit.value && currentCategory.value) {
    return filterCurrentCategory(options, currentCategory.value.categoryId)
  }
  return options
})

// 表单验证规则
const rules: FormRules = {
  categoryName: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 50, message: '分类名称长度在2到50个字符', trigger: 'blur' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序', trigger: 'blur' },
    { type: 'number', min: 0, max: 9999, message: '排序范围在0到9999', trigger: 'blur' }
  ]
}

// 加载分类数据
const loadCategoryTree = async () => {
  categoriesLoading.value = true
  try {
    console.log('🚀 正在加载分类树结构...')
    const response = await categoryAPI.getCategoryTree()
    console.log('✅ 分类树加载成功:', response)
    
    categories.value = response.map(convertResponseToCategory)
    
    if (categories.value.length === 0) {
      console.log('⚠️ 没有找到分类数据')
    }
  } catch (error: any) {
    console.error('❌ 加载分类树失败:', error)
    ElMessage.error(`加载分类数据失败：${error.message || '未知错误'}`)
    categories.value = []
  } finally {
    categoriesLoading.value = false
  }
}

// 转换后端Response为前端Category格式
const convertResponseToCategory = (response: CategoryResponse): Category => {
  return {
    categoryId: response.categoryId,
    categoryName: response.categoryName,
    parentId: response.parentId || null,
    icon: response.icon || '',
    sortOrder: response.sortOrder,
    status: response.status,
    description: response.description || '',
    children: response.children ? response.children.map(convertResponseToCategory) : undefined
  }
}

// 转换前端Category为后端Request格式
const convertCategoryToRequest = (category: Partial<Category>): CategoryRequest => {
  return {
    categoryName: category.categoryName || '',
    parentId: category.parentId || undefined,
    icon: category.icon,
    sortOrder: category.sortOrder || 0,
    isActive: category.status || 1, // 前端status字段对应后端isActive字段
    description: category.description
  }
}

// 过滤分类（根据搜索条件）
const filterCategories = (categories: Category[]): Category[] => {
  if (!searchForm.name && !searchForm.status) {
    return categories
  }
  
  return categories.filter(category => {
    const nameMatch = !searchForm.name || category.categoryName.includes(searchForm.name)
    const statusMatch = !searchForm.status || 
      (searchForm.status === '1' && category.status === 1) ||
      (searchForm.status === '0' && category.status === 0)
    return nameMatch && statusMatch
  }).map(category => ({
    ...category,
    children: category.children ? filterCategories(category.children) : undefined
  }))
}

// 排除当前分类及其子分类
const filterCurrentCategory = (categories: Category[], excludeId: number): Category[] => {
  return categories.filter(category => category.categoryId !== excludeId).map(category => ({
    ...category,
    children: category.children ? filterCurrentCategory(category.children, excludeId) : undefined
  }))
}

// 搜索
const handleSearch = () => {
  console.log('搜索分类:', searchForm)
}

// 重置搜索
const handleReset = () => {
  searchForm.name = ''
  searchForm.status = ''
}

// 添加分类
const handleAdd = () => {
  isEdit.value = false
  currentCategory.value = null
  resetForm()
  dialogVisible.value = true
}

// 添加子分类
const handleAddChild = (category: Category) => {
  isEdit.value = false
  currentCategory.value = null
  resetForm()
  form.parentId = category.categoryId
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = async (category: Category) => {
  try {
    const categoryDetail = await categoryAPI.getCategoryById(category.categoryId)
    
    isEdit.value = true
    currentCategory.value = category
    Object.assign(form, {
      categoryId: categoryDetail.categoryId,
      categoryName: categoryDetail.categoryName,
      parentId: categoryDetail.parentId,
      icon: categoryDetail.icon || '',
      sortOrder: categoryDetail.sortOrder,
      status: categoryDetail.status,
      description: categoryDetail.description || ''
    })
    dialogVisible.value = true
  } catch (error: any) {
    ElMessage.error(`获取分类详情失败：${error.message || '未知错误'}`)
  }
}

// 删除分类
const handleDelete = async (category: Category) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除分类"${category.categoryName}"吗？删除后不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await categoryAPI.deleteCategory(category.categoryId)
    await loadCategoryTree()
    
    ElMessage.success('删除成功')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`删除失败：${error.message || '未知错误'}`)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    const requestData = convertCategoryToRequest(form)
    
    if (isEdit.value) {
      await categoryAPI.updateCategory(form.categoryId, requestData)
      ElMessage.success('分类更新成功')
    } else {
      await categoryAPI.createCategory(requestData)
      ElMessage.success('分类添加成功')
    }
    
    await loadCategoryTree()
    dialogVisible.value = false
  } catch (error: any) {
    if (error.errorFields) {
      console.error('表单验证失败:', error)
    } else {
      ElMessage.error(`操作失败：${error.message || '未知错误'}`)
    }
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    categoryId: 0,
    categoryName: '',
    parentId: null,
    icon: '',
    sortOrder: 0,
    status: 1,
    description: ''
  })
  formRef.value?.clearValidate()
}

// 对话框关闭
const handleDialogClose = () => {
  resetForm()
}

// 组件挂载时加载数据
onMounted(() => {
  loadCategoryTree()
})
</script>

<style lang="scss" scoped>
.product-categories {
  padding: 20px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;
  }
  
  .search-area {
    margin-bottom: 20px;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 15px;
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


  .category-tree {
    .category-tree-component {
      border: 1px solid #ebeef5;
      border-radius: 15px;
      
      .tree-node {
        display: flex;
        justify-content: space-between;
        align-items: center;
        width: 100%;
        padding: 8px 0;
        
        .node-content {
          display: flex;
          align-items: center;
          flex: 1;
          
          .node-icon {
            margin-right: 8px;
            color: #409eff;
          }
          
          .node-label {
            margin-right: 12px;
            font-weight: 500;
          }
          
          .node-status {
            margin-right: 12px;
          }
          
          .node-sort {
            font-size: 12px;
            color: #909399;
          }
        }
        
        .node-actions {
          display: flex;
          gap: 8px;
          opacity: 0;
          transition: opacity 0.3s;
        }
        
        &:hover {
          .node-actions {
            opacity: 1;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .product-categories {
    padding: 10px;
    
    .search-area {
      .el-form {
        .el-form-item {
          width: 100%;
          margin-bottom: 10px;
          
          .el-input,
          .el-select {
            width: 100% !important;
          }
        }
      }
    }
    
    .tree-node {
      flex-direction: column;
      align-items: flex-start !important;
      
      .node-actions {
        margin-top: 8px;
        opacity: 1 !important;
      }
    }
  }
}
</style>