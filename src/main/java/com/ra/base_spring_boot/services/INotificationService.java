package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.Notification;

import java.util.Date;
import java.util.List;

public interface INotificationService {

    Notification createNotification(String title, String message, Long receiverId, String receiverType, String type, String redirectUrl);

    List<Notification> getNotificationsForCurrentUser(Long userId, String userType);
    long countUnreadForCurrentUser(Long userId, String userType);
    void markAsRead(Long notificationId);
    void markAllAsReadForCurrentUser(Long userId, String userType);

    Notification readNotification(Long notificationId);

    void deleteNotification(Long id);


    Notification createScheduleNotification(Long candidateId, Long companyId, String message, String companyName, String email, String address);
}
