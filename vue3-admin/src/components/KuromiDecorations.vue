<template>
  <div class="kuromi-decorations">


    <!-- 可爱的星星装饰 -->
    <div class="stars-container">
      <div class="star star-1 twinkling">★</div>
      <div class="star star-2 twinkling">✦</div>
      <div class="star star-3 twinkling">✧</div>
      <div class="star star-4 twinkling">⭐</div>
    </div>

    <!-- 库洛米文字装饰 -->
    <div class="kuromi-text bouncing kuromi-wiggle" @click="playDecorationSound('click')">
      <span class="letter">K</span>
      <span class="letter">u</span>
      <span class="letter">r</span>
      <span class="letter">o</span>
      <span class="letter">m</span>
      <span class="letter">i</span>
    </div>

    <!-- 小恶魔尾巴装饰 -->
    <div class="devil-tail swaying kuromi-wiggle"></div>

    <!-- 爱心装饰 -->
    <div class="hearts-container">
      <div class="heart heart-1 pulsing" @click="playDecorationSound('heart')">💜</div>
      <div class="heart heart-2 pulsing" @click="playDecorationSound('heart')">🖤</div>
      <div class="heart heart-3 pulsing" @click="playDecorationSound('heart')">💜</div>
    </div>
    
    <!-- 小恶魔角装饰 -->
    <div class="devil-horns-container">
      <div class="devil-horns devil-horns-1 kuromi-wiggle"></div>
      <div class="devil-horns devil-horns-2 kuromi-wiggle"></div>
    </div>
    
    <!-- 魔法棒装饰 -->
    <div class="magic-wand floating" @click="playDecorationSound('magic')">
      <div class="wand-stick"></div>
      <div class="wand-star">⭐</div>
      <div class="magic-sparkles">
        <span class="sparkle sparkle-1 twinkling">✨</span>
        <span class="sparkle sparkle-2 twinkling">✨</span>
        <span class="sparkle sparkle-3 twinkling">✨</span>
      </div>
    </div>
    
    <!-- 月亮装饰 -->
    <div class="moon-container">
      <div class="crescent-moon swaying">🌙</div>
      <div class="moon-glow"></div>
    </div>
    
    <!-- 小蝙蝠装饰 -->
    <div class="bat-container">
      <div class="bat bat-1 floating">🦇</div>
      <div class="bat bat-2 floating">🦇</div>
    </div>
    
    <!-- 骷髅装饰 -->
    <div class="skull-decorations">
      <div class="mini-skull mini-skull-1 bouncing">💀</div>
      <div class="mini-skull mini-skull-2 bouncing">💀</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import kuromiSounds from '@/utils/kuromiSounds'



// 音效交互函数
const playDecorationSound = (type: string) => {
  switch (type) {
    case 'heart':
      kuromiSounds.playKuromiHeartbeat()
      break
    case 'magic':
      kuromiSounds.playKuromiDecoration()
      break
    case 'click':
      kuromiSounds.playKuromiClick()
      break
    default:
      kuromiSounds.playKuromiHover()
  }
}

onMounted(() => {
  // 确保音效系统已初始化
  if (kuromiSounds.audioContext?.state === 'suspended') {
    document.addEventListener('click', () => {
      kuromiSounds.audioContext.resume()
    }, { once: true })
  }
})
</script>

<style lang="scss" scoped>
@import url('https://fonts.googleapis.com/css2?family=Fredoka+One:wght@400&display=swap');

.kuromi-decorations {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1000;
  overflow: hidden;
  
  // 为可点击元素启用指针事件
  .kuromi-skull-bow,
  .heart,
  .magic-wand,
  .kuromi-text {
    pointer-events: auto;
    cursor: pointer;
    transition: transform 0.2s ease;
    
    &:hover {
      transform: scale(1.1);
    }
    
    &:active {
      transform: scale(0.95);
    }
  }
}



// 星星装饰
.stars-container {
  position: absolute;
  width: 100%;
  height: 100%;
  
  .star {
    position: absolute;
    font-size: 16px;
    color: #ff69b4;
    text-shadow: 0 0 10px #ff69b4;
    
    &.star-1 {
      top: 10%;
      left: 10%;
      animation-delay: 0s;
    }
    
    &.star-2 {
      top: 20%;
      right: 15%;
      animation-delay: 0.5s;
    }
    
    &.star-3 {
      bottom: 30%;
      left: 20%;
      animation-delay: 1s;
    }
    
    &.star-4 {
      bottom: 20%;
      right: 25%;
      animation-delay: 1.5s;
    }
  }
}

// 库洛米文字
.kuromi-text {
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  font-family: 'Fredoka One', cursive;
  font-size: 24px;
  color: #ff1493;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  
  .letter {
    display: inline-block;
    animation: bounce 2s infinite;
    
    &:nth-child(1) { animation-delay: 0s; }
    &:nth-child(2) { animation-delay: 0.1s; }
    &:nth-child(3) { animation-delay: 0.2s; }
    &:nth-child(4) { animation-delay: 0.3s; }
    &:nth-child(5) { animation-delay: 0.4s; }
    &:nth-child(6) { animation-delay: 0.5s; }
  }
}

// 恶魔尾巴
.devil-tail {
  position: absolute;
  bottom: 50px;
  right: 30px;
  width: 30px;
  height: 60px;
  background: linear-gradient(to bottom, #333 0%, #666 50%, #333 100%);
  border-radius: 50% 50% 50% 50% / 60% 60% 40% 40%;
  transform-origin: top center;
  
  &::after {
    content: '';
    position: absolute;
    bottom: -5px;
    right: -5px;
    width: 0;
    height: 0;
    border-left: 8px solid transparent;
    border-right: 8px solid transparent;
    border-top: 12px solid #ff1493;
  }
}

// 爱心装饰
.hearts-container {
  position: absolute;
  width: 100%;
  height: 100%;
  
  .heart {
    position: absolute;
    font-size: 20px;
    
    &.heart-1 {
      top: 40%;
      left: 5%;
      animation-delay: 0s;
    }
    
    &.heart-2 {
      top: 60%;
      right: 10%;
      animation-delay: 1s;
    }
    
    &.heart-3 {
      bottom: 40%;
      left: 15%;
      animation-delay: 2s;
    }
  }
}

// 动画效果
@keyframes floating {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes twinkling {
  0%, 100% {
    opacity: 0.3;
    transform: scale(0.8);
  }
  50% {
    opacity: 1;
    transform: scale(1.2);
  }
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

@keyframes swaying {
  0%, 100% {
    transform: rotate(-5deg);
  }
  50% {
    transform: rotate(5deg);
  }
}

@keyframes pulsing {
  0%, 100% {
    transform: scale(1);
    opacity: 0.7;
  }
  50% {
    transform: scale(1.2);
    opacity: 1;
  }
}

@keyframes kuromi-blink {
  0%, 90%, 100% {
    height: 6px;
    border-radius: 50%;
  }
  95% {
    height: 2px;
    border-radius: 0;
  }
}

@keyframes kuromi-wiggle {
  0%, 100% {
    transform: rotate(0deg);
  }
  25% {
    transform: rotate(-2deg);
  }
  75% {
    transform: rotate(2deg);
  }
}

@keyframes kuromi-glow-pulse {
  0%, 100% {
    box-shadow: 0 0 5px rgba(255, 20, 147, 0.3);
  }
  50% {
    box-shadow: 0 0 20px rgba(255, 20, 147, 0.8), 0 0 30px rgba(255, 105, 180, 0.6);
  }
}

// 应用动画
.floating {
  animation: floating 3s ease-in-out infinite;
}

.twinkling {
  animation: twinkling 2s ease-in-out infinite;
}

.bouncing {
  animation: bounce 2s infinite;
}

.swaying {
  animation: swaying 4s ease-in-out infinite;
}

.pulsing {
  animation: pulsing 3s ease-in-out infinite;
}

.kuromi-wiggle {
  animation: kuromi-wiggle 3s ease-in-out infinite;
}

.kuromi-glow-pulse {
  animation: kuromi-glow-pulse 2s ease-in-out infinite;
}

// 小恶魔角样式
.devil-horns-container {
  position: absolute;
  top: 10%;
  left: 15%;
  z-index: 5;
}

.devil-horns {
  width: 0;
  height: 0;
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-bottom: 20px solid #8B0000;
  position: absolute;
  border-radius: 50% 50% 0 0;
}

.devil-horns-1 {
  transform: rotate(-15deg);
}

.devil-horns-2 {
  left: 25px;
  transform: rotate(15deg);
}

// 魔法棒样式
.magic-wand {
  position: absolute;
  top: 20%;
  right: 10%;
  z-index: 6;
}

.wand-stick {
  width: 3px;
  height: 40px;
  background: linear-gradient(to bottom, #8B4513, #D2691E);
  border-radius: 2px;
  position: relative;
}

.wand-star {
  position: absolute;
  top: -10px;
  left: -8px;
  font-size: 20px;
  filter: drop-shadow(0 0 5px #FFD700);
}

.magic-sparkles {
  position: absolute;
  top: -15px;
  left: -20px;
  width: 40px;
  height: 40px;
}

.sparkle {
  position: absolute;
  font-size: 12px;
}

.sparkle-1 {
  top: 5px;
  left: 25px;
  animation-delay: 0s;
}

.sparkle-2 {
  top: 15px;
  left: 5px;
  animation-delay: 0.5s;
}

.sparkle-3 {
  top: 25px;
  left: 20px;
  animation-delay: 1s;
}

// 月亮样式
.moon-container {
  position: absolute;
  top: 5%;
  right: 20%;
  z-index: 4;
}

.crescent-moon {
  font-size: 30px;
  filter: drop-shadow(0 0 10px #C0C0C0);
}

.moon-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 40px;
  height: 40px;
  background: radial-gradient(circle, rgba(192, 192, 192, 0.3) 0%, transparent 70%);
  border-radius: 50%;
  z-index: -1;
}

// 蝙蝠样式
.bat-container {
  position: absolute;
  top: 30%;
  left: 5%;
  z-index: 3;
}

.bat {
  position: absolute;
  font-size: 18px;
  filter: drop-shadow(0 0 3px #000);
}

.bat-1 {
  animation-delay: 0s;
}

.bat-2 {
  left: 30px;
  top: 20px;
  animation-delay: 1.5s;
}

// 骷髅装饰样式
.skull-decorations {
  position: absolute;
  bottom: 15%;
  right: 15%;
  z-index: 3;
}

.mini-skull {
  position: absolute;
  font-size: 16px;
  filter: drop-shadow(0 0 3px #8B008B);
}

.mini-skull-1 {
  animation-delay: 0s;
}

.mini-skull-2 {
  left: 25px;
  top: 15px;
  animation-delay: 1s;
}

// 响应式设计
@media (max-width: 768px) {
  .kuromi-skull-bow {
    width: 40px;
    height: 40px;
    top: 10px;
    right: 10px;
    
    .skull {
      width: 30px;
      height: 30px;
    }
    
    .bow {
      width: 40px;
      height: 15px;
    }
  }
  
  .kuromi-text {
    font-size: 18px;
    bottom: 20px;
  }
  
  .stars-container .star {
    font-size: 12px;
  }
  
  .hearts-container .heart {
    font-size: 16px;
  }
}
</style>