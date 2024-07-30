package com.group12.springboot.hoversprite.service;

import com.group12.springboot.hoversprite.dataTransferObject.response.NotificationCreationResponse;
import com.group12.springboot.hoversprite.entity.Action;
import com.group12.springboot.hoversprite.entity.Notification;
import com.group12.springboot.hoversprite.entity.User;
import com.group12.springboot.hoversprite.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public NotificationCreationResponse createNotification(User receiver, Action action){
        Notification notification = new Notification();
        notification.setReceiver(receiver);
        notification.setAction(action);
        notificationRepository.save(notification);
        return new NotificationCreationResponse(notification);
    }
}
