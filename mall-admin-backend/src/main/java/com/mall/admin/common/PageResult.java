package com.mall.admin.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分页响应结果
 *
 * @param <T> 数据类型
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Data
@Schema(description = "分页响应结果")
public class PageResult<T> {
    
    @Schema(description = "数据列表")
    private List<T> records;
    
    @Schema(description = "总记录数")
    private Long total;
    
    @Schema(description = "当前页码")
    private Long current;
    
    @Schema(description = "每页大小")
    private Long size;
    
    @Schema(description = "总页码")
    private Long pages;
    
    public PageResult() {}
    
    public PageResult(List<T> records, Long total, Long current, Long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (total + size - 1) / size;
    }
    
    /**
     * 从MyBatis-Plus的IPage转换
     */
    public static <T> PageResult<T> of(IPage<T> page) {
        return new PageResult<>(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize()
        );
    }
    
    /**
     * 空分页结�?     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(List.of(), 0L, 1L, 10L);
    }
}
