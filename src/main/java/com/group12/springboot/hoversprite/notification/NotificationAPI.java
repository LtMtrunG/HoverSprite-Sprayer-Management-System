package com.group12.springboot.hoversprite.notification;

public interface NotificationAPI {
    public void createNotification(Long receiverId, Long bookingId, String bookingStatus);
}
