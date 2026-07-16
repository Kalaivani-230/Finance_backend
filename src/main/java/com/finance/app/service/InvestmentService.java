package com.finance.app.service;

import com.finance.app.dto.InvestmentDto;
import com.finance.app.entity.Investment;
import com.finance.app.entity.User;
import com.finance.app.exception.ResourceNotFoundException;
import com.finance.app.repository.InvestmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvestmentService {
    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private UserService userService;

    public List<InvestmentDto> getAllInvestments() {
        User user = userService.getAuthenticatedUser();
        return investmentRepository.findByUser(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public InvestmentDto createInvestment(InvestmentDto dto) {
        User user = userService.getAuthenticatedUser();
        Investment investment = Investment.builder()
                .user(user)
                .assetName(dto.getAssetName())
                .type(dto.getType())
                .amountInvested(dto.getAmountInvested())
                .currentValue(dto.getCurrentValue())
                .purchaseDate(LocalDate.parse(dto.getPurchaseDate()))
                .build();
        return convertToDto(investmentRepository.save(investment));
    }

    public InvestmentDto updateInvestment(Long id, InvestmentDto dto) {
        User user = userService.getAuthenticatedUser();
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Investment not found with id: " + id));
        if (!investment.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access Denied");
        }

        investment.setAssetName(dto.getAssetName());
        investment.setType(dto.getType());
        investment.setAmountInvested(dto.getAmountInvested());
        investment.setCurrentValue(dto.getCurrentValue());
        investment.setPurchaseDate(LocalDate.parse(dto.getPurchaseDate()));

        return convertToDto(investmentRepository.save(investment));
    }

    public void deleteInvestment(Long id) {
        User user = userService.getAuthenticatedUser();
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Investment not found with id: " + id));
        if (!investment.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access Denied");
        }
        investmentRepository.delete(investment);
    }

    private InvestmentDto convertToDto(Investment investment) {
        InvestmentDto dto = new InvestmentDto();
        dto.setId(investment.getId());
        dto.setAssetName(investment.getAssetName());
        dto.setType(investment.getType());
        dto.setAmountInvested(investment.getAmountInvested());
        dto.setCurrentValue(investment.getCurrentValue());
        dto.setPurchaseDate(investment.getPurchaseDate().toString());
        return dto;
    }
}
