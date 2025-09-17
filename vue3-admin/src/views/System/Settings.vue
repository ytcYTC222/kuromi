<template>
  <div class="system-settings">
    <el-row :gutter="20">
      <!-- 基本设置 -->
      <el-col :xs="24" :lg="12">
        <el-card class="setting-card" shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><Setting /></el-icon>
              <span>基本设置</span>
            </div>
          </template>
          
          <el-form :model="basicSettings" label-width="120px">
            <el-form-item label="网站名称">
              <el-input v-model="basicSettings.siteName" placeholder="请输入网站名称" />
            </el-form-item>
            <el-form-item label="网站描述">
              <el-input 
                v-model="basicSettings.siteDescription" 
                type="textarea" 
                :rows="3"
                placeholder="请输入网站描述"
              />
            </el-form-item>
            <el-form-item label="联系邮箱">
              <el-input v-model="basicSettings.contactEmail" placeholder="请输入联系邮箱" />
            </el-form-item>
            <el-form-item label="客服电话">
              <el-input v-model="basicSettings.servicePhone" placeholder="请输入客服电话" />
            </el-form-item>
            <el-form-item label="网站状态">
              <el-switch 
                v-model="basicSettings.siteStatus" 
                active-text="开启" 
                inactive-text="关闭"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveBasicSettings">
                保存设置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      
      <!-- 邮件设置 -->
      <el-col :xs="24" :lg="12">
        <el-card class="setting-card" shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><Message /></el-icon>
              <span>邮件设置</span>
            </div>
          </template>
          
          <el-form :model="emailSettings" label-width="120px">
            <el-form-item label="SMTP服务器">
              <el-input v-model="emailSettings.smtpHost" placeholder="请输入SMTP服务器" />
            </el-form-item>
            <el-form-item label="SMTP端口">
              <el-input-number 
                v-model="emailSettings.smtpPort" 
                :min="1" 
                :max="65535"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="发送邮箱">
              <el-input v-model="emailSettings.fromEmail" placeholder="请输入发送邮箱" />
            </el-form-item>
            <el-form-item label="邮箱密码">
              <el-input 
                v-model="emailSettings.emailPassword" 
                type="password" 
                placeholder="请输入邮箱密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="启用SSL">
              <el-switch v-model="emailSettings.enableSSL" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveEmailSettings">
                保存设置
              </el-button>
              <el-button @click="testEmail">
                测试邮件
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 支付设置 -->
      <el-col :xs="24" :lg="12">
        <el-card class="setting-card" shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><CreditCard /></el-icon>
              <span>支付设置</span>
            </div>
          </template>
          
          <el-form :model="paymentSettings" label-width="120px">
            <el-form-item label="支付方式">
              <el-checkbox-group v-model="paymentSettings.enabledMethods">
                <el-checkbox label="wechat">微信支付</el-checkbox>
                <el-checkbox label="alipay">支付宝</el-checkbox>
                <el-checkbox label="bank">银行卡</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item label="微信商户号">
              <el-input v-model="paymentSettings.wechatMerchantId" placeholder="请输入微信商户号" />
            </el-form-item>
            <el-form-item label="微信密钥">
              <el-input 
                v-model="paymentSettings.wechatKey" 
                type="password" 
                placeholder="请输入微信密钥"
                show-password
              />
            </el-form-item>
            <el-form-item label="支付宝应用ID">
              <el-input v-model="paymentSettings.alipayAppId" placeholder="请输入支付宝应用ID" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="savePaymentSettings">
                保存设置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      
      <!-- 存储设置 -->
      <el-col :xs="24" :lg="12">
        <el-card class="setting-card" shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><FolderOpened /></el-icon>
              <span>存储设置</span>
            </div>
          </template>
          
          <el-form :model="storageSettings" label-width="120px">
            <el-form-item label="存储类型">
              <el-radio-group v-model="storageSettings.type">
                <el-radio label="local">本地存储</el-radio>
                <el-radio label="oss">阿里云OSS</el-radio>
                <el-radio label="qiniu">七牛云</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-if="storageSettings.type === 'oss'" label="AccessKey">
              <el-input v-model="storageSettings.ossAccessKey" placeholder="请输入AccessKey" />
            </el-form-item>
            <el-form-item v-if="storageSettings.type === 'oss'" label="SecretKey">
              <el-input 
                v-model="storageSettings.ossSecretKey" 
                type="password" 
                placeholder="请输入SecretKey"
                show-password
              />
            </el-form-item>
            <el-form-item v-if="storageSettings.type === 'oss'" label="Bucket">
              <el-input v-model="storageSettings.ossBucket" placeholder="请输入Bucket名称" />
            </el-form-item>
            <el-form-item v-if="storageSettings.type === 'oss'" label="域名">
              <el-input v-model="storageSettings.ossDomain" placeholder="请输入访问域名" />
            </el-form-item>
            <el-form-item label="最大文件大小">
              <el-input-number 
                v-model="storageSettings.maxFileSize" 
                :min="1" 
                :max="100"
                style="width: 100%"
              />
              <span style="margin-left: 8px; color: #909399;">MB</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveStorageSettings">
                保存设置
              </el-button>
              <el-button @click="testStorage">
                测试连接
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 系统信息 -->
    <el-row style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="setting-card" shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><Monitor /></el-icon>
              <span>系统信息</span>
            </div>
          </template>
          
          <el-descriptions :column="3" border>
            <el-descriptions-item label="系统版本">v1.0.0</el-descriptions-item>
            <el-descriptions-item label="Vue版本">{{ vueVersion }}</el-descriptions-item>
            <el-descriptions-item label="Element Plus版本">{{ elementVersion }}</el-descriptions-item>
            <el-descriptions-item label="运行环境">{{ nodeEnv }}</el-descriptions-item>
            <el-descriptions-item label="构建时间">{{ buildTime }}</el-descriptions-item>
            <el-descriptions-item label="最后更新">{{ lastUpdate }}</el-descriptions-item>
          </el-descriptions>
          
          <div style="margin-top: 20px">
            <el-button type="primary" @click="checkUpdate">
              检查更新
            </el-button>
            <el-button @click="clearCache">
              清除缓存
            </el-button>
            <el-button type="danger" @click="restartSystem">
              重启系统
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Setting,
  Message,
  CreditCard,
  FolderOpened,
  Monitor
} from '@element-plus/icons-vue'
import { version } from 'vue'

// 基本设置
const basicSettings = reactive({
  siteName: '商城管理系统',
  siteDescription: '一个功能完善的电商后台管理系统',
  contactEmail: 'admin@example.com',
  servicePhone: '400-123-4567',
  siteStatus: true
})

// 邮件设置
const emailSettings = reactive({
  smtpHost: 'smtp.qq.com',
  smtpPort: 587,
  fromEmail: 'admin@example.com',
  emailPassword: '',
  enableSSL: true
})

// 支付设置
const paymentSettings = reactive({
  enabledMethods: ['wechat', 'alipay'],
  wechatMerchantId: '',
  wechatKey: '',
  alipayAppId: ''
})

// 存储设置
const storageSettings = reactive({
  type: 'local',
  ossAccessKey: '',
  ossSecretKey: '',
  ossBucket: '',
  ossDomain: '',
  maxFileSize: 10
})

// 系统信息
const vueVersion = ref(version)
const elementVersion = ref('2.4.4')
const nodeEnv = ref(import.meta.env.MODE)
const buildTime = ref('2024-01-15 10:30:00')
const lastUpdate = ref('2024-01-15 10:30:00')

// 保存基本设置
const saveBasicSettings = async () => {
  try {
    // 模拟保存操作
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('基本设置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 保存邮件设置
const saveEmailSettings = async () => {
  try {
    // 模拟保存操作
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('邮件设置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 测试邮件
const testEmail = async () => {
  try {
    // 模拟测试邮件
    await new Promise(resolve => setTimeout(resolve, 2000))
    ElMessage.success('测试邮件发送成功')
  } catch (error) {
    ElMessage.error('测试邮件发送失败')
  }
}

// 保存支付设置
const savePaymentSettings = async () => {
  try {
    // 模拟保存操作
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('支付设置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 保存存储设置
const saveStorageSettings = async () => {
  try {
    // 模拟保存操作
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('存储设置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 测试存储连接
const testStorage = async () => {
  try {
    // 模拟测试连接
    await new Promise(resolve => setTimeout(resolve, 2000))
    ElMessage.success('存储连接测试成功')
  } catch (error) {
    ElMessage.error('存储连接测试失败')
  }
}

// 检查更新
const checkUpdate = async () => {
  try {
    // 模拟检查更新
    await new Promise(resolve => setTimeout(resolve, 2000))
    ElMessage.info('当前已是最新版本')
  } catch (error) {
    ElMessage.error('检查更新失败')
  }
}

// 清除缓存
const clearCache = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清除系统缓存吗？',
      '确认清除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 模拟清除缓存
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('缓存清除成功')
  } catch {
    // 用户取消
  }
}

// 重启系统
const restartSystem = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要重启系统吗？重启期间系统将暂时不可用。',
      '确认重启',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.info('系统重启中，请稍候...')
    // 这里应该调用重启系统的API
  } catch {
    // 用户取消
  }
}
</script>

<style lang="scss" scoped>
.system-settings {
  padding: 20px;
  
  .setting-card {
    margin-bottom: 20px;
    
    .card-header {
      display: flex;
      align-items: center;
      gap: 8px;
      font-weight: 600;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .system-settings {
    padding: 10px;
  }
}
</style>