package com.ecommerce.backend.entity;

// FIX: was in bare package "entity" → outside Spring component scan.
// Moved to com.ecommerce.backend.entity so @Entity is detected by Hibernate.

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private double price;

    private int stock;

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId()                       { return id; }
    public void setId(Long id)                { this.id = id; }

    public String getName()                   { return name; }
    public void setName(String name)          { this.name = name; }

    public String getDescription()                    { return description; }
    public void setDescription(String description)    { this.description = description; }

    public double getPrice()                  { return price; }
    public void setPrice(double price)        { this.price = price; }

    public int getStock()                     { return stock; }
    public void setStock(int stock)           { this.stock = stock; }
}
