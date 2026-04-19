package com.example.outboxservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.outboxservice.model.Outbox;
import com.example.outboxservice.model.ProductRequest;
import com.example.outboxservice.model.ProductRequestEvent;
import com.example.outboxservice.repository.OutboxRepository;
import com.example.outboxservice.repository.ProductRequestRepository;

import java.time.LocalDateTime;

@Service
public class ProductRequestService {

    private static final Logger log = LoggerFactory.getLogger(ProductRequestService.class);

    private final ProductRequestRepository requestRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public ProductRequestService(ProductRequestRepository requestRepository,
                                 OutboxRepository outboxRepository,
                                 ObjectMapper objectMapper) {
        this.requestRepository = requestRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ProductRequest createRequest(String userId, String productType) {
        // 1. Сохраняем бизнес-данные
        ProductRequest request = new ProductRequest();
        request.setUserId(userId);
        request.setProductType(productType);
        request.setStatus("NEW");

        request = requestRepository.save(request);
        log.info("Created product request: {}", request.getId());

        // 2. Создаём событие для outbox
        ProductRequestEvent event = new ProductRequestEvent(
                request.getId(),
                userId,
                productType,
                "PRODUCT_REQUEST_CREATED",
                LocalDateTime.now()
        );

        try {
            // 3. Сохраняем в outbox (в той же транзакции!)
            Outbox outbox = new Outbox();
            outbox.setAggregateId(request.getId().toString());
            outbox.setEventType("PRODUCT_REQUEST_CREATED");
            outbox.setPayload(objectMapper.writeValueAsString(event));
            outbox.setStatus("PENDING");

            outboxRepository.save(outbox);
            log.info("Saved outbox message for request: {}", request.getId());
        } catch (Exception e) {
            log.error("Failed to serialize event", e);
            throw new RuntimeException("Failed to serialize event", e);
        }

        return request;
    }
}
