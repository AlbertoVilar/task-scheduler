# Task Scheduler API

This project is a Spring Boot service that schedules tasks and stores them in MongoDB. It uses JWT Bearer authentication to identify the user creating a task.

## Overview & Architecture

- Stack: Spring Boot 3, MongoDB, Spring Security (JWT), Lombok.
- Create flow (`POST /tasks`): request DTO → token resolves `userEmail/userId` → entity with `status=PENDING` → auditing fills dates → persist → response DTO.
- Read flow (`GET /tasks/{id}`): fetch by id → return response DTO.

## Run Locally

- Build and test: `./mvnw.cmd clean install -DskipTests=false`
- Start the app: `java -jar target/task-scheduler-0.0.1-SNAPSHOT.jar --jwt.secret=your-dev-secret`

The app listens on `http://localhost:9090/` and connects to MongoDB at `mongodb://localhost:27017/db_agendador` by default. Configure these values via properties.

## Docker Dev Mode (no rebuilds)

Use the provided `docker-compose.dev.yml` to run the app in dev mode inside a Maven container with your source code mounted. This avoids creating a new image for each code change.

- Start dev stack (app + Mongo):
  - `docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d task-scheduler`
- Stop dev stack:
  - `docker compose -f docker-compose.yml -f docker-compose.dev.yml down`

Notes:
- The dev service runs `mvn spring-boot:run` and exposes `http://localhost:9090`.
- Mongo stays the same (`mongodb://mongo:27017/db_agendador`).
- If you add dependencies in `pom.xml`, Maven will download them automatically on next run.
- For automatic restarts on code changes, consider adding Spring Boot DevTools to `pom.xml`:
  - `<dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-devtools</artifactId>
       <scope>runtime</scope>
     </dependency>`

## Configuration

- `spring.data.mongodb.uri`: Mongo connection string
- `server.port`: HTTP port (default set to `9090`)
- `jwt.secret`: HMAC secret used to validate incoming JWTs

## Security

- All endpoints require a valid `Authorization: Bearer <token>` header, except `GET /error` (default Spring error handler).
- The controller normalizes the `Authorization` header (removes `Bearer` prefix case-insensitively) and rejects empty tokens.
- The service extracts `userId` from the token and enforces ownership:
  - `GET/PUT/DELETE /tasks/{id}`: `403 Forbidden` if the task does not belong to the authenticated user.
  - Listing endpoints return only tasks owned by the authenticated user.
- Expected JWT claims:
  - `sub`: the user’s email or username
  - `userId`: the user’s unique identifier
  - `roles` (optional): list of roles; either `ROLE_...` or plain names

## Endpoints

`POST /tasks`

- Request body:

```json
{
  "taskName": "My Task",
  "description": "Optional description",
  "scheduledDate": "2025-12-01T10:00:00"
}
```

- Response (201 Created):

```json
{
  "id": "...",
  "taskName": "My Task",
  "description": "Optional description",
  "creationDate": "2025-11-20T17:45:00",
  "scheduledDate": "2025-12-01T10:00:00",
  "updateDate": "2025-11-20T17:45:00",
  "userEmail": "user@example.com",
  "userId": "123",
  "status": "PENDING"
}
```

`GET /tasks/{id}`

- Example: `GET /tasks/691e3ad1e1070c4cf86f3f42`
- Response (200 OK): same schema as above (`TaskSchedulerResponseDTO`).
- Errors: `404 Not Found` if id does not exist; `401 Unauthorized` without/invalid token; `403 Forbidden` when the task does not belong to the authenticated user.

`GET /tasks?status=<STATUS>`

- Filters tasks by status for the authenticated user.
- Query param: `status` (e.g., `PENDING`, `SENT`, `ERROR`)
- Errors: `400 Bad Request` if status is missing/invalid; `404 Not Found` when no tasks are found; `401 Unauthorized` without/invalid token.

`GET /tasks?startDate=dd-MM-yyyy&endDate=dd-MM-yyyy`

- Returns tasks scheduled between the given dates for the authenticated user.
- Query params:
  - `startDate` and `endDate` in `dd-MM-yyyy` format.
  - Converted to `LocalDateTime` using timezone `America/Sao_Paulo` (start-of-day to end-of-day).
- Errors: `400 Bad Request` for missing/invalid dates; `404 Not Found` when no tasks are found; `401 Unauthorized` without/invalid token.

`GET /tasks`

- Returns all tasks owned by the authenticated user.
- Errors: `404 Not Found` when no tasks are found; `401 Unauthorized` without/invalid token.

`DELETE /tasks/{id}`

- Example: `DELETE /tasks/691e3ad1e1070c4cf86f3f42`
- Response: `204 No Content` on success.
- Errors: `404 Not Found` if id does not exist; `401 Unauthorized` without/invalid token; `403 Forbidden` when the task does not belong to the authenticated user.

## cURL Examples

- Unauthorized (expected 403):

```
curl -i -X POST http://localhost:9090/tasks \
  -H "Content-Type: application/json" \
  -d '{"taskName":"Test","description":"Demo","scheduledDate":"2025-12-01T10:00:00"}'
```

- Authorized (replace `<JWT>` with a valid token signed using `jwt.secret`):

```
curl -i -X POST http://localhost:9090/tasks \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{"taskName":"Test","description":"Demo","scheduledDate":"2025-12-01T10:00:00"}'
```

- GET by id:

```
curl -i http://localhost:9090/tasks/691e3ad1e1070c4cf86f3f42 \
  -H "Authorization: Bearer <JWT>"
```

- GET by status:

```
curl -i "http://localhost:9090/tasks?status=PENDING" \
  -H "Authorization: Bearer <JWT>"
```

- GET by date range (`dd-MM-yyyy`):

```
curl -i "http://localhost:9090/tasks?startDate=01-12-2025&endDate=10-12-2025" \
  -H "Authorization: Bearer <JWT>"
```

- Delete by id:

```
curl -i -X DELETE http://localhost:9090/tasks/691e3ad1e1070c4cf86f3f42 \
  -H "Authorization: Bearer <JWT>"
```

- Delete not owned task (expected 403):

```
curl -i -X DELETE http://localhost:9090/tasks/<OTHER_USER_TASK_ID> \
  -H "Authorization: Bearer <JWT>"
```

## Notes

- Auditing is enabled via `@EnableMongoAuditing`; `creationDate` and `updateDate` are automatically managed.
 - If you need a health endpoint, add Spring Boot Actuator and permit `/actuator/health` in `SecurityConfig`.
 - Ownership is enforced across read/update/delete flows. Listing endpoints are scoped to the authenticated user.

## DTOs

- Request (`TaskSchedulerRequestDTO`): `taskName`, `description`, `scheduledDate`.
- Response (`TaskSchedulerResponseDTO`): `id`, `taskName`, `description`, `creationDate`, `scheduledDate`, `updateDate`, `userEmail`, `userId`, `status`.

## Data Model

- `TaskEntity` (collection `task`): `id`, `taskName`, `description`, `creationDate`, `scheduledDate`, `updateDate`, `userEmail`, `userId`, `status`.

## MongoDB & Indexes

- Suggested indexes for performance:
  - Single: `userId`
  - Compound: `status + scheduledDate`
- Compass examples:
  - By user: `{ userId: "123" }`
  - Pending by user: `{ userId: "123", status: "PENDING" }`
  - After date: `{ scheduledDate: { $gte: ISODate("2025-12-01T00:00:00Z") } }`

## Current Status & Next Steps

- Create task (`POST /tasks`) working with auditing and user attribution.
- Fetch by id (`GET /tasks/{id}`) working.
- Optional enhancements: request validation, owner restriction, actuator health.