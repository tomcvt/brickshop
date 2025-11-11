package com.tomcvt.brickshop.clients;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.tomcvt.brickshop.utility.mockpayment.PaymentToken;

import reactor.core.publisher.Mono;

@Service
public class PaymentProviderClient {
    
    private final WebClient webClientSelf;

    public PaymentProviderClient(@Qualifier("webClientSelf") WebClient webClientSelf) {
        this.webClientSelf = webClientSelf;
    }

    public Mono<PaymentToken> getPaymentToken(String paymentMethod, BigDecimal amount) {
        return webClientSelf.post()
                .uri("/mock-payment/receive-token")
                .bodyValue(Map.of("payment_method", paymentMethod, "amount", amount))
                .retrieve()
                .bodyToMono(PaymentToken.class);
    }

    public Mono<PaymentToken> verifyPaymentToken(String token) {
        return webClientSelf.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/mock-payment/verify")
                        .queryParam("token", token)
                        .build())
                .retrieve()
                .bodyToMono(PaymentToken.class);
    }
}
