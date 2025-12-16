package com.tomcvt.brickshop.config;

import com.tomcvt.brickshop.service.NtfyService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
public class TestInterfacesConfig implements ApplicationListener<ApplicationReadyEvent> {

    private final NtfyService ntfyService;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestInterfacesConfig.class);

    TestInterfacesConfig(NtfyService ntfyService) {
        this.ntfyService = ntfyService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ntfyService.sendTestNotification("Test Notification", "This is a test notification sent on application startup.");
        log.info("Test Ntfy notification sent on application startup.");
    }
}
