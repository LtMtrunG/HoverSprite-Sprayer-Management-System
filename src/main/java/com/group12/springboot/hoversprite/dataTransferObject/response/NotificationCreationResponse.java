package com.group12.springboot.hoversprite.dataTransferObject.response;

import com.group12.springboot.hoversprite.entity.Action;
import com.group12.springboot.hoversprite.entity.Notification;
import com.group12.springboot.hoversprite.entity.User;

public class NotificationCreationResponse {
    private Long id;
    private User receiver;
    private Action action;
    
    public NotificationCreationResponse(Notification notification){
        this.id = notification.getId();
        this.receiver = notification.getReceiver();
        this.action = notification.getAction();
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
