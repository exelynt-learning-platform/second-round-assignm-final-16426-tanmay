package com.ecommerce.backend.controller;

// FIX: new controller – Product entity existed but had no controller or
//      repository, making the entity completely unreachable via the API.

import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepo;

    public ProductController(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    // GET /products – public (see SecurityConfig)
    @GetMapping
    public List<Product> listAll() {
        return productRepo.findAll();
    }

    // GET /products/{id} – public
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return productRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /products – requires valid JWT (see SecurityConfig)
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productRepo.save(product));
    }

    // PUT /products/{id} – requires valid JWT
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id,
                                          @RequestBody Product updated) {
        return productRepo.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setDescription(updated.getDescription());
            existing.setPrice(updated.getPrice());
            existing.setStock(updated.getStock());
            return ResponseEntity.ok(productRepo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /products/{id} – requires valid JWT
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!productRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
