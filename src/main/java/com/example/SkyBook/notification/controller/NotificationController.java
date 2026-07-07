package com.example.SkyBook.notification.controller;

import com.example.SkyBook.notification.dto.NotificationDto;
import com.example.SkyBook.notification.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public NotificationDto sendNotification(@RequestParam Long userId,
                                            @RequestParam String title,
                                            @RequestParam String message) {
        return notificationService.sendNotification(userId, title, message);
    }

    @GetMapping("/users/{userId}")
    public List<NotificationDto> getNotifications(@PathVariable Long userId) {
        return notificationService.getNotifications(userId);
    }
}
