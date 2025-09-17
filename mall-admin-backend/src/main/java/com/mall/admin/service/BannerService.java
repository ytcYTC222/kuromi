package com.mall.admin.service;

import com.mall.admin.common.PageResult;
import com.mall.admin.dto.request.BannerRequest;
import com.mall.admin.dto.response.BannerResponse;

/**
 * 轮播图服务接口
 * 
 * @author Admin
 * @since 2024-09-11
 */
public interface BannerService {

    /**
     * 获取轮播图列表
     * 
     * @param page 页码
     * @param size 每页大小
     * @param title 标题（模糊查询）
     * @param linkType 链接类型
     * @param isActive 是否启用
     * @return 分页结果
     */
    PageResult<BannerResponse> getBannerList(Integer page, Integer size, String title, Integer linkType, Integer isActive);

    /**
     * 根据ID获取轮播图详情
     * 
     * @param bannerId 轮播图ID
     * @return 轮播图详情
     */
    BannerResponse getBannerById(Integer bannerId);

    /**
     * 创建轮播图
     * 
     * @param request 轮播图请求数据
     * @return 轮播图ID
     */
    Integer createBanner(BannerRequest request);

    /**
     * 更新轮播图
     * 
     * @param bannerId 轮播图ID
     * @param request 轮播图请求数据
     */
    void updateBanner(Integer bannerId, BannerRequest request);

    /**
     * 删除轮播图
     * 
     * @param bannerId 轮播图ID
     */
    void deleteBanner(Integer bannerId);

    /**
     * 更新轮播图状态
     * 
     * @param bannerId 轮播图ID
     * @param isActive 是否启用
     */
    void updateBannerStatus(Integer bannerId, Integer isActive);
}