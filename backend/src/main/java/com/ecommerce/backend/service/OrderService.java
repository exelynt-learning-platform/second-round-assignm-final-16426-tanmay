package com.ecommerce.backend.service;

import com.ecommerce.backend.entity.*;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final CartRepository cartRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    public OrderService(OrderRepository o, CartRepository c, ProductRepository p, UserRepository u) {
        this.orderRepo = o; this.cartRepo = c; this.productRepo = p; this.userRepo = u;
    }

    @Transactional
    public Order placeOrder(String email, String shippingAddress) {
        User user = findUser(email);
        Cart cart = cartRepo.findByUserIdAndStatus(user.getId(), Cart.CartStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("No active cart found"));
        if (cart.getItems().isEmpty())
            throw new IllegalArgumentException("Cannot place order with an empty cart");

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);

        double total = 0;
        for (CartItem ci : cart.getItems()) {
            Product p = ci.getProduct();
            if (p.getStock() < ci.getQuantity())
                throw new IllegalArgumentException("Insufficient stock for: " + p.getName());
            OrderItem oi = new OrderItem(order, p, ci.getQuantity(), p.getPrice());
            order.getItems().add(oi);
            total += oi.getSubtotal();
            p.setStock(p.getStock() - ci.getQuantity());
            productRepo.save(p);
        }
        order.setTotalAmount(total);
        order.setStatus(Order.OrderStatus.CONFIRMED);
        cart.setStatus(Cart.CartStatus.CHECKED_OUT);
        cartRepo.save(cart);
        return orderRepo.save(order);
    }

    public Page<Order> getMyOrders(String email, Pageable pageable) {
        User user = findUser(email);
        return orderRepo.findByUserId(user.getId(), pageable);
    }

    public Order getById(Long id, String email) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        if (!order.getUser().getEmail().equals(email))
            throw new AccessDeniedException("You don't have access to this order");
        return order;
    }

    @Transactional
    public Order cancelOrder(Long id, String email) {
        Order order = getById(id, email);
        if (order.getStatus() == Order.OrderStatus.SHIPPED ||
            order.getStatus() == Order.OrderStatus.DELIVERED)
            throw new IllegalArgumentException("Cannot cancel an order that has been shipped or delivered");
        for (OrderItem item : order.getItems()) {
            Product p = item.getProduct();
            p.setStock(p.getStock() + item.getQuantity());
            productRepo.save(p);
        }
        order.setStatus(Order.OrderStatus.CANCELLED);
        return orderRepo.save(order);
    }

    @Transactional
    public Order updateStatus(Long id, Order.OrderStatus newStatus) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        order.setStatus(newStatus);
        return orderRepo.save(order);
    }

    private User findUser(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
}
