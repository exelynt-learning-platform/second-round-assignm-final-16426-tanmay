package com.ecommerce.backend.service;

import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) { this.repo = repo; }

    public Page<Product> listAll(String search, Pageable pageable) {
        if (search != null && !search.isBlank())
            return repo.findByNameContainingIgnoreCase(search, pageable);
        return repo.findAll(pageable);
    }

    public Product getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    @Transactional
    public Product create(Product p) {
        if (repo.existsByNameIgnoreCase(p.getName()))
            throw new IllegalArgumentException("Product '" + p.getName() + "' already exists");
        return repo.save(p);
    }

    @Transactional
    public Product update(Long id, Product updated) {
        Product existing = getById(id);
        repo.findByNameIgnoreCase(updated.getName()).ifPresent(found -> {
            if (!found.getId().equals(id))
                throw new IllegalArgumentException("Product '" + updated.getName() + "' already exists");
        });
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setStock(updated.getStock());
        existing.setImageUrl(updated.getImageUrl());
        return repo.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Product", id);
        repo.deleteById(id);
    }
}
