package com.mall.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 分页查询基础DTO
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Data
@Schema(description = "分页查询参数")
public class PageQuery {
    
    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;
    
    @Schema(description = "每页大小", example = "10")
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能大于100")
    private Integer pageSize = 10;
    
    @Schema(description = "排序字段")
    private String orderBy;
    
    @Schema(description = "排序方向", example = "asc")
    private String orderDirection = "desc";
    
    // 兼容前端传递的参数名
    @Schema(hidden = true)
    public void setPage(Integer page) {
        if (page != null) {
            this.pageNum = page;
        }
    }
    
    @Schema(hidden = true)
    public void setSize(Integer size) {
        if (size != null) {
            this.pageSize = size;
        }
    }
    
    @Schema(hidden = true)
    public Integer getPage() {
        return this.pageNum;
    }
    
    @Schema(hidden = true)
    public Integer getSize() {
        return this.pageSize;
    }
}
