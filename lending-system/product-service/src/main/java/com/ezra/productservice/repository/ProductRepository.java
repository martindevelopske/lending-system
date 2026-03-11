package com.ezra.productservice.repository;

import com.ezra.productservice.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsByName(String name);
}
