# 库洛米边框组件系统

## 概述

库洛米边框组件系统提供了一套完整的现代化边框设计方案，包含多种样式变体、颜色选项、宽度规格和交互效果。所有边框组件都遵循库洛米设计系统的视觉规范，确保界面的一致性和美观性。

## 设计原则

- **清晰可见**: 确保边框具有足够的对比度，突出显示内容区域
- **现代化**: 采用现代设计趋势，支持渐变和动画效果
- **一致性**: 统一的设计语言和视觉风格
- **可访问性**: 良好的颜色对比度，支持无障碍访问
- **响应式**: 适配不同屏幕尺寸和设备

## 边框变量系统

### 边框宽度

```css
--kuromi-border-width-thin: 1rpx;    /* 细边框 */
--kuromi-border-width-normal: 2rpx;  /* 标准边框 */
--kuromi-border-width-medium: 3rpx;  /* 中等边框 */
--kuromi-border-width-thick: 4rpx;   /* 粗边框 */
--kuromi-border-width-bold: 6rpx;    /* 加粗边框 */
```

### 边框颜色

```css
--kuromi-border-primary: var(--kuromi-primary);     /* 主色边框 */
--kuromi-border-secondary: var(--kuromi-secondary); /* 次色边框 */
--kuromi-border-accent: var(--kuromi-accent);       /* 强调色边框 */
--kuromi-border-light: var(--kuromi-gray-200);      /* 浅色边框 */
--kuromi-border-medium: var(--kuromi-gray-300);     /* 中性边框 */
--kuromi-border-dark: var(--kuromi-gray-600);       /* 深色边框 */
--kuromi-border-success: var(--kuromi-success);     /* 成功色边框 */
--kuromi-border-warning: var(--kuromi-warning);     /* 警告色边框 */
--kuromi-border-error: var(--kuromi-error);         /* 错误色边框 */
--kuromi-border-info: var(--kuromi-info);           /* 信息色边框 */
```

### 边框渐变

```css
--kuromi-border-gradient-1: linear-gradient(135deg, var(--kuromi-primary), var(--kuromi-secondary));
--kuromi-border-gradient-2: linear-gradient(45deg, var(--kuromi-secondary), var(--kuromi-purple));
--kuromi-border-gradient-3: linear-gradient(90deg, var(--kuromi-accent), var(--kuromi-primary));
```

## 基础边框组件

### 基础边框类

```html
<!-- 基础边框 -->
<view class="kuromi-border">
  基础边框容器
</view>
```

### 边框样式变体

```html
<!-- 实线边框 -->
<view class="kuromi-border kuromi-border-solid">
  实线边框
</view>

<!-- 虚线边框 -->
<view class="kuromi-border kuromi-border-dashed">
  虚线边框
</view>

<!-- 点线边框 -->
<view class="kuromi-border kuromi-border-dotted">
  点线边框
</view>
```

### 边框宽度变体

```html
<!-- 细边框 -->
<view class="kuromi-border kuromi-border-thin kuromi-border-primary">
  细边框
</view>

<!-- 标准边框 -->
<view class="kuromi-border kuromi-border-normal kuromi-border-primary">
  标准边框
</view>

<!-- 粗边框 -->
<view class="kuromi-border kuromi-border-thick kuromi-border-primary">
  粗边框
</view>
```

### 边框颜色变体

```html
<!-- 主色边框 -->
<view class="kuromi-border kuromi-border-primary">
  主色边框
</view>

<!-- 成功色边框 -->
<view class="kuromi-border kuromi-border-success">
  成功色边框
</view>

<!-- 错误色边框 -->
<view class="kuromi-border kuromi-border-error">
  错误色边框
</view>
```

## 高级边框组件

### 渐变边框

```html
<!-- 渐变边框 1 -->
<view class="kuromi-border-gradient kuromi-border-gradient-1">
  渐变边框内容
</view>

<!-- 渐变边框 2 -->
<view class="kuromi-border-gradient kuromi-border-gradient-2">
  渐变边框内容
</view>
```

### 交互边框

```html
<!-- 悬停交互边框 -->
<view class="kuromi-border-interactive">
  悬停查看交互效果
</view>

<!-- 发光边框 -->
<view class="kuromi-border-glow">
  自动发光动画边框
</view>
```

### 卡片边框

```html
<view class="kuromi-card-border">
  <text class="card-title">卡片标题</text>
  <text class="card-content">卡片内容描述</text>
  <view class="card-actions">
    <text class="card-action">操作按钮</text>
  </view>
</view>
```

### 输入框边框

```html
<!-- 默认输入框 -->
<input class="kuromi-input-border" placeholder="请输入内容" />

<!-- 成功状态 -->
<input class="kuromi-input-border success" placeholder="输入正确" />

<!-- 错误状态 -->
<input class="kuromi-input-border error" placeholder="输入错误" />

<!-- 警告状态 -->
<input class="kuromi-input-border warning" placeholder="输入警告" />
```

### 分割线边框

```html
<!-- 普通分割线 -->
<view class="kuromi-divider"></view>

<!-- 渐变分割线 -->
<view class="kuromi-divider-gradient"></view>
```

## 组合使用示例

### 用户信息卡片

```html
<view class="kuromi-card-border">
  <text class="card-title">用户信息</text>
  <view class="user-info">
    <view class="avatar kuromi-border kuromi-border-primary kuromi-border-medium"></view>
    <view class="user-details">
      <text class="username">用户名</text>
      <text class="user-desc">用户描述</text>
    </view>
  </view>
  <view class="kuromi-divider-gradient"></view>
  <view class="user-stats">
    <view class="stat-item kuromi-border kuromi-border-light">
      <text class="stat-number">128</text>
      <text class="stat-label">关注</text>
    </view>
  </view>
</view>
```

## 最佳实践

### 1. 选择合适的边框宽度

- **细边框 (1rpx)**: 适用于分割线、表格边框
- **标准边框 (2rpx)**: 适用于卡片、输入框等常规组件
- **粗边框 (4rpx+)**: 适用于强调重要内容或装饰性元素

### 2. 颜色对比度

- 确保边框颜色与背景有足够的对比度
- 使用语义化颜色表达不同状态（成功、警告、错误）
- 避免使用过于鲜艳的颜色影响可读性

### 3. 交互反馈

- 为可交互元素添加 hover 和 focus 状态
- 使用过渡动画提升用户体验
- 保持交互反馈的一致性

### 4. 响应式适配

- 在小屏幕设备上适当减少边框宽度
- 确保触摸目标足够大
- 考虑不同设备的显示效果

## 注意事项

1. **性能考虑**: 渐变边框和动画效果可能影响性能，请适度使用
2. **兼容性**: 某些 CSS 属性在不同平台可能有差异，请测试兼容性
3. **可访问性**: 确保边框颜色符合 WCAG 对比度标准
4. **维护性**: 使用设计系统变量而非硬编码值，便于统一修改

## 更新日志

### v1.0.0
- 初始版本发布
- 包含基础边框组件
- 支持多种样式变体
- 添加交互效果和动画
- 完善文档和示例

---

更多信息请参考 [库洛米设计系统文档](../README.md)