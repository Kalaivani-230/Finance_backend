package com.finance.app.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class ExpenseDto {
    private Long id;
    private String title;
    private BigDecimal amount;
    private String date; // YYYY-MM-DD
    private String category;
    private String description;
}
