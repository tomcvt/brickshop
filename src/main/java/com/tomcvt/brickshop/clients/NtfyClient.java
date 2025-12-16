package com.tomcvt.brickshop.clients;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class NtfyClient {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NtfyClient.class);
    private final WebClient ntfyWebClient;
    private volatile String topic;
    private int defaultPriority;
    private int urgentPriority;

    public NtfyClient(@Qualifier("webClientNtfy") WebClient ntfyWebClient, 
        @Value("${com.tomcvt.ntfy.topic}") String topic,
        @Value("${com.tomcvt.ntfy.priority}") int defaultPriority,
        @Value("${com.tomcvt.ntfy.urgent-priority}") int urgentPriority) {
        this.ntfyWebClient = ntfyWebClient;
        this.topic = topic;
        this.defaultPriority = defaultPriority;
        this.urgentPriority = urgentPriority;
    }

    public void setDefaultPriority(int defaultPriority) {
        this.defaultPriority = defaultPriority;
    }

    public void setUrgentPriority(int urgentPriority) {
        this.urgentPriority = urgentPriority;
    }

    public Mono<String> sendTestNotification(String title, String message) {
        log.info("Sending TEST Ntfy notification to topic {}: {} - {}", topic, title, message);
        return ntfyWebClient.post()
                .uri("/{topic}", topic)
                .header("Title", title)
                .header("Priority", String.valueOf(defaultPriority))
                .bodyValue(message)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Error sending TEST Ntfy notification: {}", error.getMessage()));
    }

    public Mono<String> sendNotification(String title, String message) {
        log.info("Sending Ntfy notification to topic {}: {} - {}", topic, title, message);
        return ntfyWebClient.post()
                .uri("/{topic}", topic)
                .header("Title", title)
                .header("Priority", String.valueOf(defaultPriority))
                .bodyValue(message)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Error sending Ntfy notification: {}", error.getMessage()));
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Mono<String> sendNotificationUrgent(String title, String message) {
        log.info("Sending URGENT Ntfy notification to topic {}: {} - {}", topic, title, message);
        return ntfyWebClient.post()
                .uri("/{topic}", topic)
                .header("Title", title)
                .header("Priority", String.valueOf(urgentPriority))
                .bodyValue(message)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Error sending URGENT Ntfy notification: {}", error.getMessage()));
    }
}
