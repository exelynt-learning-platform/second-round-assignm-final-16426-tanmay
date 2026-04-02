package com.ecommerce.backend.dto;
import jakarta.validation.constraints.NotBlank;

public class OrderRequest {
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    public String getShippingAddress()        { return shippingAddress; }
    public void setShippingAddress(String a)  { this.shippingAddress = a; }
}
