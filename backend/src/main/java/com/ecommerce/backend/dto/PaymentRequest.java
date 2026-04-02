package com.ecommerce.backend.dto;
import com.ecommerce.backend.entity.Payment.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {
    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private String paymentToken;

    public Long getOrderId()                       { return orderId; }
    public void setOrderId(Long id)                { this.orderId = id; }
    public PaymentMethod getPaymentMethod()        { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod m)  { this.paymentMethod = m; }
    public String getPaymentToken()                { return paymentToken; }
    public void setPaymentToken(String t)          { this.paymentToken = t; }
}
