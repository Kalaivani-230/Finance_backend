package com.finance.app.service;

import com.finance.app.dto.IncomeDto;
import com.finance.app.entity.Income;
import com.finance.app.entity.User;
import com.finance.app.exception.ResourceNotFoundException;
import com.finance.app.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncomeService {
    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserService userService;

    public List<IncomeDto> getAllIncome() {
        User user = userService.getAuthenticatedUser();
        return incomeRepository.findByUserOrderByDateDesc(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public IncomeDto getIncomeById(Long id) {
        User user = userService.getAuthenticatedUser();
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income entry not found with id: " + id));
        if (!income.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access Denied");
        }
        return convertToDto(income);
    }

    public IncomeDto createIncome(IncomeDto dto) {
        User user = userService.getAuthenticatedUser();
        Income income = Income.builder()
                .user(user)
                .source(dto.getSource())
                .amount(dto.getAmount())
                .date(LocalDate.parse(dto.getDate()))
                .category(dto.getCategory())
                .description(dto.getDescription())
                .build();
        return convertToDto(incomeRepository.save(income));
    }

    public IncomeDto updateIncome(Long id, IncomeDto dto) {
        User user = userService.getAuthenticatedUser();
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income entry not found with id: " + id));
        if (!income.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access Denied");
        }
        income.setSource(dto.getSource());
        income.setAmount(dto.getAmount());
        income.setDate(LocalDate.parse(dto.getDate()));
        income.setCategory(dto.getCategory());
        income.setDescription(dto.getDescription());
        return convertToDto(incomeRepository.save(income));
    }

    public void deleteIncome(Long id) {
        User user = userService.getAuthenticatedUser();
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income entry not found with id: " + id));
        if (!income.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access Denied");
        }
        incomeRepository.delete(income);
    }

    private IncomeDto convertToDto(Income income) {
        IncomeDto dto = new IncomeDto();
        dto.setId(income.getId());
        dto.setSource(income.getSource());
        dto.setAmount(income.getAmount());
        dto.setDate(income.getDate().toString());
        dto.setCategory(income.getCategory());
        dto.setDescription(income.getDescription());
        return dto;
    }
}
