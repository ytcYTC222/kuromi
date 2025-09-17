<template>
  <div class="error-page">
    <div class="error-container">
      <div class="error-content">
        <div class="error-image">
          <svg viewBox="0 0 404 300" class="error-svg">
            <text x="50%" y="50%" text-anchor="middle" dominant-baseline="middle" class="error-text">
              404
            </text>
          </svg>
        </div>
        
        <div class="error-info">
          <h1 class="error-title">页面未找到</h1>
          <p class="error-description">
            抱歉，您访问的页面不存在或已被删除。
          </p>
          
          <div class="error-actions">
            <el-button type="primary" @click="goHome">
              <el-icon><HomeFilled /></el-icon>
              返回首页
            </el-button>
            <el-button @click="goBack">
              <el-icon><ArrowLeft /></el-icon>
              返回上页
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 装饰元素 -->
      <div class="decoration">
        <div class="floating-shape shape-1"></div>
        <div class="floating-shape shape-2"></div>
        <div class="floating-shape shape-3"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { HomeFilled, ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()

// 返回首页
const goHome = () => {
  router.push('/')
}

// 返回上一页
const goBack = () => {
  if (window.history.length > 1) {
    router.go(-1)
  } else {
    router.push('/')
  }
}
</script>

<style lang="scss" scoped>
.error-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.error-container {
  position: relative;
  z-index: 2;
  text-align: center;
  max-width: 600px;
  padding: 40px 20px;
}

.error-content {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 60px 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.error-image {
  margin-bottom: 40px;
}

.error-svg {
  width: 200px;
  height: 150px;
  
  .error-text {
    font-size: 80px;
    font-weight: bold;
    fill: #409eff;
    font-family: 'Arial', sans-serif;
  }
}

.error-info {
  .error-title {
    font-size: 32px;
    color: #303133;
    margin-bottom: 16px;
    font-weight: 600;
  }
  
  .error-description {
    font-size: 16px;
    color: #606266;
    margin-bottom: 40px;
    line-height: 1.6;
  }
}

.error-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
  
  .el-button {
    padding: 12px 24px;
    font-size: 14px;
    border-radius: 8px;
    
    .el-icon {
      margin-right: 6px;
    }
  }
}

// 装饰元素
.decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.floating-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
  
  &.shape-1 {
    width: 80px;
    height: 80px;
    top: 20%;
    left: 10%;
    animation-delay: 0s;
  }
  
  &.shape-2 {
    width: 120px;
    height: 120px;
    top: 60%;
    right: 15%;
    animation-delay: 2s;
  }
  
  &.shape-3 {
    width: 60px;
    height: 60px;
    bottom: 20%;
    left: 20%;
    animation-delay: 4s;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .error-container {
    max-width: 90%;
    padding: 20px 10px;
  }
  
  .error-content {
    padding: 40px 20px;
  }
  
  .error-svg {
    width: 150px;
    height: 120px;
    
    .error-text {
      font-size: 60px;
    }
  }
  
  .error-title {
    font-size: 24px !important;
  }
  
  .error-actions {
    flex-direction: column;
    align-items: center;
    
    .el-button {
      width: 200px;
    }
  }
  
  .floating-shape {
    display: none;
  }
}

@media (max-width: 480px) {
  .error-content {
    padding: 30px 15px;
  }
  
  .error-title {
    font-size: 20px !important;
  }
  
  .error-description {
    font-size: 14px !important;
  }
}
</style>