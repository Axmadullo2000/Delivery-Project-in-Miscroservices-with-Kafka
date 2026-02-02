1. Заголовок и описание проекта
   markdown# 🍕 Food Delivery Microservices System

Современная микросервисная архитектура для системы доставки еды, построенная на Spring Boot 3.4.1 с использованием event-driven подхода через Apache Kafka.

## 📋 Описание

Проект демонстрирует полноценную микросервисную архитектуру с:
- Service Discovery через Eureka
- API Gateway для маршрутизации
- Event-driven коммуникация через Kafka
- Синхронные и асинхронные паттерны взаимодействия
- PostgreSQL для persistence каждого сервиса
2. Архитектура системы
   Включить ASCII диаграмму или описание:
   markdown## 🏗️ Архитектура

### Компоненты системы:

1. **API Gateway** (Port 8080) - Единая точка входа
2. **Eureka Server** (Port 8761) - Service Discovery
3. **Order Service** (Port 8081) - Управление заказами
4. **Payment Service** (Port 8082) - Обработка платежей
5. **Restaurant Service** (Port 8083) - Управление ресторанами
6. **Delivery Service** (Port 8084) - Управление доставкой

### Поток данных:
```
Client → API Gateway → Eureka (Service Discovery) → Microservice
                    ↓
                  Kafka (Events)
                    ↓
         All Services (Event Consumers)
```
3. Технологический стек
   markdown## 🛠️ Технологии

### Backend
- **Java**: 17
- **Spring Boot**: 3.4.1
- **Spring Cloud**: 2023.0.0
- **Build Tool**: Apache Maven 3.8+

### Микросервисы
- Spring Cloud Netflix Eureka (Service Discovery)
- Spring Cloud Gateway (API Gateway)
- Spring Data JPA (Data Access)
- Spring Kafka (Event Streaming)

### Базы данных
- PostgreSQL (для каждого сервиса отдельная БД)

### Message Broker
- Apache Kafka

### Дополнительно
- Lombok (уменьшение boilerplate)
- Resilience4j (Circuit Breaker)
4. Предварительные требования
   markdown## 📦 Предварительные требования

Перед запуском проекта убедитесь, что установлены:

- Java 17 или выше
- Apache Maven 3.8+
- PostgreSQL 14+
- Apache Kafka 3.x
- Docker (опционально, для контейнеризации)

### Проверка версий:
```bash
java -version
mvn -version
psql --version
```
5. Настройка базы данных
   markdown## 🗄️ Настройка PostgreSQL

Создайте базы данных для каждого сервиса:
```sql
CREATE DATABASE order_db;
CREATE DATABASE payment_db;
CREATE DATABASE restaurant_db;
CREATE DATABASE delivery_db;
```

По умолчанию используются credentials:
- Username: `postgres`
- Password: `postgres`
- Host: `localhost:5432`
6. Настройка Kafka
   markdown## 📨 Настройка Apache Kafka

### Запуск Kafka (с Zookeeper):
```bash
# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka
bin/kafka-server-start.sh config/server.properties
```

### Создание топиков:
```bash
kafka-topics.sh --create --topic order-events --bootstrap-server localhost:9092
kafka-topics.sh --create --topic payment-events --bootstrap-server localhost:9092
kafka-topics.sh --create --topic restaurant-events --bootstrap-server localhost:9092
kafka-topics.sh --create --topic delivery-events --bootstrap-server localhost:9092
```
7. Установка и запуск
   markdown## 🚀 Установка и запуск

### 1. Клонирование репозитория
```bash
git clone 
cd food-delivery-microservices
```

### 2. Сборка проекта
```bash
# Сборка всех модулей из корневой директории
mvn clean install

# Или сборка с пропуском тестов
mvn clean install -DskipTests
```

### 3. Порядок запуска сервисов

**ВАЖНО**: Запускайте сервисы в следующем порядке:
```bash
# 1. Eureka Server (должен запуститься первым)
cd eureka-server
mvn spring-boot:run

# 2. API Gateway (после того как Eureka поднялся)
cd api-gateway
mvn spring-boot:run

# 3. Микросервисы (можно запускать параллельно)
cd order-service && mvn spring-boot:run &
cd payment-service && mvn spring-boot:run &
cd restaurant-service && mvn spring-boot:run &
cd delivery-service && mvn spring-boot:run &
```

### Альтернативный способ (через JAR):
```bash
# Сборка
mvn clean package

# Запуск
java -jar eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar &
java -jar api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar &
java -jar order-service/target/order-service-0.0.1-SNAPSHOT.jar &
java -jar payment-service/target/payment-service-0.0.1-SNAPSHOT.jar &
java -jar restaurant-service/target/restaurant-service-0.0.1-SNAPSHOT.jar &
java -jar delivery-service/target/delivery-service-0.0.1-SNAPSHOT.jar &
```

### 4. Проверка работоспособности

- Eureka Dashboard: http://localhost:8761
- API Gateway: http://localhost:8080
8. API Endpoints
   markdown## 🔌 API Endpoints

Все запросы идут через API Gateway (http://localhost:8080)

### Order Service (`/api/orders`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders` | Создать новый заказ |
| GET | `/api/orders` | Получить все заказы |
| GET | `/api/orders/{id}` | Получить заказ по ID |

### Payment Service (`/api/payments`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/payments` | Создать платёж |
| GET | `/api/payments/{id}` | Получить платёж по ID |
| GET | `/api/payments/order/{orderId}` | Получить платёж по orderId |
| POST | `/api/payments/refund/{orderId}` | Инициировать возврат |
| GET | `/api/payments/refund/status/{orderId}` | Статус возврата |

### Restaurant Service (`/api/restaurants`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/restaurants` | Создать ресторан |
| GET | `/api/restaurants` | Получить все рестораны |
| GET | `/api/restaurants/{id}` | Получить ресторан по ID |

### Delivery Service (`/api/deliveries`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/deliveries` | Получить все доставки |
| GET | `/api/deliveries/{id}` | Получить доставку по ID |
| GET | `/api/deliveries/order/{orderId}` | Доставка по orderId |
| POST | `/api/deliveries/{id}/assign` | Назначить курьера |
| POST | `/api/deliveries/{id}/complete` | Завершить доставку |
| POST | `/api/deliveries/{id}/cancel` | Отменить доставку |
9. Примеры использования
   markdown## 💡 Примеры использования

### Создание заказа (полный flow)
```bash
# 1. Создать заказ
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "restaurantId": 1,
    "items": ["Pizza", "Cola"],
    "totalAmount": 25.50,
    "deliveryAddress": "123 Main St"
  }'

# Ответ: { "orderId": "550e8400-...", "status": "PENDING" }

# 2. Автоматически создаётся платёж (через Kafka)
# 3. После успешной оплаты создаётся доставка (через Kafka)

# 4. Назначить курьера
curl -X POST http://localhost:8080/api/deliveries/1/assign \
  -H "Content-Type: application/json" \
  -d '{"courierName": "John Doe"}'

# 5. Завершить доставку
curl -X POST http://localhost:8080/api/deliveries/1/complete
```

### Возврат средств
```bash
curl -X POST "http://localhost:8080/api/payments/refund/550e8400-...?reason=Customer%20request"

# Проверить статус возврата
curl http://localhost:8080/api/payments/refund/status/550e8400-...
```
10. Event Flow
    markdown## 🔄 Event-Driven Architecture

### Kafka Events Flow:

**Создание заказа:**
```
OrderCreatedEvent → Payment Service → PaymentSuccessEvent → Restaurant Service
                                                           → Delivery Service
```

**Возврат средств:**
```
RefundInitiatedEvent → Payment Service → PaymentRefundedEvent → Restaurant Service
                                                               → Order Service
```

**Доставка:**
```
PaymentSuccessEvent → Delivery Service → DeliveryAssignedEvent
                                       → DeliveryCompletedEvent
```

### Kafka Topics:
- `order-events` - события заказов
- `payment-events` - события платежей
- `restaurant-events` - события ресторанов
- `delivery-events` - события доставки
11. Структура проекта
    markdown## 📁 Структура проекта
```
food-delivery-microservices/
├── eureka-server/           # Service Discovery
├── api-gateway/             # API Gateway
├── order-service/           # Order Management
├── payment-service/         # Payment Processing
├── restaurant-service/      # Restaurant Management
├── delivery-service/        # Delivery Management
├── pom.xml                  # Parent POM file
└── README.md
```

### Maven Multi-Module структура:
```xml


eureka-server
        api-gateway
        order-service
        payment-service
        restaurant-service
        delivery-service

```
12. Troubleshooting
    markdown## 🔧 Troubleshooting

### Сервис не регистрируется в Eureka
- Проверьте, что Eureka Server запущен первым
- Убедитесь, что порт 8761 свободен
- Проверьте `eureka.client.service-url.defaultZone` в application.yml

### Kafka connection refused
- Убедитесь, что Kafka и Zookeeper запущены
- Проверьте `bootstrap-servers: localhost:9092`

### PostgreSQL connection error
- Проверьте, что все БД созданы
- Проверьте credentials в application.yml

### События не приходят
- Проверьте, что топики созданы
- Проверьте логи Kafka Consumer'ов
- Убедитесь, что `group-id` уникальны

### Maven build fails
- Убедитесь, что используете Java 17
- Выполните `mvn clean install` из корневой директории
- Проверьте, что все зависимости скачались: `mvn dependency:tree`
13. Roadmap и Future Features
    markdown## 🗺️ Roadmap

- [ ] Docker Compose для всех сервисов
- [ ] Kubernetes deployment manifests
- [ ] Centralized Configuration (Spring Cloud Config)
- [ ] Distributed Tracing (Sleuth + Zipkin)
- [ ] API Documentation (Swagger/OpenAPI)
- [ ] Authentication & Authorization (OAuth2/JWT)
- [ ] Monitoring (Prometheus + Grafana)
- [ ] WebSocket для real-time уведомлений
14. Участие и Лицензия
    markdown## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 👨‍💻 Author

Axmadullo Ubaydullayev

## 📧 Contact

For questions or support, please contact: axmadullo2000@gmail.com