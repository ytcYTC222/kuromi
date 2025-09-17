// 边框组件演示页面逻辑
Page({
  /**
   * 页面的初始数据
   */
  data: {
    borderStyles: [
      { name: '实线边框', class: 'kuromi-border-solid' },
      { name: '虚线边框', class: 'kuromi-border-dashed' },
      { name: '点线边框', class: 'kuromi-border-dotted' }
    ],
    borderWidths: [
      { name: '细边框', class: 'kuromi-border-thin' },
      { name: '标准边框', class: 'kuromi-border-normal' },
      { name: '中等边框', class: 'kuromi-border-medium' },
      { name: '粗边框', class: 'kuromi-border-thick' },
      { name: '加粗边框', class: 'kuromi-border-bold' }
    ],
    borderColors: [
      { name: '主色边框', class: 'kuromi-border-primary' },
      { name: '次色边框', class: 'kuromi-border-secondary' },
      { name: '成功边框', class: 'kuromi-border-success' },
      { name: '警告边框', class: 'kuromi-border-warning' },
      { name: '错误边框', class: 'kuromi-border-error' },
      { name: '信息边框', class: 'kuromi-border-info' }
    ],
    gradientBorders: [
      { name: '渐变边框 1', class: 'kuromi-border-gradient-1' },
      { name: '渐变边框 2', class: 'kuromi-border-gradient-2' },
      { name: '渐变边框 3', class: 'kuromi-border-gradient-3' }
    ],
    interactiveBorders: [
      { name: '悬停交互边框', class: 'kuromi-border-interactive', hint: '鼠标悬停查看效果' },
      { name: '发光边框', class: 'kuromi-border-glow', hint: '自动发光动画' }
    ],
    inputStates: [
      { placeholder: '默认输入框', class: '' },
      { placeholder: '成功状态输入框', class: 'success' },
      { placeholder: '警告状态输入框', class: 'warning' },
      { placeholder: '错误状态输入框', class: 'error' }
    ]
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    console.log('边框组件演示页面加载');
    this.initPageAnimation();
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {
    console.log('边框组件演示页面渲染完成');
    this.showBorderTips();
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    console.log('边框组件演示页面显示');
  },

  /**
   * 初始化页面动画
   */
  initPageAnimation() {
    // 页面加载动画
    wx.createAnimation({
      duration: 600,
      timingFunction: 'ease-out'
    });
  },

  /**
   * 显示边框使用提示
   */
  showBorderTips() {
    wx.showToast({
      title: '边框组件演示',
      icon: 'none',
      duration: 2000
    });
  },

  /**
   * 复制边框类名
   */
  copyClassName(e) {
    const className = e.currentTarget.dataset.class;
    if (className) {
      wx.setClipboardData({
        data: className,
        success: () => {
          wx.showToast({
            title: '类名已复制',
            icon: 'success',
            duration: 1500
          });
        }
      });
    }
  },

  /**
   * 查看边框详情
   */
  viewBorderDetail(e) {
    const borderType = e.currentTarget.dataset.type;
    const borderName = e.currentTarget.dataset.name;
    
    wx.showModal({
      title: '边框详情',
      content: `边框类型: ${borderName}\n类名: ${borderType}\n\n这是库洛米设计系统中的边框组件，具有现代化的设计风格和良好的可访问性。`,
      showCancel: true,
      cancelText: '关闭',
      confirmText: '复制类名',
      success: (res) => {
        if (res.confirm) {
          wx.setClipboardData({
            data: borderType,
            success: () => {
              wx.showToast({
                title: '类名已复制',
                icon: 'success'
              });
            }
          });
        }
      }
    });
  },

  /**
   * 输入框焦点事件
   */
  onInputFocus(e) {
    console.log('输入框获得焦点:', e.detail);
  },

  /**
   * 输入框失焦事件
   */
  onInputBlur(e) {
    console.log('输入框失去焦点:', e.detail);
  },

  /**
   * 输入框输入事件
   */
  onInputChange(e) {
    console.log('输入框内容变化:', e.detail.value);
  },

  /**
   * 卡片点击事件
   */
  onCardTap(e) {
    wx.showToast({
      title: '卡片被点击',
      icon: 'none',
      duration: 1000
    });
  },

  /**
   * 统计项点击事件
   */
  onStatTap(e) {
    const statType = e.currentTarget.dataset.type;
    wx.showToast({
      title: `查看${statType}详情`,
      icon: 'none',
      duration: 1000
    });
  },

  /**
   * 分享页面
   */
  onShareAppMessage() {
    return {
      title: '库洛米边框组件演示',
      path: '/pages/border-demo/border-demo',
      imageUrl: '/images/border-demo-share.png'
    };
  },

  /**
   * 分享到朋友圈
   */
  onShareTimeline() {
    return {
      title: '库洛米边框组件演示 - 现代化设计系统',
      imageUrl: '/images/border-demo-timeline.png'
    };
  },

  /**
   * 页面滚动事件
   */
  onPageScroll(e) {
    // 可以在这里添加滚动相关的动画效果
    const scrollTop = e.scrollTop;
    if (scrollTop > 100) {
      // 显示返回顶部按钮等
    }
  },

  /**
   * 返回顶部
   */
  backToTop() {
    wx.pageScrollTo({
      scrollTop: 0,
      duration: 300
    });
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {
    console.log('边框组件演示页面隐藏');
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {
    console.log('边框组件演示页面卸载');
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
    console.log('下拉刷新');
    // 模拟刷新
    setTimeout(() => {
      wx.stopPullDownRefresh();
      wx.showToast({
        title: '刷新完成',
        icon: 'success',
        duration: 1000
      });
    }, 1000);
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {
    console.log('上拉触底');
    wx.showToast({
      title: '已到底部',
      icon: 'none',
      duration: 1000
    });
  }
});