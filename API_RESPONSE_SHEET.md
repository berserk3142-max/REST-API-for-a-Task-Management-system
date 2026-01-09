# API Response Sheet - Task Management System

> **Complete API Documentation with Request/Response Examples**

---

## Table of Contents

1. [Base Information](#base-information)
2. [Authentication](#authentication)
3. [User API Endpoints](#user-api-endpoints)
4. [Task API Endpoints](#task-api-endpoints)
5. [Data Models](#data-models)
6. [Error Responses](#error-responses)
7. [HTTP Status Codes](#http-status-codes)
8. [Pagination](#pagination)

---

## Base Information

| Property | Value |
|----------|-------|
| **Base URL** | `https://your-app.onrender.com` or `http://localhost:8080` |
| **Content-Type** | `application/json` |
| **API Prefix** | `/api` |

---

## Authentication

This API uses **API Key Authentication** via the `X-API-KEY` header.

### Required Header

```http
X-API-KEY: your-api-key-here
```

### Example cURL Request

```bash
curl -X GET "https://your-app.onrender.com/api/tasks" \
  -H "X-API-KEY: your-api-key" \
  -H "Content-Type: application/json"
```

> [!IMPORTANT]
> All requests to `/api/**` endpoints require valid API key authentication.

---

## User API Endpoints

### 1. Create User

**Endpoint:** `POST /api/users`

**Description:** Creates a new user in the system.

#### Request Body

```json
{
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

#### Request Fields

| Field | Type | Required | Validation | Description |
|-------|------|----------|------------|-------------|
| `name` | String | Yes | Min 2 characters | User's full name |
| `email` | String | Yes | Valid email format | User's email address (must be unique) |

#### Success Response

**Status Code:** `201 Created`

```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

#### Error Responses

**Validation Error (400):**
```json
{
  "timestamp": "2026-01-09T01:23:32",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "name": "Name is required",
    "email": "Email must be valid"
  },
  "path": "/api/users"
}
```

**Duplicate Email (409):**
```json
{
  "timestamp": "2026-01-09T01:23:32",
  "status": 409,
  "error": "Conflict",
  "message": "User with email 'john.doe@example.com' already exists",
  "path": "/api/users"
}
```

---

### 2. Get All Users (Paginated)

**Endpoint:** `GET /api/users`

**Description:** Retrieves all users with pagination support.

#### Query Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | Integer | 0 | Page number (0-indexed) |
| `size` | Integer | 10 | Number of items per page |
| `sort` | String | `id` | Sort field (e.g., `id`, `name`, `email`) |

#### Example Request

```http
GET /api/users?page=0&size=10&sort=name,asc
```

#### Success Response

**Status Code:** `200 OK`

```json
{
  "content": [
    {
      "id": 1,
      "name": "Alice Johnson",
      "email": "alice@example.com"
    },
    {
      "id": 2,
      "name": "Bob Smith",
      "email": "bob@example.com"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 2,
  "last": true,
  "first": true,
  "size": 10,
  "number": 0,
  "numberOfElements": 2,
  "empty": false
}
```

---

### 3. Get User by ID

**Endpoint:** `GET /api/users/{id}`

**Description:** Retrieves a specific user by their ID.

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | Long | User's unique identifier |

#### Success Response

**Status Code:** `200 OK`

```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

#### Error Response

**Not Found (404):**
```json
{
  "timestamp": "2026-01-09T01:23:32",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 999",
  "path": "/api/users/999"
}
```

---

## Task API Endpoints

### 1. Create Task

**Endpoint:** `POST /api/tasks`

**Description:** Creates a new task.

#### Request Body

```json
{
  "title": "Complete API Documentation",
  "description": "Write comprehensive API documentation with examples",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2026-01-15",
  "assignedToId": 1
}
```

#### Request Fields

| Field | Type | Required | Validation | Description |
|-------|------|----------|------------|-------------|
| `title` | String | Yes | 3-100 characters | Task title |
| `description` | String | No | Max 500 characters | Detailed description |
| `status` | Enum | No | `TODO`, `IN_PROGRESS`, `DONE` | Task status (default: `TODO`) |
| `priority` | Enum | No | `LOW`, `MEDIUM`, `HIGH` | Priority level (default: `MEDIUM`) |
| `dueDate` | Date | No | Format: `YYYY-MM-DD` | Task due date |
| `assignedToId` | Long | No | Valid user ID | User to assign the task to |

#### Success Response

**Status Code:** `201 Created`

```json
{
  "id": 1,
  "title": "Complete API Documentation",
  "description": "Write comprehensive API documentation with examples",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2026-01-15",
  "createdAt": "2026-01-09T01:23:32",
  "updatedAt": "2026-01-09T01:23:32",
  "assignedTo": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com"
  }
}
```

#### Error Responses

**Validation Error (400):**
```json
{
  "timestamp": "2026-01-09T01:23:32",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "title": "Title must be between 3 and 100 characters"
  },
  "path": "/api/tasks"
}
```

**User Not Found (404):**
```json
{
  "timestamp": "2026-01-09T01:23:32",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 999",
  "path": "/api/tasks"
}
```

---

### 2. Get All Tasks (Paginated with Filters)

**Endpoint:** `GET /api/tasks`

**Description:** Retrieves all tasks with optional filters and pagination.

#### Query Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `status` | Enum | - | Filter by status: `TODO`, `IN_PROGRESS`, `DONE` |
| `priority` | Enum | - | Filter by priority: `LOW`, `MEDIUM`, `HIGH` |
| `assignedToId` | Long | - | Filter by assigned user ID |
| `page` | Integer | 0 | Page number (0-indexed) |
| `size` | Integer | 10 | Number of items per page |
| `sort` | String | `createdAt` | Sort field |

#### Example Requests

```http
# Get all tasks
GET /api/tasks

# Get high priority TODO tasks
GET /api/tasks?status=TODO&priority=HIGH

# Get tasks assigned to user 1, sorted by due date
GET /api/tasks?assignedToId=1&sort=dueDate,asc

# Pagination example
GET /api/tasks?page=0&size=5&sort=createdAt,desc
```

#### Success Response

**Status Code:** `200 OK`

```json
{
  "content": [
    {
      "id": 1,
      "title": "Complete API Documentation",
      "description": "Write comprehensive API documentation",
      "status": "IN_PROGRESS",
      "priority": "HIGH",
      "dueDate": "2026-01-15",
      "createdAt": "2026-01-09T01:23:32",
      "updatedAt": "2026-01-09T02:00:00",
      "assignedTo": {
        "id": 1,
        "name": "John Doe",
        "email": "john.doe@example.com"
      }
    },
    {
      "id": 2,
      "title": "Review Code Changes",
      "description": "Review pull request #42",
      "status": "TODO",
      "priority": "MEDIUM",
      "dueDate": "2026-01-10",
      "createdAt": "2026-01-08T10:00:00",
      "updatedAt": "2026-01-08T10:00:00",
      "assignedTo": null
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 2,
  "last": true,
  "first": true,
  "size": 10,
  "number": 0,
  "numberOfElements": 2,
  "empty": false
}
```

---

### 3. Get Task by ID

**Endpoint:** `GET /api/tasks/{id}`

**Description:** Retrieves a specific task by its ID.

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | Long | Task's unique identifier |

#### Success Response

**Status Code:** `200 OK`

```json
{
  "id": 1,
  "title": "Complete API Documentation",
  "description": "Write comprehensive API documentation with examples",
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "dueDate": "2026-01-15",
  "createdAt": "2026-01-09T01:23:32",
  "updatedAt": "2026-01-09T02:00:00",
  "assignedTo": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com"
  }
}
```

#### Error Response

**Not Found (404):**
```json
{
  "timestamp": "2026-01-09T01:23:32",
  "status": 404,
  "error": "Not Found",
  "message": "Task not found with id: 999",
  "path": "/api/tasks/999"
}
```

---

### 4. Update Task (Full Update)

**Endpoint:** `PUT /api/tasks/{id}`

**Description:** Fully updates an existing task.

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | Long | Task's unique identifier |

#### Request Body

```json
{
  "title": "Complete API Documentation - Updated",
  "description": "Updated description with more details",
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "dueDate": "2026-01-20",
  "assignedToId": 2
}
```

#### Request Fields

| Field | Type | Required | Validation | Description |
|-------|------|----------|------------|-------------|
| `title` | String | Yes | 3-100 characters | Task title |
| `description` | String | No | Max 500 characters | Detailed description |
| `status` | Enum | No | `TODO`, `IN_PROGRESS`, `DONE` | Task status |
| `priority` | Enum | No | `LOW`, `MEDIUM`, `HIGH` | Priority level |
| `dueDate` | Date | No | Format: `YYYY-MM-DD` | Task due date |
| `assignedToId` | Long | No | Valid user ID | User to assign the task to |

#### Success Response

**Status Code:** `200 OK`

```json
{
  "id": 1,
  "title": "Complete API Documentation - Updated",
  "description": "Updated description with more details",
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "dueDate": "2026-01-20",
  "createdAt": "2026-01-09T01:23:32",
  "updatedAt": "2026-01-09T03:00:00",
  "assignedTo": {
    "id": 2,
    "name": "Jane Smith",
    "email": "jane@example.com"
  }
}
```

---

### 5. Update Task Status (Partial Update)

**Endpoint:** `PATCH /api/tasks/{id}/status`

**Description:** Updates only the status of a task.

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | Long | Task's unique identifier |

#### Request Body

```json
{
  "status": "DONE"
}
```

#### Request Fields

| Field | Type | Required | Validation | Description |
|-------|------|----------|------------|-------------|
| `status` | Enum | Yes | `TODO`, `IN_PROGRESS`, `DONE` | New task status |

#### Success Response

**Status Code:** `200 OK`

```json
{
  "id": 1,
  "title": "Complete API Documentation",
  "description": "Write comprehensive API documentation with examples",
  "status": "DONE",
  "priority": "HIGH",
  "dueDate": "2026-01-15",
  "createdAt": "2026-01-09T01:23:32",
  "updatedAt": "2026-01-09T04:00:00",
  "assignedTo": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com"
  }
}
```

#### Error Response

**Validation Error (400):**
```json
{
  "timestamp": "2026-01-09T01:23:32",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "status": "Status is required"
  },
  "path": "/api/tasks/1/status"
}
```

---

### 6. Delete Task

**Endpoint:** `DELETE /api/tasks/{id}`

**Description:** Permanently deletes a task.

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | Long | Task's unique identifier |

#### Success Response

**Status Code:** `204 No Content`

*(No response body)*

#### Error Response

**Not Found (404):**
```json
{
  "timestamp": "2026-01-09T01:23:32",
  "status": 404,
  "error": "Not Found",
  "message": "Task not found with id: 999",
  "path": "/api/tasks/999"
}
```

---

## Data Models

### UserResponse

```json
{
  "id": "Long",
  "name": "String",
  "email": "String"
}
```

### TaskResponse

```json
{
  "id": "Long",
  "title": "String",
  "description": "String (nullable)",
  "status": "Enum (TODO | IN_PROGRESS | DONE)",
  "priority": "Enum (LOW | MEDIUM | HIGH)",
  "dueDate": "LocalDate (nullable, format: YYYY-MM-DD)",
  "createdAt": "LocalDateTime (format: YYYY-MM-DDTHH:mm:ss)",
  "updatedAt": "LocalDateTime (format: YYYY-MM-DDTHH:mm:ss)",
  "assignedTo": "UserResponse (nullable)"
}
```

### Enums

#### TaskStatus
| Value | Description |
|-------|-------------|
| `TODO` | Task not yet started |
| `IN_PROGRESS` | Task is currently being worked on |
| `DONE` | Task is completed |

#### TaskPriority
| Value | Description |
|-------|-------------|
| `LOW` | Low priority task |
| `MEDIUM` | Medium priority task (default) |
| `HIGH` | High priority, urgent task |

---

## Error Responses

### Standard Error Response

```json
{
  "timestamp": "2026-01-09T01:23:32",
  "status": 404,
  "error": "Not Found",
  "message": "Task not found with id: 999",
  "path": "/api/tasks/999"
}
```

### Validation Error Response

```json
{
  "timestamp": "2026-01-09T01:23:32",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "fieldName": "Error message"
  },
  "path": "/api/resource"
}
```

### Error Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `timestamp` | LocalDateTime | When the error occurred |
| `status` | Integer | HTTP status code |
| `error` | String | Error type description |
| `message` | String | Detailed error message |
| `errors` | Object | Field-specific validation errors (only for validation errors) |
| `path` | String | Request path that caused the error |

---

## HTTP Status Codes

| Code | Status | Description |
|------|--------|-------------|
| `200` | OK | Request successful |
| `201` | Created | Resource created successfully |
| `204` | No Content | Request successful, no content to return (DELETE) |
| `400` | Bad Request | Validation failed or malformed request |
| `401` | Unauthorized | Missing or invalid API key |
| `403` | Forbidden | Valid API key but insufficient permissions |
| `404` | Not Found | Requested resource not found |
| `409` | Conflict | Resource conflict (e.g., duplicate email) |
| `500` | Internal Server Error | Unexpected server error |

---

## Pagination

All list endpoints return paginated responses using Spring's `Page` object.

### Pagination Query Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | Integer | 0 | Page number (0-indexed) |
| `size` | Integer | 10 | Number of items per page |
| `sort` | String | varies | Sort field and direction (e.g., `createdAt,desc`) |

### Pagination Response Structure

```json
{
  "content": [],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 5,
  "totalElements": 42,
  "last": false,
  "first": true,
  "size": 10,
  "number": 0,
  "numberOfElements": 10,
  "empty": false
}
```

### Pagination Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `content` | Array | The actual data items |
| `totalPages` | Integer | Total number of pages |
| `totalElements` | Long | Total number of items across all pages |
| `first` | Boolean | True if this is the first page |
| `last` | Boolean | True if this is the last page |
| `size` | Integer | Items per page (requested) |
| `number` | Integer | Current page number (0-indexed) |
| `numberOfElements` | Integer | Number of elements on current page |
| `empty` | Boolean | True if page has no content |

---

## cURL Examples

### Create a User

```bash
curl -X POST "http://localhost:8080/api/users" \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: your-api-key" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com"
  }'
```

### Create a Task

```bash
curl -X POST "http://localhost:8080/api/tasks" \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: your-api-key" \
  -d '{
    "title": "Complete Documentation",
    "description": "Write API docs",
    "status": "TODO",
    "priority": "HIGH",
    "dueDate": "2026-01-15",
    "assignedToId": 1
  }'
```

### Get All Tasks with Filters

```bash
curl -X GET "http://localhost:8080/api/tasks?status=TODO&priority=HIGH&page=0&size=5" \
  -H "X-API-KEY: your-api-key"
```

### Update Task Status

```bash
curl -X PATCH "http://localhost:8080/api/tasks/1/status" \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: your-api-key" \
  -d '{"status": "DONE"}'
```

### Delete a Task

```bash
curl -X DELETE "http://localhost:8080/api/tasks/1" \
  -H "X-API-KEY: your-api-key"
```

---

## Quick Reference Card

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/users` | Create user |
| `GET` | `/api/users` | List users (paginated) |
| `GET` | `/api/users/{id}` | Get user by ID |
| `POST` | `/api/tasks` | Create task |
| `GET` | `/api/tasks` | List tasks (filtered, paginated) |
| `GET` | `/api/tasks/{id}` | Get task by ID |
| `PUT` | `/api/tasks/{id}` | Full update task |
| `PATCH` | `/api/tasks/{id}/status` | Update task status only |
| `DELETE` | `/api/tasks/{id}` | Delete task |

---

> **Last Updated:** January 9, 2026  
> **Version:** 1.0.0
