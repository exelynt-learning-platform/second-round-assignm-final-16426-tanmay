package com.ecommerce.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false)
    private double unitPrice;

    public OrderItem() {}
    public OrderItem(Order order, Product product, int quantity, double unitPrice) {
        this.order = order; this.product = product;
        this.quantity = quantity; this.unitPrice = unitPrice;
    }

    public double getSubtotal() { return unitPrice * quantity; }

    public Long getId()                 { return id; }
    public void setId(Long id)          { this.id = id; }
    public Order getOrder()             { return order; }
    public void setOrder(Order o)       { this.order = o; }
    public Product getProduct()         { return product; }
    public void setProduct(Product p)   { this.product = p; }
    public int getQuantity()            { return quantity; }
    public void setQuantity(int q)      { this.quantity = q; }
    public double getUnitPrice()        { return unitPrice; }
    public void setUnitPrice(double p)  { this.unitPrice = p; }
}
