# Task Scheduler API

This project is a Spring Boot service that schedules tasks and stores them in MongoDB. It uses JWT Bearer authentication to identify the user creating a task.

## Run Locally

- Build and test: `./mvnw.cmd clean install -DskipTests=false`
- Start the app: `java -jar target/task-scheduler-0.0.1-SNAPSHOT.jar --jwt.secret=your-dev-secret`

The app listens on `http://localhost:9090/` and connects to MongoDB at `mongodb://localhost:27017/db_agendador` by default. Configure these values via properties.

## Configuration

- `spring.data.mongodb.uri`: Mongo connection string
- `server.port`: HTTP port (default set to `9090`)
- `jwt.secret`: HMAC secret used to validate incoming JWTs

## Security

- All endpoints require a valid `Authorization: Bearer <token>` header, except `GET /error` (default Spring error handler).
- Expected JWT claims:
  - `sub`: the user’s email or username
  - `userId`: the user’s unique identifier
  - `roles` (optional): list of roles; either `ROLE_...` or plain names

## Endpoint

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

## Notes

- Auditing is enabled via `@EnableMongoAuditing`; `creationDate` and `updateDate` are automatically managed.
- If you need a health endpoint, add Spring Boot Actuator and permit `/actuator/health` in `SecurityConfig`.