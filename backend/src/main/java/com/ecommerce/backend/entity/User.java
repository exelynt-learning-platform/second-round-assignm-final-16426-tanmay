package com.ecommerce.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Cart> carts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();

    public enum Role { USER, ADMIN }

    public User() {}

    public Long getId()               { return id; }
    public void setId(Long id)        { this.id = id; }
    public String getName()           { return name; }
    public void setName(String n)     { this.name = n; }
    public String getEmail()          { return email; }
    public void setEmail(String e)    { this.email = e; }
    public String getPassword()       { return password; }
    public void setPassword(String p) { this.password = p; }
    public Role getRole()             { return role; }
    public void setRole(Role r)       { this.role = r; }
    public List<Cart> getCarts()      { return carts; }
    public List<Order> getOrders()    { return orders; }
}
