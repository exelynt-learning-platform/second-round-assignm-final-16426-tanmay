package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.ApiResponse;
import com.ecommerce.backend.dto.OrderRequest;
import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService s) { this.orderService = s; }

    @PostMapping
    public ResponseEntity<ApiResponse<Order>> placeOrder(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody OrderRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Order placed successfully",
                        orderService.placeOrder(email, req.getShippingAddress())));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Order>>> myOrders(
            @AuthenticationPrincipal String email,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok("Orders retrieved",
                orderService.getMyOrders(email,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getById(
            @AuthenticationPrincipal String email, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Order found", orderService.getById(id, email)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(
            @AuthenticationPrincipal String email, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Order cancelled", orderService.cancelOrder(id, email)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Order>> updateStatus(
            @PathVariable Long id, @RequestParam Order.OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.ok("Order status updated", orderService.updateStatus(id, status)));
    }
}
