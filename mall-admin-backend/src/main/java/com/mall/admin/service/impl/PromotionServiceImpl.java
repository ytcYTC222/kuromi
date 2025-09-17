package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.admin.common.PageResult;
import com.mall.admin.dto.request.PromotionRequest;
import com.mall.admin.dto.response.PromotionResponse;
import com.mall.admin.entity.Promotion;
import com.mall.admin.mapper.PromotionMapper;
import com.mall.admin.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 促销活动服务实现类
 * 
 * @author Admin
 * @since 2024-09-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionMapper promotionMapper;

    @Override
    public PageResult<PromotionResponse> getPromotionList(Integer page, Integer size, String promotionName, Integer promotionType, Integer isActive) {
        log.info("获取促销活动列表，page: {}, size: {}, promotionName: {}, promotionType: {}, isActive: {}", 
                page, size, promotionName, promotionType, isActive);

        // 创建分页对象
        Page<Promotion> pageObj = new Page<>(page, size);

        // 构建查询条件
        LambdaQueryWrapper<Promotion> queryWrapper = new LambdaQueryWrapper<>();

        // 促销活动名称模糊查询
        if (StringUtils.hasText(promotionName)) {
            queryWrapper.like(Promotion::getPromotionName, promotionName);
        }

        // 促销类型精确查询
        if (promotionType != null) {
            queryWrapper.eq(Promotion::getPromotionType, promotionType);
        }

        // 启用状态精确查询
        if (isActive != null) {
            queryWrapper.eq(Promotion::getIsActive, isActive);
        }

        // 按排序权重和创建时间排序
        queryWrapper.orderByDesc(Promotion::getSortOrder)
                   .orderByDesc(Promotion::getCreateTime);

        // 执行分页查询
        Page<Promotion> resultPage = promotionMapper.selectPage(pageObj, queryWrapper);

        // 转换为响应DTO
        List<PromotionResponse> responseList = resultPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new PageResult<>(responseList, resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize());
    }

    @Override
    public PromotionResponse getPromotionById(Integer promotionId) {
        log.info("获取促销活动详情，promotionId: {}", promotionId);

        // 参数验证
        if (promotionId == null || promotionId <= 0) {
            throw new IllegalArgumentException("促销活动ID不能为空");
        }

        // 查询促销活动
        Promotion promotion = promotionMapper.selectById(promotionId);
        if (promotion == null) {
            throw new RuntimeException("促销活动不存在");
        }

        return convertToResponse(promotion);
    }

    @Override
    @Transactional
    public Integer createPromotion(PromotionRequest request) {
        log.info("创建促销活动，request: {}", request);

        // 验证请求数据
        validatePromotionRequest(request);

        // 转换为实体
        Promotion promotion = convertToEntity(request);

        // 保存到数据库
        int result = promotionMapper.insert(promotion);
        if (result <= 0) {
            throw new RuntimeException("创建促销活动失败");
        }

        log.info("促销活动创建成功，promotionId: {}", promotion.getPromotionId());
        return promotion.getPromotionId();
    }

    @Override
    @Transactional
    public void updatePromotion(Integer promotionId, PromotionRequest request) {
        log.info("更新促销活动，promotionId: {}, request: {}", promotionId, request);

        // 参数验证
        if (promotionId == null || promotionId <= 0) {
            throw new IllegalArgumentException("促销活动ID不能为空");
        }

        // 检查促销活动是否存在
        Promotion existingPromotion = promotionMapper.selectById(promotionId);
        if (existingPromotion == null) {
            throw new RuntimeException("促销活动不存在");
        }

        // 验证请求数据
        validatePromotionRequest(request);

        // 转换为实体
        Promotion promotion = convertToEntity(request);
        promotion.setPromotionId(promotionId);

        // 更新到数据库
        int result = promotionMapper.updateById(promotion);
        if (result <= 0) {
            throw new RuntimeException("更新促销活动失败");
        }

        log.info("促销活动更新成功，promotionId: {}", promotionId);
    }

    @Override
    @Transactional
    public void deletePromotion(Integer promotionId) {
        log.info("删除促销活动，promotionId: {}", promotionId);

        // 参数验证
        if (promotionId == null || promotionId <= 0) {
            throw new IllegalArgumentException("促销活动ID不能为空");
        }

        // 检查促销活动是否存在
        Promotion promotion = promotionMapper.selectById(promotionId);
        if (promotion == null) {
            throw new RuntimeException("促销活动不存在");
        }

        // 检查促销活动状态，进行中的促销活动不能删除
        if (promotion.getIsActive() == 1) {
            LocalDateTime now = LocalDateTime.now();
            if (promotion.getStartTime().isBefore(now) && promotion.getEndTime().isAfter(now)) {
                throw new RuntimeException("进行中的促销活动不能删除，请先禁用促销活动");
            }
        }

        // 删除促销活动
        int result = promotionMapper.deleteById(promotionId);
        if (result <= 0) {
            throw new RuntimeException("删除促销活动失败");
        }

        log.info("促销活动删除成功，promotionId: {}", promotionId);
    }

    @Override
    @Transactional
    public void updatePromotionStatus(Integer promotionId, Integer isActive) {
        log.info("更新促销活动状态，promotionId: {}, isActive: {}", promotionId, isActive);

        // 参数验证
        if (promotionId == null || promotionId <= 0) {
            throw new IllegalArgumentException("促销活动ID不能为空");
        }
        if (isActive == null || (isActive != 0 && isActive != 1)) {
            throw new IllegalArgumentException("启用状态无效");
        }

        // 检查促销活动是否存在
        Promotion promotion = promotionMapper.selectById(promotionId);
        if (promotion == null) {
            throw new RuntimeException("促销活动不存在");
        }

        // 更新状态
        Promotion updatePromotion = new Promotion();
        updatePromotion.setPromotionId(promotionId);
        updatePromotion.setIsActive(isActive);

        int result = promotionMapper.updateById(updatePromotion);
        if (result <= 0) {
            throw new RuntimeException("更新促销活动状态失败");
        }

        log.info("促销活动状态更新成功，promotionId: {}, isActive: {}", promotionId, isActive);
    }

    /**
     * 验证促销活动请求数据
     */
    private void validatePromotionRequest(PromotionRequest request) {
        // 验证时间
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new IllegalArgumentException("开始时间和结束时间不能为空");
        }
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException("结束时间必须晚于开始时间");
        }

        // 验证促销类型和目标ID的关系
        if (request.getPromotionType() == 1 || request.getPromotionType() == 2) {
            if (request.getTargetId() == null || request.getTargetId() <= 0) {
                throw new IllegalArgumentException("商品促销或分类促销必须指定目标ID");
            }
        }

        // 验证折扣值的合理性
        if (request.getDiscountType() == 1) {
            // 百分比折扣，折扣值应该在0-100之间
            if (request.getDiscountValue().doubleValue() <= 0 || request.getDiscountValue().doubleValue() >= 100) {
                throw new IllegalArgumentException("百分比折扣值应该在0-100之间");
            }
        }
    }

    /**
     * 转换为响应DTO
     */
    private PromotionResponse convertToResponse(Promotion promotion) {
        PromotionResponse response = new PromotionResponse();
        BeanUtils.copyProperties(promotion, response);

        // 设置名称字段
        response.setPromotionTypeName(getPromotionTypeName(promotion.getPromotionType()));
        response.setDiscountTypeName(getDiscountTypeName(promotion.getDiscountType()));
        response.setStatusName(promotion.getIsActive() == 1 ? "启用" : "禁用");

        return response;
    }

    /**
     * 转换为实体
     */
    private Promotion convertToEntity(PromotionRequest request) {
        Promotion promotion = new Promotion();
        BeanUtils.copyProperties(request, promotion);
        return promotion;
    }

    /**
     * 获取促销类型名称
     */
    private String getPromotionTypeName(Integer promotionType) {
        switch (promotionType) {
            case 1: return "商品促销";
            case 2: return "分类促销";
            case 3: return "全场促销";
            default: return "未知";
        }
    }

    /**
     * 获取折扣类型名称
     */
    private String getDiscountTypeName(Integer discountType) {
        switch (discountType) {
            case 1: return "百分比折扣";
            case 2: return "固定金额减免";
            default: return "未知";
        }
    }
}