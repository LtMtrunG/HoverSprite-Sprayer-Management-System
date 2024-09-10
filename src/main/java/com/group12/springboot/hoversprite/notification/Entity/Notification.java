package com.group12.springboot.hoversprite.notification.Entity;

import com.group12.springboot.hoversprite.notification.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="TBL_NOTIFICATIONS")
@NoArgsConstructor
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="receiver_id")
    private Long receiverId;

    @Enumerated(EnumType.STRING)
    @Column(name="notification_status")
    private NotificationStatus notificationStatus;

    @Column(name="title")
    private String title;

    @Column(name="booking_id")
    private Long bookingId;
}
