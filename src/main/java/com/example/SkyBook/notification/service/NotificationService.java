package com.example.SkyBook.notification.service;

import com.example.SkyBook.notification.dto.NotificationDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {
    private final Map<Long, List<NotificationDto>> notifications = new ConcurrentHashMap<>();

    public NotificationDto sendNotification(Long userId, String title, String message) {
        NotificationDto notification = new NotificationDto();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setStatus("SENT");

        notifications.computeIfAbsent(userId, id -> new ArrayList<>()).add(notification);
        return notification;
    }

    public List<NotificationDto> getNotifications(Long userId) {
        return notifications.getOrDefault(userId, new ArrayList<>());
    }
}
