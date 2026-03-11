package com.ezra.productservice.repository;

import com.ezra.productservice.models.Fee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FeeRepository extends JpaRepository<Fee, UUID> {
}
