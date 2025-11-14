package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.model.Notification;
import com.ra.base_spring_boot.repository.NotificationRepository;
import com.ra.base_spring_boot.services.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public Notification createNotification(String title, String message, Long receiverId, String receiverType, String type, String redirectUrl) {
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .type(type)
                .redirectUrl(redirectUrl)
                .receiverId(receiverId)
                .receiverType(receiverType)
                .isRead(false)
                .build();
        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotificationsForCurrentUser(Long userId, String userType) {
        return notificationRepository.findByReceiverIdAndReceiverTypeOrderByCreatedAtDesc(userId, userType);
    }

    @Override
    public long countUnreadForCurrentUser(Long userId, String userType) {
        return notificationRepository.countByReceiverIdAndReceiverTypeAndIsReadFalse(userId, userType);
    }

    @Override
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    @Override
    public void markAllAsReadForCurrentUser(Long userId, String userType) {
        notificationRepository.findByReceiverIdAndReceiverTypeOrderByCreatedAtDesc(userId, userType)
                .forEach(n -> {
                    n.setRead(true);
                    notificationRepository.save(n);
                });
    }
    @Override
    public Notification readNotification(Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        if (!n.isRead()) {
            n.setRead(true);
            notificationRepository.save(n);
        }
        return n;
    }
    @Override
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
    @Override
    public Notification createScheduleNotification(Long candidateId, Long companyId, String message, String companyName, String email, String address) {

        String finalMessage = companyName + email + address +
                               message ;

        Notification notification = Notification.builder()
                .title("Lịch hẹn phỏng vấn")
                .message(finalMessage)
                .receiverId(candidateId)
                .receiverType("CANDIDATE")
                .type("SCHEDULE_INTERVIEW")
                .redirectUrl("/candidate/interview-schedule")
                .isRead(false)
                .build();

        return notificationRepository.save(notification);
    }


}
