package com.ecommerce.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonIgnore
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull
    private Product product;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private int quantity;

    public CartItem() {}
    public CartItem(Cart cart, Product product, int quantity) {
        this.cart = cart; this.product = product; this.quantity = quantity;
    }

    public Long getId()                  { return id; }
    public void setId(Long id)           { this.id = id; }
    public Cart getCart()                { return cart; }
    public void setCart(Cart c)          { this.cart = c; }
    public Product getProduct()          { return product; }
    public void setProduct(Product p)    { this.product = p; }
    public int getQuantity()             { return quantity; }
    public void setQuantity(int q)       { this.quantity = q; }
}
