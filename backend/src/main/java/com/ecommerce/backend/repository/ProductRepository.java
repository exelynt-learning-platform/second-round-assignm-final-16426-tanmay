package com.ecommerce.backend.repository;

// FIX: entirely new file – Product entity existed but had no repository,
//      making it impossible to persist or query products.

import com.ecommerce.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // JpaRepository provides findAll, findById, save, deleteById, existsById out-of-the-box
}
