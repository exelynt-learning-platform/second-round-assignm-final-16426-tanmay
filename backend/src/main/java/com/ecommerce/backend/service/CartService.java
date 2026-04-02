package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.CartItemRequest;
import com.ecommerce.backend.entity.*;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class CartService {

    private final CartRepository cartRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    public CartService(CartRepository c, ProductRepository p, UserRepository u) {
        this.cartRepo = c; this.productRepo = p; this.userRepo = u;
    }

    @Transactional
    public Cart getOrCreateCart(String email) {
        User user = findUser(email);
        return cartRepo.findByUserIdAndStatus(user.getId(), Cart.CartStatus.ACTIVE)
                .orElseGet(() -> cartRepo.save(new Cart(user)));
    }

    @Transactional
    public Cart addItem(String email, CartItemRequest req) {
        Cart cart = getOrCreateCart(email);
        Product product = productRepo.findById(req.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", req.getProductId()));
        if (product.getStock() < req.getQuantity())
            throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStock());
        cart.getItems().stream()
            .filter(i -> i.getProduct().getId().equals(req.getProductId()))
            .findFirst()
            .ifPresentOrElse(
                i -> i.setQuantity(i.getQuantity() + req.getQuantity()),
                () -> cart.getItems().add(new CartItem(cart, product, req.getQuantity()))
            );
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepo.save(cart);
    }

    @Transactional
    public Cart updateItem(String email, Long itemId, int quantity) {
        Cart cart = getOrCreateCart(email);
        CartItem item = cart.getItems().stream().filter(i -> i.getId().equals(itemId))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Cart item", itemId));
        if (quantity <= 0) cart.getItems().remove(item);
        else item.setQuantity(quantity);
        return cartRepo.save(cart);
    }

    @Transactional
    public Cart removeItem(String email, Long itemId) {
        Cart cart = getOrCreateCart(email);
        cart.getItems().removeIf(i -> i.getId().equals(itemId));
        return cartRepo.save(cart);
    }

    @Transactional
    public void clearCart(String email) {
        Cart cart = getOrCreateCart(email);
        cart.getItems().clear();
        cartRepo.save(cart);
    }

    private User findUser(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
}
