package com.ecommerce.backend.repository;

// FIX: two copies existed:
//   • repository.UserRepository    – bare package, outside component scan;
//                                    imported com.ecommerce.backend.entity.User
//                                    (the stub class → JpaRepository typed to a stub)
//   • com.ecommerce.backend.repository.UserRepository – correct package, but also
//                                    pointed at the stub entity
//
// FIX: one file in the correct package importing the fixed, full entity.

import com.ecommerce.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // FIX: returns Optional<User> instead of nullable User so callers can avoid NPEs
    Optional<User> findByEmail(String email);
}
