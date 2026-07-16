package com.finance.app.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class DashboardSummary {
    private int financialHealthScore;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpenses;
    private BigDecimal savings;
    private BigDecimal investmentSummary;
    private List<TransactionDto> recentTransactions;

    @Getter
    @Setter
    @Builder
    public static class TransactionDto {
        private Long id;
        private String type; // INCOME or EXPENSE
        private String description;
        private BigDecimal amount;
        private String date;
        private String category;
    }
}
