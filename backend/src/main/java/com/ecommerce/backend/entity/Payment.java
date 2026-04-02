package com.ecommerce.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    @JsonIgnore
    private Order order;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum PaymentStatus  { PENDING, COMPLETED, FAILED, REFUNDED }
    public enum PaymentMethod  { CREDIT_CARD, DEBIT_CARD, PAYPAL, STRIPE, BANK_TRANSFER }

    public Payment() {}

    public Long getId()                       { return id; }
    public void setId(Long id)                { this.id = id; }
    public Order getOrder()                   { return order; }
    public void setOrder(Order o)             { this.order = o; }
    public double getAmount()                 { return amount; }
    public void setAmount(double a)           { this.amount = a; }
    public PaymentStatus getStatus()          { return status; }
    public void setStatus(PaymentStatus s)    { this.status = s; }
    public PaymentMethod getPaymentMethod()   { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod m) { this.paymentMethod = m; }
    public String getTransactionId()          { return transactionId; }
    public void setTransactionId(String t)    { this.transactionId = t; }
    public LocalDateTime getCreatedAt()       { return createdAt; }
    public LocalDateTime getUpdatedAt()       { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t) { this.updatedAt = t; }
}
