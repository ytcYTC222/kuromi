// pages/address/address.js
const { config, getApiUrl, request } = require('../../config/api.js');

Page({
  data: {
    loading: false,
    saving: false,
    addressList: [],
    showAddressForm: false,
    editingAddress: null,
    
    // 表单数据
    formData: {
      receiver_name: '',
      receiver_phone: '',
      province: '',
      city: '',
      district: '',
      detail_address: '',
      is_default: false
    },
    
    // 地区选择数据
    regionData: [[], [], []],
    regionIndex: [0, 0, 0],
    
    // 页面参数
    action: '', // add, edit, select
    fromPage: ''
  },

  onLoad(options) {
    this.setData({
      action: options.action || '',
      fromPage: options.from || ''
    });
    
    this.loadAddressList();
    this.initRegionData();
  },

  // 加载地址列表
  async loadAddressList() {
    this.setData({ loading: true });
    
    try {
      const response = await request({
        url: getApiUrl('user.addresses'),
        method: 'GET'
      });
      
      if (response.code === 200) {
        this.setData({
          addressList: response.data || []
        });
      } else {
        throw new Error(response.message || '加载地址失败');
      }
    } catch (error) {
      console.error('加载地址失败:', error);
      wx.showToast({
        title: '加载失败，请重试',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  // 初始化地区数据（简化版，实际项目中应该从API获取）
  initRegionData() {
    const provinces = [
      { name: '北京市', code: '110000' },
      { name: '上海市', code: '310000' },
      { name: '广东省', code: '440000' },
      { name: '浙江省', code: '330000' },
      { name: '江苏省', code: '320000' },
      { name: '山东省', code: '370000' }
    ];
    
    const cities = {
      '110000': [{ name: '北京市', code: '110100' }],
      '310000': [{ name: '上海市', code: '310100' }],
      '440000': [
        { name: '广州市', code: '440100' },
        { name: '深圳市', code: '440300' },
        { name: '东莞市', code: '441900' }
      ],
      '330000': [
        { name: '杭州市', code: '330100' },
        { name: '宁波市', code: '330200' },
        { name: '温州市', code: '330300' }
      ],
      '320000': [
        { name: '南京市', code: '320100' },
        { name: '苏州市', code: '320500' },
        { name: '无锡市', code: '320200' }
      ],
      '370000': [
        { name: '济南市', code: '370100' },
        { name: '青岛市', code: '370200' },
        { name: '烟台市', code: '370600' }
      ]
    };
    
    const districts = {
      '110100': [{ name: '东城区', code: '110101' }, { name: '西城区', code: '110102' }],
      '310100': [{ name: '黄浦区', code: '310101' }, { name: '徐汇区', code: '310104' }],
      '440100': [{ name: '荔湾区', code: '440103' }, { name: '越秀区', code: '440104' }],
      '440300': [{ name: '罗湖区', code: '440303' }, { name: '福田区', code: '440304' }],
      '441900': [{ name: '莞城街道', code: '441900001' }, { name: '南城街道', code: '441900002' }]
    };
    
    this.regionData = { provinces, cities, districts };
    
    this.setData({
      regionData: [provinces, cities['440000'] || [], districts['440100'] || []]
    });
  },

  // 地区选择变化
  onRegionChange(e) {
    const value = e.detail.value;
    const { provinces, cities, districts } = this.regionData;
    
    const selectedProvince = provinces[value[0]];
    const provinceCities = cities[selectedProvince.code] || [];
    const selectedCity = provinceCities[value[1]] || provinceCities[0];
    const cityDistricts = districts[selectedCity?.code] || [];
    const selectedDistrict = cityDistricts[value[2]] || cityDistricts[0];
    
    this.setData({
      regionIndex: value,
      regionData: [provinces, provinceCities, cityDistricts],
      'formData.province': selectedProvince.name,
      'formData.city': selectedCity?.name || '',
      'formData.district': selectedDistrict?.name || ''
    });
  },

  // 显示新增表单
  showAddForm() {
    this.setData({
      showAddressForm: true,
      editingAddress: null,
      formData: {
        receiver_name: '',
        receiver_phone: '',
        province: '',
        city: '',
        district: '',
        detail_address: '',
        is_default: false
      }
    });
  },

  // 隐藏表单
  hideAddForm() {
    this.setData({
      showAddressForm: false,
      editingAddress: null
    });
  },

  // 编辑地址
  editAddress(e) {
    const address = e.currentTarget.dataset.address;
    this.setData({
      showAddressForm: true,
      editingAddress: address,
      formData: {
        receiver_name: address.receiver_name,
        receiver_phone: address.receiver_phone,
        province: address.province,
        city: address.city,
        district: address.district,
        detail_address: address.detail_address,
        is_default: address.is_default === 1
      }
    });
  },

  // 表单输入变化
  onInputChange(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`formData.${field}`]: value
    });
  },

  // 切换默认地址
  toggleDefault() {
    this.setData({
      'formData.is_default': !this.data.formData.is_default
    });
  },

  // 保存地址
  async saveAddress() {
    const { formData, editingAddress } = this.data;
    
    // 表单验证
    if (!formData.receiver_name.trim()) {
      wx.showToast({ title: '请输入收货人姓名', icon: 'none' });
      return;
    }
    
    if (!formData.receiver_phone.trim()) {
      wx.showToast({ title: '请输入手机号', icon: 'none' });
      return;
    }
    
    if (!/^1[3-9]\d{9}$/.test(formData.receiver_phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' });
      return;
    }
    
    if (!formData.province || !formData.city || !formData.district) {
      wx.showToast({ title: '请选择所在地区', icon: 'none' });
      return;
    }
    
    if (!formData.detail_address.trim()) {
      wx.showToast({ title: '请输入详细地址', icon: 'none' });
      return;
    }
    
    this.setData({ saving: true });
    
    try {
      const addressData = {
        receiver_name: formData.receiver_name.trim(),
        receiver_phone: formData.receiver_phone.trim(),
        province: formData.province,
        city: formData.city,
        district: formData.district,
        detail_address: formData.detail_address.trim(),
        is_default: formData.is_default ? 1 : 0
      };
      
      let response;
      if (editingAddress) {
        // 编辑地址
        response = await request({
          url: getApiUrl(`user.addresses/${editingAddress.address_id}`),
          method: 'PUT',
          data: addressData
        });
      } else {
        // 新增地址
        response = await request({
          url: getApiUrl('user.addresses'),
          method: 'POST',
          data: addressData
        });
      }
      
      if (response.code === 200) {
        wx.showToast({
          title: editingAddress ? '地址更新成功' : '地址添加成功',
          icon: 'success'
        });
        
        this.hideAddForm();
        this.loadAddressList();
      } else {
        throw new Error(response.message || '保存失败');
      }
    } catch (error) {
      console.error('保存地址失败:', error);
      wx.showToast({
        title: error.message || '保存失败，请重试',
        icon: 'none'
      });
    } finally {
      this.setData({ saving: false });
    }
  },

  // 删除地址
  deleteAddress(e) {
    const addressId = e.currentTarget.dataset.id;
    
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这个收货地址吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            const response = await request({
              url: getApiUrl(`user.addresses/${addressId}`),
              method: 'DELETE'
            });
            
            if (response.code === 200) {
              wx.showToast({
                title: '删除成功',
                icon: 'success'
              });
              this.loadAddressList();
            } else {
              throw new Error(response.message || '删除失败');
            }
          } catch (error) {
            console.error('删除地址失败:', error);
            wx.showToast({
              title: '删除失败，请重试',
              icon: 'none'
            });
          }
        }
      }
    });
  },

  // 选择地址（从结算页面跳转过来时）
  selectAddress(e) {
    if (this.data.action === 'select') {
      const address = e.currentTarget.dataset.address;
      const pages = getCurrentPages();
      const prevPage = pages[pages.length - 2];
      
      if (prevPage) {
        prevPage.setData({
          selectedAddress: address
        });
      }
      
      wx.navigateBack();
    }
  },

  // 页面显示时刷新数据
  onShow() {
    if (this.data.addressList.length > 0) {
      this.loadAddressList();
    }
  },

  // 分享功能
  onShareAppMessage() {
    return {
      title: '现代家居 - 收货地址',
      path: '/pages/index/index'
    };
  }
});