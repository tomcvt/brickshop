package com.tomcvt.brickshop.events;

public class NotificationEvent extends DomainEvent {
    private final String title;
    private final String message;
    private final int priority;

    public NotificationEvent(String title, String message, int priority) {
        super();
        this.title = title;
        this.message = message;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public int getPriority() {
        return priority;
    }
    
}
