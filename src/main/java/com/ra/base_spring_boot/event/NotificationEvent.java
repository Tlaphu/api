package com.ra.base_spring_boot.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NotificationEvent extends ApplicationEvent {

    private final String title;
    private final String message;
    private final String type;
    private final Long receiverId;
    private final String receiverType;
    private final String redirectUrl;

    public NotificationEvent(Object source, String title, String message, String type, Long receiverId, String receiverType, String redirectUrl) {
        super(source);
        this.title = title;
        this.message = message;
        this.type = type;
        this.receiverId = receiverId;
        this.receiverType = receiverType;
        this.redirectUrl = redirectUrl;
    }

}
