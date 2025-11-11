package com.tomcvt.brickshop.utility.mockpayment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mock-payment")
public class MockPaymentProvider {
    @PostMapping("/receive-token")
    public ResponseEntity<PaymentToken> receivePaymentToken() {
        // Simulate processing the payment token
        
        PaymentToken paymentToken = new PaymentToken("token-1234", "pending");
        return ResponseEntity.ok(paymentToken);
    }
    @GetMapping("/verify")
    public ResponseEntity<PaymentToken> verifyPaymentToken(@RequestParam String token) {
        // Simulate verifying the payment token
        
        PaymentToken paymentToken = new PaymentToken(token, "verified");
        return ResponseEntity.ok(paymentToken);
    }
}
