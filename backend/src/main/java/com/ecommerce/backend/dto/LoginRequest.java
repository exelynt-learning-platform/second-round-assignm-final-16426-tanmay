package com.ecommerce.backend.dto;
import jakarta.validation.constraints.*;

public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    public String getEmail()          { return email; }
    public void setEmail(String e)    { this.email = e; }
    public String getPassword()       { return password; }
    public void setPassword(String p) { this.password = p; }
}
