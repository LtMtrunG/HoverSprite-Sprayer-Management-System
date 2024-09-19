package com.group12.springboot.hoversprite.notification.controller;

import com.group12.springboot.hoversprite.common.ApiResponse;
import com.group12.springboot.hoversprite.notification.NotificationResponse;
import com.group12.springboot.hoversprite.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/all")
    ApiResponse<Page<NotificationResponse>> getAllNotifications(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize,
            @RequestParam(value = "status", defaultValue = "all", required = false) String status) {
        ApiResponse<Page<NotificationResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(notificationService.getAllNotifications(pageNo, pageSize, status));
        return apiResponse;
    }

    @PostMapping("/read")
    ApiResponse<String> markAsRead(@RequestParam("id") Long id) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        notificationService.markAsRead(id);
        apiResponse.setResult("Marked as read successfully");
        return apiResponse;
    }

    @PostMapping("/read/all")
    ApiResponse<String> markAllAsRead() {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        notificationService.markAllAsRead();
        apiResponse.setResult("Marked all as read successfully");
        return apiResponse;
    }
}
