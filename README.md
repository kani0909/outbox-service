# Outbox Service - Transactional Outbox Pattern

Сервис, демонстрирующий паттерн Transactional Outbox для надежной доставки событий в распределенных системах.

## Технологии
- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL (JSONB)
- Apache Kafka
- Lombok

## API Endpoints

### Создать заявку
POST /api/requests?userId={userId}&productType={productType}
### Health check
GET /api/requests/health
## Как это работает

1. **Создание заявки** - бизнес-данные и событие сохраняются в одной транзакции
2. **OutboxPublisher** - каждые 5 секунд проверяет PENDING сообщения
3. **Отправка в Kafka** - при успехе статус меняется на SENT
4. **Повторные попытки** - если Kafka недоступна, сообщение останется PENDING

## Запуск

```bash
mvn spring-boot:run
