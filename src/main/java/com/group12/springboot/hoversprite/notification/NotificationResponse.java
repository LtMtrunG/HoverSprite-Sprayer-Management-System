package com.group12.springboot.hoversprite.notification;

import com.group12.springboot.hoversprite.notification.Entity.Notification;
import com.group12.springboot.hoversprite.notification.enums.NotificationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationResponse {
    private Long id;
    private Long receiverId;
    private NotificationStatus notificationStatus;
    private String title;
    private Long bookingId;

    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.receiverId = notification.getReceiverId();
        this.notificationStatus = notification.getNotificationStatus();
        this.title = notification.getTitle();
        this.bookingId = notification.getBookingId();
    }
}
