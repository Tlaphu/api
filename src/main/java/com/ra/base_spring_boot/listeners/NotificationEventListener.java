package com.ra.base_spring_boot.listeners;

import com.ra.base_spring_boot.event.NotificationEvent;
import com.ra.base_spring_boot.services.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final INotificationService notificationService;

    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        notificationService.createNotification(
                event.getTitle(),
                event.getMessage(),
                event.getReceiverId(),
                event.getReceiverType(),
                event.getType(),
                event.getRedirectUrl(),
                event.getSenderType(),
                event.getSenderId()
        );
    }
}

