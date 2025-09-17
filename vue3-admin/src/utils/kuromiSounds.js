// 库洛米音效管理器
class KuromiSoundManager {
  constructor() {
    this.audioContext = null;
    this.sounds = new Map();
    this.enabled = true;
    this.volume = 0.3;
    this.init();
  }

  // 初始化音频上下文
  async init() {
    try {
      this.audioContext = new (window.AudioContext || window.webkitAudioContext)();
      await this.createSounds();
    } catch (error) {
      console.warn('库洛米音效初始化失败:', error);
      this.enabled = false;
    }
  }

  // 创建音效
  async createSounds() {
    const soundDefinitions = {
      // 点击音效 - 可爱的"啵"声
      click: {
        frequency: 800,
        duration: 0.1,
        type: 'sine',
        envelope: { attack: 0.01, decay: 0.09 }
      },
      // 悬停音效 - 轻柔的"叮"声
      hover: {
        frequency: 1200,
        duration: 0.05,
        type: 'triangle',
        envelope: { attack: 0.005, decay: 0.045 }
      },
      // 成功音效 - 愉快的上升音调
      success: {
        frequencies: [523, 659, 784], // C5, E5, G5
        duration: 0.6,
        type: 'sine',
        envelope: { attack: 0.1, decay: 0.5 }
      },
      // 错误音效 - 低沉的"咚"声
      error: {
        frequency: 200,
        duration: 0.3,
        type: 'sawtooth',
        envelope: { attack: 0.05, decay: 0.25 }
      },
      // 魔法音效 - 闪烁的音调
      magic: {
        frequencies: [440, 554, 659, 831], // A4, C#5, E5, G#5
        duration: 0.8,
        type: 'sine',
        envelope: { attack: 0.1, decay: 0.1, sustain: 0.3, release: 0.4 }
      },
      // 心跳音效 - 双重低音
      heartbeat: {
        frequencies: [80, 120],
        duration: 0.4,
        type: 'sine',
        envelope: { attack: 0.01, decay: 0.19, sustain: 0.1, release: 0.1 }
      },
      // 装饰音效 - 轻快的铃声
      decoration: {
        frequencies: [1047, 1319, 1568], // C6, E6, G6
        duration: 0.3,
        type: 'triangle',
        envelope: { attack: 0.05, decay: 0.25 }
      }
    };

    for (const [name, config] of Object.entries(soundDefinitions)) {
      this.sounds.set(name, config);
    }
  }

  // 播放音效
  async play(soundName, options = {}) {
    if (!this.enabled || !this.audioContext || this.audioContext.state === 'suspended') {
      return;
    }

    try {
      // 恢复音频上下文（用户交互后）
      if (this.audioContext.state === 'suspended') {
        await this.audioContext.resume();
      }

      const soundConfig = this.sounds.get(soundName);
      if (!soundConfig) {
        console.warn(`未找到音效: ${soundName}`);
        return;
      }

      const volume = options.volume !== undefined ? options.volume : this.volume;
      const pitch = options.pitch || 1;

      if (soundConfig.frequencies) {
        // 播放和弦或序列音效
        this.playChord(soundConfig, volume, pitch, options.sequence);
      } else {
        // 播放单音效
        this.playTone(soundConfig, volume, pitch);
      }
    } catch (error) {
      console.warn('播放音效失败:', error);
    }
  }

  // 播放单音
  playTone(config, volume, pitch) {
    const oscillator = this.audioContext.createOscillator();
    const gainNode = this.audioContext.createGain();
    
    oscillator.connect(gainNode);
    gainNode.connect(this.audioContext.destination);
    
    oscillator.type = config.type;
    oscillator.frequency.setValueAtTime(config.frequency * pitch, this.audioContext.currentTime);
    
    const now = this.audioContext.currentTime;
    const { attack, decay, sustain, release } = config.envelope;
    
    gainNode.gain.setValueAtTime(0, now);
    gainNode.gain.linearRampToValueAtTime(volume, now + attack);
    
    if (sustain !== undefined) {
      gainNode.gain.linearRampToValueAtTime(volume * sustain, now + attack + decay);
      gainNode.gain.setValueAtTime(volume * sustain, now + config.duration - release);
      gainNode.gain.linearRampToValueAtTime(0, now + config.duration);
    } else {
      gainNode.gain.linearRampToValueAtTime(0, now + attack + decay);
    }
    
    oscillator.start(now);
    oscillator.stop(now + config.duration);
  }

  // 播放和弦或序列
  playChord(config, volume, pitch, sequence = false) {
    const { frequencies, duration, type, envelope } = config;
    
    if (sequence) {
      // 序列播放
      frequencies.forEach((freq, index) => {
        setTimeout(() => {
          const tempConfig = { ...config, frequency: freq };
          this.playTone(tempConfig, volume, pitch);
        }, index * 100);
      });
    } else {
      // 和弦播放
      frequencies.forEach(freq => {
        const tempConfig = { ...config, frequency: freq };
        this.playTone(tempConfig, volume * 0.7, pitch); // 降低音量避免过响
      });
    }
  }

  // 设置音量
  setVolume(volume) {
    this.volume = Math.max(0, Math.min(1, volume));
  }

  // 启用/禁用音效
  setEnabled(enabled) {
    this.enabled = enabled;
  }

  // 获取状态
  getStatus() {
    return {
      enabled: this.enabled,
      volume: this.volume,
      contextState: this.audioContext?.state,
      soundsLoaded: this.sounds.size
    };
  }

  // 预设音效组合
  playKuromiClick() {
    this.play('click', { pitch: 1.2 });
  }

  playKuromiHover() {
    this.play('hover', { volume: 0.2 });
  }

  playKuromiSuccess() {
    this.play('success', { sequence: true });
  }

  playKuromiError() {
    this.play('error');
  }

  playKuromiMagic() {
    this.play('magic', { sequence: true, volume: 0.4 });
  }

  playKuromiHeartbeat() {
    this.play('heartbeat');
    setTimeout(() => this.play('heartbeat', { pitch: 1.1 }), 200);
  }

  playKuromiDecoration() {
    this.play('decoration', { volume: 0.25, sequence: true });
  }
}

// 创建全局实例
const kuromiSounds = new KuromiSoundManager();

// Vue 3 插件
export const KuromiSoundsPlugin = {
  install(app) {
    app.config.globalProperties.$kuromiSounds = kuromiSounds;
    app.provide('kuromiSounds', kuromiSounds);
  }
};

// 导出实例和工具函数
export default kuromiSounds;

// 便捷的指令函数
export const kuromiSoundDirectives = {
  // v-kuromi-click
  'kuromi-click': {
    mounted(el, binding) {
      const soundName = binding.value || 'click';
      el.addEventListener('click', () => {
        kuromiSounds.play(soundName);
      });
    }
  },
  
  // v-kuromi-hover
  'kuromi-hover': {
    mounted(el, binding) {
      const soundName = binding.value || 'hover';
      el.addEventListener('mouseenter', () => {
        kuromiSounds.play(soundName);
      });
    }
  },
  
  // v-kuromi-magic
  'kuromi-magic': {
    mounted(el, binding) {
      el.addEventListener('click', () => {
        kuromiSounds.playKuromiMagic();
      });
    }
  }
};

// 自动初始化用户交互监听
document.addEventListener('click', () => {
  if (kuromiSounds.audioContext?.state === 'suspended') {
    kuromiSounds.audioContext.resume();
  }
}, { once: true });