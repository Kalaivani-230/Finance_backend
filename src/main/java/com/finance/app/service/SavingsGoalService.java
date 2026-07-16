package com.finance.app.service;

import com.finance.app.dto.SavingsGoalDto;
import com.finance.app.entity.Notification;
import com.finance.app.entity.SavingsGoal;
import com.finance.app.entity.User;
import com.finance.app.exception.ResourceNotFoundException;
import com.finance.app.repository.NotificationRepository;
import com.finance.app.repository.SavingsGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavingsGoalService {
    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;

    public List<SavingsGoalDto> getAllGoals() {
        User user = userService.getAuthenticatedUser();
        return savingsGoalRepository.findByUser(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public SavingsGoalDto createGoal(SavingsGoalDto dto) {
        User user = userService.getAuthenticatedUser();
        SavingsGoal goal = SavingsGoal.builder()
                .user(user)
                .goalName(dto.getGoalName())
                .targetAmount(dto.getTargetAmount())
                .currentAmount(dto.getCurrentAmount())
                .targetDate(LocalDate.parse(dto.getTargetDate()))
                .status("IN_PROGRESS")
                .build();

        checkGoalCompletion(user, goal);
        return convertToDto(savingsGoalRepository.save(goal));
    }

    public SavingsGoalDto updateGoal(Long id, SavingsGoalDto dto) {
        User user = userService.getAuthenticatedUser();
        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Savings Goal not found with id: " + id));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access Denied");
        }

        goal.setGoalName(dto.getGoalName());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setCurrentAmount(dto.getCurrentAmount());
        goal.setTargetDate(LocalDate.parse(dto.getTargetDate()));

        checkGoalCompletion(user, goal);
        return convertToDto(savingsGoalRepository.save(goal));
    }

    public void deleteGoal(Long id) {
        User user = userService.getAuthenticatedUser();
        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Savings Goal not found with id: " + id));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access Denied");
        }
        savingsGoalRepository.delete(goal);
    }

    private void checkGoalCompletion(User user, SavingsGoal goal) {
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            if (!"COMPLETED".equals(goal.getStatus())) {
                goal.setStatus("COMPLETED");

                String msg = String.format("Congratulations! You have achieved your savings goal '%s' of $%s!",
                        goal.getGoalName(), goal.getTargetAmount());
                Notification notification = Notification.builder()
                        .user(user)
                        .message(msg)
                        .type("GOAL_COMPLETION")
                        .isRead(false)
                        .build();
                notificationRepository.save(notification);
            }
        } else {
            goal.setStatus("IN_PROGRESS");
        }
    }

    private SavingsGoalDto convertToDto(SavingsGoal goal) {
        SavingsGoalDto dto = new SavingsGoalDto();
        dto.setId(goal.getId());
        dto.setGoalName(goal.getGoalName());
        dto.setTargetAmount(goal.getTargetAmount());
        dto.setCurrentAmount(goal.getCurrentAmount());
        dto.setTargetDate(goal.getTargetDate().toString());
        dto.setStatus(goal.getStatus());
        return dto;
    }
}
