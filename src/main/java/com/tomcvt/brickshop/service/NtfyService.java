package com.tomcvt.brickshop.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tomcvt.brickshop.clients.NtfyClient;

@Service
public class NtfyService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NtfyService.class);
    private final NtfyClient ntfyClient;
    private final boolean ntfyEnabled;

    public NtfyService(NtfyClient ntfyClient, @Value("${com.tomcvt.ntfy.enabled:false}") boolean ntfyEnabled) {
        this.ntfyClient = ntfyClient;
        this.ntfyEnabled = ntfyEnabled;
    }

    public void sendNotification(String title, String message) {
        if (ntfyEnabled) {
            ntfyClient.sendNotification(title, message)
                .subscribe(
                    response -> {
                        log.info("Ntfy notification sent successfully: {}", response);
                    },
                    error -> {
                        log.error("Failed to send Ntfy notification: {}", error.getMessage());
                    }
                );
        } else {
            log.info("Ntfy notifications are disabled. Skipping notification: {} - {}", title, message);
        }
    }
}
