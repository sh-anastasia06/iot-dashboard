# IoT Metrics & Telemetry Dashboard

A full-stack IoT monitoring system that simulates telemetry data from multiple nodes and exposes it via a REST API with live frontend visualisation.

---

## Architecture

```
[ IoT Simulator ]  →  [ Java Backend ]  →  [ SQLite ]
                            ↕ REST API
                     [ React Frontend ]
```

The simulator runs as background threads inside the Java process, writing telemetry to the database every 2 seconds. The backend exposes a REST API that aggregates and serves that data on demand. The frontend polls the API periodically and renders live charts per node.

---

## Backend

### Tech Stack

| Component | Library / Version |
|---|---|
| Build tool | Maven |
| Java version | 21 |
| HTTP server | Javalin 6.3.0 |
| JSON serialisation | Jackson Databind 2.17.2 |
| Database driver | SQLite JDBC 3.46.1.0 |
| Connection pool | HikariCP 5.1.0 |
| Logging | SLF4J + Logback |

The project is packaged as a fat JAR via `maven-shade-plugin`. Main class: `com.iot.dashboard.Main`.

---

### Package Structure

```
com.iot.dashboard/
├── Main.java
├── server/
│   └── ApiServer.java
├── api/
│   ├── NodeController.java
│   └── TelemetryController.java
├── simulation/
│   └── TelemetrySimulator.java
├── db/
│   ├── DatabaseManager.java
│   └── TelemetryRepository.java
├── model/
│   ├── Node.java
│   ├── TelemetryRecord.java
│   └── TelemetrySummary.java
└── validation/
    └── InputValidator.java
```

---

### Database

**Engine:** SQLite via JDBC, managed through a HikariCP connection pool (max 10 connections, min idle 2).

**Pragmas applied on startup:**
- `PRAGMA journal_mode=WAL` — enables concurrent reads alongside writes

**Schema:**

`nodes`
| Column | Type | Notes |
|---|---|---|
| `node_id` | `INTEGER PK AUTOINCREMENT` | |
| `node_name` | `TEXT` | max 50 chars |
| `ip_address` | `TEXT` | validated on insert, max 15 chars |
| `location` | `TEXT` | max 100 chars, supports Unicode |
| `registered_at` | `TEXT` | ISO-8601 datetime |

`telemetry`
| Column | Type | Notes |
|---|---|---|
| `record_id` | `INTEGER PK AUTOINCREMENT` | |
| `node_id` | `INTEGER FK` | → `nodes.node_id` |
| `cpu_usage` | `INTEGER` | 0–100 |
| `ram_usage` | `INTEGER` | 0–100 |
| `net_ping_ms` | `INTEGER` | 0–999 |
| `recorded_at` | `TEXT` | ISO-8601 datetime |

`alerts`
| Column | Type | Notes |
|---|---|---|
| `alert_id` | `INTEGER PK AUTOINCREMENT` | |
| `node_id` | `INTEGER FK` | → `nodes.node_id` |
| `metric` | `TEXT` | e.g. `"CPU"` |
| `threshold_val` | `INTEGER` | |
| `triggered_at` | `TEXT` | ISO-8601 datetime |

A composite index on `telemetry(node_id, recorded_at)` powers fast time-range aggregation queries.

---

### REST API

Base URL: `http://localhost:8080`

All responses are JSON. All errors return `{ "error": "...", "code": 4xx }`.

| Method | Route | Query Params | Description |
|---|---|---|---|
| `GET` | `/api/nodes` | — | Returns all registered nodes |
| `POST` | `/api/nodes` | — | Registers a new node; validates IP, name, location; returns 201 |
| `GET` | `/api/telemetry/{nodeId}` | `limit` (default 40) | Returns latest N telemetry records for a node |
| `GET` | `/api/telemetry/{nodeId}/summary` | `window` (default 30) | Returns AVG/MAX aggregation over a rolling time window (minutes) |

CORS is configured to allow any host (development mode).

---

### Simulation Engine

`TelemetrySimulator` uses a `ScheduledExecutorService` with 32 threads. Each node gets its own scheduled task firing every 2 seconds.

Values evolve via a **random walk** — each tick drifts the previous value by a small random delta — so metrics behave realistically rather than spiking randomly. Per-node state is stored in a `Map<Integer, int[]>` holding `[cpu, ram, ping]`.

New nodes registered at runtime via `POST /api/nodes` are added to the scheduler immediately through `addNode()`, with no restart required.

---

### Validation

`InputValidator` (pure utility class, all static methods) enforces:

- `isValidIp(String)` — full IPv4 regex, rejects anything over 15 characters
- `isValidNodeName(String)` — non-null, non-blank, max 50 characters
- `isValidLocation(String)` — non-null, non-blank, max 100 characters

Invalid `POST /api/nodes` requests return HTTP 400 with a structured error body.

---

### Startup Sequence

`Main.java` wires everything together in this order:

1. `DatabaseManager` — initialised with `iot-dashboard.db`
2. `TelemetryRepository` — injected with `DatabaseManager`
3. `TelemetrySimulator` — injected with `TelemetryRepository`
4. `ApiServer` — injected with `TelemetryRepository` + `TelemetrySimulator`
5. Shutdown hook registered (stops simulator → stops server → closes DB pool)
6. 6 default nodes seeded if the `nodes` table is empty
7. `simulator.start()` called with all existing nodes
8. `server.start(8080)` — API available at `http://localhost:8080/api/nodes`

---

### Build & Run

**Prerequisites:** Java 21, Maven 3.8+

```bash
# Build fat JAR
mvn clean package

# Run
java -jar target/backend-1.0-SNAPSHOT.jar
```

The database file `iot-dashboard.db` is created in the working directory on first run.

---

### Key Design Decisions

| Decision | Choice | Reason |
|---|---|---|
| HTTP server | Javalin | Minimal setup, no Spring overhead |
| Database | SQLite | Portable, zero-config, sufficient for dev |
| Connection handling | HikariCP pool | Thread-safe concurrent JDBC writes from simulator threads |
| Concurrency | `ScheduledExecutorService` | One scheduled task per node mirrors real IoT devices |
| Architecture | Repository pattern | All SQL in `TelemetryRepository`; controllers never touch JDBC |
| Models | Immutable POJOs | All fields `final`, no setters, single constructor |

---

## Frontend

*Documentation coming soon.*

---
