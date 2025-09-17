<template>
  <div class="user-form">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑用户' : '添加用户' }}</span>
          <div class="header-actions">
            <el-button @click="handleCancel">取消</el-button>
            <el-button type="primary" :loading="loading" @click="handleSubmit">
              {{ isEdit ? '更新' : '创建' }}
            </el-button>
          </div>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        class="user-form-content"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户昵称" prop="nickname">
              <el-input
                v-model="formData.nickname"
                placeholder="请输入用户昵称"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input
                v-model="formData.phone"
                placeholder="请输入手机号"
                maxlength="11"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="formData.email"
                placeholder="请输入邮箱"
                maxlength="100"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="formData.gender" placeholder="请选择性别">
                <el-option
                  v-for="(text, value) in userGenderMap"
                  :key="value"
                  :label="text"
                  :value="Number(value)"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="生日" prop="birthday">
              <el-date-picker
                v-model="formData.birthday"
                type="date"
                placeholder="请选择生日"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择状态">
                <el-option
                  v-for="(text, value) in userStatusMap"
                  :key="value"
                  :label="text"
                  :value="Number(value)"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" v-if="!isEdit">
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="formData.password"
                type="password"
                placeholder="请输入密码"
                maxlength="20"
                show-password
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="formData.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                maxlength="20"
                show-password
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="头像URL">
              <el-input
                v-model="formData.avatarUrl"
                placeholder="请输入头像URL（可选）"
                maxlength="500"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="微信OpenID">
              <el-input
                v-model="formData.openid"
                placeholder="请输入微信OpenID（可选）"
                maxlength="100"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="微信UnionID">
              <el-input
                v-model="formData.unionid"
                placeholder="请输入微信UnionID（可选）"
                maxlength="100"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { userAPI, type UserRequest, type UserResponse, userStatusMap, userGenderMap, UserStatus, UserGender } from '@/api/user'

const route = useRoute()
const router = useRouter()

// 表单引用
const formRef = ref<FormInstance>()

// 加载状态
const loading = ref(false)

// 是否编辑模式
const isEdit = computed(() => !!route.params.id)

// 用户ID
const userId = computed(() => Number(route.params.id))

// 表单数据
const formData = reactive<UserRequest & { confirmPassword?: string }>({
  nickname: '',
  phone: '',
  email: '',
  password: '',
  confirmPassword: '',
  gender: UserGender.UNKNOWN,
  birthday: '',
  status: UserStatus.ACTIVE,
  avatarUrl: '',
  openid: '',
  unionid: ''
})

// 表单验证规则
const formRules: FormRules = {
  nickname: [
    { required: true, message: '请输入用户昵称', trigger: 'blur' },
    { min: 2, max: 50, message: '昵称长度在2到50个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== formData.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 加载用户数据（编辑模式）
const loadUserData = async () => {
  if (!isEdit.value) return

  try {
    loading.value = true
    const userData = await userAPI.getUserById(userId.value)
    
    // 填充表单数据
    Object.assign(formData, {
      nickname: userData.nickname,
      phone: userData.phone || '',
      email: userData.email || '',
      gender: userData.gender || UserGender.UNKNOWN,
      birthday: userData.birthday || '',
      status: userData.status,
      avatarUrl: userData.avatarUrl || '',
      openid: userData.openid || '',
      unionid: userData.unionid || ''
    })
  } catch (error: any) {
    ElMessage.error(`加载用户数据失败：${error.message || '系统错误'}`)
    router.push('/users/list')
  } finally {
    loading.value = false
  }
}

// 处理提交
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    // 表单验证
    const valid = await formRef.value.validate()
    if (!valid) return

    loading.value = true

    // 准备提交数据
    const submitData: UserRequest = {
      nickname: formData.nickname,
      phone: formData.phone || undefined,
      email: formData.email || undefined,
      gender: formData.gender,
      birthday: formData.birthday || undefined,
      status: formData.status,
      avatarUrl: formData.avatarUrl || undefined,
      openid: formData.openid || undefined,
      unionid: formData.unionid || undefined
    }

    // 只在创建时包含密码
    if (!isEdit.value) {
      submitData.password = formData.password
    }

    if (isEdit.value) {
      // 更新用户
      await userAPI.updateUser(userId.value, submitData)
      ElMessage.success('用户更新成功')
    } else {
      // 创建用户
      await userAPI.createUser(submitData)
      ElMessage.success('用户创建成功')
    }

    // 返回列表页
    router.push('/users/list')
  } catch (error: any) {
    ElMessage.error(`${isEdit.value ? '更新' : '创建'}用户失败：${error.message || '系统错误'}`)
  } finally {
    loading.value = false
  }
}

// 处理取消
const handleCancel = () => {
  router.push('/users/list')
}

// 组件挂载时加载数据
onMounted(() => {
  if (isEdit.value) {
    loadUserData()
  }
})
</script>

<style lang="scss" scoped>
.user-form {
  padding: 25px;
  min-height: 100vh;
  box-sizing: border-box;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    .header-actions {
      display: flex;
      gap: 10px;
    }
  }
  
  .user-form-content {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .user-form {
    padding: 20px 15px;
    
    .user-form-content {
      max-width: 100%;
      padding: 15px;
      margin: 0 5px;
      
      .el-col {
        margin-bottom: 10px;
      }
      
      // 在小屏幕上将所有字段改为单列布局
      .el-col[class*="span-12"] {
        flex: 0 0 100%;
        max-width: 100%;
      }
    }
  }
}

@media (max-width: 480px) {
  .user-form {
    padding: 15px 10px;
    
    .user-form-content {
      padding: 12px;
      margin: 0 2px;
      
      .el-form-item {
        margin-bottom: 15px;
      }
      
      .el-form-item__label {
        width: 100px !important;
        font-size: 14px;
      }
    }
  }
}
</style>