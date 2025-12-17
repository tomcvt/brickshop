package com.tomcvt.brickshop.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class EventProvider {
    private final ApplicationEventPublisher eventPublisher;

    public EventProvider(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishEvent(DomainEvent event) {
        eventPublisher.publishEvent(event);
    }
    
}
