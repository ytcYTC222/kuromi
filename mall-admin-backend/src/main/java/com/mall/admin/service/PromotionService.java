package com.mall.admin.service;

import com.mall.admin.common.PageResult;
import com.mall.admin.dto.request.PromotionRequest;
import com.mall.admin.dto.response.PromotionResponse;

/**
 * 促销活动服务接口
 * 
 * @author Admin
 * @since 2024-09-11
 */
public interface PromotionService {

    /**
     * 获取促销活动列表（分页）
     *
     * @param page 页码
     * @param size 每页大小
     * @param promotionName 促销活动名称
     * @param promotionType 促销类型
     * @param isActive 是否启用
     * @return 分页结果
     */
    PageResult<PromotionResponse> getPromotionList(Integer page, Integer size, String promotionName, Integer promotionType, Integer isActive);

    /**
     * 根据ID获取促销活动详情
     *
     * @param promotionId 促销活动ID
     * @return 促销活动详情
     */
    PromotionResponse getPromotionById(Integer promotionId);

    /**
     * 创建促销活动
     *
     * @param request 促销活动请求数据
     * @return 促销活动ID
     */
    Integer createPromotion(PromotionRequest request);

    /**
     * 更新促销活动
     *
     * @param promotionId 促销活动ID
     * @param request 促销活动请求数据
     */
    void updatePromotion(Integer promotionId, PromotionRequest request);

    /**
     * 删除促销活动
     *
     * @param promotionId 促销活动ID
     */
    void deletePromotion(Integer promotionId);

    /**
     * 更新促销活动状态
     *
     * @param promotionId 促销活动ID
     * @param isActive 是否启用
     */
    void updatePromotionStatus(Integer promotionId, Integer isActive);
}