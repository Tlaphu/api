package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.model.Notification;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.services.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;
    private final JwtProvider jwtProvider;

    /**
     * @apiNote Lấy tất cả thông báo của người dùng hiện tại
     */
    @GetMapping
    public ResponseEntity<?> getNotifications() {
        Long userId = jwtProvider.getCurrentUserId();
        String userType = jwtProvider.getCurrentUserType();
        List<Notification> notifications = notificationService.getNotificationsForCurrentUser(userId, userType);

        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(notifications)
                        .build()
        );
    }

    /**
     * @apiNote Đếm số lượng thông báo chưa đọc
     */
    @GetMapping("/unread-count")
    public ResponseEntity<?> countUnread() {
        Long userId = jwtProvider.getCurrentUserId();
        String userType = jwtProvider.getCurrentUserType();
        long unreadCount = notificationService.countUnreadForCurrentUser(userId, userType);

        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(unreadCount)
                        .build()
        );
    }

    /**
     * @apiNote Đánh dấu một thông báo là đã đọc
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Notification marked as read")
                        .build()
        );
    }

    /**
     * @apiNote Đánh dấu tất cả thông báo của người dùng hiện tại là đã đọc
     */
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead() {
        Long userId = jwtProvider.getCurrentUserId();
        String userType = jwtProvider.getCurrentUserType();
        notificationService.markAllAsReadForCurrentUser(userId, userType);

        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("All notifications marked as read")
                        .build()
        );
    }

    /**
     * @apiNote Xem chi tiết 1 thông báo và tự động đánh dấu đã đọc
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getNotificationById(@PathVariable Long id) {
        Notification notification = notificationService.readNotification(id);

        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(notification)
                        .build()
        );
    }

    /**
     * @apiNote Xóa một thông báo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);

        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Notification deleted")
                        .build()
        );
    }

    /**
     * @apiNote Tạo một thông báo mới
     */
    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody Notification request) {

        Notification notification = notificationService.createNotification(
                request.getTitle(),
                request.getMessage(),
                request.getReceiverId(),
                request.getReceiverType(),
                request.getType(),
                request.getRedirectUrl()
        );

        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(notification)
                        .build()
        );
    }


}
