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

    public NtfyClient(@Qualifier("webClientNtfy") WebClient ntfyWebClient, 
        @Value("${com.tomcvt.ntfy.topic}") String topic) {
        this.ntfyWebClient = ntfyWebClient;
        this.topic = topic;
    }

    public Mono<String> sendNotification(String title, String message) {
        log.info("Sending Ntfy notification to topic {}: {} - {}", topic, title, message);
        return ntfyWebClient.post()
                .uri("/{topic}", topic)
                .header("Title", title)
                .bodyValue(message)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Error sending Ntfy notification: {}", error.getMessage()));
    }
}
