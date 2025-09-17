const app = getApp()
const { getApiUrl } = require('../../config/api.js')

Page({
  data: {
    currentTab: 'login', // 当前标签：login 或 register
    
    // 登录表单数据
    loginForm: {
      phone: '',
      password: ''
    },
    
    // 注册表单数据
    registerForm: {
      nickname: '',
      phone: '',
      email: '',
      password: '',
      confirmPassword: ''
    },
    
    // 加载状态
    loginLoading: false,
    registerLoading: false
  },

  onLoad: function (options) {
    // 如果有重定向参数，保存起来
    if (options.redirect) {
      this.redirectUrl = decodeURIComponent(options.redirect);
    }
  },

  // 切换标签
  switchTab: function (e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({
      currentTab: tab
    });
  },

  // 登录表单输入
  onLoginInput: function (e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`loginForm.${field}`]: value
    });
  },

  // 注册表单输入
  onRegisterInput: function (e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`registerForm.${field}`]: value
    });
  },



  // 登录表单验证
  validateLoginForm: function () {
    const { phone, password } = this.data.loginForm;
    
    if (!phone) {
      wx.showToast({
        title: '请输入手机号',
        icon: 'none'
      });
      return false;
    }
    
    // 验证手机号必须是11位数字
    if (!/^\d{11}$/.test(phone)) {
      wx.showToast({
        title: '手机号必须是11位数字',
        icon: 'none'
      });
      return false;
    }
    
    // 验证手机号格式（1开头，第二位是3-9）
    if (!/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({
        title: '手机号格式不正确',
        icon: 'none'
      });
      return false;
    }
    
    if (!password) {
      wx.showToast({
        title: '请输入密码',
        icon: 'none'
      });
      return false;
    }
    
    return true;
  },

  // 注册表单验证
  validateRegisterForm: function () {
    const { nickname, phone, email, password, confirmPassword } = this.data.registerForm;
    
    if (!nickname) {
      wx.showToast({
        title: '请输入昵称',
        icon: 'none'
      });
      return false;
    }
    
    if (nickname.length < 2 || nickname.length > 20) {
      wx.showToast({
        title: '昵称长度为2-20个字符',
        icon: 'none'
      });
      return false;
    }
    
    if (!phone) {
      wx.showToast({
        title: '请输入手机号',
        icon: 'none'
      });
      return false;
    }
    
    if (!/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({
        title: '手机号格式不正确',
        icon: 'none'
      });
      return false;
    }
    
    if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      wx.showToast({
        title: '邮箱格式不正确',
        icon: 'none'
      });
      return false;
    }
    
    if (!password) {
      wx.showToast({
        title: '请输入密码',
        icon: 'none'
      });
      return false;
    }
    
    if (password.length < 6 || password.length > 20) {
      wx.showToast({
        title: '密码长度为6-20位',
        icon: 'none'
      });
      return false;
    }
    
    if (password !== confirmPassword) {
      wx.showToast({
        title: '两次输入的密码不一致',
        icon: 'none'
      });
      return false;
    }
    
    return true;
  },

  // 登录
  onLogin: function (e) {
    if (!this.validateLoginForm()) {
      return;
    }
    
    const that = this;
    const { phone, password } = this.data.loginForm;
    
    this.setData({ loginLoading: true });
    
    wx.request({
      url: getApiUrl('/auth/login'),
      method: 'POST',
      data: {
        phone: phone,
        password: password
      },
      success: function (res) {
        that.setData({ loginLoading: false });
        
        if (res.data.success) {
          const userInfo = res.data.data;
          
          // 保存用户信息到本地存储
          wx.setStorageSync('userInfo', userInfo);
          
          // 更新全局用户信息
          app.globalData.userInfo = userInfo;
          
          wx.showToast({
            title: '登录成功',
            icon: 'success'
          });
          
          // 延迟跳转，让用户看到成功提示
          setTimeout(() => {
            that.navigateBack();
          }, 1500);
        } else {
          // 检查是否为账号不存在的情况
          if (res.data.message && (res.data.message.includes('用户不存在') || res.data.message.includes('手机号未注册'))) {
            wx.showModal({
              title: '账号不存在',
              content: '该手机号尚未注册，是否前往注册页面？',
              confirmText: '去注册',
              cancelText: '取消',
              success: function(modalRes) {
                if (modalRes.confirm) {
                  // 切换到注册页面并预填手机号
                  that.setData({
                    currentTab: 'register',
                    'registerForm.phone': phone
                  });
                }
              }
            });
          } else {
            wx.showToast({
              title: res.data.message || '登录失败',
              icon: 'none'
            });
          }
        }
      },
      fail: function (error) {
        that.setData({ loginLoading: false });
        console.error('登录请求失败:', error);
        wx.showToast({
          title: '网络请求失败',
          icon: 'none'
        });
      }
    });
  },

  // 注册
  onRegister: function (e) {
    if (!this.validateRegisterForm()) {
      return;
    }
    
    const that = this;
    const { nickname, phone, email, password } = this.data.registerForm;
    
    this.setData({ registerLoading: true });
    
    wx.request({
      url: getApiUrl('/auth/register'),
      method: 'POST',
      data: {
        nickname: nickname,
        phone: phone,
        email: email || null,
        password: password
      },
      success: function (res) {
        that.setData({ registerLoading: false });
        
        if (res.data.success) {
          wx.showToast({
            title: '注册成功',
            icon: 'success'
          });
          
          // 注册成功后自动切换到登录页面
          setTimeout(() => {
            that.setData({
              currentTab: 'login',
              'loginForm.phone': phone,
              'loginForm.password': password
            });
          }, 1500);
        } else {
          wx.showToast({
            title: res.data.message || '注册失败',
            icon: 'none'
          });
        }
      },
      fail: function (error) {
        that.setData({ registerLoading: false });
        console.error('注册请求失败:', error);
        wx.showToast({
          title: '网络请求失败',
          icon: 'none'
        });
      }
    });
  },



  // 忘记密码
  forgotPassword: function () {
    wx.showToast({
      title: '请联系客服重置密码',
      icon: 'none'
    });
  },

  // 返回上一页
  navigateBack: function () {
    if (this.redirectUrl) {
      wx.redirectTo({
        url: this.redirectUrl
      });
    } else {
      wx.navigateBack({
        delta: 1
      });
    }
  }
})