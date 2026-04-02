package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.ApiResponse;
import com.ecommerce.backend.dto.PaymentRequest;
import com.ecommerce.backend.entity.Payment;
import com.ecommerce.backend.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    public PaymentController(PaymentService s) { this.paymentService = s; }

    @PostMapping
    public ResponseEntity<ApiResponse<Payment>> processPayment(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody PaymentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Payment processed successfully",
                        paymentService.processPayment(req, email)));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<Payment>> getPayment(
            @AuthenticationPrincipal String email, @PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.ok("Payment found",
                paymentService.getPaymentByOrder(orderId, email)));
    }

    @PostMapping("/order/{orderId}/refund")
    public ResponseEntity<ApiResponse<Payment>> refund(
            @AuthenticationPrincipal String email, @PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.ok("Payment refunded",
                paymentService.refund(orderId, email)));
    }
}
