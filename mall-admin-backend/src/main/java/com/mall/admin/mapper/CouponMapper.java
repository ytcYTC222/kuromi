package com.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.admin.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠卷Mapper接口
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {
}