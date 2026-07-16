-- Seed Roles
INSERT INTO roles (id, name) VALUES (1, 'ROLE_USER');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_ADMIN');

-- Seed Users (BCrypt hashed 'password')
INSERT INTO users (id, username, email, password, first_name, last_name, created_at)
VALUES (1, 'user', 'user@example.com', 'user@123', 'John', 'Doe', CURRENT_TIMESTAMP);

INSERT INTO users (id, username, email, password, first_name, last_name, created_at)
VALUES (2, 'admin', 'admin@example.com', 'user@456', 'System', 'Admin', CURRENT_TIMESTAMP);

-- Link Roles
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2);

-- Sample Income
INSERT INTO incomes (id, source, amount, date, category, description, user_id)
VALUES (1, 'Monthly Salary', 5000.00, '2026-07-01', 'Salary', 'Regular payroll', 1);

-- Sample Expenses
INSERT INTO expenses (id, title, amount, date, category, description, user_id)
VALUES (1, 'Apartment Rent', 1200.00, '2026-07-02', 'Housing', 'Monthly rent payment', 1);
INSERT INTO expenses (id, title, amount, date, category, description, user_id)
VALUES (2, 'Supermarket Grocery', 340.50, '2026-07-05', 'Food', 'Weekly groceries', 1);

-- Sample Budgets
INSERT INTO budgets (id, category, limit_amount, spent_amount, month, user_id)
VALUES (1, 'Housing', 1300.00, 1200.00, '2026-07', 1);
INSERT INTO budgets (id, category, limit_amount, spent_amount, month, user_id)
VALUES (2, 'Food', 500.00, 340.50, '2026-07', 1);

-- Sample Savings
INSERT INTO savings_goals (id, goal_name, target_amount, current_amount, target_date, status, user_id)
VALUES (1, 'New Apple Laptop', 1800.00, 600.00, '2026-10-15', 'IN_PROGRESS', 1);

-- Sample Investments
INSERT INTO investments (id, asset_name, type, amount_invested, current_value, purchase_date, user_id)
VALUES (1, 'Vanguard S&P 500 ETF', 'MUTUAL_FUNDS', 5000.00, 5600.00, '2026-01-15', 1);

-- Sample Loans
INSERT INTO loans (id, loan_name, principal_amount, interest_rate, tenure_months, start_date, remaining_balance, emi_amount, user_id)
VALUES (1, 'Car Loan', 18000.00, 4.5, 36, '2025-07-15', 12340.00, 535.48, 1);

-- Sample Notification
INSERT INTO notifications (id, message, type, created_at, is_read, user_id)
VALUES (1, 'Welcome to your financial service account!', 'SAVINGS_REMINDER', CURRENT_TIMESTAMP, false, 1);
