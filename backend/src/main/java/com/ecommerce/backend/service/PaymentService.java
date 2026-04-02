package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.PaymentRequest;
import com.ecommerce.backend.entity.*;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.PaymentRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final OrderRepository orderRepo;

    public PaymentService(PaymentRepository p, OrderRepository o) {
        this.paymentRepo = p; this.orderRepo = o;
    }

    @Transactional
    public Payment processPayment(PaymentRequest req, String email) {
        Order order = orderRepo.findById(req.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", req.getOrderId()));
        if (!order.getUser().getEmail().equals(email))
            throw new AccessDeniedException("You don't have access to this order");
        if (order.getStatus() == Order.OrderStatus.CANCELLED)
            throw new IllegalArgumentException("Cannot pay for a cancelled order");
        paymentRepo.findByOrderId(req.getOrderId()).ifPresent(p -> {
            if (p.getStatus() == Payment.PaymentStatus.COMPLETED)
                throw new IllegalArgumentException("Order has already been paid");
        });
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(req.getPaymentMethod());
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().toUpperCase());
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        order.setStatus(Order.OrderStatus.PROCESSING);
        orderRepo.save(order);
        return paymentRepo.save(payment);
    }

    public Payment getPaymentByOrder(Long orderId, String email) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        if (!order.getUser().getEmail().equals(email))
            throw new AccessDeniedException("You don't have access to this order");
        return paymentRepo.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No payment found for order: " + orderId));
    }

    @Transactional
    public Payment refund(Long orderId, String email) {
        Payment payment = getPaymentByOrder(orderId, email);
        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED)
            throw new IllegalArgumentException("Only completed payments can be refunded");
        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        payment.setUpdatedAt(LocalDateTime.now());
        payment.getOrder().setStatus(Order.OrderStatus.REFUNDED);
        orderRepo.save(payment.getOrder());
        return paymentRepo.save(payment);
    }
}
