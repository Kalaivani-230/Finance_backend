package com.finance.app.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class SavingsGoalDto {
    private Long id;
    private String goalName;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private String targetDate; // YYYY-MM-DD
    private String status; // IN_PROGRESS, COMPLETED
}
