package com.ecommerce.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 200, message = "Name must be 2-200 characters")
    @Column(nullable = false)
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Column(nullable = false)
    private Double price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    @Column(nullable = false)
    private Integer stock;

    @Column(name = "image_url")
    private String imageUrl;

    public Product() {}

    public Long getId()                    { return id; }
    public void setId(Long id)             { this.id = id; }
    public String getName()                { return name; }
    public void setName(String n)          { this.name = n; }
    public String getDescription()         { return description; }
    public void setDescription(String d)   { this.description = d; }
    public Double getPrice()               { return price; }
    public void setPrice(Double p)         { this.price = p; }
    public Integer getStock()              { return stock; }
    public void setStock(Integer s)        { this.stock = s; }
    public String getImageUrl()            { return imageUrl; }
    public void setImageUrl(String u)      { this.imageUrl = u; }
}
