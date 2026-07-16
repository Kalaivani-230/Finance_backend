package com.finance.app.controller;

import com.finance.app.dto.BudgetDto;
import com.finance.app.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    @Autowired
    private BudgetService budgetService;

    @GetMapping
    public ResponseEntity<List<BudgetDto>> getAllBudgets() {
        return ResponseEntity.ok(budgetService.getAllBudgets());
    }

    @GetMapping("/month/{month}")
    public ResponseEntity<List<BudgetDto>> getBudgetsByMonth(@PathVariable String month) {
        return ResponseEntity.ok(budgetService.getBudgetsByMonth(month));
    }

    @PostMapping
    public ResponseEntity<BudgetDto> createBudget(@Valid @RequestBody BudgetDto budgetDto) {
        return ResponseEntity.ok(budgetService.createBudget(budgetDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetDto> updateBudget(@PathVariable Long id, @Valid @RequestBody BudgetDto budgetDto) {
        return ResponseEntity.ok(budgetService.updateBudget(id, budgetDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.ok("Budget deleted successfully!");
    }
}
