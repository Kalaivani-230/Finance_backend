package com.finance.app.service;

import com.finance.app.dto.LoanDto;
import com.finance.app.entity.Loan;
import com.finance.app.entity.User;
import com.finance.app.exception.ResourceNotFoundException;
import com.finance.app.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {
    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserService userService;

    public List<LoanDto> getAllLoans() {
        User user = userService.getAuthenticatedUser();
        return loanRepository.findByUser(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public LoanDto createLoan(LoanDto dto) {
        User user = userService.getAuthenticatedUser();
        BigDecimal emi = calculateEmi(dto.getPrincipalAmount(), dto.getInterestRate(), dto.getTenureMonths());

        Loan loan = Loan.builder()
                .user(user)
                .loanName(dto.getLoanName())
                .principalAmount(dto.getPrincipalAmount())
                .interestRate(dto.getInterestRate())
                .tenureMonths(dto.getTenureMonths())
                .startDate(LocalDate.parse(dto.getStartDate()))
                .emiAmount(emi)
                .remainingBalance(dto.getRemainingBalance() != null ? dto.getRemainingBalance() : dto.getPrincipalAmount())
                .build();

        return convertToDto(loanRepository.save(loan));
    }

    public LoanDto updateLoan(Long id, LoanDto dto) {
        User user = userService.getAuthenticatedUser();
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + id));
        if (!loan.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access Denied");
        }

        BigDecimal emi = calculateEmi(dto.getPrincipalAmount(), dto.getInterestRate(), dto.getTenureMonths());

        loan.setLoanName(dto.getLoanName());
        loan.setPrincipalAmount(dto.getPrincipalAmount());
        loan.setInterestRate(dto.getInterestRate());
        loan.setTenureMonths(dto.getTenureMonths());
        loan.setStartDate(LocalDate.parse(dto.getStartDate()));
        loan.setEmiAmount(emi);
        loan.setRemainingBalance(dto.getRemainingBalance());

        return convertToDto(loanRepository.save(loan));
    }

    public void deleteLoan(Long id) {
        User user = userService.getAuthenticatedUser();
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + id));
        if (!loan.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access Denied");
        }
        loanRepository.delete(loan);
    }

    private BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualRate, int tenureMonths) {
        if (principal.compareTo(BigDecimal.ZERO) == 0 || tenureMonths == 0) {
            return BigDecimal.ZERO;
        }
        double p = principal.doubleValue();
        double r = annualRate.doubleValue() / (12.0 * 100.0);
        int n = tenureMonths;

        if (r == 0) {
            return BigDecimal.valueOf(p / n).setScale(2, RoundingMode.HALF_UP);
        }

        double emiVal = (p * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
        return BigDecimal.valueOf(emiVal).setScale(2, RoundingMode.HALF_UP);
    }

    private LoanDto convertToDto(Loan loan) {
        LoanDto dto = new LoanDto();
        dto.setId(loan.getId());
        dto.setLoanName(loan.getLoanName());
        dto.setPrincipalAmount(loan.getPrincipalAmount());
        dto.setInterestRate(loan.getInterestRate());
        dto.setTenureMonths(loan.getTenureMonths());
        dto.setStartDate(loan.getStartDate().toString());
        dto.setEmiAmount(loan.getEmiAmount());
        dto.setRemainingBalance(loan.getRemainingBalance());
        return dto;
    }
}
