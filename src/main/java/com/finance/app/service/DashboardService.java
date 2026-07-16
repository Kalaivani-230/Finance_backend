package com.finance.app.service;

import com.finance.app.dto.DashboardSummary;
import com.finance.app.entity.*;
import com.finance.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private UserService userService;

    public DashboardSummary getDashboardSummary() {
        User user = userService.getAuthenticatedUser();
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = LocalDate.of(now.getYear(), now.getMonth(), 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

        List<Income> allIncome = incomeRepository.findByUser(user);
        List<Expense> allExpenses = expenseRepository.findByUser(user);
        List<Investment> allInvestments = investmentRepository.findByUser(user);

        BigDecimal monthlyIncome = allIncome.stream()
                .filter(i -> !i.getDate().isBefore(startOfMonth) && !i.getDate().isAfter(endOfMonth))
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal monthlyExpenses = allExpenses.stream()
                .filter(e -> !e.getDate().isBefore(startOfMonth) && !e.getDate().isAfter(endOfMonth))
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal savings = monthlyIncome.subtract(monthlyExpenses);
        if (savings.compareTo(BigDecimal.ZERO) < 0) {
            savings = BigDecimal.ZERO;
        }

        BigDecimal totalInvestments = allInvestments.stream()
                .map(Investment::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<DashboardSummary.TransactionDto> ledger = new ArrayList<>();
        allIncome.forEach(i -> ledger.add(DashboardSummary.TransactionDto.builder()
                .id(i.getId())
                .type("INCOME")
                .description(i.getSource())
                .amount(i.getAmount())
                .date(i.getDate().toString())
                .category(i.getCategory())
                .build()));

        allExpenses.forEach(e -> ledger.add(DashboardSummary.TransactionDto.builder()
                .id(e.getId())
                .type("EXPENSE")
                .description(e.getTitle())
                .amount(e.getAmount())
                .date(e.getDate().toString())
                .category(e.getCategory())
                .build()));

        List<DashboardSummary.TransactionDto> recentTransactions = ledger.stream()
                .sorted(Comparator.comparing(DashboardSummary.TransactionDto::getDate).reversed())
                .limit(10)
                .collect(Collectors.toList());

        int healthScore = calculateHealthScore(monthlyIncome, monthlyExpenses, totalInvestments);

        return DashboardSummary.builder()
                .financialHealthScore(healthScore)
                .monthlyIncome(monthlyIncome)
                .monthlyExpenses(monthlyExpenses)
                .savings(savings)
                .investmentSummary(totalInvestments)
                .recentTransactions(recentTransactions)
                .build();
    }

    private int calculateHealthScore(BigDecimal income, BigDecimal expenses, BigDecimal investments) {
        if (income.compareTo(BigDecimal.ZERO) <= 0) {
            return 30;
        }
        BigDecimal savings = income.subtract(expenses);
        if (savings.compareTo(BigDecimal.ZERO) <= 0) {
            return 20;
        }

        double savingsRatio = savings.doubleValue() / income.doubleValue();
        int score = 40 + (int) (savingsRatio * 40.0);

        if (investments.compareTo(BigDecimal.ZERO) > 0) {
            score += 15;
        }

        return Math.max(10, Math.min(100, score));
    }
}
