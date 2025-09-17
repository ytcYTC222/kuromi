<template>
  <div class="banners-container">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="轮播图标题">
          <el-input
            v-model="searchForm.title"
            placeholder="请输入轮播图标题"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="链接类型">
          <el-select
            v-model="searchForm.linkType"
            placeholder="请选择链接类型"
            clearable
            style="width: 150px"
          >
            <el-option label="商品" :value="1" />
            <el-option label="分类" :value="2" />
            <el-option label="文章" :value="3" />
            <el-option label="外链" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.isActive"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
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

    <!-- 轮播图列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>轮播图管理</span>
          <el-button type="primary" class="add-button" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增轮播图
          </el-button>
        </div>
      </template>

      <el-table
        :data="tableData"
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="bannerId" label="ID" width="80" />
        <el-table-column label="轮播图片" width="120">
          <template #default="{ row }">
            <el-image
              :src="row.imageUrl"
              :preview-src-list="[row.imageUrl]"
              fit="cover"
              style="width: 80px; height: 45px; border-radius: 4px;"
            />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="150" />
        <el-table-column prop="linkTypeName" label="链接类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getLinkTypeTagType(row.linkType)">{{ row.linkTypeName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="linkValue" label="链接值" min-width="200" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="statusName" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              :model-value="row.isActive === 1"
              @change="(value) => handleStatusChange(row, value ? 1 : 0)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" class="action-view-btn" @click="handleView(row)">
              查看
            </el-button>
            <el-button type="warning" size="small" class="action-edit-btn" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" class="action-delete-btn" @click="handleDelete(row)">
              删除
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

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="轮播图标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入轮播图标题" />
        </el-form-item>
        <el-form-item label="轮播图片" prop="imageUrl">
          <div class="upload-container">
            <el-input v-model="formData.imageUrl" placeholder="请输入图片URL" />
            <div class="upload-tip">建议尺寸：1920x600px，支持jpg、png格式</div>
            <div v-if="formData.imageUrl" class="image-preview">
              <el-image
                :src="formData.imageUrl"
                :preview-src-list="[formData.imageUrl]"
                fit="cover"
                style="width: 300px; height: 150px; border-radius: 4px;"
              />
            </div>
          </div>
        </el-form-item>
        <el-form-item label="链接类型" prop="linkType">
          <el-select v-model="formData.linkType" placeholder="请选择链接类型" style="width: 100%">
            <el-option label="商品" :value="1" />
            <el-option label="分类" :value="2" />
            <el-option label="文章" :value="3" />
            <el-option label="外链" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="链接值" prop="linkValue">
          <el-input v-model="formData.linkValue" placeholder="请输入链接值（如商品ID、分类ID、文章ID或外链URL）" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="formData.startTime"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="formData.endTime"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="isActive">
          <el-radio-group v-model="formData.isActive">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button class="cancel-button" @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" class="confirm-button" @click="handleSubmit" :loading="submitLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="横幅详情"
      width="600px"
    >
      <div class="banner-detail" v-if="currentBanner">
        <div class="detail-item">
          <label>轮播图片：</label>
          <el-image
            :src="currentBanner.imageUrl"
            :preview-src-list="[currentBanner.imageUrl]"
            fit="cover"
            style="width: 300px; height: 150px; border-radius: 4px;"
          />
        </div>
        <div class="detail-item">
          <label>标题：</label>
          <span>{{ currentBanner.title }}</span>
        </div>
        <div class="detail-item">
          <label>链接类型：</label>
          <el-tag :type="getLinkTypeTagType(currentBanner.linkType)">{{ currentBanner.linkTypeName }}</el-tag>
        </div>
        <div class="detail-item">
          <label>链接值：</label>
          <span>{{ currentBanner.linkValue || '无' }}</span>
        </div>
        <div class="detail-item">
          <label>排序：</label>
          <span>{{ currentBanner.sortOrder }}</span>
        </div>
        <div class="detail-item">
          <label>状态：</label>
          <el-tag :type="currentBanner.isActive === 1 ? 'success' : 'danger'">
            {{ currentBanner.statusName }}
          </el-tag>
        </div>
        <div class="detail-item">
          <label>开始时间：</label>
          <span>{{ currentBanner.startTime || '无限制' }}</span>
        </div>
        <div class="detail-item">
          <label>结束时间：</label>
          <span>{{ currentBanner.endTime || '无限制' }}</span>
        </div>
        <div class="detail-item">
          <label>创建时间：</label>
          <span>{{ currentBanner.createTime }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { bannerAPI, type BannerResponse, type BannerRequest, BannerStatus, LinkType } from '@/api/banner'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const currentBanner = ref<BannerResponse | null>(null)

// 搜索表单
const searchForm = reactive({
  title: '',
  linkType: undefined as number | undefined,
  isActive: undefined as number | undefined
})

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 表格数据
const tableData = ref<BannerResponse[]>([])

// 表单数据
const formData = reactive({
  bannerId: undefined as number | undefined,
  title: '',
  imageUrl: '',
  linkType: LinkType.PRODUCT,
  linkValue: '',
  sortOrder: 0,
  isActive: BannerStatus.ENABLED,
  startTime: '',
  endTime: ''
})

// 表单验证规则
const rules: FormRules = {
  title: [
    { required: true, message: '请输入轮播图标题', trigger: 'blur' }
  ],
  imageUrl: [
    { required: true, message: '请输入图片URL', trigger: 'blur' }
  ],
  linkType: [
    { required: true, message: '请选择链接类型', trigger: 'change' }
  ]
}

// 获取链接类型标签类型
const getLinkTypeTagType = (linkType: number) => {
  const typeMap: Record<number, string> = {
    [LinkType.PRODUCT]: 'success',
    [LinkType.CATEGORY]: 'warning', 
    [LinkType.ARTICLE]: 'info',
    [LinkType.EXTERNAL]: 'danger'
  }
  return typeMap[linkType] || ''
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  fetchData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    title: '',
    linkType: undefined,
    isActive: undefined
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增轮播图'
  resetForm()
  dialogVisible.value = true
}

// 查看
const handleView = (row: BannerResponse) => {
  currentBanner.value = row
  viewDialogVisible.value = true
}

// 编辑
const handleEdit = async (row: BannerResponse) => {
  try {
    dialogTitle.value = '编辑轮播图'
    const response = await bannerAPI.getBannerById(row.bannerId)
    const banner = response.data
    Object.assign(formData, {
      bannerId: banner.bannerId,
      title: banner.title,
      imageUrl: banner.imageUrl,
      linkType: banner.linkType,
      linkValue: banner.linkValue || '',
      sortOrder: banner.sortOrder,
      isActive: banner.isActive,
      startTime: banner.startTime || '',
      endTime: banner.endTime || ''
    })
    dialogVisible.value = true
  } catch (error: any) {
    ElMessage.error(`获取轮播图详情失败：${error.message || '未知错误'}`)
  }
}

// 删除
const handleDelete = async (row: BannerResponse) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除轮播图 "${row.title}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await bannerAPI.deleteBanner(row.bannerId)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`删除失败：${error.message || '未知错误'}`)
    }
  }
}

// 状态切换
const handleStatusChange = async (row: BannerResponse, isActive: number) => {
  try {
    await bannerAPI.updateBannerStatus(row.bannerId, isActive)
    ElMessage.success(`${isActive === 1 ? '启用' : '禁用'}成功`)
    row.isActive = isActive
    row.statusName = isActive === 1 ? '启用' : '禁用'
  } catch (error: any) {
    ElMessage.error(`操作失败：${error.message || '未知错误'}`)
    fetchData()
  }
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  fetchData()
}

// 当前页改变
const handleCurrentChange = (page: number) => {
  pagination.currentPage = page
  fetchData()
}

// 对话框关闭
const handleDialogClose = () => {
  resetForm()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitLoading.value = true
    
    const requestData: BannerRequest = {
      title: formData.title,
      imageUrl: formData.imageUrl,
      linkType: formData.linkType,
      linkValue: formData.linkValue || undefined,
      sortOrder: formData.sortOrder,
      isActive: formData.isActive,
      startTime: formData.startTime || undefined,
      endTime: formData.endTime || undefined
    }
    
    if (formData.bannerId) {
      await bannerAPI.updateBanner(formData.bannerId, requestData)
      ElMessage.success('更新成功')
    } else {
      await bannerAPI.createBanner(requestData)
      ElMessage.success('创建成功')
    }
    
    dialogVisible.value = false
    fetchData()
  } catch (error: any) {
    ElMessage.error(`提交失败：${error.message || '未知错误'}`)
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    bannerId: undefined,
    title: '',
    imageUrl: '',
    linkType: LinkType.PRODUCT,
    linkValue: '',
    sortOrder: 0,
    isActive: BannerStatus.ENABLED,
    startTime: '',
    endTime: ''
  })
  formRef.value?.clearValidate()
}

// 获取数据
const fetchData = async () => {
  try {
    loading.value = true
    
    const response = await bannerAPI.getBannerList({
      page: pagination.currentPage,
      size: pagination.pageSize,
      title: searchForm.title || undefined,
      linkType: searchForm.linkType,
      isActive: searchForm.isActive
    })
    
    tableData.value = response.data.records
    pagination.total = response.data.total
  } catch (error: any) {
    ElMessage.error(`获取数据失败：${error.message || '未知错误'}`)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.banners-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
  
  .search-button {
    background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 50%, #fecfef 100%) !important;
      color: #fff !important;
      border: 1px solid #ff9a9e !important;
      border-radius: 25px !important;
      padding: 8px 20px !important;
      transition: all 0.3s ease !important;
      box-shadow: 0 4px 15px rgba(255, 154, 158, 0.4) !important;
      
      &:hover {
        /* background: linear-gradient(135deg, #ff6b6b 0%, #ff8a95 50%, #ffa8cc 100%) !important; */
        transform: translateY(-3px) !important;
        box-shadow: 0 8px 25px rgba(255, 107, 107, 0.5) !important;
      }
  }
  
  .reset-button {
    background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%) !important;
      color: #333 !important;
      border: 1px solid #a8edea !important;
      border-radius: 25px !important;
      padding: 8px 20px !important;
      transition: all 0.3s ease !important;
      box-shadow: 0 4px 15px rgba(168, 237, 234, 0.4) !important;
      
      &:hover {
        /* background: linear-gradient(135deg, #79f1a4 0%, #0e4b99 100%) !important; */
        color: #fff !important;
        transform: translateY(-3px) !important;
        box-shadow: 0 8px 25px rgba(121, 241, 164, 0.5) !important;
      }
  }
}

.search-form {
  margin-bottom: 0;
}

.table-card {
  margin-bottom: 20px;
  
  .add-button {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: 1px solid #667eea;
    color: white;
    border-radius: 20px;
    padding: 8px 20px;
    font-weight: 500;
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
    transition: all 0.3s ease;
    
    &:hover {
      background: linear-gradient(135deg, #566eda 0%, #664a92 100%);
      transform: translateY(-3px);
      box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6);
    }
  }
  
  .action-view-btn {
    background: linear-gradient(135deg, #d7ffa9 0%, #6efd7a 100%) !important;
    color: #fff !important;
    border: 1px solid #ff9a9e !important;
    border-radius: 20px !important;
    padding: 6px 12px !important;
    margin: 0 2px !important;
    transition: all 0.3s ease !important;
    box-shadow: 0 3px 10px rgba(255, 154, 158, 0.4) !important;
    
    &:hover {
      background: linear-gradient(135deg, #5aff0d 0%, #ee5a6f 100%) !important;
      transform: translateY(-3px) !important;
      box-shadow: 0 6px 20px rgba(255, 107, 107, 0.5) !important;
    }
  }
  
    /* // 操作列按钮样式 - 少女心渐变色彩 */
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
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
  
  .cancel-button {
    background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
    border: 1px solid #ffecd2;
    color: white;
    border-radius: 20px;
    padding: 8px 20px;
    font-weight: 500;
    box-shadow: 0 4px 15px rgba(255, 236, 210, 0.4);
    transition: all 0.3s ease;
    
    &:hover {
      background: linear-gradient(135deg, #ffdcc2 0%, #eca68f 100%);
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(255, 236, 210, 0.6);
    }
  }
  
  .confirm-button {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: 1px solid #667eea;
    color: white;
    border-radius: 20px;
    padding: 8px 20px;
    font-weight: 500;
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
    transition: all 0.3s ease;
    
    &:hover {
      background: linear-gradient(135deg, #566eda 0%, #664a92 100%);
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
    }
  }
}

.upload-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.image-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.image-uploader:hover {
  border-color: #409eff;
}

.image-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 300px;
  height: 150px;
  text-align: center;
  line-height: 150px;
}

.uploaded-image {
  width: 300px;
  height: 150px;
  display: block;
  object-fit: cover;
}

.upload-tip {
  font-size: 12px;
  color: #999;
}

.banner-detail {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.detail-item label {
  font-weight: 600;
  min-width: 80px;
  color: #606266;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .banners-container {
    padding: 10px;
  }
  
  .search-form {
    display: block;
  }
  
  .search-form .el-form-item {
    display: block;
    margin-bottom: 10px;
  }
  
  .uploaded-image,
  .image-uploader-icon {
    width: 200px;
    height: 100px;
    line-height: 100px;
  }
}
</style>