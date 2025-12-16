package com.tomcvt.brickshop.events;

import java.time.Instant;

public abstract class DomainEvent {
    private final Instant timestamp;

    public DomainEvent() {
        this.timestamp = Instant.now();
    }
}
