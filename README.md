# Task Management REST API

A professional REST API for Task Management built with Spring Boot, JPA/Hibernate, and H2 database.

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database** (in-memory)
- **Flyway** for database migrations
- **Lombok** for boilerplate reduction

## Project Structure

```
src/main/java/com/taskmanager/
├── config/          # Security and API key filter
├── controller/      # REST controllers
├── dto/             # Request/Response DTOs
├── entity/          # JPA entities
├── enums/           # TaskStatus, TaskPriority
├── exception/       # Custom exceptions and handlers
├── repository/      # Spring Data repositories
└── service/         # Business logic
```

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Run the Application

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

### H2 Console
Access the H2 database console at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:taskdb`
- Username: `sa`
- Password: (empty)

## API Authentication

All API endpoints require the `X-API-KEY` header.

**API Key:** `taskmanager-secret-api-key-2024`

Example:
```bash
curl -H "X-API-KEY: taskmanager-secret-api-key-2024" http://localhost:8080/api/users
```

## API Endpoints

### Users

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users` | Create a new user |
| GET | `/api/users` | List all users (paginated) |
| GET | `/api/users/{id}` | Get user by ID |

### Tasks

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/tasks` | Create a new task |
| GET | `/api/tasks` | List tasks (filtered, paginated) |
| GET | `/api/tasks/{id}` | Get task by ID |
| PUT | `/api/tasks/{id}` | Update task |
| PATCH | `/api/tasks/{id}/status` | Update task status |
| DELETE | `/api/tasks/{id}` | Delete task |

### Query Parameters for GET /api/tasks

- `status` - Filter by status (TODO, IN_PROGRESS, DONE)
- `priority` - Filter by priority (LOW, MEDIUM, HIGH)
- `assignedToId` - Filter by assigned user ID
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)

## Sample Requests

### Create User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: taskmanager-secret-api-key-2024" \
  -d '{"name": "John Doe", "email": "john@example.com"}'
```

### Create Task
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: taskmanager-secret-api-key-2024" \
  -d '{
    "title": "Complete API documentation",
    "description": "Write comprehensive API docs",
    "status": "TODO",
    "priority": "HIGH",
    "dueDate": "2024-12-31",
    "assignedToId": 1
  }'
```

### Get Tasks with Filters
```bash
curl "http://localhost:8080/api/tasks?status=TODO&priority=HIGH&page=0&size=10" \
  -H "X-API-KEY: taskmanager-secret-api-key-2024"
```

### Update Task Status
```bash
curl -X PATCH http://localhost:8080/api/tasks/1/status \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: taskmanager-secret-api-key-2024" \
  -d '{"status": "IN_PROGRESS"}'
```

## Error Responses

The API returns structured error responses:

```json
{
  "timestamp": "2024-01-08T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Task not found with id: 10",
  "path": "/api/tasks/10"
}
```

### HTTP Status Codes

| Code | Description |
|------|-------------|
| 200 | Success |
| 201 | Created |
| 204 | No Content (Delete) |
| 400 | Bad Request (Validation Error) |
| 401 | Unauthorized (Invalid/Missing API Key) |
| 404 | Not Found |
| 409 | Conflict (Duplicate email) |

## Database Migrations

Migrations are managed by Flyway and located in:
```
src/main/resources/db/migration/
├── V1__create_users_table.sql
└── V2__create_tasks_table.sql
```

## Design Decisions

1. **DTOs over Entities**: Request/Response DTOs prevent entity exposure and over-posting vulnerabilities
2. **Service Layer**: Business logic is separated from controllers for better testability
3. **Global Exception Handler**: Consistent error responses across all endpoints
4. **API Key Authentication**: Simple but effective authentication mechanism using filter
5. **Flyway Migrations**: Version-controlled database schema for production readiness
