package com.tomcvt.brickshop.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BanRegistry {
    private final Map<String, Long> bannedIps = new ConcurrentHashMap<>();
    private final long defaultBanDuration;

    public BanRegistry(@Value("${com.tomcvt.rate-limiter.ban-duration-minutes}") long banDurationMinutes) {
        this.defaultBanDuration = banDurationMinutes * 60000L;
    }



    public void banIp(String ip, long durationMinutes) {
        long banUntil = System.currentTimeMillis() + durationMinutes * 60000L;
        bannedIps.put(ip, banUntil);
    }

    public void banIp(String ip) {
        banIp(ip, defaultBanDuration);
    }

    public boolean isIpBanned(String ip) {
        Long banUntil = bannedIps.get(ip);
        if (banUntil == null) {
            return false;
        }
        if (System.currentTimeMillis() > banUntil) {
            bannedIps.remove(ip);
            return false;
        }
        return true;
    }

    public Map<String, Long> getBannedIPs() {
        return bannedIps;
    }

    public void unbanIp(String ip) {
        bannedIps.remove(ip);
    }
}
