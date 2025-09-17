package com.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.admin.entity.Banner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 轮播图Mapper接口
 * 
 * @author Admin
 * @since 2024-09-11
 */
@Mapper
public interface BannerMapper extends BaseMapper<Banner> {

    /**
     * 查询有效的轮播图列表（用于前端展示）
     * 
     * @param currentTime 当前时间
     * @return 轮播图列表
     */
    @Select("SELECT * FROM banners WHERE is_active = 1 " +
            "AND (start_time IS NULL OR start_time <= #{currentTime}) " +
            "AND (end_time IS NULL OR end_time >= #{currentTime}) " +
            "ORDER BY sort_order ASC, banner_id ASC")
    List<Banner> findActiveBanners(@Param("currentTime") LocalDateTime currentTime);

    /**
     * 查询即将过期的轮播图（未来24小时内过期）
     * 
     * @param currentTime 当前时间
     * @param endTime 结束时间（24小时后）
     * @return 轮播图列表
     */
    @Select("SELECT * FROM banners WHERE is_active = 1 " +
            "AND end_time IS NOT NULL " +
            "AND end_time > #{currentTime} AND end_time <= #{endTime} " +
            "ORDER BY end_time ASC")
    List<Banner> findExpiringBanners(@Param("currentTime") LocalDateTime currentTime, 
                                    @Param("endTime") LocalDateTime endTime);

    /**
     * 查询指定链接类型的轮播图
     * 
     * @param linkType 链接类型
     * @return 轮播图列表
     */
    @Select("SELECT * FROM banners WHERE link_type = #{linkType} " +
            "AND is_active = 1 " +
            "ORDER BY sort_order ASC, banner_id ASC")
    List<Banner> findBannersByLinkType(@Param("linkType") Integer linkType);

    /**
     * 查询指定链接值的轮播图
     * 
     * @param linkType 链接类型
     * @param linkValue 链接值
     * @return 轮播图列表
     */
    @Select("SELECT * FROM banners WHERE link_type = #{linkType} " +
            "AND link_value = #{linkValue} " +
            "AND is_active = 1 " +
            "ORDER BY sort_order ASC, banner_id ASC")
    List<Banner> findBannersByLinkValue(@Param("linkType") Integer linkType,
                                       @Param("linkValue") String linkValue);

    /**
     * 批量更新轮播图状态
     * 
     * @param isActive 状态值
     * @param currentTime 当前时间
     * @return 更新记录数
     */
    @Update("UPDATE banners SET is_active = #{isActive} " +
            "WHERE is_active != #{isActive} " +
            "AND ((start_time IS NOT NULL AND start_time > #{currentTime}) " +
            "OR (end_time IS NOT NULL AND end_time < #{currentTime}))")
    int batchUpdateBannerStatus(@Param("isActive") Integer isActive, 
                               @Param("currentTime") LocalDateTime currentTime);

    /**
     * 统计各链接类型的轮播图数量
     * 
     * @return 统计结果（link_type, count）
     */
    @Select("SELECT link_type, COUNT(*) as count FROM banners " +
            "WHERE is_active = 1 GROUP BY link_type")
    List<java.util.Map<String, Object>> countBannersByLinkType();

    /**
     * 获取最大排序值
     * 
     * @return 最大排序值
     */
    @Select("SELECT COALESCE(MAX(sort_order), 0) FROM banners")
    Integer getMaxSortOrder();
}