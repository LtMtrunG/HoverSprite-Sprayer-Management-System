package com.group12.springboot.hoversprite.notification.repository;

import com.group12.springboot.hoversprite.notification.Entity.Notification;
import com.group12.springboot.hoversprite.notification.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findPagedByReceiverIdAndNotificationStatus(Long receiverId, NotificationStatus status, Pageable pageable);
    Page<Notification> findAllByReceiverId(Long receiverId, Pageable pageable);
    Optional<Notification> findById(Long id);
    List<Notification> findAllByReceiverIdAndNotificationStatus(Long receiverId, NotificationStatus status);
}
