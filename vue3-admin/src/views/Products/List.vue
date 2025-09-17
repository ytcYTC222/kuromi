<template>
  <div class="product-list">
    <!-- 搜索和筛选区域 -->
    <el-card class="search-card card" shadow="never">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="商品名称">
          <div class="search-box">
            <el-input
              v-model="searchForm.productName"
              placeholder="请输入商品名称"
              clearable
              style="width: 200px"
            />
          </div>
        </el-form-item>
        <el-form-item label="商品分类">
          <el-select
            v-model="searchForm.categoryId"
            placeholder="请选择分类"
            clearable
            style="width: 150px"
            :loading="categoriesLoading"
          >
            <el-option
              v-for="category in categoryList"
              :key="category.categoryId"
              :label="category.categoryName"
              :value="category.categoryId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="商品状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="上架" value="1" />
            <el-option label="下架" value="0" />
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

    <!-- 商品列表 -->
    <el-card class="table-card card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">商品列表</span>
          <div class="header-actions">
            <el-button type="primary" class="add-button" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              添加商品
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="productList"
        style="width: 100%; overflow: visible !important;"
        @selection-change="handleSelectionChange"
        class="custom-table"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="productId" label="ID" width="80" />
        <el-table-column label="商品图片" width="100">
          <template #default="{ row }">
            <el-image
              :src="getMainImage(row)"
              :preview-src-list="getPreviewImages(row)"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px"
              :preview-teleported="true"
            />
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="商品名称" min-width="200" />
        <el-table-column label="分类" width="120">
          <template #default="{ row }">
            {{ row.categoryName || '未分类' }}
          </template>
        </el-table-column>
        <el-table-column label="现价" width="100">
          <template #default="{ row }">
            <span class="price">¥{{ row.currentPrice?.toFixed(2) || '暂无' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="库存" width="80">
          <template #default="{ row }">
            <span :class="{ 'low-stock': (row.stockQuantity || 0) < 10 }">
              {{ row.stockQuantity || '暂无' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="销量" width="80">
          <template #default="{ row }">
            {{ row.salesCount || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <span :class="['status-tag', row.status === 1 ? 'status-active' : 'status-inactive']">
              {{ getStatusText(row.status) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button 
              :type="row.status === 1 ? 'warning' : 'success'" 
              size="small" 
              text 
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button type="danger" size="small" text @click="handleDelete(row)">
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

    <!-- 商品表单模态框 -->
    <ProductForm
      v-model="showProductForm"
      :is-edit="isEditMode"
      :product-data="currentProduct"
      @success="handleFormSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDate } from '@/utils'
import {
  Search,
  Refresh,
  Plus
} from '@element-plus/icons-vue'
import { productAPI, type ProductResponse, type ProductQueryParams } from '@/api/product'
import { categoryAPI, type CategoryResponse } from '@/api/category'
import ProductForm from '@/views/Products/ProductForm.vue'

// 搜索表单
const searchForm = reactive({
  productName: '',
  categoryId: '',
  status: ''
})

// 加载状态
const loading = ref(false)
const categoriesLoading = ref(false)

// 选中的商品
const selectedProducts = ref([])

// 分页信息
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 商品列表数据
const productList = ref<ProductResponse[]>([])

// 分类列表数据
const categoryList = ref<CategoryResponse[]>([])

// 表单相关
const showProductForm = ref(false)
const isEditMode = ref(false)
const currentProduct = ref<ProductResponse | null>(null)

// 获取状态类型
const getStatusType = (status: number) => {
  const statusMap: Record<number, string> = {
    1: 'success',  // 上架
    0: 'warning'   // 下架
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    1: '上架',
    0: '下架'
  }
  return statusMap[status] || '未知'
}

// 获取商品主图
const getMainImage = (product: ProductResponse) => {
  // 优先使用 mainImage 字段
  if (product.mainImage) {
    return product.mainImage
  }
  
  // 其次使用 imageUrl 字段
  if (product.imageUrl) {
    return product.imageUrl
  }
  
  // 从 images 数组中获取第一张图片（处理不同的数据类型）
  if (product.images && product.images.length > 0) {
    const firstImage = product.images[0]
    if (typeof firstImage === 'string') {
      return firstImage
    } else if (firstImage && typeof firstImage === 'object' && firstImage.imageUrl) {
      return firstImage.imageUrl
    }
  }
  
  // 默认占位图
  return 'https://via.placeholder.com/60x60?text=No+Image'
}

// 获取预览图片列表
const getPreviewImages = (product: ProductResponse) => {
  const images: string[] = []
  
  // 添加主图
  const mainImg = getMainImage(product)
  if (mainImg && !mainImg.includes('placeholder')) {
    images.push(mainImg)
  }
  
  // 添加其他图片（后端返回的是字符串数组）
  if (product.images && product.images.length > 0) {
    product.images.forEach(img => {
      const imageUrl = typeof img === 'string' ? img : img.imageUrl || ''
      if (imageUrl && !images.includes(imageUrl)) {
        images.push(imageUrl)
      }
    })
  }
  
  return images.length > 0 ? images : ['https://via.placeholder.com/60x60?text=No+Image']
}

// 处理搜索
const handleSearch = () => {
  pagination.page = 1
  loadProductList()
}

// 处理重置
const handleReset = () => {
  Object.assign(searchForm, {
    productName: '',
    categoryId: '',
    status: ''
  })
  pagination.page = 1
  loadProductList()
}

// 处理选择变化
const handleSelectionChange = (selection: any[]) => {
  selectedProducts.value = selection
}

// 处理分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.page = 1
  loadProductList()
}

// 处理页码变化
const handleCurrentChange = (page: number) => {
  pagination.page = page
  loadProductList()
}

// 添加商品
const handleAdd = () => {
  isEditMode.value = false
  currentProduct.value = null
  showProductForm.value = true
}

// 编辑商品
const handleEdit = async (product: ProductResponse) => {
  try {
    // 获取完整的商品详情
    const productDetail = await productAPI.getProductById(product.productId)
    isEditMode.value = true
    currentProduct.value = productDetail
    showProductForm.value = true
  } catch (error: any) {
    ElMessage.error(`获取商品详情失败：${error.message || '未知错误'}`)
  }
}

// 表单提交成功回调
const handleFormSuccess = () => {
  showProductForm.value = false
  loadProductList() // 刷新列表
}

// 切换状态
const toggleStatus = async (product: ProductResponse) => {
  const action = product.status === 1 ? '下架' : '上架'
  const newStatus = product.status === 1 ? 0 : 1
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}商品 "${product.productName}" 吗？`,
      `确认${action}`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用API更新状态
    await productAPI.updateProductStatus(product.productId, newStatus)
    product.status = newStatus
    
    ElMessage.success(`${action}成功`)
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败：${error.message || '未知错误'}`)
    }
  }
}

// 删除商品
const handleDelete = async (product: ProductResponse) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除商品 "${product.productName}" 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用API删除商品
    await productAPI.deleteProduct(product.productId)
    
    // 重新加载列表
    await loadProductList()
    
    ElMessage.success('删除成功')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`删除失败：${error.message || '未知错误'}`)
    }
  }
}

// 加载商品列表
const loadProductList = async () => {
  loading.value = true
  
  try {
    const params: ProductQueryParams = {
      page: pagination.page,
      size: pagination.size,
      productName: searchForm.productName || undefined,
      categoryId: searchForm.categoryId ? Number(searchForm.categoryId) : undefined,
      status: searchForm.status ? Number(searchForm.status) : undefined
    }
    
    console.log('🚀 正在加载商品列表...', params)
    const response = await productAPI.getProductList(params)
    console.log('✅ 商品列表加载成功:', response)
    
    productList.value = response.records || []
    pagination.total = response.total || 0
    
    if (productList.value.length === 0) {
      console.log('⚠️ 没有找到商品数据')
    }
  } catch (error: any) {
    console.error('❌ 加载商品列表失败:', error)
    ElMessage.error(`加载商品列表失败：${error.message || '未知错误'}`)
    productList.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

// 加载分类列表
const loadCategoryList = async () => {
  categoriesLoading.value = true
  
  try {
    console.log('🚀 正在加载分类列表...')
    // 获取所有启用的分类，用于筛选下拉框
    const response = await categoryAPI.getCategoryList({
      page: 1,
      size: 1000, // 获取所有分类
      status: 1 // 只获取启用的分类（isActive=1）
    })
    console.log('✅ 分类列表加载成功:', response)
    
    categoryList.value = response.records || []
    
    if (categoryList.value.length === 0) {
      console.log('⚠️ 没有找到分类数据')
    }
  } catch (error: any) {
    console.error('❌ 加载分类列表失败:', error)
    ElMessage.error(`加载分类列表失败：${error.message || '未知错误'}`)
    categoryList.value = []
  } finally {
    categoriesLoading.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadProductList()
  loadCategoryList()
})
</script>

<style lang="scss" scoped>
@use 'sass:color';
@import '@/assets/styles/variables.scss';

.product-list {
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
      box-shadow: 0 4px 15px rgba(255, 154, 158, 0.4);
      
      &:hover {
        background: linear-gradient(135deg, #ff6b6b 0%, #ff8a95 50%, #ffa8cc 100%) !important;
        transform: translateY(-3px);
        box-shadow: 0 8px 25px rgba(255, 107, 107, 0.5);
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
      box-shadow: 0 4px 15px rgba(168, 237, 234, 0.4);
      
      &:hover {
        background: linear-gradient(135deg, #79f1a4 0%, #0e4b99 100%) !important;
        transform: translateY(-3px);
        box-shadow: 0 8px 25px rgba(121, 241, 164, 0.5);
        color: #fff !important;
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
    
    // 添加商品按钮样式 - 少女心渐变色彩
    .add-button {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
      border: 1px solid #667eea !important;
      color: #fff !important;
      font-weight: 600;
      border-radius: 25px;
      padding: 10px 20px;
      transition: all 0.3s ease;
      box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
      
      &:hover {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%) !important;
        transform: translateY(-3px);
        box-shadow: 0 8px 25px rgba(240, 147, 251, 0.5);
        color: #fff !important;
      }
    }
    
    .price {
      font-weight: 600;
      color: #E6A23C;
    }
    
    .low-stock {
      color: #F56C6C;
      font-weight: 600;
    }
    
    .pagination-wrapper {
      margin-top: 20px;
      display: flex;
      justify-content: center;
    }
    
    // 表格容器样式强化
    :deep(.custom-table) {
      overflow: visible !important;
      
      .el-table__body-wrapper {
        overflow: visible !important;
      }
      
      .el-table__fixed-right-patch {
        overflow: visible !important;
      }
      
      .el-table__row {
        overflow: visible !important;
        
        .el-table__cell {
          overflow: visible !important;
        }
      }
    }
    
    // 确保操作列装饰元素显示
    :deep(.el-table__fixed-right) {
      overflow: visible !important;
      
      .el-table__cell {
        overflow: visible !important;
        position: relative;
      }
      
      .el-button {
        position: relative !important;
        overflow: visible !important;
        
        &.sparkle::before,
        &.heart-button::after {
          z-index: 99999 !important;
          position: absolute !important;
        }
      }
    }
    
    // 操作列按钮样式 - 少女心渐变色彩
    :deep(.el-table__body) {
      .el-button {
        &.el-button--primary {
          background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%) !important;
          border: 1px solid #a8edea !important;
          color: #333 !important;
          border-radius: 20px !important;
          padding: 6px 12px !important;
          margin: 0 2px !important;
          transition: all 0.3s ease !important;
          box-shadow: 0 3px 10px rgba(168, 237, 234, 0.4) !important;
          
          &:hover {
            background: linear-gradient(135deg, #79f1a4 0%, #0e4b99 100%) !important;
            transform: translateY(-3px) !important;
            box-shadow: 0 6px 20px rgba(121, 241, 164, 0.5) !important;
            color: #fff !important;
          }
        }
        
        &.el-button--success {
          background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%) !important;
          border: 1px solid #84fab0 !important;
          color: #fff !important;
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
        
        &.el-button--warning {
          background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%) !important;
          border: 1px solid #ffecd2 !important;
          color: #333 !important;
          border-radius: 20px !important;
          padding: 6px 12px !important;
          margin: 0 2px !important;
          transition: all 0.3s ease !important;
          box-shadow: 0 3px 10px rgba(255, 236, 210, 0.4) !important;
          
          &:hover {
            background: linear-gradient(135deg, #ff9a56 0%, #ff6a88 100%) !important;
            transform: translateY(-3px) !important;
            box-shadow: 0 6px 20px rgba(255, 154, 86, 0.5) !important;
            color: #fff !important;
          }
        }
        
        &.el-button--danger {
          background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%) !important;
          border: 1px solid #ff9a9e !important;
          color: #fff !important;
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
  }
}

// 响应式设计
@media (max-width: 768px) {
  .product-list {
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