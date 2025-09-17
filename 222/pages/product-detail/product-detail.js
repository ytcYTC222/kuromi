// 商品详情页逻辑
const app = getApp()
const { getApiUrl } = require('../../config/api.js')
const { request } = require('../../utils/request.js')

Page({
  /**
   * 页面的初始数据
   */
  data: {
    productId: null,           // 商品ID
    productDetail: {},         // 商品详情信息
    productImages: [],         // 商品图片列表
    currentImageIndex: 0,      // 当前显示的图片索引
    selectedSpecs: {},         // 已选择的规格
    quantity: 1,               // 购买数量
    cartCount: 0,              // 购物车数量
    loading: true,             // 加载状态
    error: false,              // 错误状态
    isBuyNowMode: false        // 是否为立即购买模式
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    // 获取商品ID
    const productId = options.id;
    const buyNow = options.buyNow === 'true';
    
    if (productId) {
      this.setData({ 
        productId,
        isBuyNowMode: buyNow
      });
      this.loadProductDetail(productId);
      
      // 如果是立即购买模式，显示提示
      if (buyNow) {
        wx.showToast({
          title: '请选择规格后立即购买',
          icon: 'none',
          duration: 2000
        });
      }
    } else {
      // 如果没有商品ID，显示错误
      wx.showToast({
        title: '商品不存在',
        icon: 'none'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }
    
    // 获取购物车数量
    this.getCartCount();
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    // 更新购物车数量
    this.getCartCount();
  },

  /**
   * 加载商品详情数据
   */
  loadProductDetail: function(productId) {
    const that = this;
    
    wx.request({
      url: getApiUrl(`/products/${productId}`),
      method: 'GET',
      success: function(res) {
        if (res.data.success) {
          const product = res.data.data;
          
          // 处理商品图片
          let productImages = [];
          let detailImages = [];
          
          if (product.images && product.images.length > 0) {
            // 分离主图和详情图
            const mainImages = product.images.filter(img => img.type === 1);
            const detailImgs = product.images.filter(img => img.type === 2);
            
            productImages = mainImages.length > 0 ? mainImages.map(img => img.url) : ['/img/product-default.jpg'];
            detailImages = detailImgs.map(img => img.url);
          } else {
            productImages = ['/img/product-default.jpg'];
          }
          
          // 计算折扣文本
          let discountText = '';
          if (product.originalPrice && product.currentPrice && parseFloat(product.originalPrice) > parseFloat(product.currentPrice)) {
            const discount = Math.round((1 - parseFloat(product.currentPrice) / parseFloat(product.originalPrice)) * 100);
            discountText = `立减${discount}%`;
          }
          
          // 设置商品数据
          that.setData({
            productDetail: {
              id: product.id,
              title: product.name,
              description: product.description,
              currentPrice: product.currentPrice,
              originalPrice: product.originalPrice,
              discountText: discountText,
              rating: product.rating ? product.rating.average : 4.5,
              ratingCount: product.rating ? product.rating.count : 0,
              salesCount: product.salesCount || 0,
              stock: product.stockQuantity || 0,
              isFavorite: product.isFavorite || false,
              specifications: product.specifications || [],
              parameters: product.specifications || [], // 使用specifications作为参数
              reviews: product.reviews || [],
              detailImages: detailImages
            },
            productImages: productImages,
            loading: false
          });
          
          // 设置页面标题
          wx.setNavigationBarTitle({
            title: product.name
          });
          
        } else {
          console.error('获取商品详情失败:', res.data.message);
          that.setData({ 
            loading: false,
            error: true
          });
          wx.showToast({
            title: res.data.message || '获取商品信息失败',
            icon: 'none'
          });
        }
      },
      fail: function(error) {
        console.error('请求失败:', error);
        that.setData({ 
          loading: false,
          error: true
        });
        wx.showToast({
          title: '网络请求失败',
          icon: 'none'
        });
      }
    });
  },

  /**
   * 获取购物车数量
   */
  getCartCount: function() {
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.userId) {
      this.setData({ cartCount: 0 });
      return;
    }
    
    const that = this;
    wx.request({
      url: getApiUrl('/cart/count'),
      method: 'GET',
      data: { userId: userInfo.userId },
      success: function(res) {
        if (res.data.success) {
          that.setData({ cartCount: res.data.data.count || 0 });
        }
      },
      fail: function(error) {
        console.error('获取购物车数量失败:', error);
      }
    });
  },

  /**
   * 预览商品图片
   */
  previewImages: function(e) {
    const current = e.currentTarget.dataset.current;
    wx.previewImage({
      current: current,
      urls: this.data.productImages
    });
  },

  /**
   * 预览详情图片
   */
  previewDetailImages: function(e) {
    const current = e.currentTarget.dataset.current;
    wx.previewImage({
      current: current,
      urls: this.data.productDetail.detailImages
    });
  },

  /**
   * 选择商品规格
   */
  selectSpec: function(e) {
    const specName = e.currentTarget.dataset.specName;
    const specValue = e.currentTarget.dataset.specValue;
    
    const selectedSpecs = { ...this.data.selectedSpecs };
    selectedSpecs[specName] = specValue;
    
    this.setData({ selectedSpecs });
  },

  /**
   * 减少购买数量
   */
  decreaseQuantity: function() {
    let quantity = this.data.quantity;
    if (quantity > 1) {
      this.setData({ quantity: quantity - 1 });
    }
  },

  /**
   * 增加购买数量
   */
  increaseQuantity: function() {
    let quantity = this.data.quantity;
    const stock = this.data.productDetail.stock || 0;
    
    if (quantity < stock) {
      this.setData({ quantity: quantity + 1 });
    } else {
      wx.showToast({
        title: '库存不足',
        icon: 'none'
      });
    }
  },

  /**
   * 数量输入处理
   */
  onQuantityInput: function(e) {
    let value = parseInt(e.detail.value) || 1;
    const stock = this.data.productDetail.stock || 0;
    
    if (value < 1) {
      value = 1;
    } else if (value > stock) {
      value = stock;
      wx.showToast({
        title: '库存不足',
        icon: 'none'
      });
    }
    
    this.setData({ quantity: value });
  },

  /**
   * 切换收藏状态
   */
  toggleFavorite: function() {
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.userId) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return;
    }
    
    const that = this;
    const productId = this.data.productId;
    const isFavorite = this.data.productDetail.isFavorite;
    
    const apiUrl = isFavorite ? '/favorites/remove' : '/favorites/add';
    
    wx.request({
      url: getApiUrl(apiUrl),
      method: 'POST',
      data: {
        userId: userInfo.userId,
        productId: productId
      },
      success: function(res) {
        if (res.data.success) {
          // 更新收藏状态
          const productDetail = { ...that.data.productDetail };
          productDetail.isFavorite = !productDetail.isFavorite;
          
          that.setData({ productDetail });
          
          wx.showToast({
            title: !isFavorite ? '已收藏' : '已取消收藏',
            icon: 'success'
          });
        } else {
          wx.showToast({
            title: res.data.message || '操作失败',
            icon: 'none'
          });
        }
      },
      fail: function(error) {
        console.error('收藏操作失败:', error);
        wx.showToast({
          title: '网络请求失败',
          icon: 'none'
        });
      }
    });
  },

  /**
   * 加入购物车
   */
  addToCart: function() {
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.userId) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return;
    }
    
    // 检查库存
    const stock = this.data.productDetail.stock || 0;
    if (stock <= 0) {
      wx.showToast({
        title: '商品已售罄',
        icon: 'none'
      });
      return;
    }
    
    const that = this;
    const productId = this.data.productId;
    const quantity = this.data.quantity;
    const selectedSpecs = this.data.selectedSpecs;
    
    request({
      url: '/cart/add',
      method: 'POST',
      data: {
        productId: productId,
        quantity: quantity,
        specifications: selectedSpecs
      }
    }).then(res => {
      if (res.success) {
        // 更新购物车数量
        that.getCartCount();
        
        // 设置购物车需要刷新的标志
        const app = getApp();
        app.globalData.cartNeedsRefresh = true;
        
        wx.showToast({
          title: '已加入购物车',
          icon: 'success'
        });
      } else {
        wx.showToast({
          title: res.message || '添加失败',
          icon: 'none'
        });
      }
    }).catch(error => {
      console.error('添加购物车失败:', error);
      wx.showToast({
        title: '网络请求失败',
        icon: 'none'
      });
    });
  },

  /**
   * 立即购买
   */
  buyNow: function() {
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.userId) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return;
    }
    
    // 检查库存
    const stock = this.data.productDetail.stock || 0;
    if (stock <= 0) {
      wx.showToast({
        title: '商品已售罄',
        icon: 'none'
      });
      return;
    }
    
    // 构建订单数据
    const orderData = {
      productId: this.data.productId,
      quantity: this.data.quantity,
      specifications: this.data.selectedSpecs,
      totalPrice: this.data.productDetail.currentPrice * this.data.quantity
    };
    
    // 暂时显示提示信息，订单确认页面待开发
    wx.showModal({
      title: '功能开发中',
      content: '立即购买功能正在开发中，敬请期待！',
      showCancel: false,
      confirmText: '知道了'
    });
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {
    const product = this.data.productDetail;
    return {
      title: product.title || '精选家具商品',
      path: `/pages/product-detail/product-detail?id=${this.data.productId}`,
      imageUrl: this.data.productImages[0] || '/img/product-default.jpg'
    };
  },

  /**
   * 用户点击右上角分享朋友圈
   */
  onShareTimeline: function () {
    const product = this.data.productDetail;
    return {
      title: product.title || '精选家具商品',
      query: `id=${this.data.productId}`,
      imageUrl: this.data.productImages[0] || '/img/product-default.jpg'
    };
  }
})