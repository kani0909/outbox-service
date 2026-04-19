package com.example.outboxservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.outboxservice.model.ProductRequest;
import com.example.outboxservice.service.ProductRequestService;

import java.util.Map;

@RestController
@RequestMapping("/api/requests")
public class RequestController {
    private final ProductRequestService requestService;

    public RequestController(ProductRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ResponseEntity<ProductRequest> createRequest(
            @RequestParam String userId,
            @RequestParam String productType) {

        ProductRequest request = requestService.createRequest(userId, productType);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "OK"));
    }
}
