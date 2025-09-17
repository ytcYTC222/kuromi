// pages/checkout/checkout.js
const { config, getApiUrl, request } = require('../../config/api.js');

Page({
  data: {
    // 基础数据
    loading: false,
    submitting: false,
    
    // 订单商品
    orderItems: [],
    
    // 收货地址
    selectedAddress: null,
    userAddresses: [],
    showAddressModal: false,
    
    // 优惠券
    selectedCoupon: null,
    availableCoupons: [],
    showCouponModal: false,
    couponDiscount: 0,
    
    // 配送方式
    deliveryMethod: {
      name: '标准配送',
      desc: '预计3-5个工作日送达'
    },
    
    // 费用计算
    totalAmount: 0,      // 商品总价
    shippingFee: 0,      // 运费
    actualAmount: 0,     // 实付金额
    freeShippingAmount: 299, // 免邮金额
    
    // 订单备注
    orderRemark: ''
  },

  onLoad(options) {
    // 获取购物车选中的商品
    try {
      const cartItemsStr = decodeURIComponent(options.cartItems || '[]');
      const cartItems = JSON.parse(cartItemsStr);
      this.setData({
        orderItems: cartItems
      });
    } catch (error) {
      console.error('解析购物车数据失败:', error);
      wx.showToast({
        title: '数据解析失败',
        icon: 'none'
      });
      this.setData({
        orderItems: []
      });
    }
    
    this.initPage();
  },

  async initPage() {
    this.setData({ loading: true });
    
    try {
      await Promise.all([
        this.loadUserAddresses(),
        this.loadAvailableCoupons(),
        this.calculateAmount()
      ]);
    } catch (error) {
      console.error('初始化页面失败:', error);
      wx.showToast({
        title: '加载失败，请重试',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  // 加载用户地址
  async loadUserAddresses() {
    try {
      const response = await request({
        url: getApiUrl('user.addresses'),
        method: 'GET'
      });
      
      if (response.code === 200) {
        const addresses = response.data || [];
        const defaultAddress = addresses.find(addr => addr.is_default === 1);
        
        this.setData({
          userAddresses: addresses,
          selectedAddress: defaultAddress || (addresses.length > 0 ? addresses[0] : null)
        });
      }
    } catch (error) {
      console.error('加载地址失败:', error);
    }
  },

  // 加载可用优惠券
  async loadAvailableCoupons() {
    try {
      const response = await request({
        url: getApiUrl('user.coupons'),
        method: 'GET',
        data: {
          status: 0, // 未使用
          amount: this.data.totalAmount
        }
      });
      
      if (response.code === 200) {
        this.setData({
          availableCoupons: response.data || []
        });
      }
    } catch (error) {
      console.error('加载优惠券失败:', error);
    }
  },

  // 计算金额
  calculateAmount() {
    const { orderItems, selectedCoupon, freeShippingAmount } = this.data;
    
    // 计算商品总价
    let totalAmount = 0;
    orderItems.forEach(item => {
      totalAmount += parseFloat(item.price) * parseInt(item.quantity);
    });
    
    // 计算优惠券折扣
    let couponDiscount = 0;
    if (selectedCoupon) {
      if (selectedCoupon.coupon_type === 1) {
        // 满减券
        if (totalAmount >= parseFloat(selectedCoupon.min_amount)) {
          couponDiscount = parseFloat(selectedCoupon.discount_value);
        }
      } else if (selectedCoupon.coupon_type === 2) {
        // 折扣券
        couponDiscount = totalAmount * (1 - parseFloat(selectedCoupon.discount_value) / 100);
        if (selectedCoupon.max_discount && couponDiscount > parseFloat(selectedCoupon.max_discount)) {
          couponDiscount = parseFloat(selectedCoupon.max_discount);
        }
      }
    }
    
    // 确保couponDiscount是数字类型
    couponDiscount = parseFloat(couponDiscount) || 0;
    
    // 计算运费
    let shippingFee = 0;
    if (totalAmount - couponDiscount < freeShippingAmount) {
      shippingFee = 50; // 默认运费50元
    }
    
    // 计算实付金额
    const actualAmount = totalAmount - couponDiscount + shippingFee;
    
    this.setData({
      totalAmount: totalAmount.toFixed(2),
      couponDiscount: couponDiscount.toFixed(2),
      shippingFee: shippingFee.toFixed(2),
      actualAmount: actualAmount.toFixed(2)
    });
  },

  // 选择收货地址
  selectAddress() {
    if (this.data.userAddresses.length === 0) {
      this.addNewAddress();
      return;
    }
    
    this.setData({
      showAddressModal: true
    });
  },

  // 关闭地址弹窗
  closeAddressModal() {
    this.setData({
      showAddressModal: false
    });
  },

  // 选择地址项
  selectAddressItem(e) {
    const address = e.currentTarget.dataset.address;
    this.setData({
      selectedAddress: address,
      showAddressModal: false
    });
  },

  // 新增地址
  addNewAddress() {
    wx.navigateTo({
      url: '/pages/address/address?action=add'
    });
  },

  // 选择优惠券
  selectCoupon() {
    this.setData({
      showCouponModal: true
    });
  },

  // 关闭优惠券弹窗
  closeCouponModal() {
    this.setData({
      showCouponModal: false
    });
  },

  // 选择优惠券项
  selectCouponItem(e) {
    const coupon = e.currentTarget.dataset.coupon;
    this.setData({
      selectedCoupon: coupon,
      showCouponModal: false
    }, () => {
      this.calculateAmount();
    });
  },

  // 订单备注输入
  onRemarkInput(e) {
    this.setData({
      orderRemark: e.detail.value
    });
  },

  // 提交订单
  async submitOrder() {
    const { selectedAddress, orderItems, selectedCoupon, orderRemark, actualAmount } = this.data;
    
    if (!selectedAddress) {
      wx.showToast({
        title: '请选择收货地址',
        icon: 'none'
      });
      return;
    }
    
    if (orderItems.length === 0) {
      wx.showToast({
        title: '购物车为空',
        icon: 'none'
      });
      return;
    }
    
    this.setData({ submitting: true });
    
    try {
      // 构建订单数据
      const orderData = {
        items: orderItems.map(item => ({
          product_id: item.id,
          quantity: item.quantity,
          price: item.price
        })),
        address: {
          receiver_name: selectedAddress.receiver_name,
          receiver_phone: selectedAddress.receiver_phone,
          receiver_address: `${selectedAddress.province} ${selectedAddress.city} ${selectedAddress.district} ${selectedAddress.detail_address}`
        },
        coupon_id: selectedCoupon ? selectedCoupon.coupon_id : null,
        remark: orderRemark,
        total_amount: this.data.totalAmount,
        discount_amount: this.data.couponDiscount,
        shipping_fee: this.data.shippingFee,
        actual_amount: actualAmount
      };
      
      const response = await request({
        url: getApiUrl('order.create'),
        method: 'POST',
        data: orderData
      });
      
      if (response.code === 200) {
        const orderId = response.data.order_id;
        
        // 清空购物车中已下单的商品
        await this.clearCartItems();
        
        wx.showToast({
          title: '订单提交成功',
          icon: 'success'
        });
        
        // 返回购物车页面并刷新
        setTimeout(() => {
          wx.navigateBack({
            delta: 1,
            success: () => {
              // 通过事件通知购物车页面刷新
              const pages = getCurrentPages();
              const cartPage = pages[pages.length - 1];
              if (cartPage && cartPage.route === 'pages/cart/cart') {
                cartPage.loadCartItems && cartPage.loadCartItems();
              }
            }
          });
        }, 1500);
      } else {
        throw new Error(response.message || '订单提交失败');
      }
    } catch (error) {
      console.error('提交订单失败:', error);
      wx.showToast({
        title: error.message || '订单提交失败',
        icon: 'none'
      });
    } finally {
      this.setData({ submitting: false });
    }
  },

  // 清空购物车中已下单的商品
  async clearCartItems() {
    try {
      const cartIds = this.data.orderItems.map(item => item.cartId);
      await request({
        url: getApiUrl('cart.batchDelete'),
        method: 'DELETE',
        data: {
          cartIds: cartIds
        }
      });
    } catch (error) {
      console.error('清空购物车失败:', error);
    }
  },

  // 页面显示时刷新地址
  onShow() {
    // 如果从地址页面返回，重新加载地址列表
    if (this.data.userAddresses.length === 0 || !this.data.selectedAddress) {
      this.loadUserAddresses();
    }
  },

  // 分享功能
  onShareAppMessage() {
    return {
      title: '现代家居 - 确认订单',
      path: '/pages/index/index'
    };
  }
});