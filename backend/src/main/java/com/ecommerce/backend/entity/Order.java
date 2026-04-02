package com.ecommerce.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum OrderStatus {
        PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUNDED
    }

    public Order() {}

    public Long getId()                       { return id; }
    public void setId(Long id)                { this.id = id; }
    public User getUser()                     { return user; }
    public void setUser(User u)               { this.user = u; }
    public List<OrderItem> getItems()         { return items; }
    public void setItems(List<OrderItem> i)   { this.items = i; }
    public double getTotalAmount()            { return totalAmount; }
    public void setTotalAmount(double t)      { this.totalAmount = t; }
    public OrderStatus getStatus()            { return status; }
    public void setStatus(OrderStatus s)      { this.status = s; }
    public String getShippingAddress()        { return shippingAddress; }
    public void setShippingAddress(String a)  { this.shippingAddress = a; }
    public LocalDateTime getCreatedAt()       { return createdAt; }
    public LocalDateTime getUpdatedAt()       { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t) { this.updatedAt = t; }
}
