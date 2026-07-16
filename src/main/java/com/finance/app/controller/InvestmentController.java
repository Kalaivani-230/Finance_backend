package com.finance.app.controller;

import com.finance.app.dto.InvestmentDto;
import com.finance.app.service.InvestmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/investments")
public class InvestmentController {
    @Autowired
    private InvestmentService investmentService;

    @GetMapping
    public ResponseEntity<List<InvestmentDto>> getAllInvestments() {
        return ResponseEntity.ok(investmentService.getAllInvestments());
    }

    @PostMapping
    public ResponseEntity<InvestmentDto> createInvestment(@Valid @RequestBody InvestmentDto dto) {
        return ResponseEntity.ok(investmentService.createInvestment(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvestmentDto> updateInvestment(@PathVariable Long id, @Valid @RequestBody InvestmentDto dto) {
        return ResponseEntity.ok(investmentService.updateInvestment(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInvestment(@PathVariable Long id) {
        investmentService.deleteInvestment(id);
        return ResponseEntity.ok("Investment deleted successfully!");
    }
}
