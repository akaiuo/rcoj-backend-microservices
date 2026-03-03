# Whoj Backend - Online Judge System Backend

Whoj Backend is a microservice-based backend for an Online Judge (OJ) system, supporting user management, problem management, code submission and evaluation, forum discussions, and more.

## System Architecture

This project adopts a Spring Cloud microservices architecture, consisting of the following service modules:

| Service Module | Port | Description |
|----------------|------|-------------|
| whoj-backend-gateway | 8101 | API Gateway, unified entry point |
| whoj-backend-user-service | 8102 | User Service (registration, login, management) |
| whoj-backend-question-service | 8102 | Problem Service (problem management, submission records) |
| whoj-backend-judge-service | 8102 | Judge Service (code execution, result determination) |
| whoj-backend-post-service | 8102 | Post Service (forum, comments, interaction) |
| whoj-backend-validation-service | 8102 | Validation Service (email verification, Redis) |

## Technology Stack

- **Core Framework**: Spring Boot 2.x / Spring Cloud
- **Database**: MySQL + MyBatis-Plus
- **Cache**: Redis
- **Message Queue**: RabbitMQ
- **Service Registry**: Nacos
- **API Gateway**: Spring Cloud Gateway
- **Remote Invocation**: OpenFeign
- **Containerization**: Docker

## Key Features

### User Module
- User registration (with email verification code)
- User login/logout
- User profile management
- Administrator permission control

### Problem Module
- CRUD operations for problems
- Problem categorization and tagging
- Problem difficulty configuration (time limit, memory limit)
- Code submission and status tracking

### Judge Module
- Multi-language support (Java, Python, C++, etc.)
- Code sandbox execution
- Batch evaluation with multiple test cases
- Result analysis (execution time, memory usage, output)

### Post Module
- Post creation
- Comments and replies
- Like, favorite, and follow functionality
- User interaction

## Quick Start

### Prerequisites
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6+
- RabbitMQ 3.8+
- Nacos 2.x

### Database Initialization

```sql
-- Create database
CREATE DATABASE whoj_backend;

-- Import SQL scripts (located in the sql/ directory)
```

### Configuration Updates

Modify the `application.yml` or `application-prod.yml` files in each service module:

```yaml
# Database configuration
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/whoj_backend
    username: your_username
    password: your_password

# Redis configuration
  redis:
    host: localhost
    port: 6379

# Nacos configuration
  cloud:
    nacos:
      server-addr: localhost:8848
```

### Starting Services

1. Start the Nacos registry center
2. Start Redis
3. Start RabbitMQ
4. Start each microservice in sequence:

```bash
# Build the project
mvn clean install -DskipTests

# Start the gateway
cd whoj-backend-gateway
mvn spring-boot:run

# Start the user service
cd whoj-backend-user-service
mvn spring-boot:run

# Start other services...
```

### Docker Deployment

```bash
# Start using docker-compose
docker-compose -f docker-compose.service.yml up -d
```

## API Documentation

### User Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/user/register | User registration |
| POST | /api/user/login | User login |
| POST | /api/user/logout | User logout |
| GET | /api/user/get/login | Get current logged-in user |
| PUT | /api/user/update/my | Update personal profile |

### Problem Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/question/add | Add a new problem |
| POST | /api/question/submit/do | Submit code |
| POST | /api/question/list/page/vo | Paginated problem query |
| GET | /api/question/get/vo | Get problem details |

### Post Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/post/add | Create a post |
| GET | /api/post/get | Get post details |
| POST | /api/post/list/page/vo | Paginated post query |
| POST | /api/post/comment/add | Add a comment |

## Project Structure

```
whoj-backend-microservice/
├── whoj-backend-common/          # Common module
│   ├── annotation/               # Custom annotations
│   ├── common/                   # Generic response classes
│   ├── config/                   # Configuration classes
│   ├── constant/                 # Constant definitions
│   ├── exception/                # Exception handling
│   └── utils/                    # Utility classes
├── whoj-backend-gateway/         # Gateway service
├── whoj-backend-judge-service/  # Judge service
│   ├── judge/                    # Core judging logic
│   │   ├── strategy/             # Judging strategies
│   │   └── codeSandbox/          # Code sandbox
│   └── message/                  # Message handling
├── whoj-backend-model/           # Data models
│   ├── model/entity/             # Database entities
│   ├── model/dto/                # Data transfer objects
│   ├── model/vo/                 # View objects
│   └── model/enums/              # Enumerations
├── whoj-backend-post-service/    # Post service
├── whoj-backend-question-service/ # Problem service
├── whoj-backend-service-client/  # Feign clients
├── whoj-backend-user-service/    # User service
└── whoj-backend-validation-service/ # Validation service
```

## License

This project is open-sourced under the MIT License.