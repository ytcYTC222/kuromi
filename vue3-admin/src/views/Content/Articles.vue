<template>
  <div class="articles-container">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="文章标题">
          <el-input
            v-model="searchForm.title"
            placeholder="请输入文章标题"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="文章分类">
          <el-input
            v-model="searchForm.category"
            placeholder="请输入文章分类"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
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

    <!-- 文章列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>文章管理</span>
          <el-button type="primary" class="add-button" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增文章
          </el-button>
        </div>
      </template>

      <el-table
        :data="tableData"
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="articleId" label="ID" width="80" />
        <el-table-column label="封面图片" width="120">
          <template #default="{ row }">
            <el-image
              v-if="row.coverImage"
              :src="row.coverImage"
              :preview-src-list="[row.coverImage]"
              fit="cover"
              style="width: 80px; height: 45px; border-radius: 4px;"
            />
            <span v-else class="no-image">无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="author" label="作者" width="100" />
        <el-table-column prop="viewCount" label="浏览量" width="80" />
        <el-table-column prop="likeCount" label="点赞数" width="80" />
        <el-table-column prop="statusName" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 1"
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
        <el-form-item label="文章标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入文章标题" />
        </el-form-item>
        <el-form-item label="文章摘要" prop="excerpt">
          <el-input
            v-model="formData.excerpt"
            type="textarea"
            :rows="3"
            placeholder="请输入文章摘要"
          />
        </el-form-item>
        <el-form-item label="文章内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="8"
            placeholder="请输入文章内容"
          />
        </el-form-item>
        <el-form-item label="封面图片" prop="coverImage">
          <div class="upload-container">
            <el-input v-model="formData.coverImage" placeholder="请输入图片URL" />
            <div class="upload-tip">建议尺寸：1200x600px，支持jpg、png格式</div>
            <div v-if="formData.coverImage" class="image-preview">
              <el-image
                :src="formData.coverImage"
                :preview-src-list="[formData.coverImage]"
                fit="cover"
                style="width: 200px; height: 100px; border-radius: 4px;"
              />
            </div>
          </div>
        </el-form-item>
        <el-form-item label="文章分类" prop="category">
          <el-input v-model="formData.category" placeholder="请输入文章分类" />
        </el-form-item>
        <el-form-item label="文章标签" prop="tags">
          <el-input v-model="formData.tags" placeholder="多个标签用逗号分隔" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="formData.author" placeholder="请输入作者名称" />
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker
            v-model="formData.publishTime"
            type="datetime"
            placeholder="选择发布时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="0">草稿</el-radio>
            <el-radio :label="1">发布</el-radio>
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
      title="文章详情"
      width="800px"
    >
      <div class="article-detail" v-if="currentArticle">
        <div class="detail-item">
          <label>封面图片：</label>
          <el-image
            v-if="currentArticle.coverImage"
            :src="currentArticle.coverImage"
            :preview-src-list="[currentArticle.coverImage]"
            fit="cover"
            style="width: 200px; height: 100px; border-radius: 4px;"
          />
          <span v-else>无图片</span>
        </div>
        <div class="detail-item">
          <label>标题：</label>
          <span>{{ currentArticle.title }}</span>
        </div>
        <div class="detail-item">
          <label>摘要：</label>
          <span>{{ currentArticle.excerpt || '无摘要' }}</span>
        </div>
        <div class="detail-item">
          <label>分类：</label>
          <span>{{ currentArticle.category || '无分类' }}</span>
        </div>
        <div class="detail-item">
          <label>标签：</label>
          <span>{{ currentArticle.tags || '无标签' }}</span>
        </div>
        <div class="detail-item">
          <label>作者：</label>
          <span>{{ currentArticle.author || '匿名' }}</span>
        </div>
        <div class="detail-item">
          <label>浏览量：</label>
          <span>{{ currentArticle.viewCount }}</span>
        </div>
        <div class="detail-item">
          <label>点赞数：</label>
          <span>{{ currentArticle.likeCount }}</span>
        </div>
        <div class="detail-item">
          <label>状态：</label>
          <el-tag :type="currentArticle.status === 1 ? 'success' : 'warning'">
            {{ currentArticle.statusName }}
          </el-tag>
        </div>
        <div class="detail-item">
          <label>发布时间：</label>
          <span>{{ currentArticle.publishTime || '未发布' }}</span>
        </div>
        <div class="detail-item">
          <label>创建时间：</label>
          <span>{{ currentArticle.createTime }}</span>
        </div>
        <div class="detail-item detail-content">
          <label>内容：</label>
          <div class="content-text">{{ currentArticle.content }}</div>
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
import { articleAPI, type ArticleResponse, type ArticleRequest, ArticleStatus } from '@/api/article'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const currentArticle = ref<ArticleResponse | null>(null)

// 搜索表单
const searchForm = reactive({
  title: '',
  category: '',
  status: undefined as number | undefined
})

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 表格数据
const tableData = ref<ArticleResponse[]>([])

// 表单数据
const formData = reactive({
  articleId: undefined as number | undefined,
  title: '',
  excerpt: '',
  content: '',
  coverImage: '',
  category: '',
  tags: '',
  author: '',
  status: ArticleStatus.DRAFT,
  publishTime: ''
})

// 表单验证规则
const rules: FormRules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入文章内容', trigger: 'blur' }
  ]
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
    category: '',
    status: undefined
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增文章'
  resetForm()
  dialogVisible.value = true
}

// 查看
const handleView = (row: ArticleResponse) => {
  currentArticle.value = row
  viewDialogVisible.value = true
}

// 编辑
const handleEdit = async (row: ArticleResponse) => {
  try {
    dialogTitle.value = '编辑文章'
    const response = await articleAPI.getArticleById(row.articleId)
    const article = response.data
    Object.assign(formData, {
      articleId: article.articleId,
      title: article.title,
      excerpt: article.excerpt || '',
      content: article.content,
      coverImage: article.coverImage || '',
      category: article.category || '',
      tags: article.tags || '',
      author: article.author || '',
      status: article.status,
      publishTime: article.publishTime || ''
    })
    dialogVisible.value = true
  } catch (error: any) {
    ElMessage.error(`获取文章详情失败：${error.message || '未知错误'}`)
  }
}

// 删除
const handleDelete = async (row: ArticleResponse) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除文章 "${row.title}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await articleAPI.deleteArticle(row.articleId)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`删除失败：${error.message || '未知错误'}`)
    }
  }
}

// 状态切换
const handleStatusChange = async (row: ArticleResponse, status: number) => {
  try {
    await articleAPI.updateArticleStatus(row.articleId, status)
    ElMessage.success(`${status === 1 ? '发布' : '下架'}成功`)
    row.status = status
    row.statusName = status === 1 ? '已发布' : '草稿'
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
    
    const requestData: ArticleRequest = {
      title: formData.title,
      excerpt: formData.excerpt || undefined,
      content: formData.content,
      coverImage: formData.coverImage || undefined,
      category: formData.category || undefined,
      tags: formData.tags || undefined,
      author: formData.author || undefined,
      status: formData.status,
      publishTime: formData.publishTime || undefined
    }
    
    if (formData.articleId) {
      await articleAPI.updateArticle(formData.articleId, requestData)
      ElMessage.success('更新成功')
    } else {
      await articleAPI.createArticle(requestData)
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
    articleId: undefined,
    title: '',
    excerpt: '',
    content: '',
    coverImage: '',
    category: '',
    tags: '',
    author: '',
    status: ArticleStatus.DRAFT,
    publishTime: ''
  })
  formRef.value?.clearValidate()
}

// 获取数据
const fetchData = async () => {
  try {
    loading.value = true
    
    const response = await articleAPI.getArticleList({
      page: pagination.currentPage,
      size: pagination.pageSize,
      title: searchForm.title || undefined,
      category: searchForm.category || undefined,
      status: searchForm.status
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
.articles-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 0;
}

/* 搜索区域按钮样式 */
/* .search-card .search-button {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 50%, #fecfef 100%);
  border: 1px solid #ff9a9e;
  color: white;
  border-radius: 20px;
  padding: 8px 20px;
  box-shadow: 0 4px 15px rgba(255, 154, 158, 0.4);
  transition: all 0.3s ease;
}

.search-card .search-button:hover {
  background: linear-gradient(135deg, #ff8a8e 0%, #fdbfdf 50%, #fdbfdf 100%);
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(255, 154, 158, 0.6);
}

.search-card .reset-button {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  border: 1px solid #a8edea;
  color: #333;
  border-radius: 20px;
  padding: 8px 20px;
  box-shadow: 0 4px 15px rgba(168, 237, 234, 0.4);
  transition: all 0.3s ease;
}

.search-card .reset-button:hover {
  background: linear-gradient(135deg, #98ddd8 0%, #edc6d3 100%);
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(168, 237, 234, 0.6);
  color: #333;
} */
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
.table-card {
  margin-bottom: 20px;
}

/* 表格区域按钮样式 */
.table-card .add-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: 1px solid #667eea;
  color: white;
  border-radius: 20px;
  padding: 8px 20px;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.table-card .add-button:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

/* 操作列按钮样式 */
/* .action-view-btn {
  background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
  border: 1px solid #84fab0;
  color: #333;
  border-radius: 20px;
  padding: 6px 16px;
  box-shadow: 0 3px 10px rgba(132, 250, 176, 0.4);
  transition: all 0.3s ease;
}

.action-view-btn:hover {
  background: linear-gradient(135deg, #74ea9f 0%, #7fc3e4 100%);
  color: #333;
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(132, 250, 176, 0.6);
}

.action-edit-btn {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
  border: 1px solid #ffecd2;
  color: #333;
  border-radius: 20px;
  padding: 6px 16px;
  box-shadow: 0 3px 10px rgba(255, 236, 210, 0.4);
  transition: all 0.3s ease;
}

.action-edit-btn:hover {
  background: linear-gradient(135deg, #ffdcc2 0%, #fba68f 100%);
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(255, 236, 210, 0.6);
  color: #333;
}

.action-delete-btn {
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
  border: 1px solid #ff9a9e;
  color: white;
  border-radius: 20px;
  padding: 6px 16px;
  box-shadow: 0 3px 10px rgba(255, 154, 158, 0.4);
  transition: all 0.3s ease;
}

.action-delete-btn:hover {
  background: linear-gradient(135deg, #ff8a8e 0%, #f9c0b4 100%);
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(255, 154, 158, 0.6);
} */
    /* // 操作列按钮样式 - 少女心渐变色彩 */
  .action-view-btn {
    background: linear-gradient(135deg, #eaf7dc 0%, #a4ffab 100%) !important;
    color: #fff !important;
    border: 1px solid #ff9a9e !important;
    border-radius: 20px !important;
    padding: 6px 12px !important;
    margin: 0 2px !important;
    transition: all 0.3s ease !important;
    box-shadow: 0 3px 10px rgba(252, 191, 193, 0.4) !important;
    
    &:hover {
      background: linear-gradient(135deg, #5aff0d 0%, #f7a6b2 100%) !important;
      transform: translateY(-3px) !important;
      box-shadow: 0 6px 20px rgba(255, 107, 107, 0.5) !important;
    }
  }

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
}

/* 对话框按钮样式 */
.dialog-footer .cancel-button {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
  border: 1px solid #ffecd2;
  color: #333;
  border-radius: 20px;
  padding: 8px 20px;
  box-shadow: 0 4px 15px rgba(255, 236, 210, 0.4);
  transition: all 0.3s ease;
}

.dialog-footer .cancel-button:hover {
  background: linear-gradient(135deg, #ffdcc2 0%, #fba68f 100%);
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(255, 236, 210, 0.6);
  color: #333;
}

.dialog-footer .confirm-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: 1px solid #667eea;
  color: white;
  border-radius: 20px;
  padding: 8px 20px;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.dialog-footer .confirm-button:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

.upload-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.upload-tip {
  font-size: 12px;
  color: #999;
}

.image-preview {
  margin-top: 10px;
}

.no-image {
  color: #999;
  font-size: 12px;
}

.article-detail {
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

.detail-content {
  flex-direction: column;
  align-items: flex-start;
}

.content-text {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  white-space: pre-wrap;
  max-height: 300px;
  overflow-y: auto;
  width: 100%;
  line-height: 1.6;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .articles-container {
    padding: 10px;
  }
  
  .search-form {
    display: block;
  }
  
  .search-form .el-form-item {
    display: block;
    margin-bottom: 10px;
  }
}
</style>