package com.finance.app.service;

import com.finance.app.dto.ReportDto;
import com.finance.app.entity.Expense;
import com.finance.app.entity.Income;
import com.finance.app.entity.Investment;
import com.finance.app.entity.User;
import com.finance.app.repository.ExpenseRepository;
import com.finance.app.repository.IncomeRepository;
import com.finance.app.repository.InvestmentRepository;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class ReportService {
    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private UserService userService;

    public ReportDto getReportData() {
        User user = userService.getAuthenticatedUser();

        List<Income> incomes = incomeRepository.findByUser(user);
        List<Expense> expenses = expenseRepository.findByUser(user);
        List<Investment> investments = investmentRepository.findByUser(user);

        BigDecimal totalIncome = incomes.stream().map(Income::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpense = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalInvestments = investments.stream().map(Investment::getAmountInvested).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalSavings = totalIncome.subtract(totalExpense);
        if (totalSavings.compareTo(BigDecimal.ZERO) < 0) {
            totalSavings = BigDecimal.ZERO;
        }

        Map<String, BigDecimal> expenseByCategory = new HashMap<>();
        expenses.forEach(e -> expenseByCategory.merge(e.getCategory(), e.getAmount(), BigDecimal::add));

        Map<String, BigDecimal> incomeByCategory = new HashMap<>();
        incomes.forEach(i -> incomeByCategory.merge(i.getCategory(), i.getAmount(), BigDecimal::add));

        Map<String, BigDecimal> monthlySavingsTrend = new TreeMap<>();
        incomes.forEach(i -> {
            String month = i.getDate().toString().substring(0, 7);
            monthlySavingsTrend.merge(month, i.getAmount(), BigDecimal::add);
        });
        expenses.forEach(e -> {
            String month = e.getDate().toString().substring(0, 7);
            monthlySavingsTrend.merge(month, e.getAmount().negate(), BigDecimal::add);
        });

        return ReportDto.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .totalSavings(totalSavings)
                .totalInvestments(totalInvestments)
                .expenseByCategory(expenseByCategory)
                .incomeByCategory(incomeByCategory)
                .monthlySavingsTrend(monthlySavingsTrend)
                .build();
    }

    public byte[] generatePdfReport() {
        ReportDto data = getReportData();
        User user = userService.getAuthenticatedUser();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);

            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Font.BOLD);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Font.BOLD);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.NORMAL);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Font.BOLD);

            Paragraph title = new Paragraph("Personal Financial Statement", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            Paragraph userInfo = new Paragraph(String.format("Prepared For: %s %s (%s)\nDate Generated: %s\n",
                    user.getFirstName() != null ? user.getFirstName() : "",
                    user.getLastName() != null ? user.getLastName() : "",
                    user.getEmail(),
                    LocalDate.now().toString()), normalFont);
            userInfo.setSpacingAfter(20);
            document.add(userInfo);

            Paragraph summaryHeading = new Paragraph("Financial Summary", sectionFont);
            summaryHeading.setSpacingAfter(10);
            document.add(summaryHeading);

            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingAfter(20);

            summaryTable.addCell(new PdfPCell(new Phrase("Total Income", boldFont)));
            summaryTable.addCell(new PdfPCell(new Phrase("$" + data.getTotalIncome().toString(), normalFont)));

            summaryTable.addCell(new PdfPCell(new Phrase("Total Expenses", boldFont)));
            summaryTable.addCell(new PdfPCell(new Phrase("$" + data.getTotalExpense().toString(), normalFont)));

            summaryTable.addCell(new PdfPCell(new Phrase("Net Savings", boldFont)));
            summaryTable.addCell(new PdfPCell(new Phrase("$" + data.getTotalSavings().toString(), normalFont)));

            summaryTable.addCell(new PdfPCell(new Phrase("Total Investments", boldFont)));
            summaryTable.addCell(new PdfPCell(new Phrase("$" + data.getTotalInvestments().toString(), normalFont)));

            document.add(summaryTable);

            Paragraph expenseHeading = new Paragraph("Expenses by Category", sectionFont);
            expenseHeading.setSpacingAfter(10);
            document.add(expenseHeading);

            PdfPTable expenseTable = new PdfPTable(2);
            expenseTable.setWidthPercentage(100);
            expenseTable.setSpacingAfter(20);
            expenseTable.addCell(new PdfPCell(new Phrase("Category", boldFont)));
            expenseTable.addCell(new PdfPCell(new Phrase("Amount", boldFont)));

            for (Map.Entry<String, BigDecimal> entry : data.getExpenseByCategory().entrySet()) {
                expenseTable.addCell(new PdfPCell(new Phrase(entry.getKey(), normalFont)));
                expenseTable.addCell(new PdfPCell(new Phrase("$" + entry.getValue().toString(), normalFont)));
            }
            document.add(expenseTable);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }
}
