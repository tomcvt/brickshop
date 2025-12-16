package com.tomcvt.brickshop.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.tomcvt.brickshop.clients.CvtCaptchaClient;
import com.tomcvt.brickshop.cvtcaptcha.CaptchaRequest;
import com.tomcvt.brickshop.cvtcaptcha.CaptchaResponse;
import com.tomcvt.brickshop.dto.CaptchaSolution;
import com.tomcvt.brickshop.dto.CaptchaTokenResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/captcha")
public class CaptchaApiController {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CaptchaApiController.class);
    private final CvtCaptchaClient cvtCaptchaClient;

    public CaptchaApiController(CvtCaptchaClient cvtCaptchaClient) {
        this.cvtCaptchaClient = cvtCaptchaClient;
    }
    //TODO add rate limiting to prevent abuse and origin validation
    @PostMapping("/create")
    public ResponseEntity<CaptchaResponse> createCaptcha(@RequestBody CaptchaRequest request, HttpServletRequest httpRequest) {
        CaptchaResponse captchaResponse = null;
        try {
            captchaResponse = cvtCaptchaClient.getCaptcha(request).block();
        } catch (WebClientResponseException e) {
            log.error("Error creating captcha: " + e.getResponseBodyAsString(), e);
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            log.error("Error creating captcha", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(captchaResponse);
    }

    @PostMapping("/solve")
    public ResponseEntity<CaptchaTokenResponse> getCaptchaToken(@RequestBody CaptchaSolution solution, HttpServletRequest httpRequest) {
        CaptchaTokenResponse token = null;
        try {
            token = cvtCaptchaClient.getCaptchaToken(solution).block();
        } catch (WebClientResponseException e) {
            log.error("Error getting captcha token: " + e.getResponseBodyAsString(), e);
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            log.error("Error getting captcha token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(token);
    }
    
}
