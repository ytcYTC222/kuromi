package com.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.admin.entity.Promotion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 促销活动Mapper接口
 * 
 * @author Admin
 * @since 2024-09-11
 */
@Mapper
public interface PromotionMapper extends BaseMapper<Promotion> {

    /**
     * 根据商品ID查询有效的促销活动
     * 
     * @param productId 商品ID
     * @param currentTime 当前时间
     * @return 促销活动列表
     */
    @Select("SELECT * FROM promotions WHERE promotion_type = 1 AND target_id = #{productId} " +
            "AND is_active = 1 AND start_time <= #{currentTime} AND end_time >= #{currentTime} " +
            "ORDER BY sort_order DESC")
    List<Promotion> findActivePromotionsByProductId(@Param("productId") Integer productId, 
                                                   @Param("currentTime") LocalDateTime currentTime);

    /**
     * 根据分类ID查询有效的促销活动
     * 
     * @param categoryId 分类ID
     * @param currentTime 当前时间
     * @return 促销活动列表
     */
    @Select("SELECT * FROM promotions WHERE promotion_type = 2 AND target_id = #{categoryId} " +
            "AND is_active = 1 AND start_time <= #{currentTime} AND end_time >= #{currentTime} " +
            "ORDER BY sort_order DESC")
    List<Promotion> findActivePromotionsByCategoryId(@Param("categoryId") Integer categoryId, 
                                                    @Param("currentTime") LocalDateTime currentTime);

    /**
     * 查询有效的全场促销活动
     * 
     * @param currentTime 当前时间
     * @return 促销活动列表
     */
    @Select("SELECT * FROM promotions WHERE promotion_type = 3 " +
            "AND is_active = 1 AND start_time <= #{currentTime} AND end_time >= #{currentTime} " +
            "ORDER BY sort_order DESC")
    List<Promotion> findActiveGlobalPromotions(@Param("currentTime") LocalDateTime currentTime);

    /**
     * 查询即将开始的促销活动（未来24小时内）
     * 
     * @param currentTime 当前时间
     * @param endTime 结束时间（24小时后）
     * @return 促销活动列表
     */
    @Select("SELECT * FROM promotions WHERE is_active = 1 " +
            "AND start_time > #{currentTime} AND start_time <= #{endTime} " +
            "ORDER BY start_time ASC")
    List<Promotion> findUpcomingPromotions(@Param("currentTime") LocalDateTime currentTime, 
                                          @Param("endTime") LocalDateTime endTime);

    /**
     * 查询即将结束的促销活动（未来24小时内结束）
     * 
     * @param currentTime 当前时间
     * @param endTime 结束时间（24小时后）
     * @return 促销活动列表
     */
    @Select("SELECT * FROM promotions WHERE is_active = 1 " +
            "AND start_time <= #{currentTime} AND end_time > #{currentTime} AND end_time <= #{endTime} " +
            "ORDER BY end_time ASC")
    List<Promotion> findExpiringPromotions(@Param("currentTime") LocalDateTime currentTime, 
                                          @Param("endTime") LocalDateTime endTime);

    /**
     * 批量更新促销活动状态（用于定时任务自动启用/禁用）
     * 
     * @param isActive 状态值
     * @param currentTime 当前时间
     * @return 更新记录数
     */
    @Update("UPDATE promotions SET is_active = #{isActive} " +
            "WHERE is_active != #{isActive} AND start_time <= #{currentTime} AND end_time >= #{currentTime}")
    int batchUpdatePromotionStatus(@Param("isActive") Integer isActive, 
                                  @Param("currentTime") LocalDateTime currentTime);

    /**
     * 统计各类型促销活动数量
     * 
     * @return 统计结果（promotion_type, count）
     */
    @Select("SELECT promotion_type, COUNT(*) as count FROM promotions " +
            "WHERE is_active = 1 GROUP BY promotion_type")
    List<java.util.Map<String, Object>> countPromotionsByType();

    /**
     * 查询指定时间段内的促销活动（用于冲突检测）
     * 
     * @param promotionType 促销类型
     * @param targetId 目标ID（可为null）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeId 排除的促销活动ID（用于更新时排除自己）
     * @return 促销活动列表
     */
    @Select("<script>" +
            "SELECT * FROM promotions WHERE promotion_type = #{promotionType} " +
            "<if test='targetId != null'>AND target_id = #{targetId}</if> " +
            "AND is_active = 1 " +
            "AND ((start_time &lt;= #{startTime} AND end_time &gt;= #{startTime}) " +
            "OR (start_time &lt;= #{endTime} AND end_time &gt;= #{endTime}) " +
            "OR (start_time &gt;= #{startTime} AND end_time &lt;= #{endTime})) " +
            "<if test='excludeId != null'>AND promotion_id != #{excludeId}</if> " +
            "ORDER BY start_time ASC" +
            "</script>")
    List<Promotion> findConflictingPromotions(@Param("promotionType") Integer promotionType,
                                             @Param("targetId") Integer targetId,
                                             @Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime,
                                             @Param("excludeId") Integer excludeId);

}