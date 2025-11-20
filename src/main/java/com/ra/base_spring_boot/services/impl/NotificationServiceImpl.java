package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.NotificationResponse;
import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.model.Company;
import com.ra.base_spring_boot.model.Notification;
import com.ra.base_spring_boot.repository.ICandidateRepository;
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
    private final ICandidateRepository candidateRepository;
    private final JwtProvider jwtProvider;
    private final HttpServletRequest request;

    @Override
    public Notification createNotification(
            String title,
            String message,
            Long receiverId,
            String receiverType,
            Long senderId,
            String senderType,
            String type,
            String redirectUrl
    ) {

        String senderLogo = null;

        if ("COMPANY".equals(senderType)) {
            senderLogo = companyRepository.findById(senderId)
                    .map(Company::getLogo)
                    .orElse(null);

        } else if ("CANDIDATE".equals(senderType)) {
            senderLogo = candidateRepository.findById(senderId)
                    .map(Candidate::getLogo)
                    .orElse(null);
        } else if ("ADMIN".equals(senderType)) {
            senderId = null;
            senderType = "SYSTEM";
            senderLogo =  null; // <-- LOGO HỆ THỐNG (tùy bạn cấu hình)
        }

        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .type(type)
                .redirectUrl(redirectUrl)
                .receiverId(receiverId)
                .receiverType(receiverType)
                .senderId(senderId)
                .senderType(senderType)
                .logo(senderLogo)
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
    public List<NotificationResponse> getAllNotificationsForAdmin() {
        return notificationRepository.findAllByOrderByCreatedAtDesc()
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
                .senderId(companyId)
                .senderType("COMPANY")
                .build();

        return notificationRepository.save(notification);
    }

    private NotificationResponse toResponse(Notification n) {

        String logo = null;

        if ("COMPANY".equals(n.getSenderType())) {
            logo = companyRepository.findById(n.getSenderId())
                    .map(Company::getLogo)
                    .orElse(null);

        } else if ("CANDIDATE".equals(n.getSenderType())) {
            logo = candidateRepository.findById(n.getSenderId())
                    .map(Candidate::getLogo)
                    .orElse(null);

        } else if ("SYSTEM".equals(n.getSenderType())) {
            logo = null; // Hoặc để null nếu không muốn logo
        }

        return NotificationResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .redirectUrl(n.getRedirectUrl())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .logo(logo)
                .build();
    }

}
