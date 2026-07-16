package com.finance.app.controller;

import com.finance.app.dto.IncomeDto;
import com.finance.app.service.IncomeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/income")
public class IncomeController {
    @Autowired
    private IncomeService incomeService;

    @GetMapping
    public ResponseEntity<List<IncomeDto>> getAllIncome() {
        return ResponseEntity.ok(incomeService.getAllIncome());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncomeDto> getIncomeById(@PathVariable Long id) {
        return ResponseEntity.ok(incomeService.getIncomeById(id));
    }

    @PostMapping
    public ResponseEntity<IncomeDto> createIncome(@Valid @RequestBody IncomeDto incomeDto) {
        return ResponseEntity.ok(incomeService.createIncome(incomeDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomeDto> updateIncome(@PathVariable Long id, @Valid @RequestBody IncomeDto incomeDto) {
        return ResponseEntity.ok(incomeService.updateIncome(id, incomeDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIncome(@PathVariable Long id) {
        incomeService.deleteIncome(id);
        return ResponseEntity.ok("Income deleted successfully!");
    }
}
