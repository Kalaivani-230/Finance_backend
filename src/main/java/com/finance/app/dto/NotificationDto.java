package com.finance.app.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationDto {
    private Long id;
    private String message;
    private String type; // BUDGET_EXCEEDED, SAVINGS_REMINDER, EMI_REMINDER, GOAL_COMPLETION
    private Boolean isRead;
    private LocalDateTime createdAt;
}
