package com.group12.springboot.hoversprite.notification.service;

import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
import com.group12.springboot.hoversprite.notification.Entity.Notification;
import com.group12.springboot.hoversprite.notification.NotificationAPI;
import com.group12.springboot.hoversprite.notification.NotificationResponse;
import com.group12.springboot.hoversprite.notification.enums.NotificationStatus;
import com.group12.springboot.hoversprite.notification.repository.NotificationRepository;
import com.group12.springboot.hoversprite.user.UserAPI;
import com.group12.springboot.hoversprite.user.UserAuthenticateDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class NotificationService implements NotificationAPI {

    @Autowired
    private NotificationRepository notificationRepository;
    private final UserAPI userAPI;

    @Override
    public void createNotification(Long receiverId, Long bookingId, String bookingStatus) {
        Notification notification = new Notification();
        notification.setNotificationStatus(NotificationStatus.UNREAD);
        notification.setBookingId(bookingId);
        notification.setReceiverId(receiverId);
        // Use String.format to create the formatted title
        String formattedTitle = String.format("Booking %s - Booking ID %d", bookingStatus, bookingId);
        notification.setTitle(formattedTitle);
        notification.setCreatedTime(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @PreAuthorize("hasAuthority('APPROVE_NOTIFICATION')")
    public Page<NotificationResponse> getAllNotifications(int pageNo, int pageSize, String status) {
        Long currentUserId = userAPI.getCurrentUserId();
        UserAuthenticateDTO userAuthenticateDTO = userAPI.findUserById(currentUserId);
        if (userAuthenticateDTO == null) {
            throw new CustomException(ErrorCode.USER_NOT_EXISTS);
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdTime")));
        Page<Notification> notificationsPage;

        if (status.equals("READ")) {
            notificationsPage = notificationRepository.findAllByReceiverIdAndNotificationStatus(currentUserId, NotificationStatus.READ, pageable);
        } else if (status.equals("UNREAD")) {
            notificationsPage = notificationRepository.findAllByReceiverIdAndNotificationStatus(currentUserId, NotificationStatus.UNREAD, pageable);
        } else {
            notificationsPage = notificationRepository.findAllByReceiverId(currentUserId, pageable);
        }

        List<NotificationResponse> notificationResponses = notificationsPage.stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());

        return  new PageImpl<>(notificationResponses, pageable, notificationsPage.getTotalElements());
    }

    @PreAuthorize("hasAuthority('APPROVE_NOTIFICATION')")
    public void markAsRead(Long id) {
        Long currentUserId = userAPI.getCurrentUserId();
        UserAuthenticateDTO userAuthenticateDTO = userAPI.findUserById(currentUserId);
        if (userAuthenticateDTO == null) {
            throw new CustomException(ErrorCode.USER_NOT_EXISTS);
        }

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_EXISTS));
        notification.setNotificationStatus(NotificationStatus.READ);
        notificationRepository.save(notification);
    }

    @PreAuthorize("hasAuthority('APPROVE_NOTIFICATION')")
    public void markAllAsRead() {
        Long currentUserId = userAPI.getCurrentUserId();
        UserAuthenticateDTO userAuthenticateDTO = userAPI.findUserById(currentUserId);
        if (userAuthenticateDTO == null) {
            throw new CustomException(ErrorCode.USER_NOT_EXISTS);
        }

        List<Notification> notificationList = notificationRepository.findAllByReceiverIdAndNotificationStatus(currentUserId, NotificationStatus.UNREAD);

        // Mark each notification as READ
        for (Notification notification : notificationList) {
            notification.setNotificationStatus(NotificationStatus.READ);
        }

        // Save the updated notifications back to the database
        notificationRepository.saveAll(notificationList);
    }
}
