package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.admin.common.PageResult;
import com.mall.admin.dto.request.BannerRequest;
import com.mall.admin.dto.response.BannerResponse;
import com.mall.admin.entity.Banner;
import com.mall.admin.mapper.BannerMapper;
import com.mall.admin.service.BannerService;
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
 * 轮播图服务实现类
 * 
 * @author Admin
 * @since 2024-09-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerMapper bannerMapper;

    @Override
    public PageResult<BannerResponse> getBannerList(Integer page, Integer size, String title, Integer linkType, Integer isActive) {
        log.info("获取轮播图列表，page: {}, size: {}, title: {}, linkType: {}, isActive: {}", 
                page, size, title, linkType, isActive);

        // 创建分页对象
        Page<Banner> pageObj = new Page<>(page, size);

        // 构建查询条件
        LambdaQueryWrapper<Banner> queryWrapper = new LambdaQueryWrapper<>();

        // 标题模糊查询
        if (StringUtils.hasText(title)) {
            queryWrapper.like(Banner::getTitle, title);
        }

        // 链接类型精确查询
        if (linkType != null) {
            queryWrapper.eq(Banner::getLinkType, linkType);
        }

        // 启用状态精确查询
        if (isActive != null) {
            queryWrapper.eq(Banner::getIsActive, isActive);
        }

        // 按排序权重和创建时间排序
        queryWrapper.orderByAsc(Banner::getSortOrder)
                   .orderByDesc(Banner::getCreateTime);

        // 执行分页查询
        Page<Banner> resultPage = bannerMapper.selectPage(pageObj, queryWrapper);

        // 转换为响应DTO
        List<BannerResponse> responseList = resultPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new PageResult<>(responseList, resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize());
    }

    @Override
    public BannerResponse getBannerById(Integer bannerId) {
        log.info("获取轮播图详情，bannerId: {}", bannerId);

        // 参数验证
        if (bannerId == null || bannerId <= 0) {
            throw new IllegalArgumentException("轮播图ID不能为空");
        }

        // 查询轮播图
        Banner banner = bannerMapper.selectById(bannerId);
        if (banner == null) {
            throw new RuntimeException("轮播图不存在");
        }

        return convertToResponse(banner);
    }

    @Override
    @Transactional
    public Integer createBanner(BannerRequest request) {
        log.info("创建轮播图，request: {}", request);

        // 验证请求数据
        validateBannerRequest(request);

        // 转换为实体
        Banner banner = convertToEntity(request);

        // 设置默认排序值
        if (banner.getSortOrder() == null) {
            Integer maxSortOrder = bannerMapper.getMaxSortOrder();
            banner.setSortOrder(maxSortOrder + 1);
        }

        // 保存到数据库
        int result = bannerMapper.insert(banner);
        if (result <= 0) {
            throw new RuntimeException("创建轮播图失败");
        }

        log.info("轮播图创建成功，bannerId: {}", banner.getBannerId());
        return banner.getBannerId();
    }

    @Override
    @Transactional
    public void updateBanner(Integer bannerId, BannerRequest request) {
        log.info("更新轮播图，bannerId: {}, request: {}", bannerId, request);

        // 参数验证
        if (bannerId == null || bannerId <= 0) {
            throw new IllegalArgumentException("轮播图ID不能为空");
        }

        // 检查轮播图是否存在
        Banner existingBanner = bannerMapper.selectById(bannerId);
        if (existingBanner == null) {
            throw new RuntimeException("轮播图不存在");
        }

        // 验证请求数据
        validateBannerRequest(request);

        // 转换为实体
        Banner banner = convertToEntity(request);
        banner.setBannerId(bannerId);

        // 更新到数据库
        int result = bannerMapper.updateById(banner);
        if (result <= 0) {
            throw new RuntimeException("更新轮播图失败");
        }

        log.info("轮播图更新成功，bannerId: {}", bannerId);
    }

    @Override
    @Transactional
    public void deleteBanner(Integer bannerId) {
        log.info("删除轮播图，bannerId: {}", bannerId);

        // 参数验证
        if (bannerId == null || bannerId <= 0) {
            throw new IllegalArgumentException("轮播图ID不能为空");
        }

        // 检查轮播图是否存在
        Banner banner = bannerMapper.selectById(bannerId);
        if (banner == null) {
            throw new RuntimeException("轮播图不存在");
        }

        // 删除轮播图
        int result = bannerMapper.deleteById(bannerId);
        if (result <= 0) {
            throw new RuntimeException("删除轮播图失败");
        }

        log.info("轮播图删除成功，bannerId: {}", bannerId);
    }

    @Override
    @Transactional
    public void updateBannerStatus(Integer bannerId, Integer isActive) {
        log.info("更新轮播图状态，bannerId: {}, isActive: {}", bannerId, isActive);

        // 参数验证
        if (bannerId == null || bannerId <= 0) {
            throw new IllegalArgumentException("轮播图ID不能为空");
        }
        if (isActive == null || (isActive != 0 && isActive != 1)) {
            throw new IllegalArgumentException("启用状态无效");
        }

        // 检查轮播图是否存在
        Banner banner = bannerMapper.selectById(bannerId);
        if (banner == null) {
            throw new RuntimeException("轮播图不存在");
        }

        // 更新状态
        Banner updateBanner = new Banner();
        updateBanner.setBannerId(bannerId);
        updateBanner.setIsActive(isActive);

        int result = bannerMapper.updateById(updateBanner);
        if (result <= 0) {
            throw new RuntimeException("更新轮播图状态失败");
        }

        log.info("轮播图状态更新成功，bannerId: {}, isActive: {}", bannerId, isActive);
    }

    /**
     * 验证轮播图请求数据
     */
    private void validateBannerRequest(BannerRequest request) {
        // 验证时间
        if (request.getStartTime() != null && request.getEndTime() != null) {
            if (!request.getEndTime().isAfter(request.getStartTime())) {
                throw new IllegalArgumentException("结束时间必须晚于开始时间");
            }
        }

        // 验证链接类型
        if (request.getLinkType() < 1 || request.getLinkType() > 4) {
            throw new IllegalArgumentException("链接类型值必须在1-4之间");
        }
    }

    /**
     * 转换为响应DTO
     */
    private BannerResponse convertToResponse(Banner banner) {
        BannerResponse response = new BannerResponse();
        BeanUtils.copyProperties(banner, response);

        // 设置名称字段
        response.setLinkTypeName(getLinkTypeName(banner.getLinkType()));
        response.setStatusName(banner.getIsActive() == 1 ? "启用" : "禁用");

        return response;
    }

    /**
     * 转换为实体
     */
    private Banner convertToEntity(BannerRequest request) {
        Banner banner = new Banner();
        BeanUtils.copyProperties(request, banner);
        return banner;
    }

    /**
     * 获取链接类型名称
     */
    private String getLinkTypeName(Integer linkType) {
        switch (linkType) {
            case 1: return "商品";
            case 2: return "分类";
            case 3: return "文章";
            case 4: return "外链";
            default: return "未知";
        }
    }
}