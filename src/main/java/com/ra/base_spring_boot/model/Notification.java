package com.ra.base_spring_boot.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false, length = 100)
    private String type;

    private String redirectUrl;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private Long receiverId;

    @Column(nullable = false, length = 20)
    private String receiverType; // "CANDIDATE" hoặc "COMPANY"

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @PrePersist
    public void onCreate() {
        if (createdAt == null) createdAt = new Date();
    }
    private String logo;
    private Long senderId;
    private String senderType;
}
