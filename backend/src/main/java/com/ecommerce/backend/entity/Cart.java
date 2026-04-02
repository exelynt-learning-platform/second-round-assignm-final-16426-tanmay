package com.ecommerce.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CartStatus status = CartStatus.ACTIVE;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum CartStatus { ACTIVE, CHECKED_OUT, ABANDONED }

    public Cart() {}
    public Cart(User user) { this.user = user; }

    public double getTotalAmount() {
        return items.stream()
            .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
            .sum();
    }

    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }
    public User getUser()                       { return user; }
    public void setUser(User u)                 { this.user = u; }
    public List<CartItem> getItems()            { return items; }
    public void setItems(List<CartItem> items)  { this.items = items; }
    public CartStatus getStatus()               { return status; }
    public void setStatus(CartStatus s)         { this.status = s; }
    public LocalDateTime getCreatedAt()         { return createdAt; }
    public LocalDateTime getUpdatedAt()         { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t)   { this.updatedAt = t; }
}
