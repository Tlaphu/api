package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.NotificationResponse;
import com.ra.base_spring_boot.model.Company;
import com.ra.base_spring_boot.model.Notification;
import com.ra.base_spring_boot.repository.ICompanyRepository;
import com.ra.base_spring_boot.repository.NotificationRepository;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.services.INotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final ICompanyRepository companyRepository;
    private final JwtProvider jwtProvider;
    private final HttpServletRequest request;


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
    public List<NotificationResponse> getNotificationsForCurrentUser(Long userId, String userType) {
        return notificationRepository
                .findByReceiverIdAndReceiverTypeOrderByCreatedAtDesc(userId, userType)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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
    public Notification createScheduleNotification(Long candidateId, Long companyId, String message) {

        Notification notification = Notification.builder()
                .title("Lịch hẹn phỏng vấn")
                .message(message)
                .receiverId(candidateId)
                .receiverType("CANDIDATE")
                .type("SCHEDULE_INTERVIEW")
                .redirectUrl("/candidate/interview-schedule")
                .isRead(false)
                .senderCompanyId(companyId)
                .build();

        return notificationRepository.save(notification);
    }

    private NotificationResponse toResponse(Notification n) {

        String companyLogo = null;

        if (n.getSenderCompanyId() != null) {
            Company company = companyRepository.findById(n.getSenderCompanyId()).orElse(null);
            if (company != null) {
                companyLogo = company.getLogo();
            }
        }

        return NotificationResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .redirectUrl(n.getRedirectUrl())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .companyLogo(companyLogo)
                .build();
    }
}
