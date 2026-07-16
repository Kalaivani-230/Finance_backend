package com.finance.app.controller;

import com.finance.app.dto.SavingsGoalDto;
import com.finance.app.service.SavingsGoalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/savings")
public class SavingsGoalController {
    @Autowired
    private SavingsGoalService savingsGoalService;

    @GetMapping
    public ResponseEntity<List<SavingsGoalDto>> getAllGoals() {
        return ResponseEntity.ok(savingsGoalService.getAllGoals());
    }

    @PostMapping
    public ResponseEntity<SavingsGoalDto> createGoal(@Valid @RequestBody SavingsGoalDto dto) {
        return ResponseEntity.ok(savingsGoalService.createGoal(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavingsGoalDto> updateGoal(@PathVariable Long id, @Valid @RequestBody SavingsGoalDto dto) {
        return ResponseEntity.ok(savingsGoalService.updateGoal(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long id) {
        savingsGoalService.deleteGoal(id);
        return ResponseEntity.ok("Savings Goal deleted successfully!");
    }
}
