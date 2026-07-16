package com.finance.app.config;

import com.finance.app.entity.*;
import com.finance.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            roleRepository.save(Role.builder().name(ERole.ROLE_USER).build());
            roleRepository.save(Role.builder().name(ERole.ROLE_ADMIN).build());
        }

        if (userRepository.count() == 0) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Role User not found"));
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Role Admin not found"));

            User user = User.builder()
                    .username("user")
                    .email("user@example.com")
                    .password(encoder.encode("password"))
                    .firstName("John")
                    .lastName("Doe")
                    .roles(new HashSet<>(Set.of(userRole)))
                    .build();

            User admin = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(encoder.encode("password"))
                    .firstName("System")
                    .lastName("Admin")
                    .roles(new HashSet<>(Set.of(adminRole)))
                    .build();

            userRepository.save(user);
            userRepository.save(admin);

            seedUserData(user);
        }
    }

    private void seedUserData(User user) {
        LocalDate today = LocalDate.now();
        String currentMonthStr = today.toString().substring(0, 7); // YYYY-MM

        incomeRepository.save(Income.builder()
                .user(user)
                .source("Monthly Salary")
                .amount(BigDecimal.valueOf(5000.00))
                .date(today.minusDays(5))
                .category("Salary")
                .description("Regular corporate salary payout")
                .build());

        incomeRepository.save(Income.builder()
                .user(user)
                .source("Freelance Consulting")
                .amount(BigDecimal.valueOf(750.00))
                .date(today.minusDays(1))
                .category("Freelance")
                .description("Web development design project")
                .build());

        expenseRepository.save(Expense.builder()
                .user(user)
                .title("Apartment Rent")
                .amount(BigDecimal.valueOf(1200.00))
                .date(today.minusDays(10))
                .category("Housing")
                .description("Monthly rental payment")
                .build());

        expenseRepository.save(Expense.builder()
                .user(user)
                .title("Supermarket Groceries")
                .amount(BigDecimal.valueOf(340.50))
                .date(today.minusDays(3))
                .category("Food")
                .description("Weekly family groceries")
                .build());

        expenseRepository.save(Expense.builder()
                .user(user)
                .title("Electricity Bill")
                .amount(BigDecimal.valueOf(115.00))
                .date(today.minusDays(8))
                .category("Utilities")
                .description("Power and grid payment")
                .build());

        expenseRepository.save(Expense.builder()
                .user(user)
                .title("Steakhouse Dinner")
                .amount(BigDecimal.valueOf(85.00))
                .date(today.minusDays(2))
                .category("Entertainment")
                .description("Weekend anniversary dining")
                .build());

        budgetRepository.save(Budget.builder()
                .user(user)
                .category("Housing")
                .limitAmount(BigDecimal.valueOf(1300.00))
                .spentAmount(BigDecimal.valueOf(1200.00))
                .month(currentMonthStr)
                .build());

        budgetRepository.save(Budget.builder()
                .user(user)
                .category("Food")
                .limitAmount(BigDecimal.valueOf(500.00))
                .spentAmount(BigDecimal.valueOf(340.50))
                .month(currentMonthStr)
                .build());

        budgetRepository.save(Budget.builder()
                .user(user)
                .category("Utilities")
                .limitAmount(BigDecimal.valueOf(200.00))
                .spentAmount(BigDecimal.valueOf(115.00))
                .month(currentMonthStr)
                .build());

        savingsGoalRepository.save(SavingsGoal.builder()
                .user(user)
                .goalName("New Apple Laptop")
                .targetAmount(BigDecimal.valueOf(1800.00))
                .currentAmount(BigDecimal.valueOf(600.00))
                .targetDate(today.plusMonths(3))
                .status("IN_PROGRESS")
                .build());

        savingsGoalRepository.save(SavingsGoal.builder()
                .user(user)
                .goalName("Emergency Reserve Fund")
                .targetAmount(BigDecimal.valueOf(12000.00))
                .currentAmount(BigDecimal.valueOf(4000.00))
                .targetDate(today.plusYears(1))
                .status("IN_PROGRESS")
                .build());

        investmentRepository.save(Investment.builder()
                .user(user)
                .assetName("Vanguard S&P 500 ETF")
                .type("MUTUAL_FUNDS")
                .amountInvested(BigDecimal.valueOf(5000.00))
                .currentValue(BigDecimal.valueOf(5600.00))
                .purchaseDate(today.minusMonths(6))
                .build());

        investmentRepository.save(Investment.builder()
                .user(user)
                .assetName("Tesla, Inc. (TSLA)")
                .type("STOCKS")
                .amountInvested(BigDecimal.valueOf(2500.00))
                .currentValue(BigDecimal.valueOf(2350.00))
                .purchaseDate(today.minusMonths(3))
                .build());

        loanRepository.save(Loan.builder()
                .user(user)
                .loanName("Federal Car Loan")
                .principalAmount(BigDecimal.valueOf(18000.00))
                .interestRate(BigDecimal.valueOf(4.5))
                .tenureMonths(36)
                .startDate(today.minusMonths(12))
                .emiAmount(BigDecimal.valueOf(535.48))
                .remainingBalance(BigDecimal.valueOf(12340.00))
                .build());

        notificationRepository.save(Notification.builder()
                .user(user)
                .message("Welcome to your Antigravity Personal Finance suite! Start tracking budgets today.")
                .type("SAVINGS_REMINDER")
                .isRead(false)
                .build());
    }
}
