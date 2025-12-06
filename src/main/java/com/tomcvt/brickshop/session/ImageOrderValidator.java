package com.tomcvt.brickshop.session;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class ImageOrderValidator {
    private Map<UUID, Set<String>> map;

    public ImageOrderValidator() {
        this.map = new ConcurrentHashMap<>();
    }
    public void storeImageOrder(UUID publicId, Set<String> imageUrls) {
        map.put(publicId, imageUrls);
    }
    public void clearImageOrder(UUID publicId) {
        map.remove(publicId);
    }
    public boolean validateImageOrder(UUID publicId, List<String> imageUrlsList) {
        for (String url : imageUrlsList) {
            System.out.println(" - " + url);
        }
        if (publicId == null) return false;
        if (!map.containsKey(publicId)) return false;
        if (imageUrlsList == null) return false;
        Set<String> imageUrls = Set.copyOf(imageUrlsList);
        Set<String> storedUrls = map.get(publicId);
        if (storedUrls.size() != imageUrls.size()) return false;
        for (String url : imageUrls) {
            if (!storedUrls.contains(url)) return false;
        }
        return true;
    }
}
