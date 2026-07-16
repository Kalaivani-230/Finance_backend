package com.finance.app.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class LoanDto {
    private Long id;
    private String loanName;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private String startDate; // YYYY-MM-DD
    private BigDecimal emiAmount;
    private BigDecimal remainingBalance;
}
