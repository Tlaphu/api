package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverIdAndReceiverTypeOrderByCreatedAtDesc(Long receiverId, String receiverType);
    long countByReceiverIdAndReceiverTypeAndIsReadFalse(Long receiverId, String receiverType);
    List<Notification> findAllByOrderByCreatedAtDesc();

}
