<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '编辑商品' : '新增商品'"
    width="800px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      class="product-form"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="商品名称" prop="productName">
            <el-input
              v-model="formData.productName"
              placeholder="请输入商品名称"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="商品编码" prop="productCode">
            <el-input
              v-model="formData.productCode"
              placeholder="请输入商品编码"
              maxlength="50"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="商品分类" prop="categoryId">
            <el-select
              v-model="formData.categoryId"
              placeholder="请选择分类"
              style="width: 100%"
              filterable
            >
              <el-option
                v-for="category in categoryList"
                :key="category.categoryId"
                :label="category.categoryName"
                :value="category.categoryId"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="品牌" prop="brand">
            <el-input
              v-model="formData.brand"
              placeholder="请输入品牌"
              maxlength="50"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="原价" prop="originalPrice">
            <el-input-number
              v-model="formData.originalPrice"
              :min="0"
              :precision="2"
              style="width: 100%"
              placeholder="原价"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="现价" prop="currentPrice">
            <el-input-number
              v-model="formData.currentPrice"
              :min="0"
              :precision="2"
              style="width: 100%"
              placeholder="现价"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="成本价" prop="costPrice">
            <el-input-number
              v-model="formData.costPrice"
              :min="0"
              :precision="2"
              style="width: 100%"
              placeholder="成本价"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="库存数量" prop="stockQuantity">
            <el-input-number
              v-model="formData.stockQuantity"
              :min="0"
              style="width: 100%"
              placeholder="库存数量"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="重量(kg)" prop="weight">
            <el-input-number
              v-model="formData.weight"
              :min="0"
              :precision="2"
              style="width: 100%"
              placeholder="重量"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="状态" prop="status">
            <el-select v-model="formData.status" style="width: 100%">
              <el-option label="上架" :value="1" />
              <el-option label="下架" :value="0" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="材质" prop="material">
            <el-input
              v-model="formData.material"
              placeholder="请输入材质"
              maxlength="50"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="颜色" prop="color">
            <el-input
              v-model="formData.color"
              placeholder="请输入颜色"
              maxlength="50"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="尺寸" prop="size">
            <el-input
              v-model="formData.size"
              placeholder="请输入尺寸"
              maxlength="50"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="是否热门">
            <el-switch
              v-model="formData.isHot"
              :active-value="1"
              :inactive-value="0"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="是否新品">
            <el-switch
              v-model="formData.isNew"
              :active-value="1"
              :inactive-value="0"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="是否促销">
            <el-switch
              v-model="formData.isPromotion"
              :active-value="1"
              :inactive-value="0"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="商品描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="4"
          placeholder="请输入商品描述"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          {{ isEdit ? '更新' : '创建' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, nextTick } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { productAPI, type ProductRequest, type ProductResponse } from '@/api/product'
import { categoryAPI, type CategoryResponse } from '@/api/category'

// Props
interface Props {
  modelValue: boolean
  isEdit?: boolean
  productData?: ProductResponse | null
}

const props = withDefaults(defineProps<Props>(), {
  isEdit: false,
  productData: null
})

// Emits
interface Emits {
  'update:modelValue': [value: boolean]
  'success': []
}

const emit = defineEmits<Emits>()

// Refs
const formRef = ref<FormInstance>()
const visible = ref(false)
const submitLoading = ref(false)
const categoryList = ref<CategoryResponse[]>([])

// 表单数据
const formData = reactive<ProductRequest>({
  productName: '',
  productCode: '',
  categoryId: 0,
  brand: '',
  description: '',
  material: '',
  color: '',
  size: '',
  weight: 0,
  originalPrice: 0,
  currentPrice: 0,
  costPrice: 0,
  stockQuantity: 0,
  isHot: 0,
  isNew: 0,
  isPromotion: 0,
  status: 1
})

// 表单验证规则
const formRules: FormRules = {
  productName: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { min: 2, max: 100, message: '商品名称长度在2到100个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  originalPrice: [
    { required: true, message: '请输入原价', trigger: 'blur' },
    { type: 'number', min: 0, message: '原价不能小于0', trigger: 'blur' }
  ],
  currentPrice: [
    { required: true, message: '请输入现价', trigger: 'blur' },
    { type: 'number', min: 0, message: '现价不能小于0', trigger: 'blur' }
  ],
  stockQuantity: [
    { required: true, message: '请输入库存数量', trigger: 'blur' },
    { type: 'number', min: 0, message: '库存数量不能小于0', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 监听 modelValue 变化
watch(
  () => props.modelValue,
  (newVal) => {
    visible.value = newVal
    if (newVal) {
      loadCategories()
      if (props.isEdit && props.productData) {
        fillFormData(props.productData)
      } else {
        resetFormData()
      }
    }
  },
  { immediate: true }
)

// 监听 visible 变化
watch(visible, (newVal) => {
  emit('update:modelValue', newVal)
})

// 加载分类列表
const loadCategories = async () => {
  try {
    const response = await categoryAPI.getCategoryList({ page: 1, size: 1000 })
    categoryList.value = response.records
  } catch (error: any) {
    ElMessage.error(`加载分类列表失败：${error.message || '未知错误'}`)
  }
}

// 填充表单数据（编辑模式）
const fillFormData = (data: ProductResponse) => {
  Object.assign(formData, {
    productName: data.productName || '',
    productCode: data.productCode || '',
    categoryId: data.categoryId || 0,
    brand: data.brand || '',
    description: data.description || '',
    material: data.material || '',
    color: data.color || '',
    size: data.size || '',
    weight: data.weight || 0,
    originalPrice: data.originalPrice || 0,
    currentPrice: data.currentPrice || 0,
    costPrice: data.costPrice || 0,
    stockQuantity: data.stockQuantity || 0,
    isHot: data.isHot || 0,
    isNew: data.isNew || 0,
    isPromotion: data.isPromotion || 0,
    status: data.status || 1
  })
}

// 重置表单数据
const resetFormData = () => {
  Object.assign(formData, {
    productName: '',
    productCode: '',
    categoryId: 0,
    brand: '',
    description: '',
    material: '',
    color: '',
    size: '',
    weight: 0,
    originalPrice: 0,
    currentPrice: 0,
    costPrice: 0,
    stockQuantity: 0,
    isHot: 0,
    isNew: 0,
    isPromotion: 0,
    status: 1
  })
  
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

// 处理关闭
const handleClose = () => {
  visible.value = false
}

// 处理提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    submitLoading.value = true
    
    if (props.isEdit && props.productData) {
      // 编辑模式
      await productAPI.updateProduct(props.productData.productId, formData)
      ElMessage.success('商品更新成功')
    } else {
      // 新增模式
      await productAPI.createProduct(formData)
      ElMessage.success('商品创建成功')
    }
    
    emit('success')
    handleClose()
  } catch (error: any) {
    if (error.message) {
      ElMessage.error(`操作失败：${error.message}`)
    }
  } finally {
    submitLoading.value = false
  }
}
</script>

<style lang="scss" scoped>
.product-form {
  .el-form-item {
    margin-bottom: 20px;
  }
}

.dialog-footer {
  text-align: right;
}

/* 数字输入框宽度设置 - 使用更强的选择器 */
:deep(.el-form-item .el-input-number) {
  width: 110px !important;
  min-width: 110px !important;
  outline: none !important;
  box-shadow: none !important;
}

:deep(.el-form-item .el-input-number .el-input__wrapper) {
  width: 100% !important;
}

:deep(.el-form-item .el-input-number .el-input__inner) {
  width: 100% !important;
  min-width: 110px !important;
  outline: none !important;
  border: 1px solid #dcdfe6 !important;
  box-shadow: none !important;
}

/* 原价字段特殊样式 */
.original-price-field :deep(.el-input-number) {
  width: 160px !important;
  min-width: 160px !important;
}

.original-price-field :deep(.el-input-number .el-input__inner) {
  width: 100% !important;
  min-width: 140px !important;
}

/* 全局数字输入框样式覆盖 */
:deep(.el-input-number) {
  width: 160px !important;
  min-width: 160px !important;
}

:deep(.el-input-number .el-input__inner) {
  width: 100% !important;
  min-width: 140px !important;
}

:deep(.el-input-number .el-input__inner:focus) {
  outline: none !important;
  border: 1px solid #dcdfe6 !important;
  box-shadow: none !important;
}

:deep(.el-input-number:focus-within) {
  outline: none !important;
  box-shadow: none !important;
}

:deep(.el-input-number:focus) {
  outline: none !important;
  box-shadow: none !important;
}

:deep(.el-input-number.is-focus .el-input__inner) {
  outline: none !important;
  border: 1px solid #dcdfe6 !important;
  box-shadow: none !important;
}
</style>