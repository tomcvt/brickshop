package com.tomcvt.brickshop.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tomcvt.brickshop.exception.IllegalUsageException;
import com.tomcvt.brickshop.model.RequestCounter;

@Service
public class RateLimiterService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RateLimiterService.class);
    private final long banThresholdPerMinute;
    private final int maxRequests;
    private final long timeWindowMillis;
    private final Map<String, RequestCounter> requestCounters = new ConcurrentHashMap<>();

    public RateLimiterService(
        @Value("${com.tomcvt.rate-limiter.max-requests}") int maxRequests,
        @Value("${com.tomcvt.rate-limiter.time-window-seconds}") int timeWindowSeconds,
        @Value("${com.tomcvt.rate-limiter.ban-threshold-per-minute}") int banThresholdPerMinute
    ) {
        this.maxRequests = maxRequests;
        this.timeWindowMillis = timeWindowSeconds * 1000L;
        this.banThresholdPerMinute = banThresholdPerMinute;
    }

    public boolean checkAndIncrement(String clientIp) throws IllegalUsageException {
        long currentTime = System.currentTimeMillis();
        RequestCounter counter = requestCounters.get(clientIp);

        if (counter == null) {
            counter = new RequestCounter();
            requestCounters.put(clientIp, counter);
            return true;
        }

        requestCounters.compute(clientIp, (ip, existingCounter) -> {
            if (currentTime - existingCounter.getBanWindowStartMillis() > 60000) {
                existingCounter.resetBanWindow();
            }
            if (currentTime - existingCounter.getWindowStartMillis() > timeWindowMillis) {
                existingCounter.resetWindow();
            } else {
                existingCounter.increment();
            }
            return existingCounter;
        });

        if (counter.getSmallCount() > banThresholdPerMinute) {
            throw new IllegalUsageException("Too many requests - temporary ban applied");
        }

        if (counter.getCount() > maxRequests) {
            return false;
        }
        return true;
    }

    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("RateLimiterService Status:\n");
        sb.append("Total tracked IPs: ").append(requestCounters.size()).append("\n");
        for (Map.Entry<String, RequestCounter> entry : requestCounters.entrySet()) {
            sb.append("IP: ").append(entry.getKey())
              .append(", Requests in window: ").append(entry.getValue().getCount())
              .append(", Requests in ban window: ").append(entry.getValue().getSmallCount())
              .append("\n");
        }
        return sb.toString();
    }

    public String checkStatus(String clientIp) {
        RequestCounter counter = requestCounters.get(clientIp);
        if (counter == null) {
            return "No requests recorded for IP: " + clientIp;
        }
        return "IP: " + clientIp + ", Requests in window: " + counter.getCount() +
               ", Requests in ban window: " + counter.getSmallCount();
    }

    @Scheduled(fixedRate = 60000 * 10)
    public void cleanupOldEntries() {
        long currentTime = System.currentTimeMillis();
        int count = requestCounters.size();
        int removed = 0;
        for (Map.Entry<String, RequestCounter> entry : requestCounters.entrySet()) {
            if (currentTime - entry.getValue().getWindowStartMillis() > timeWindowMillis) {
                requestCounters.remove(entry.getKey());
                removed++;
            }
        }
        log.info("RateLimiterService cleanup: total entries = {}, removed = {}", count, removed);
    }
}
