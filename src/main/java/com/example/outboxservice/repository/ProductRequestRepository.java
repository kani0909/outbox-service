package com.example.outboxservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.outboxservice.model.ProductRequest;

import java.util.UUID;

public interface ProductRequestRepository extends JpaRepository<ProductRequest, UUID> {

}
