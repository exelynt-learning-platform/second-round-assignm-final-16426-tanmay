package com.ecommerce.backend.controller;

// FIX: was in bare package "controller" → outside Spring component scan → 404 on every call.
// FIX: register stored plain-text passwords (no BCrypt encoding).
// FIX: login compared plain-text strings (== instead of BCrypt.matches).
// FIX: login returned a plain string instead of a JWT token.
// FIX: UserRepository.findByEmail returned nullable User → potential NPE.
//      Now uses Optional<User>.

import com.ecommerce.backend.config.JwtUtil;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository    userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil           jwtUtil;

    public AuthController(UserRepository userRepo,
                          BCryptPasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.userRepo        = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil         = jwtUtil;
    }

    // ── POST /auth/register ───────────────────────────────────────────────────

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {

        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email already registered"));
        }

        // FIX: hash the password before persisting – never store plain text
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Default role if caller did not specify one
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }

        User saved = userRepo.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                    "message", "User registered successfully",
                    "id",      saved.getId(),
                    "email",   saved.getEmail()
                ));
    }

    // ── POST /auth/login ──────────────────────────────────────────────────────

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {

        // FIX: use Optional to avoid NullPointerException when user is not found
        Optional<User> dbUserOpt = userRepo.findByEmail(user.getEmail());

        if (dbUserOpt.isEmpty()) {
            // FIX: return 401 with a JSON body instead of a plain string
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }

        User dbUser = dbUserOpt.get();

        // FIX: compare using BCrypt – never compare plain-text strings
        if (!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }

        // FIX: issue a JWT token instead of returning a plain "Login Success" string
        String token = jwtUtil.generateToken(dbUser.getEmail(), dbUser.getRole());
        return ResponseEntity.ok(Map.of(
            "token", token,
            "type",  "Bearer",
            "email", dbUser.getEmail(),
            "role",  dbUser.getRole()
        ));
    }
}
