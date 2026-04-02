package com.ecommerce.backend.entity;

// FIX: was split across TWO files in TWO different packages:
//   • entity.User           – full JPA entity but in a bare "entity" package
//                             (outside Spring's component scan)
//   • com.ecommerce.backend.entity.User – stub with only null-returning methods,
//                             missing @Entity, @Table, all fields, and all real getters/setters
//
// RESULT: JPA could not find any managed @Entity class → startup failure.
//
// FIX: one canonical User class in the correct package with all fields and
//      proper JPA annotations.

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    // NOTE: this field stores the BCrypt hash, never the plain-text password.
    @Column(nullable = false)
    private String password;

    // Stores a simple role string e.g. "USER" or "ADMIN".
    // For richer RBAC use a Role entity + ManyToMany instead.
    private String role;

    // ── Constructors ──────────────────────────────────────────────────────────

    public User() {}

    public User(Long id, String name, String email, String password, String role) {
        this.id       = id;
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId()                { return id; }
    public void setId(Long id)         { this.id = id; }

    public String getName()            { return name; }
    public void setName(String name)   { this.name = name; }

    public String getEmail()           { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword()               { return password; }
    public void setPassword(String password)  { this.password = password; }

    public String getRole()            { return role; }
    public void setRole(String role)   { this.role = role; }
}
