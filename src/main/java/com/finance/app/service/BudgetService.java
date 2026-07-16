package com.finance.app.service;

import com.finance.app.dto.BudgetDto;
import com.finance.app.entity.Budget;
import com.finance.app.entity.Expense;
import com.finance.app.entity.User;
import com.finance.app.exception.ResourceNotFoundException;
import com.finance.app.repository.BudgetRepository;
import com.finance.app.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserService userService;

    public List<BudgetDto> getAllBudgets() {
        User user = userService.getAuthenticatedUser();
        return budgetRepository.findByUser(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BudgetDto> getBudgetsByMonth(String month) {
        User user = userService.getAuthenticatedUser();
        return budgetRepository.findByUserAndMonth(user, month).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public BudgetDto createBudget(BudgetDto dto) {
        User user = userService.getAuthenticatedUser();
        BigDecimal spentAmount = calculateSpentForCategoryAndMonth(user, dto.getCategory(), dto.getMonth());

        Budget budget = Budget.builder()
                .user(user)
                .category(dto.getCategory())
                .limitAmount(dto.getLimitAmount())
                .spentAmount(spentAmount)
                .month(dto.getMonth())
                .build();

        return convertToDto(budgetRepository.save(budget));
    }

    public BudgetDto updateBudget(Long id, BudgetDto dto) {
        User user = userService.getAuthenticatedUser();
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget entry not found with id: " + id));
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access Denied");
        }

        budget.setLimitAmount(dto.getLimitAmount());
        budget.setCategory(dto.getCategory());
        budget.setMonth(dto.getMonth());
        budget.setSpentAmount(calculateSpentForCategoryAndMonth(user, dto.getCategory(), dto.getMonth()));

        return convertToDto(budgetRepository.save(budget));
    }

    public void deleteBudget(Long id) {
        User user = userService.getAuthenticatedUser();
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget entry not found with id: " + id));
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access Denied");
        }
        budgetRepository.delete(budget);
    }

    private BigDecimal calculateSpentForCategoryAndMonth(User user, String category, String month) {
        try {
            String[] parts = month.split("-");
            int year = Integer.parseInt(parts[0]);
            int monthVal = Integer.parseInt(parts[1]);

            LocalDate start = LocalDate.of(year, monthVal, 1);
            LocalDate end = start.plusMonths(1).minusDays(1);

            List<Expense> monthlyExpenses = expenseRepository.findByUserAndDateBetweenOrderByDateDesc(user, start, end);
            return monthlyExpenses.stream()
                    .filter(exp -> exp.getCategory().equalsIgnoreCase(category))
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private BudgetDto convertToDto(Budget budget) {
        BudgetDto dto = new BudgetDto();
        dto.setId(budget.getId());
        dto.setCategory(budget.getCategory());
        dto.setLimitAmount(budget.getLimitAmount());
        dto.setSpentAmount(budget.getSpentAmount());
        dto.setMonth(budget.getMonth());
        return dto;
    }
}
