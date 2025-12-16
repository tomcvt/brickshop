package com.tomcvt.brickshop.events;

import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties.Application;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.tomcvt.brickshop.service.NtfyService;

@Service
public class EventProvider {
    private final NtfyService ntfyService;

    private final ApplicationEventPublisher eventPublisher;

    public EventProvider(NtfyService ntfyService, ApplicationEventPublisher eventPublisher) {
        this.ntfyService = ntfyService;
        this.eventPublisher = eventPublisher;
    }

    public void publishEvent(DomainEvent event) {
        eventPublisher.publishEvent(event);
    }
    
}
