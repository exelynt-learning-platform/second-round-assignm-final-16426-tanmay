package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.ApiResponse;
import com.ecommerce.backend.dto.CartItemRequest;
import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    public CartController(CartService s) { this.cartService = s; }

    @GetMapping
    public ResponseEntity<ApiResponse<Cart>> getCart(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(ApiResponse.ok("Cart retrieved", cartService.getOrCreateCart(email)));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<Cart>> addItem(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody CartItemRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Item added to cart", cartService.addItem(email, req)));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<Cart>> updateItem(
            @AuthenticationPrincipal String email,
            @PathVariable Long itemId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(ApiResponse.ok("Cart updated", cartService.updateItem(email, itemId, quantity)));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<Cart>> removeItem(
            @AuthenticationPrincipal String email,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponse.ok("Item removed", cartService.removeItem(email, itemId)));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal String email) {
        cartService.clearCart(email);
        return ResponseEntity.ok(ApiResponse.ok("Cart cleared", null));
    }
}
