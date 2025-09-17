<template>
  <div class="coupon-form">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑优惠卷' : '新增优惠卷' }}</span>
          <el-button @click="handleBack">返回列表</el-button>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="120px"
        class="coupon-form-content"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="优惠卷名称" prop="couponName">
              <el-input
                v-model="formData.couponName"
                placeholder="请输入优惠卷名称"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优惠卷类型" prop="couponType">
              <el-select
                v-model="formData.couponType"
                placeholder="请选择优惠卷类型"
                style="width: 100%"
                @change="handleTypeChange"
              >
                <el-option 
                  v-for="(label, value) in couponTypeMap" 
                  :key="value" 
                  :label="label" 
                  :value="Number(value)" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="优惠值" prop="discountValue">
              <el-input-number
                v-model="formData.discountValue"
                :min="0.01"
                :max="formData.couponType === CouponType.DISCOUNT ? 0.99 : 99999"
                :precision="2"
                :step="formData.couponType === CouponType.DISCOUNT ? 0.1 : 1"
                style="width: 100%"
                placeholder="请输入优惠值"
              />
              <div class="form-tip">
                <span v-if="formData.couponType === CouponType.FULL_REDUCTION">
                  满减优惠卷：输入减免金额（元）
                </span>
                <span v-else-if="formData.couponType === CouponType.DISCOUNT">
                  折扣优惠卷：输入折扣比例（0.1-0.99）
                </span>
                <span v-else-if="formData.couponType === CouponType.FREE_SHIPPING">
                  免邮优惠卷：固定为免邮功能
                </span>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item 
              label="最低消费金额" 
              prop="minAmount"
              v-if="formData.couponType !== CouponType.FREE_SHIPPING"
            >
              <el-input-number
                v-model="formData.minAmount"
                :min="0"
                :precision="2"
                style="width: 100%"
                placeholder="0表示无门槛"
              />
              <div class="form-tip">设置为0表示无使用门槛</div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" v-if="formData.couponType === CouponType.DISCOUNT">
          <el-col :span="12">
            <el-form-item label="最大优惠金额" prop="maxDiscount">
              <el-input-number
                v-model="formData.maxDiscount"
                :min="0.01"
                :precision="2"
                style="width: 100%"
                placeholder="请输入最大优惠金额"
              />
              <div class="form-tip">折扣优惠卷的最大优惠金额限制</div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="发放总数" prop="totalQuantity">
              <el-input-number
                v-model="formData.totalQuantity"
                :min="1"
                :step="1"
                style="width: 100%"
                placeholder="请输入发放总数"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="formData.status">
                <el-radio :label="CouponStatus.ENABLED">启用</el-radio>
                <el-radio :label="CouponStatus.DISABLED">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="formData.startTime"
                type="datetime"
                placeholder="请选择开始时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="formData.endTime"
                type="datetime"
                placeholder="请选择结束时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '更新' : '创建' }}优惠卷
          </el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button @click="handleBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { 
  couponAPI, 
  type CouponRequest,
  CouponType,
  CouponStatus,
  couponTypeMap
} from '@/api/coupon'

const route = useRoute()
const router = useRouter()

// 表单引用
const formRef = ref<FormInstance>()

// 是否为编辑模式
const isEdit = computed(() => route.name === 'MarketingCouponsEdit')

// 提交状态
const submitting = ref(false)

// 表单数据
const formData = reactive<CouponRequest>({
  couponName: '',
  couponType: CouponType.FULL_REDUCTION,
  discountValue: 0,
  minAmount: 0,
  maxDiscount: undefined,
  totalQuantity: 100,
  startTime: '',
  endTime: '',
  status: CouponStatus.ENABLED
})

// 表单验证规则
const rules: FormRules = {
  couponName: [
    { required: true, message: '请输入优惠卷名称', trigger: 'blur' },
    { min: 2, max: 100, message: '优惠卷名称长度在2-100个字符之间', trigger: 'blur' }
  ],
  couponType: [
    { required: true, message: '请选择优惠卷类型', trigger: 'change' }
  ],
  discountValue: [
    { required: true, message: '请输入优惠值', trigger: 'blur' },
    { 
      validator: (rule: any, value: number, callback: any) => {
        if (value <= 0) {
          callback(new Error('优惠值必须大于0'))
        } else if (formData.couponType === CouponType.DISCOUNT && value >= 1) {
          callback(new Error('折扣优惠卷的优惠值必须小于1'))
        } else if (formData.couponType === CouponType.FULL_REDUCTION && formData.minAmount && value >= formData.minAmount) {
          callback(new Error('满减优惠卷的优惠值不能大于等于最低消费金额'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  minAmount: [
    { 
      validator: (rule: any, value: number, callback: any) => {
        if (value < 0) {
          callback(new Error('最低消费金额不能小于0'))
        } else if (formData.couponType === CouponType.FULL_REDUCTION && value > 0 && formData.discountValue >= value) {
          callback(new Error('最低消费金额必须大于优惠值'))
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
        if (formData.couponType === CouponType.DISCOUNT) {
          if (!value || value <= 0) {
            callback(new Error('折扣优惠卷必须设置最大优惠金额'))
          } else {
            callback()
          }
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  totalQuantity: [
    { required: true, message: '请输入发放总数', trigger: 'blur' },
    { type: 'number', min: 1, message: '发放总数必须大于0', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' },
    { 
      validator: (rule: any, value: string, callback: any) => {
        if (value && formData.startTime && new Date(value) <= new Date(formData.startTime)) {
          callback(new Error('结束时间必须晚于开始时间'))
        } else {
          callback()
        }
      }, 
      trigger: 'change' 
    }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 日期时间格式化函数
const formatDateTimeForForm = (dateTimeStr: string) => {
  if (!dateTimeStr) return ''
  try {
    // 处理各种可能的日期格式
    const date = new Date(dateTimeStr)
    if (isNaN(date.getTime())) {
      console.error('无效的日期格式:', dateTimeStr)
      return ''
    }
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
  } catch (error) {
    console.error('日期格式化错误:', error, '原始数据:', dateTimeStr)
    return ''
  }
}

// 确保提交数据的日期格式正确
const ensureDateTimeFormat = (dateTimeStr: string) => {
  if (!dateTimeStr) return ''
  // 如果已经是正确格式，直接返回
  if (/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/.test(dateTimeStr)) {
    return dateTimeStr
  }
  // 否则转换格式
  return formatDateTimeForForm(dateTimeStr)
}

// 处理类型变化
const handleTypeChange = (type: CouponType) => {
  // 清空相关字段
  formData.discountValue = 0
  formData.minAmount = 0
  formData.maxDiscount = undefined
  
  // 根据类型设置默认值
  if (type === CouponType.FREE_SHIPPING) {
    formData.discountValue = 0
    formData.minAmount = 0
  } else if (type === CouponType.DISCOUNT) {
    formData.discountValue = 0.8 // 默认8折
  }
}

// 处理提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    // 表单验证
    await formRef.value.validate()
    
    submitting.value = true
    
    console.log('提交表单数据:', formData)
    console.log('是否编辑模式:', isEdit.value)
    
    // 准备提交数据，确保日期格式正确
    const submitData = {
      ...formData,
      startTime: ensureDateTimeFormat(formData.startTime),
      endTime: ensureDateTimeFormat(formData.endTime)
    }
    console.log('最终提交数据:', submitData)
    
    if (isEdit.value) {
      // 编辑模式
      const couponId = Number(route.params.id)
      console.log('更新优惠卷ID:', couponId)
      
      const response = await couponAPI.updateCoupon(couponId, submitData)
      console.log('更新响应:', response)
      
      ElMessage.success('优惠卷更新成功')
    } else {
      // 新增模式
      console.log('创建新优惠卷')
      
      const response = await couponAPI.createCoupon(submitData)
      console.log('创建响应:', response)
      
      ElMessage.success('优惠卷创建成功')
    }
    
    // 返回列表页
    router.push('/marketing/coupons/list')
    
  } catch (error: any) {
    console.error('提交失败:', error)
    ElMessage.error(`操作失败：${error.message || '系统错误'}`)
  } finally {
    submitting.value = false
  }
}

// 处理重置
const handleReset = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 处理返回
const handleBack = () => {
  router.push('/marketing/coupons/list')
}

// 加载优惠卷详情（编辑模式）
const loadCouponDetail = async () => {
  if (!isEdit.value) return
  
  try {
    const couponId = Number(route.params.id)
    console.log('加载优惠卷详情，ID:', couponId)
    
    const response = await couponAPI.getCouponById(couponId)
    console.log('优惠卷详情响应:', response)
    
    // 检查响应结构
    const coupon = response.data || response
    console.log('优惠卷数据:', coupon)
    
    // 填充表单数据
    Object.assign(formData, {
      couponName: coupon.couponName,
      couponType: coupon.couponType,
      discountValue: coupon.discountValue,
      minAmount: coupon.minAmount || 0,
      maxDiscount: coupon.maxDiscount,
      totalQuantity: coupon.totalQuantity,
      startTime: formatDateTimeForForm(coupon.startTime),
      endTime: formatDateTimeForForm(coupon.endTime),
      status: coupon.status
    })
    
    console.log('表单数据已填充:', formData)
    
  } catch (error: any) {
    console.error('加载优惠卷详情失败:', error)
    ElMessage.error(`加载优惠卷详情失败：${error.message || '系统错误'}`)
    handleBack()
  }
}

// 组件挂载时加载数据
onMounted(() => {
  console.log('优惠卷表单组件挂载')
  console.log('当前路由:', route.name)
  console.log('路由参数:', route.params)
  console.log('是否编辑模式:', isEdit.value)
  
  if (isEdit.value) {
    loadCouponDetail()
  }
})
</script>

<style lang="scss" scoped>
.coupon-form {
  padding: 20px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .coupon-form-content {
    max-width: 800px;
    
    .form-tip {
      font-size: 12px;
      color: #909399;
      margin-top: 5px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .coupon-form {
    padding: 10px;
    
    .coupon-form-content {
      .el-col {
        margin-bottom: 10px;
      }
    }
  }
}
</style>