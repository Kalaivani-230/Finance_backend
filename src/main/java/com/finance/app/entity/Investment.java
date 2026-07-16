package com.finance.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "investments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Investment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "asset_name", nullable = false)
    private String assetName;

    @Column(nullable = false)
    private String type; // STOCKS, MUTUAL_FUNDS, GOLD, FIXED_DEPOSITS, SIP

    @Column(name = "amount_invested", nullable = false, precision = 15, scale = 2)
    private BigDecimal amountInvested;

    @Column(name = "current_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentValue;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;
}
