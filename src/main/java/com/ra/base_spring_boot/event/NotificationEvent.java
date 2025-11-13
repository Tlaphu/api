package com.ra.base_spring_boot.event;

import org.springframework.context.ApplicationEvent;

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

    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public Long getReceiverId() { return receiverId; }
    public String getReceiverType() { return receiverType; }
    public String getRedirectUrl() { return redirectUrl; }
}
