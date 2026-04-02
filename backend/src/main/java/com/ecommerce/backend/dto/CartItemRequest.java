package com.ecommerce.backend.dto;
import jakarta.validation.constraints.*;

public class CartItemRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    public Long getProductId()         { return productId; }
    public void setProductId(Long id)  { this.productId = id; }
    public int getQuantity()           { return quantity; }
    public void setQuantity(int q)     { this.quantity = q; }
}
