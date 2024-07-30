package com.group12.springboot.hoversprite.repository;

import com.group12.springboot.hoversprite.entity.Notification;
import com.group12.springboot.hoversprite.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByReceiver(User receiver, Pageable pageable);
}
