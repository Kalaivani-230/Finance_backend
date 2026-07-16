package com.finance.app.service;

import com.finance.app.dto.NotificationDto;
import com.finance.app.entity.Notification;
import com.finance.app.entity.User;
import com.finance.app.exception.ResourceNotFoundException;
import com.finance.app.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;

    public List<NotificationDto> getAllNotifications() {
        User user = userService.getAuthenticatedUser();
        return notificationRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<NotificationDto> getUnreadNotifications() {
        User user = userService.getAuthenticatedUser();
        return notificationRepository.findByUserAndIsReadOrderByCreatedAtDesc(user, false).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Long getUnreadCount() {
        User user = userService.getAuthenticatedUser();
        return notificationRepository.countByUserAndIsRead(user, false);
    }

    public NotificationDto markAsRead(Long id) {
        User user = userService.getAuthenticatedUser();
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access Denied");
        }
        notification.setIsRead(true);
        return convertToDto(notificationRepository.save(notification));
    }

    public void markAllAsRead() {
        User user = userService.getAuthenticatedUser();
        List<Notification> unread = notificationRepository.findByUserAndIsReadOrderByCreatedAtDesc(user, false);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }

    public void deleteNotification(Long id) {
        User user = userService.getAuthenticatedUser();
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access Denied");
        }
        notificationRepository.delete(notification);
    }

    private NotificationDto convertToDto(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.setId(n.getId());
        dto.setMessage(n.getMessage());
        dto.setType(n.getType());
        dto.setIsRead(n.getIsRead());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }
}
