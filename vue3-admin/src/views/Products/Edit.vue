<template>
  <div class="product-edit">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>编辑商品</span>
          <el-button @click="$router.go(-1)">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
        </div>
      </template>
      
      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        label-width="120px"
        class="product-form"
        v-loading="loading"
      >
        <el-row :gutter="20">
          <el-col :span="16">
            <!-- 基本信息 -->
            <el-card shadow="never" class="form-section">
              <template #header>
                <span class="section-title">基本信息</span>
              </template>
              
              <el-form-item label="商品名称" prop="name">
                <el-input 
                  v-model="form.name" 
                  placeholder="请输入商品名称"
                  maxlength="100"
                  show-word-limit
                />
              </el-form-item>
              
              <el-form-item label="商品分类" prop="categoryId">
                <el-cascader
                  v-model="form.categoryId"
                  :options="categories"
                  :props="{ value: 'id', label: 'name', children: 'children' }"
                  placeholder="请选择商品分类"
                  style="width: 100%"
                  clearable
                />
              </el-form-item>
              
              <el-form-item label="商品品牌" prop="brandId">
                <el-select 
                  v-model="form.brandId" 
                  placeholder="请选择商品品牌"
                  style="width: 100%"
                  clearable
                >
                  <el-option 
                    v-for="brand in brands" 
                    :key="brand.id" 
                    :label="brand.name" 
                    :value="brand.id" 
                  />
                </el-select>
              </el-form-item>
              
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
            
            <!-- 商品图片 -->
            <el-card shadow="never" class="form-section">
              <template #header>
                <span class="section-title">商品图片</span>
              </template>
              
              <el-form-item label="商品主图" prop="mainImage">
                <el-upload
                  class="image-uploader"
                  action="#"
                  :show-file-list="false"
                  :before-upload="beforeUpload"
                  :on-success="handleMainImageSuccess"
                >
                  <img v-if="form.mainImage" :src="form.mainImage" class="uploaded-image" />
                  <el-icon v-else class="image-uploader-icon"><Plus /></el-icon>
                </el-upload>
              </el-form-item>
              
              <el-form-item label="商品图片">
                <el-upload
                  class="image-uploader"
                  action="#"
                  :file-list="form.images"
                  list-type="picture-card"
                  :before-upload="beforeUpload"
                  :on-success="handleImageSuccess"
                  :on-remove="handleImageRemove"
                  multiple
                >
                  <el-icon class="image-uploader-icon"><Plus /></el-icon>
                </el-upload>
              </el-form-item>
            </el-card>
            
            <!-- 商品规格 -->
            <el-card shadow="never" class="form-section">
              <template #header>
                <div class="section-header">
                  <span class="section-title">商品规格</span>
                  <el-button type="primary" size="small" @click="addSpec">
                    <el-icon><Plus /></el-icon>
                    添加规格
                  </el-button>
                </div>
              </template>
              
              <div v-for="(spec, index) in form.specs" :key="index" class="spec-item">
                <el-row :gutter="10" align="middle">
                  <el-col :span="6">
                    <el-input v-model="spec.name" placeholder="规格名称" />
                  </el-col>
                  <el-col :span="6">
                    <el-input v-model="spec.value" placeholder="规格值" />
                  </el-col>
                  <el-col :span="4">
                    <el-input-number 
                      v-model="spec.price" 
                      :min="0" 
                      :precision="2" 
                      placeholder="价格"
                      style="width: 100%"
                    />
                  </el-col>
                  <el-col :span="4">
                    <el-input-number 
                      v-model="spec.stock" 
                      :min="0" 
                      placeholder="库存"
                      style="width: 100%"
                    />
                  </el-col>
                  <el-col :span="4">
                    <el-button type="danger" size="small" @click="removeSpec(index)">
                      删除
                    </el-button>
                  </el-col>
                </el-row>
              </div>
            </el-card>
          </el-col>
          
          <el-col :span="8">
            <!-- 价格库存 -->
            <el-card shadow="never" class="form-section">
              <template #header>
                <span class="section-title">价格库存</span>
              </template>
              
              <el-form-item label="商品价格" prop="price">
                <el-input-number 
                  v-model="form.price" 
                  :min="0" 
                  :precision="2"
                  style="width: 100%"
                  placeholder="请输入商品价格"
                />
              </el-form-item>
              
              <el-form-item label="市场价格" prop="marketPrice">
                <el-input-number 
                  v-model="form.marketPrice" 
                  :min="0" 
                  :precision="2"
                  style="width: 100%"
                  placeholder="请输入市场价格"
                />
              </el-form-item>
              
              <el-form-item label="商品库存" prop="stock">
                <el-input-number 
                  v-model="form.stock" 
                  :min="0"
                  style="width: 100%"
                  placeholder="请输入商品库存"
                />
              </el-form-item>
              
              <el-form-item label="库存预警" prop="lowStock">
                <el-input-number 
                  v-model="form.lowStock" 
                  :min="0"
                  style="width: 100%"
                  placeholder="库存预警数量"
                />
              </el-form-item>
            </el-card>
            
            <!-- 商品设置 -->
            <el-card shadow="never" class="form-section">
              <template #header>
                <span class="section-title">商品设置</span>
              </template>
              
              <el-form-item label="商品状态">
                <el-radio-group v-model="form.status">
                  <el-radio label="active">上架</el-radio>
                  <el-radio label="inactive">下架</el-radio>
                </el-radio-group>
              </el-form-item>
              
              <el-form-item label="是否推荐">
                <el-switch v-model="form.isRecommended" />
              </el-form-item>
              
              <el-form-item label="是否热销">
                <el-switch v-model="form.isHot" />
              </el-form-item>
              
              <el-form-item label="是否新品">
                <el-switch v-model="form.isNew" />
              </el-form-item>
              
              <el-form-item label="排序权重" prop="sort">
                <el-input-number 
                  v-model="form.sort" 
                  :min="0"
                  style="width: 100%"
                  placeholder="数值越大排序越靠前"
                />
              </el-form-item>
            </el-card>
            
            <!-- 操作按钮 -->
            <el-card shadow="never" class="form-section">
              <el-form-item>
                <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
                  保存修改
                </el-button>
                <el-button @click="handleReset">
                  重置
                </el-button>
                <el-button @click="$router.go(-1)">
                  取消
                </el-button>
              </el-form-item>
            </el-card>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  Plus
} from '@element-plus/icons-vue'
import type { FormInstance, FormRules, UploadProps } from 'element-plus'

const router = useRouter()
const route = useRoute()
const formRef = ref<FormInstance>()
const loading = ref(false)
const submitLoading = ref(false)

// 表单数据
const form = reactive({
  id: '',
  name: '',
  categoryId: [],
  brandId: '',
  description: '',
  mainImage: '',
  images: [],
  price: 0,
  marketPrice: 0,
  stock: 0,
  lowStock: 10,
  status: 'active',
  isRecommended: false,
  isHot: false,
  isNew: false,
  sort: 0,
  specs: []
})

// 分类数据
const categories = ref([
  {
    id: '1',
    name: '电子产品',
    children: [
      { id: '1-1', name: '手机' },
      { id: '1-2', name: '电脑' },
      { id: '1-3', name: '平板' }
    ]
  },
  {
    id: '2',
    name: '服装鞋帽',
    children: [
      { id: '2-1', name: '男装' },
      { id: '2-2', name: '女装' },
      { id: '2-3', name: '童装' }
    ]
  },
  {
    id: '3',
    name: '家居用品',
    children: [
      { id: '3-1', name: '家具' },
      { id: '3-2', name: '家电' },
      { id: '3-3', name: '装饰' }
    ]
  }
])

// 品牌数据
const brands = ref([
  { id: '1', name: '苹果' },
  { id: '2', name: '华为' },
  { id: '3', name: '小米' },
  { id: '4', name: '三星' },
  { id: '5', name: 'OPPO' }
])

// 表单验证规则
const rules: FormRules = {
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { min: 2, max: 100, message: '商品名称长度在2到100个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '商品价格必须大于0', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入商品库存', trigger: 'blur' },
    { type: 'number', min: 0, message: '商品库存不能小于0', trigger: 'blur' }
  ]
}

// 添加规格
const addSpec = () => {
  form.specs.push({
    name: '',
    value: '',
    price: 0,
    stock: 0
  })
}

// 删除规格
const removeSpec = (index: number) => {
  form.specs.splice(index, 1)
}

// 图片上传前验证
const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('上传图片只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

// 主图上传成功
const handleMainImageSuccess = (response: any) => {
  // 这里应该处理实际的上传响应
  form.mainImage = URL.createObjectURL(response.raw || response)
}

// 图片上传成功
const handleImageSuccess = (response: any) => {
  // 这里应该处理实际的上传响应
  const imageUrl = URL.createObjectURL(response.raw || response)
  form.images.push({
    name: response.name,
    url: imageUrl
  })
}

// 删除图片
const handleImageRemove = (file: any) => {
  const index = form.images.findIndex(item => item.url === file.url)
  if (index > -1) {
    form.images.splice(index, 1)
  }
}

// 加载商品数据
const loadProduct = async () => {
  try {
    loading.value = true
    const productId = route.params.id as string
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟商品数据
    const productData = {
      id: productId,
      name: 'iPhone 15 Pro',
      categoryId: ['1', '1-1'],
      brandId: '1',
      description: '最新款iPhone，搭载A17 Pro芯片',
      mainImage: 'https://via.placeholder.com/300x300',
      images: [
        { name: 'image1.jpg', url: 'https://via.placeholder.com/300x300' },
        { name: 'image2.jpg', url: 'https://via.placeholder.com/300x300' }
      ],
      price: 7999,
      marketPrice: 8999,
      stock: 100,
      lowStock: 10,
      status: 'active',
      isRecommended: true,
      isHot: true,
      isNew: false,
      sort: 100,
      specs: [
        { name: '颜色', value: '深空黑', price: 7999, stock: 50 },
        { name: '颜色', value: '银色', price: 7999, stock: 50 }
      ]
    }
    
    Object.assign(form, productData)
  } catch (error) {
    ElMessage.error('加载商品数据失败')
    console.error('加载商品数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitLoading.value = true
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    ElMessage.success('商品更新成功')
    router.push('/products/list')
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const handleReset = () => {
  loadProduct()
}

onMounted(() => {
  loadProduct()
})
</script>

<style lang="scss" scoped>
.product-edit {
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
    
    .spec-item {
      margin-bottom: 10px;
      padding: 10px;
      background: #f8f9fa;
      border-radius: $border-radius-sm;
    }
  }
  
  .image-uploader {
    :deep(.el-upload) {
      border: 1px dashed #d9d9d9;
      border-radius: $border-radius-sm;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: 0.2s;
      
      &:hover {
        border-color: #409eff;
      }
    }
    
    .image-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 148px;
      height: 148px;
      text-align: center;
      line-height: 148px;
    }
    
    .uploaded-image {
      width: 148px;
      height: 148px;
      display: block;
      object-fit: cover;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .product-edit {
    padding: 10px;
    
    .el-col {
      margin-bottom: 20px;
    }
  }
}
</style>