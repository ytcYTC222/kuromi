<template>
  <div class="kuromi-loading-container" v-if="visible">
    <div class="kuromi-loading-backdrop" @click="closeOnBackdrop && $emit('close')"></div>
    <div class="kuromi-loading-content">
      <!-- 库洛米头像加载动画 -->
      <div class="kuromi-avatar-loading">
        <div class="kuromi-face">
          <div class="kuromi-ears">
            <div class="ear ear-left"></div>
            <div class="ear ear-right"></div>
          </div>
          <div class="kuromi-head">
            <div class="kuromi-eyes">
              <div class="eye eye-left kuromi-blink"></div>
              <div class="eye eye-right kuromi-blink"></div>
            </div>
            <div class="kuromi-nose"></div>
            <div class="kuromi-mouth"></div>
          </div>
          <div class="kuromi-bow kuromi-glow-pulse">
            <div class="bow-center"></div>
            <div class="bow-skull">💀</div>
          </div>
        </div>
      </div>
      
      <!-- 加载文字 -->
      <div class="kuromi-loading-text kuromi-wiggle">
        {{ loadingText }}
      </div>
      
      <!-- 进度条 -->
      <div class="kuromi-progress-container" v-if="showProgress">
        <div class="kuromi-progress-bar">
          <div class="kuromi-progress-fill" :style="{ width: progress + '%' }"></div>
          <div class="kuromi-progress-glow"></div>
        </div>
        <div class="kuromi-progress-text">{{ progress }}%</div>
      </div>
      
      <!-- 装饰元素 -->
      <div class="kuromi-loading-decorations">
        <div class="decoration decoration-1 floating">💜</div>
        <div class="decoration decoration-2 twinkling">✨</div>
        <div class="decoration decoration-3 floating">🖤</div>
        <div class="decoration decoration-4 twinkling">⭐</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  loadingText: {
    type: String,
    default: '库洛米正在努力加载中...'
  },
  progress: {
    type: Number,
    default: 0
  },
  showProgress: {
    type: Boolean,
    default: false
  },
  closeOnBackdrop: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close'])
</script>

<style scoped>
.kuromi-loading-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.kuromi-loading-backdrop {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(5px);
}

.kuromi-loading-content {
  position: relative;
  background: var(--kuromi-gradient-5);
  border-radius: 25px;
  padding: 40px;
  box-shadow: var(--kuromi-shadow-strong), var(--kuromi-glow-purple);
  text-align: center;
  border: 3px solid var(--kuromi-secondary);
}

/* 库洛米头像加载动画 */
.kuromi-avatar-loading {
  margin-bottom: 20px;
}

.kuromi-face {
  position: relative;
  width: 80px;
  height: 80px;
  margin: 0 auto;
}

.kuromi-ears {
  position: absolute;
  top: -10px;
  width: 100%;
}

.ear {
  width: 20px;
  height: 25px;
  background: var(--kuromi-black);
  border-radius: 50% 50% 0 0;
  position: absolute;
}

.ear-left {
  left: 10px;
  transform: rotate(-20deg);
  animation: ear-wiggle 2s ease-in-out infinite;
}

.ear-right {
  right: 10px;
  transform: rotate(20deg);
  animation: ear-wiggle 2s ease-in-out infinite reverse;
}

.kuromi-head {
  width: 60px;
  height: 60px;
  background: white;
  border-radius: 50%;
  position: relative;
  margin: 10px auto;
  border: 3px solid var(--kuromi-black);
  animation: head-bounce 1.5s ease-in-out infinite;
}

.kuromi-eyes {
  position: absolute;
  top: 18px;
  width: 100%;
}

.eye {
  width: 8px;
  height: 8px;
  background: var(--kuromi-black);
  border-radius: 50%;
  position: absolute;
}

.eye-left {
  left: 15px;
}

.eye-right {
  right: 15px;
}

.kuromi-nose {
  position: absolute;
  top: 28px;
  left: 50%;
  transform: translateX(-50%);
  width: 4px;
  height: 3px;
  background: var(--kuromi-pink);
  border-radius: 50%;
}

.kuromi-mouth {
  position: absolute;
  top: 35px;
  left: 50%;
  transform: translateX(-50%);
  width: 12px;
  height: 6px;
  border: 2px solid var(--kuromi-black);
  border-top: none;
  border-radius: 0 0 12px 12px;
}

.kuromi-bow {
  position: absolute;
  top: -5px;
  right: -10px;
  width: 25px;
  height: 15px;
  background: var(--kuromi-gradient-2);
  border-radius: 50%;
  border: 2px solid var(--kuromi-black);
}

.bow-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 6px;
  height: 10px;
  background: var(--kuromi-black);
  border-radius: 2px;
}

.bow-skull {
  position: absolute;
  top: -8px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 10px;
}

/* 加载文字 */
.kuromi-loading-text {
  font-family: 'Fredoka One', cursive;
  font-size: 18px;
  color: var(--kuromi-primary);
  margin-bottom: 20px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

/* 进度条 */
.kuromi-progress-container {
  margin-top: 20px;
}

.kuromi-progress-bar {
  width: 200px;
  height: 8px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 10px;
  margin: 0 auto 10px;
  position: relative;
  overflow: hidden;
  border: 2px solid var(--kuromi-secondary);
}

.kuromi-progress-fill {
  height: 100%;
  background: var(--kuromi-gradient-1);
  border-radius: 8px;
  transition: width 0.3s ease;
  position: relative;
}

.kuromi-progress-glow {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  animation: progress-shine 2s ease-in-out infinite;
}

.kuromi-progress-text {
  font-family: 'Fredoka One', cursive;
  font-size: 14px;
  color: var(--kuromi-primary);
}

/* 装饰元素 */
.kuromi-loading-decorations {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.decoration {
  position: absolute;
  font-size: 16px;
}

.decoration-1 {
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.decoration-2 {
  top: 30%;
  right: 15%;
  animation-delay: 0.5s;
}

.decoration-3 {
  bottom: 30%;
  left: 15%;
  animation-delay: 1s;
}

.decoration-4 {
  bottom: 20%;
  right: 10%;
  animation-delay: 1.5s;
}

/* 动画效果 */
@keyframes head-bounce {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-5px); }
}

@keyframes ear-wiggle {
  0%, 100% { transform: rotate(-20deg); }
  50% { transform: rotate(-25deg); }
}

@keyframes progress-shine {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

@keyframes floating {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-8px); }
}

@keyframes twinkling {
  0%, 100% { opacity: 0.3; transform: scale(0.8); }
  50% { opacity: 1; transform: scale(1.2); }
}

@keyframes kuromi-blink {
  0%, 90% { opacity: 1; }
  95% { opacity: 0.3; }
  100% { opacity: 1; }
}

@keyframes kuromi-wiggle {
  0%, 100% { transform: rotate(-2deg); }
  50% { transform: rotate(2deg); }
}

@keyframes kuromi-glow-pulse {
  0%, 100% { 
    box-shadow: 0 0 5px rgba(139, 0, 139, 0.5);
    transform: scale(1);
  }
  50% { 
    box-shadow: 0 0 15px rgba(139, 0, 139, 0.8);
    transform: scale(1.05);
  }
}

.kuromi-blink {
  animation: kuromi-blink 3s infinite;
}

.kuromi-wiggle {
  animation: kuromi-wiggle 2s ease-in-out infinite;
}

.kuromi-glow-pulse {
  animation: kuromi-glow-pulse 2s ease-in-out infinite;
}

.floating {
  animation: floating 2s ease-in-out infinite;
}

.twinkling {
  animation: twinkling 1.5s ease-in-out infinite;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .kuromi-loading-content {
    padding: 30px 20px;
  }
  
  .kuromi-face {
    width: 60px;
    height: 60px;
  }
  
  .kuromi-head {
    width: 45px;
    height: 45px;
  }
  
  .kuromi-loading-text {
    font-size: 16px;
  }
  
  .kuromi-progress-bar {
    width: 150px;
  }
}
</style>