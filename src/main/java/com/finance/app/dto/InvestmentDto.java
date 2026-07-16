package com.finance.app.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class InvestmentDto {
    private Long id;
    private String assetName;
    private String type; // STOCKS, MUTUAL_FUNDS, GOLD, FIXED_DEPOSITS, SIP
    private BigDecimal amountInvested;
    private BigDecimal currentValue;
    private String purchaseDate; // YYYY-MM-DD
}
