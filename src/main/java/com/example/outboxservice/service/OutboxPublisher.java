package com.example.outboxservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.outboxservice.model.Outbox;
import com.example.outboxservice.model.ProductRequestEvent;
import com.example.outboxservice.repository.OutboxRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, ProductRequestEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(OutboxRepository outboxRepository,
                           KafkaTemplate<String, ProductRequestEvent> kafkaTemplate,
                           ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    // fixedDelay = 5000 — запускается каждые 5 секунд после завершения предыдущего запуска
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishOutboxMessages() {
        // 1. Находит все PENDING сообщения
        List<Outbox> pendingMessages = outboxRepository.findPendingOutboxMessages();

        if (pendingMessages.isEmpty()) {
            return;
        }

        log.info("Found {} pending outbox messages", pendingMessages.size());

        // 2. Для каждого сообщения отправляет в Kafka
        for (Outbox message : pendingMessages) {
            try {
                // Десериализуем JSON обратно в объект
                ProductRequestEvent event = objectMapper.readValue(
                        message.getPayload(),
                        ProductRequestEvent.class
                );

                // Отправляем в Kafka
                kafkaTemplate.send("product-requests", event.getRequestId().toString(), event)
                        .whenComplete((result, ex) -> {
                            if (ex == null) {
                                // 3. Если отправилось успешно — меняем статус на SENT
                                message.setStatus("SENT");
                                message.setProcessedAt(LocalDateTime.now());
                                outboxRepository.save(message);
                                log.info("Published outbox message {} to Kafka", message.getId());
                            } else {
                                log.error("Failed to publish outbox message {}: {}",
                                        message.getId(), ex.getMessage());
                            }
                        });

            } catch (Exception e) {
                log.error("Error processing outbox message {}: {}",
                        message.getId(), e.getMessage());
            }
        }
    }
}
