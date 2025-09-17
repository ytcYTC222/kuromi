<template>
  <div class="product-add-edit">
    <el-card class="card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">{{ isEdit ? '编辑商品' : '添加商品' }}</span>
          <div class="header-actions">
            <el-button class="cute-button" @click="goBack">
              返回列表
            </el-button>
          </div>
        </div>
      </template>
      
      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        label-width="120px"
        class="product-form"
      >
        <el-row :gutter="20">
          <!-- 基本信息 -->
          <el-col :span="24">
            <el-card class="form-section card" shadow="never">
              <template #header>
                <span class="section-title card-title">基本信息</span>
              </template>
              
              <el-row :gutter="20">
                <el-col :xs="24" :md="12">
                  <el-form-item label="商品名称" prop="name">
                    <el-input 
                      v-model="form.name" 
                      placeholder="请输入商品名称"
                      maxlength="100"
                      show-word-limit
                    />
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :md="12">
                  <el-form-item label="商品编码" prop="code">
                    <el-input 
                      v-model="form.code" 
                      placeholder="请输入商品编码"
                      maxlength="50"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-row :gutter="20">
                <el-col :xs="24" :md="12">
                  <el-form-item label="商品分类" prop="categoryId">
                    <el-select 
                      v-model="form.categoryId" 
                      placeholder="请选择商品分类"
                      style="width: 100%"
                    >
                      <el-option 
                        v-for="category in categories" 
                        :key="category.id" 
                        :label="category.name" 
                        :value="category.id"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :md="12">
                  <el-form-item label="商品品牌" prop="brandId">
                    <el-select 
                      v-model="form.brandId" 
                      placeholder="请选择商品品牌"
                      style="width: 100%"
                    >
                      <el-option 
                        v-for="brand in brands" 
                        :key="brand.id" 
                        :label="brand.name" 
                        :value="brand.id"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-form-item label="商品描述" prop="description">
                <el-input 
                  v-model="form.description" 
                  type="textarea" 
                  :rows="4"
                  placeholder="请输入商品描述"
                  maxlength="500"
                  show-word-limit
                />
              </el-form-item>
            </el-card>
          </el-col>
          
          <!-- 价格库存 -->
          <el-col :span="24">
            <el-card class="form-section card" shadow="never">
              <template #header>
                <span class="section-title card-title">价格库存</span>
              </template>
              
              <el-row :gutter="20">
                <el-col :xs="24" :md="8">
                  <el-form-item label="销售价格" prop="price">
                    <el-input-number 
                      v-model="form.price" 
                      :min="0" 
                      :precision="2"
                      style="width: 100%"
                      placeholder="请输入销售价格"
                    />
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :md="8">
                  <el-form-item label="成本价格" prop="costPrice">
                    <el-input-number 
                      v-model="form.costPrice" 
                      :min="0" 
                      :precision="2"
                      style="width: 100%"
                      placeholder="请输入成本价格"
                    />
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :md="8">
                  <el-form-item label="库存数量" prop="stock">
                    <el-input-number 
                      v-model="form.stock" 
                      :min="0" 
                      style="width: 100%"
                      placeholder="请输入库存数量"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-row :gutter="20">
                <el-col :xs="24" :md="8">
                  <el-form-item label="重量(kg)" prop="weight">
                    <el-input-number 
                      v-model="form.weight" 
                      :min="0" 
                      :precision="2"
                      style="width: 100%"
                      placeholder="请输入商品重量"
                    />
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :md="8">
                  <el-form-item label="库存预警" prop="lowStock">
                    <el-input-number 
                      v-model="form.lowStock" 
                      :min="0" 
                      style="width: 100%"
                      placeholder="库存预警数量"
                    />
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :md="8">
                  <el-form-item label="商品状态" prop="status">
                    <el-select v-model="form.status" style="width: 100%">
                      <el-option label="上架" value="active" />
                      <el-option label="下架" value="inactive" />
                      <el-option label="草稿" value="draft" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-card>
          </el-col>
          
          <!-- 商品图片 -->
          <el-col :span="24">
            <el-card class="form-section" shadow="never">
              <template #header>
                <span class="section-title">商品图片</span>
              </template>
              
              <el-form-item label="商品图片" prop="images">
                <el-upload
                  v-model:file-list="form.images"
                  action="#"
                  list-type="picture-card"
                  :auto-upload="false"
                  :limit="5"
                  accept="image/*"
                  @change="handleImageChange"
                >
                  <el-icon><Plus /></el-icon>
                </el-upload>
                <div class="upload-tip">
                  支持jpg、png格式，单张图片不超过2MB，最多上传5张
                </div>
              </el-form-item>
            </el-card>
          </el-col>
          
          <!-- 商品属性 -->
          <el-col :span="24">
            <el-card class="form-section card" shadow="never">
              <template #header>
                <div class="section-header">
                  <span class="section-title card-title">商品属性</span>
                  <el-button type="primary" size="small" class="cute-button" @click="addAttribute">
                    添加属性
                  </el-button>
                </div>
              </template>
              
              <div v-if="form.attributes.length === 0" class="empty-attributes">
                <el-empty description="暂无商品属性" :image-size="80" />
              </div>
              
              <div v-else class="attributes-list">
                <div 
                  v-for="(attr, index) in form.attributes" 
                  :key="index" 
                  class="attribute-item"
                >
                  <el-row :gutter="10" align="middle">
                    <el-col :span="8">
                      <el-input 
                        v-model="attr.name" 
                        placeholder="属性名称"
                      />
                    </el-col>
                    <el-col :span="12">
                      <el-input 
                        v-model="attr.value" 
                        placeholder="属性值"
                      />
                    </el-col>
                    <el-col :span="4">
                      <el-button 
                        type="danger" 
                        size="small" 
                        @click="removeAttribute(index)"
                      >
                        删除
                      </el-button>
                    </el-col>
                  </el-row>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <!-- 操作按钮 -->
        <div class="form-actions">
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            {{ isEdit ? '更新商品' : '创建商品' }}
          </el-button>
          <el-button @click="handleReset">
            重置
          </el-button>
          <el-button @click="goBack">
            取消
          </el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules, UploadFile } from 'element-plus'

const router = useRouter()
const route = useRoute()
const formRef = ref<FormInstance>()
const loading = ref(false)

// 判断是否为编辑模式
const isEdit = computed(() => !!route.params.id)

// 表单数据
const form = reactive({
  name: '',
  code: '',
  categoryId: '',
  brandId: '',
  description: '',
  price: 0,
  costPrice: 0,
  stock: 0,
  weight: 0,
  lowStock: 10,
  status: 'active',
  images: [] as UploadFile[],
  attributes: [] as Array<{ name: string; value: string }>
})

// 分类数据
const categories = ref([
  { id: '1', name: '电子产品' },
  { id: '2', name: '服装鞋帽' },
  { id: '3', name: '家居用品' },
  { id: '4', name: '食品饮料' },
  { id: '5', name: '图书音像' }
])

// 品牌数据
const brands = ref([
  { id: '1', name: '苹果' },
  { id: '2', name: '华为' },
  { id: '3', name: '小米' },
  { id: '4', name: '三星' },
  { id: '5', name: '其他' }
])

// 表单验证规则
const rules: FormRules = {
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { min: 2, max: 100, message: '商品名称长度在2到100个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入商品编码', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9]+$/, message: '商品编码只能包含字母和数字', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入销售价格', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '销售价格必须大于0', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存数量', trigger: 'blur' },
    { type: 'number', min: 0, message: '库存数量不能小于0', trigger: 'blur' }
  ]
}

// 添加属性
const addAttribute = () => {
  form.attributes.push({ name: '', value: '' })
}

// 删除属性
const removeAttribute = (index: number) => {
  form.attributes.splice(index, 1)
}

// 处理图片变化
const handleImageChange = (file: UploadFile) => {
  // 这里可以添加图片上传逻辑
  console.log('图片变化:', file)
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    ElMessage.success(isEdit.value ? '商品更新成功' : '商品创建成功')
    router.push('/products')
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    loading.value = false
  }
}

// 重置表单
const handleReset = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要重置表单吗？所有未保存的数据将丢失。',
      '确认重置',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    formRef.value?.resetFields()
    form.images = []
    form.attributes = []
  } catch {
    // 用户取消
  }
}

// 返回列表
const goBack = () => {
  router.push('/products')
}

// 加载商品数据（编辑模式）
const loadProductData = async () => {
  if (!isEdit.value) return
  
  try {
    // 模拟加载商品数据
    const productId = route.params.id
    console.log('加载商品数据:', productId)
    
    // 这里应该调用API获取商品详情
    // const product = await getProductById(productId)
    
    // 模拟数据
    Object.assign(form, {
      name: '示例商品',
      code: 'DEMO001',
      categoryId: '1',
      brandId: '1',
      description: '这是一个示例商品描述',
      price: 99.99,
      costPrice: 50.00,
      stock: 100,
      weight: 0.5,
      lowStock: 10,
      status: 'active',
      attributes: [
        { name: '颜色', value: '红色' },
        { name: '尺寸', value: 'L' }
      ]
    })
  } catch (error) {
    ElMessage.error('加载商品数据失败')
    console.error('加载商品数据失败:', error)
  }
}

onMounted(() => {
  loadProductData()
})
</script>

<style lang="scss" scoped>
.product-add-edit {
  padding: 20px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;
  }
  
  .product-form {
    .form-section {
      margin-bottom: 20px;
      border: 1px solid #ebeef5;
      
      .section-title {
        font-weight: 600;
        color: #303133;
      }
      
      .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }
    }
    
    .upload-tip {
      margin-top: 8px;
      font-size: 12px;
      color: #909399;
    }
    
    .empty-attributes {
      padding: 20px 0;
    }
    
    .attributes-list {
      .attribute-item {
        margin-bottom: 10px;
        padding: 10px;
        background: #f8f9fa;
        border-radius: 4px;
        
        &:last-child {
          margin-bottom: 0;
        }
      }
    }
    
    .form-actions {
      margin-top: 30px;
      padding-top: 20px;
      border-top: 1px solid #ebeef5;
      text-align: center;
      
      .el-button {
        margin: 0 10px;
        min-width: 100px;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .product-add-edit {
    padding: 10px;
    
    .card-header {
      flex-direction: column;
      gap: 10px;
      align-items: flex-start;
    }
    
    .form-actions {
      .el-button {
        width: 100%;
        margin: 5px 0;
      }
    }
  }
}
</style>