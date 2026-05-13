# notes-cloud-reminder-service

Spring Boot microservice responsible for reminder scheduling and in-app notifications within the **Notes in the Cloud** platform.

## Overview

The reminder-service lets users create time-based reminders with a date, time, priority, and optional description. Each reminder is tied to a specific user and has a status that moves through a lifecycle: **PENDING → FIRED → COMPLETED**.

A background scheduler runs every 15 seconds and checks for reminders whose trigger time has passed. When it finds one, it:
1. Marks the reminder as **FIRED**
2. Creates a **Notification** record in the database
3. Pushes the notification to the gateway so connected clients receive it in real time

Notifications are persistent — users can list them, mark them as read, and delete them independently of the reminders that created them. A reminder can be marked **COMPLETED** explicitly by the user via the update endpoint once they have acknowledged it.

## Tech Stack

| | |
|---|---|
| Runtime | Java 21 |
| Framework | Spring Boot 3.5.14 |
| Database | PostgreSQL 16 (both local and prod) |
| ORM | Spring Data JPA / Hibernate |
| API Docs | Springdoc OpenAPI 2.8.6 |
| Build | Maven (Maven Wrapper included) |

## Architecture

Strict layered architecture: **Controller → Service → Repository → JPA Entity**

```
com.notescloud.reminderservice
├── controller      ReminderController, NotificationController, HealthController
├── service         ReminderService, NotificationService, SchedulerService, NotificationDispatcher
├── client          GatewayNotificationClient
├── repository      ReminderRepository, NotificationRepository
├── entity          Reminder, Notification
├── model           Request DTOs  (ReminderModel)
├── view            Response DTOs (ReminderView, NotificationView, GatewayNotificationPushRequest)
├── converter       Entity ↔ DTO converters
├── enums           Status, Priority, ReminderFilter
└── config          DatabaseProperties, GatewayProperties, ReminderProperties, DataSourceConfig
```

### Scheduler

`SchedulerService` polls every **15 seconds** for reminders where `remindAt <= now` and `status = PENDING`. It processes up to **100 reminders per tick**, advances their status to `FIRED`, creates a `Notification` record, and dispatches a push to the gateway via `GatewayNotificationClient`.

### Status Lifecycle

```
PENDING → FIRED → COMPLETED
```

`FIRED` is set automatically by the scheduler. `COMPLETED` is set explicitly via the update endpoint.

### Timezone Handling

Each request can include a `timezone` field (IANA zone ID, e.g. `Europe/Sofia`). If absent or invalid, the service falls back to the value of `REMINDER_TIMEZONE` (default: `Europe/Sofia`). `reminderTime` is always truncated to minutes before being stored.

### Gateway Integration

When a reminder fires, `NotificationDispatcher` sends a `POST /internal/notifications/{userId}` request to the gateway with an `X-Internal-Token` header. The push is skipped if `notifyInApp = false` on the reminder.

## API Endpoints

### Reminders — `/api/users/{userId}/reminders`

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/users/{userId}/reminders` | Create a reminder |
| `PUT` | `/api/users/{userId}/reminders` | Update a reminder (id in body) |
| `GET` | `/api/users/{userId}/reminders` | List all reminders |
| `GET` | `/api/users/{userId}/reminders?status=PENDING` | List pending + fired reminders |
| `GET` | `/api/users/{userId}/reminders?status=COMPLETED` | List completed reminders |
| `GET` | `/api/users/{userId}/reminders/{id}` | Get reminder by ID |
| `DELETE` | `/api/users/{userId}/reminders/{id}` | Delete a reminder |

### Notifications — `/api/users/{userId}/notifications`

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/users/{userId}/notifications` | List all notifications |
| `GET` | `/api/users/{userId}/notifications?read=false` | List unread notifications |
| `GET` | `/api/users/{userId}/notifications/unread-count` | Count unread notifications |
| `POST` | `/api/users/{userId}/notifications/{id}/read` | Mark notification as read |
| `POST` | `/api/users/{userId}/notifications/read-all` | Mark all as read |
| `DELETE` | `/api/users/{userId}/notifications` | Delete all notifications |

### Health

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/healthz` | Liveness probe — always returns `200 UP` |
| `GET` | `/api/readyz` | Readiness probe — checks DB connectivity |

### Swagger UI

Available at `/swagger-ui.html` in all profiles.

## Running Locally

Requires **Java 21** and a running PostgreSQL instance reachable on `localhost:5432` (see prerequisites below).

### Prerequisites

The local profile connects to the shared PostgreSQL instance inside the Kubernetes cluster. Before starting the service, port-forward the database:

```bash
kubectl port-forward -n notes-cloud svc/postgres 5432:5432
```

The `reminder` schema must also exist (created once after the initial cluster setup):

```sql
CREATE SCHEMA IF NOT EXISTS reminder;
```

### Commands

```bash
# Run with local profile (default)
./mvnw spring-boot:run

# Run tests
./mvnw test

# Build JAR
./mvnw clean package

# Build JAR (skip tests)
./mvnw clean package -DskipTests
```

## Profiles

| Profile | Database | DDL | Notes |
|---|---|---|---|
| `local` (default) | PostgreSQL `localhost:5432` via `kubectl port-forward` | `update` | `show-sql=true` |
| `prod` | PostgreSQL via env vars | `validate` | `show-sql=false` |

Both profiles use the `reminder` schema inside the `notes_cloud` database.

## Docker

```bash
# Build image (multi-stage, Alpine-based)
docker build -f Dockerfile -t reminder-service:latest .

# Run container (prod profile)
docker run -p 8084:8084 \
  -e DB_HOST=<host> \
  -e POSTGRES_DB=<db> \
  -e POSTGRES_USER=<user> \
  -e POSTGRES_PASSWORD=<password> \
  -e GATEWAY_BASE_URL=<gateway-url> \
  -e GATEWAY_INTERNAL_TOKEN=<token> \
  reminder-service:latest
```

Published to Docker Hub as `hristo12319/notes-cloud-reminder-service`.

## Environment Variables

| Variable | Profile | Default | Description |
|---|---|---|---|
| `DB_HOST` | prod | `postgres` | PostgreSQL host |
| `DB_PORT` | prod | `5432` | PostgreSQL port |
| `POSTGRES_DB` | prod | — | Database name |
| `POSTGRES_USER` | prod | — | Database user |
| `POSTGRES_PASSWORD` | prod | — | Database password |
| `GATEWAY_BASE_URL` | all | `http://localhost:8090` | Gateway base URL for notification push |
| `GATEWAY_INTERNAL_TOKEN` | all | _(empty)_ | Token sent as `X-Internal-Token` to the gateway |
| `REMINDER_TIMEZONE` | all | `Europe/Sofia` | Fallback timezone for `remindAt` calculation |

## Kubernetes Deployment

The service runs on port **8084** in the `notes-cloud` namespace of the `notes-cloud-cluster` k3d cluster. See `notes-cloud-infrastructure/` for manifests.

```bash
kubectl get pods -n notes-cloud -l app=reminder-service
kubectl logs -n notes-cloud -l app=reminder-service -f
kubectl rollout restart deployment reminder-service -n notes-cloud
```
