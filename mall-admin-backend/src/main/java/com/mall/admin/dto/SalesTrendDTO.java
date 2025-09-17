package com.mall.admin.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SalesTrendDTO {
    private String date;
    private BigDecimal amount;
}