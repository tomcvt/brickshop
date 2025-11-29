package com.tomcvt.brickshop.clients;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.tomcvt.brickshop.cvtcaptcha.CaptchaRequest;
import com.tomcvt.brickshop.cvtcaptcha.CaptchaResponse;
import com.tomcvt.brickshop.dto.CaptchaTokenResponse;
import com.tomcvt.brickshop.dto.CaptchaVerificationResponse;

import reactor.core.publisher.Mono;

@Service
public class CvtCaptchaClient {
    private volatile String cvtCaptchaKey;
    private final WebClient captchaWebClient;

    public CvtCaptchaClient(@Value("${com.tomcvt.cvtcaptcha.key}") String cvtCaptchaKey,
            @Qualifier("webClientCvtCaptcha") WebClient captchaWebClient) {
        this.cvtCaptchaKey = cvtCaptchaKey;
        this.captchaWebClient = captchaWebClient;
    }

    public void setCaptchaKey(String key) {
        this.cvtCaptchaKey = key;
    }

    public Mono<CaptchaVerificationResponse> verifyCaptcha(String token) {
        return captchaWebClient.post()
                .uri("/verify?token=" + token)
                .header("X-API-KEY", cvtCaptchaKey)
                .retrieve()
                .bodyToMono(CaptchaVerificationResponse.class);
    }

    public Mono<CaptchaResponse> getCaptcha(CaptchaRequest request) {
        return captchaWebClient.post()
                .uri("/create")
                .header("X-API-KEY", cvtCaptchaKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CaptchaResponse.class);
    }

    public Mono<CaptchaTokenResponse> getCaptchaToken() {
        return captchaWebClient.post()
                .uri("/token")
                .header("X-API-KEY", cvtCaptchaKey)
                .retrieve()
                .bodyToMono(CaptchaTokenResponse.class);
    }


}
