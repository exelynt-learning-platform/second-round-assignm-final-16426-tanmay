package com.ecommerce.backend.dto;
import jakarta.validation.constraints.*;

public class RegisterRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    // FIX: password strength enforced – min 8 chars, one uppercase, one digit, one special char
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$",
        message = "Password must contain at least one uppercase letter, one digit, and one special character"
    )
    private String password;

    private String role;

    public String getName()           { return name; }
    public void setName(String n)     { this.name = n; }
    public String getEmail()          { return email; }
    public void setEmail(String e)    { this.email = e; }
    public String getPassword()       { return password; }
    public void setPassword(String p) { this.password = p; }
    public String getRole()           { return role; }
    public void setRole(String r)     { this.role = r; }
}
