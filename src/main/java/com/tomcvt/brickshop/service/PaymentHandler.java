package com.tomcvt.brickshop.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.tomcvt.brickshop.clients.PaymentProviderClient;
import com.tomcvt.brickshop.utility.mockpayment.PaymentToken;

@Service
public class PaymentHandler {
    private static final Logger log = LoggerFactory.getLogger(PaymentHandler.class);
    private final PaymentProviderClient paymentProviderClient;

    public PaymentHandler(PaymentProviderClient paymentProviderClient) {
        this.paymentProviderClient = paymentProviderClient;
    }

    public String getNewToken(Enum<?> paymentMethod, BigDecimal amount) {
        PaymentToken paymentToken = null;
        try {
            paymentToken = paymentProviderClient.getPaymentToken(paymentMethod.toString(), amount)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error while getting payment token: {}", e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while getting payment token", e);
            throw e;
        }
        if (paymentToken == null || paymentToken.token() == null) {
            log.error("Received null payment token from payment provider");
            throw new RuntimeException("Failed to obtain payment token");
        }
        if (paymentToken.status().equals("failed")) {
            log.error("Payment provider returned failure status for token request");
            throw new RuntimeException("Payment provider failed to create token");
        }
        if (paymentToken.status().equals("pending")) {
            log.info("Successfully obtained payment token");
            return paymentToken.token();
        }
        log.error("Unexpected payment token status: {}", paymentToken.status());
        throw new RuntimeException("Unexpected payment token status");
    }
}