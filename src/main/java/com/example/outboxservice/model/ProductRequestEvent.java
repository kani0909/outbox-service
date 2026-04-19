package com.example.outboxservice.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProductRequestEvent {
    private UUID requestId;
    private String userId;
    private String productType;
    private String eventType;
    private LocalDateTime timestamp;

    public ProductRequestEvent() {
    }

    public ProductRequestEvent(UUID requestId, String userId, String productType,
                               String eventType, LocalDateTime timestamp) {
        this.requestId = requestId;
        this.userId = userId;
        this.productType = productType;
        this.eventType = eventType;
        this.timestamp = timestamp;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ProductRequestEvent{" +
                "requestId=" + requestId +
                ", userId='" + userId + '\'' +
                ", productType='" + productType + '\'' +
                ", eventType='" + eventType + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
