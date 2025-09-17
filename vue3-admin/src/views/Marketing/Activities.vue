<template>
  <div class="activities-container">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="活动名称">
          <el-input
            v-model="searchForm.promotionName"
            placeholder="请输入活动名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="促销类型">
          <el-select
            v-model="searchForm.promotionType"
            placeholder="请选择促销类型"
            clearable
            style="width: 150px"
          >
            <el-option 
              v-for="(label, value) in promotionTypeMap" 
              :key="value" 
              :label="label" 
              :value="Number(value)" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="活动状态">
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

    <!-- 活动列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>促销活动列表</span>
          <el-button type="primary" class="add-button" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增活动
          </el-button>
        </div>
      </template>

      <el-table
        :data="tableData"
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="promotionId" label="ID" width="80" />
        <el-table-column prop="promotionName" label="活动名称" min-width="150" width="300"/>
        <el-table-column prop="promotionType" label="促销类型" width="150">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.promotionType)">{{ row.promotionTypeName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="discountType" label="折扣类型" width="150">
          <template #default="{ row }">
            <el-tag :type="getDiscountTagType(row.discountType)">{{ row.discountTypeName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="discountValue" label="折扣值" width="130">
          <template #default="{ row }">
            <span v-if="row.discountType === 1">{{ row.discountValue }}%</span>
            <span v-else>￥{{ row.discountValue }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="200" />
        <el-table-column prop="endTime" label="结束时间" width="200" />
        <el-table-column prop="isActive" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isActive === 1 ? 'success' : 'danger'">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <!-- <el-button type="primary" size="small" @click="handleView(row)">
              查看
            </el-button> -->
            <el-button type="warning" size="small" class="action-edit-btn" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button 
              :type="row.isActive === 1 ? 'danger' : 'success'" 
              size="small" 
              :class="row.isActive === 1 ? 'action-disable-btn' : 'action-enable-btn'"
              @click="handleToggleStatus(row)"
            >
              {{ row.isActive === 1 ? '禁用' : '启用' }}
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
        label-width="120px"
      >
        <el-form-item label="活动名称" prop="promotionName">
          <el-input v-model="formData.promotionName" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="促销类型" prop="promotionType">
          <el-select v-model="formData.promotionType" placeholder="请选择促销类型" style="width: 100%" @change="handleTypeChange">
            <el-option 
              v-for="(label, value) in promotionTypeMap" 
              :key="value" 
              :label="label" 
              :value="Number(value)" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="目标ID" prop="targetId" v-if="formData.promotionType !== 3">
          <el-input-number v-model="formData.targetId" :min="1" style="width: 100%" placeholder="商品ID或分类ID" />
        </el-form-item>
        <el-form-item label="折扣类型" prop="discountType">
          <el-select v-model="formData.discountType" placeholder="请选择折扣类型" style="width: 100%">
            <el-option 
              v-for="(label, value) in discountTypeMap" 
              :key="value" 
              :label="label" 
              :value="Number(value)" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="折扣值" prop="discountValue">
          <el-input-number 
            v-model="formData.discountValue" 
            :min="0.01" 
            :precision="2"
            style="width: 100%" 
            :placeholder="formData.discountType === 1 ? '输入百分比 (如: 80 表示8折)' : '输入减免金额'"
          />
        </el-form-item>
        <el-form-item label="最低消费" prop="minAmount">
          <el-input-number 
            v-model="formData.minAmount" 
            :min="0" 
            :precision="2"
            style="width: 100%" 
            placeholder="最低消费金额，0表示无门槛"
          />
        </el-form-item>
        <el-form-item label="最大优惠" prop="maxDiscount" v-if="formData.discountType === 1">
          <el-input-number 
            v-model="formData.maxDiscount" 
            :min="0.01" 
            :precision="2"
            style="width: 100%" 
            placeholder="百分比折扣的最大优惠金额"
          />
        </el-form-item>
        <el-form-item label="活动时间" prop="timeRange">
          <el-date-picker
            v-model="formData.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 100%"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="排序权重" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" style="width: 100%" placeholder="数字越大排序越靠前" />
        </el-form-item>
        <el-form-item label="横幅图片" prop="bannerImage">
          <el-input v-model="formData.bannerImage" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="活动描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入活动描述"
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { 
  promotionAPI, 
  type PromotionRequest, 
  type PromotionResponse,
  type PromotionListQuery,
  PromotionType,
  DiscountType,
  PromotionStatus,
  promotionTypeMap,
  discountTypeMap
} from '@/api/promotion'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()

// 搜索表单
const searchForm = reactive<PromotionListQuery>({
  promotionName: '',
  promotionType: undefined,
  isActive: undefined
})

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 表格数据
const tableData = ref<PromotionResponse[]>([])

// 表单数据
const formData = reactive<PromotionRequest & { 
  promotionId?: number 
  timeRange?: string[]
}>({
  promotionName: '',
  promotionType: PromotionType.PRODUCT,
  targetId: undefined,
  discountType: DiscountType.PERCENTAGE,
  discountValue: 0,
  minAmount: 0,
  maxDiscount: undefined,
  startTime: '',
  endTime: '',
  isActive: PromotionStatus.ENABLED,
  sortOrder: 0,
  bannerImage: '',
  description: '',
  timeRange: []
})

// 表单验证规则
const rules: FormRules = {
  promotionName: [
    { required: true, message: '请输入促销活动名称', trigger: 'blur' }
  ],
  promotionType: [
    { required: true, message: '请选择促销类型', trigger: 'change' }
  ],
  targetId: [
    { 
      validator: (rule: any, value: number, callback: any) => {
        if (formData.promotionType !== PromotionType.GLOBAL && (!value || value <= 0)) {
          callback(new Error('商品促销或分类促销必须指定目标ID'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  discountType: [
    { required: true, message: '请选择折扣类型', trigger: 'change' }
  ],
  discountValue: [
    { required: true, message: '请输入折扣值', trigger: 'blur' },
    { 
      validator: (rule: any, value: number, callback: any) => {
        if (!value || value <= 0) {
          callback(new Error('折扣值必须大于0'))
        } else if (formData.discountType === DiscountType.PERCENTAGE && value >= 100) {
          callback(new Error('百分比折扣值应该小于100'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  maxDiscount: [
    { 
      validator: (rule: any, value: number, callback: any) => {
        if (formData.discountType === DiscountType.PERCENTAGE && (!value || value <= 0)) {
          callback(new Error('百分比折扣必须设置最大优惠金额'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  timeRange: [
    { required: true, message: '请选择活动时间', trigger: 'change' }
  ]
}

// 获取促销类型标签类型
const getTypeTagType = (type: PromotionType) => {
  const typeMap: Record<PromotionType, string> = {
    [PromotionType.PRODUCT]: 'success',
    [PromotionType.CATEGORY]: 'warning', 
    [PromotionType.GLOBAL]: 'danger'
  }
  return typeMap[type] || ''
}

// 获取折扣标签类型
const getDiscountTagType = (type: DiscountType) => {
  const typeMap: Record<DiscountType, string> = {
    [DiscountType.PERCENTAGE]: 'success',
    [DiscountType.FIXED]: 'warning'
  }
  return typeMap[type] || ''
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  fetchData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    promotionName: '',
    promotionType: undefined,
    isActive: undefined
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增促销活动'
  resetForm()
  dialogVisible.value = true
}

// 查看
const handleView = (row: PromotionResponse) => {
  ElMessage.info('查看功能开发中')
}

// 编辑
const handleEdit = async (row: PromotionResponse) => {
  try {
    dialogTitle.value = '编辑促销活动'
    
    // 获取完整的促销活动信息
    const response = await promotionAPI.getPromotionById(row.promotionId)
    const promotion = response.data
    
    // 填充表单
    Object.assign(formData, {
      promotionId: promotion.promotionId,
      promotionName: promotion.promotionName,
      promotionType: promotion.promotionType,
      targetId: promotion.targetId,
      discountType: promotion.discountType,
      discountValue: promotion.discountValue,
      minAmount: promotion.minAmount,
      maxDiscount: promotion.maxDiscount,
      startTime: promotion.startTime,
      endTime: promotion.endTime,
      isActive: promotion.isActive,
      sortOrder: promotion.sortOrder,
      bannerImage: promotion.bannerImage,
      description: promotion.description,
      timeRange: [promotion.startTime, promotion.endTime]
    })
    
    dialogVisible.value = true
  } catch (error: any) {
    ElMessage.error(`获取促销活动详情失败：${error.message || '未知错误'}`)
  }
}

// 切换状态
const handleToggleStatus = async (row: PromotionResponse) => {
  try {
    const newStatus = row.isActive === 1 ? 0 : 1
    const action = newStatus === 1 ? '启用' : '禁用'
    
    await ElMessageBox.confirm(
      `确定要${action}促销活动 "${row.promotionName}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await promotionAPI.updatePromotionStatus(row.promotionId, newStatus)
    ElMessage.success(`${action}成功`)
    fetchData()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`操作失败：${error.message || '未知错误'}`)
    }
  }
}

// 删除
const handleDelete = async (row: PromotionResponse) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除促销活动 "${row.promotionName}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await promotionAPI.deletePromotion(row.promotionId)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(`删除失败：${error.message || '未知错误'}`)
    }
  }
}

// 处理促销类型变化
const handleTypeChange = (type: PromotionType) => {
  if (type === PromotionType.GLOBAL) {
    formData.targetId = undefined
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
    
    // 处理时间范围
    if (formData.timeRange && formData.timeRange.length === 2) {
      formData.startTime = formData.timeRange[0]
      formData.endTime = formData.timeRange[1]
    }
    
    // 准备提交数据
    const submitData: PromotionRequest = {
      promotionName: formData.promotionName,
      promotionType: formData.promotionType,
      targetId: formData.targetId,
      discountType: formData.discountType,
      discountValue: formData.discountValue,
      minAmount: formData.minAmount,
      maxDiscount: formData.maxDiscount,
      startTime: formData.startTime,
      endTime: formData.endTime,
      isActive: formData.isActive,
      sortOrder: formData.sortOrder,
      bannerImage: formData.bannerImage,
      description: formData.description
    }
    
    if (formData.promotionId) {
      // 编辑模式
      await promotionAPI.updatePromotion(formData.promotionId, submitData)
      ElMessage.success('更新成功')
    } else {
      // 新增模式
      await promotionAPI.createPromotion(submitData)
      ElMessage.success('创建成功')
    }
    
    dialogVisible.value = false
    fetchData()
  } catch (error: any) {
    ElMessage.error(`操作失败：${error.message || '系统错误'}`)
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    promotionId: undefined,
    promotionName: '',
    promotionType: PromotionType.PRODUCT,
    targetId: undefined,
    discountType: DiscountType.PERCENTAGE,
    discountValue: 0,
    minAmount: 0,
    maxDiscount: undefined,
    startTime: '',
    endTime: '',
    isActive: PromotionStatus.ENABLED,
    sortOrder: 0,
    bannerImage: '',
    description: '',
    timeRange: []
  })
  formRef.value?.clearValidate()
}

// 获取数据
const fetchData = async () => {
  try {
    loading.value = true
    
    const params: PromotionListQuery = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      ...searchForm
    }
    
    const response = await promotionAPI.getPromotionList(params)
    const result = response.data
    
    tableData.value = result.records
    pagination.total = result.total
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
.activities-container {
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

.table-card {
  margin-bottom: 20px;
}

/* // 搜索按钮样式 - 少女心渐变色彩 */
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

/* // 重置按钮样式 - 少女心渐变色彩 */
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
      /* background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%) !important; */
      transform: translateY(-3px) !important;
      box-shadow: 0 6px 20px rgba(240, 147, 251, 0.5) !important;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .activities-container {
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