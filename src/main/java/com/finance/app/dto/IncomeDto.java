package com.finance.app.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class IncomeDto {
    private Long id;
    private String source;
    private BigDecimal amount;
    private String date; // YYYY-MM-DD
    private String description;
    private String category;
}
