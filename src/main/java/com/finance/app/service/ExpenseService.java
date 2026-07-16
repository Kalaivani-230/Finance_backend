package com.finance.app.service;

import com.finance.app.dto.ExpenseDto;
import com.finance.app.entity.Budget;
import com.finance.app.entity.Expense;
import com.finance.app.entity.Notification;
import com.finance.app.entity.User;
import com.finance.app.exception.ResourceNotFoundException;
import com.finance.app.repository.BudgetRepository;
import com.finance.app.repository.ExpenseRepository;
import com.finance.app.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;

    public List<ExpenseDto> getAllExpenses() {
        User user = userService.getAuthenticatedUser();
        return expenseRepository.findByUserOrderByDateDesc(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ExpenseDto> getExpensesByDateRange(String startDate, String endDate) {
        User user = userService.getAuthenticatedUser();
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return expenseRepository.findByUserAndDateBetweenOrderByDateDesc(user, start, end).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ExpenseDto getExpenseById(Long id) {
        User user = userService.getAuthenticatedUser();
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense entry not found with id: " + id));
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access Denied");
        }
        return convertToDto(expense);
    }

    public ExpenseDto createExpense(ExpenseDto dto) {
        User user = userService.getAuthenticatedUser();
        Expense expense = Expense.builder()
                .user(user)
                .title(dto.getTitle())
                .amount(dto.getAmount())
                .date(LocalDate.parse(dto.getDate()))
                .category(dto.getCategory())
                .description(dto.getDescription())
                .build();

        Expense savedExpense = expenseRepository.save(expense);
        updateBudgetSpentAmount(user, savedExpense.getCategory(), savedExpense.getDate());
        return convertToDto(savedExpense);
    }

    public ExpenseDto updateExpense(Long id, ExpenseDto dto) {
        User user = userService.getAuthenticatedUser();
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense entry not found with id: " + id));
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access Denied");
        }

        String oldCategory = expense.getCategory();
        LocalDate oldDate = expense.getDate();

        expense.setTitle(dto.getTitle());
        expense.setAmount(dto.getAmount());
        expense.setDate(LocalDate.parse(dto.getDate()));
        expense.setCategory(dto.getCategory());
        expense.setDescription(dto.getDescription());

        Expense savedExpense = expenseRepository.save(expense);

        // Update old and new budgets
        updateBudgetSpentAmount(user, oldCategory, oldDate);
        if (!oldCategory.equals(savedExpense.getCategory()) || !oldDate.getMonth().equals(savedExpense.getDate().getMonth())) {
            updateBudgetSpentAmount(user, savedExpense.getCategory(), savedExpense.getDate());
        }

        return convertToDto(savedExpense);
    }

    public void deleteExpense(Long id) {
        User user = userService.getAuthenticatedUser();
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense entry not found with id: " + id));
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access Denied");
        }
        expenseRepository.delete(expense);
        updateBudgetSpentAmount(user, expense.getCategory(), expense.getDate());
    }

    private void updateBudgetSpentAmount(User user, String category, LocalDate date) {
        String month = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        Optional<Budget> budgetOpt = budgetRepository.findByUserAndCategoryAndMonth(user, category, month);

        if (budgetOpt.isPresent()) {
            Budget budget = budgetOpt.get();
            LocalDate start = LocalDate.of(date.getYear(), date.getMonth(), 1);
            LocalDate end = start.plusMonths(1).minusDays(1);

            List<Expense> monthlyExpenses = expenseRepository.findByUserAndDateBetweenOrderByDateDesc(user, start, end);
            BigDecimal totalSpent = monthlyExpenses.stream()
                    .filter(exp -> exp.getCategory().equalsIgnoreCase(category))
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            budget.setSpentAmount(totalSpent);
            budgetRepository.save(budget);

            if (totalSpent.compareTo(budget.getLimitAmount()) > 0) {
                String notificationMsg = String.format("Budget Exceeded! You have spent $%s on %s which exceeds your monthly limit of $%s.",
                        totalSpent, category, budget.getLimitAmount());
                Notification notification = Notification.builder()
                        .user(user)
                        .message(notificationMsg)
                        .type("BUDGET_EXCEEDED")
                        .isRead(false)
                        .build();
                notificationRepository.save(notification);
            }
        }
    }

    private ExpenseDto convertToDto(Expense expense) {
        ExpenseDto dto = new ExpenseDto();
        dto.setId(expense.getId());
        dto.setTitle(expense.getTitle());
        dto.setAmount(expense.getAmount());
        dto.setDate(expense.getDate().toString());
        dto.setCategory(expense.getCategory());
        dto.setDescription(expense.getDescription());
        return dto;
    }
}
