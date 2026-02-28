# IoT Home Automation System

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen?style=for-the-badge&logo=spring)
![MariaDB](https://img.shields.io/badge/MariaDB-Latest-003545?style=for-the-badge&logo=mariadb)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?style=for-the-badge&logo=docker)
![Kubernetes](https://img.shields.io/badge/Kubernetes-Ready-326CE5?style=for-the-badge&logo=kubernetes)

**A full-stack smart home platform that actually works. Control everything from your thermostat to your garage door, with proper security and monitoring included.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

This is a complete IoT home automation system built from scratch. Think of it as your own personal smart home hub that you actually control, not some vendor's cloud service.

**The good stuff:**
- Control **17+ types of smart devices** - lights, thermostats, cameras, door locks, appliances, you name it
- Talk to your devices naturally with voice commands
- Set up automations (like "turn on sprinklers at 6 AM" or "dim lights at sunset")
- Get emails when something happens (new device added, suspicious activity, etc.)
- Monitor everything in real-time with dashboards
- Secure by design - your data stays yours, isolated per user
- Every action gets logged for security audits

**What makes it different:**
- Actually production-ready (with Docker, Kubernetes, monitoring, the works)
- Built with enterprise tools, so it scales and doesn't fall over
- 44+ automated tests because broken smart homes are annoying
- Proper security with JWT tokens and role-based access
- Can integrate with ThingsBoard if you want external IoT platforms

The whole thing runs on Spring Boot with MariaDB, and I've thrown in Prometheus + Grafana because knowing what's happening in your system is kind of important.

---

## Tech Stack

**Backend:** Java 21 + Spring Boot 3.4.3 + Spring Security (JWT) + Maven  
**Database:** MariaDB with JdbcTemplate (clean DAO pattern)  
**Frontend:** Thymeleaf templates + HTML/CSS/JS  
**Deployment:** Docker + Docker Compose + Kubernetes manifests  
**Monitoring:** Prometheus + Grafana + Loki  
**Testing:** JUnit 5 (44+ test cases)

### Architecture

Classic layered architecture - nothing fancy, but it works:

```
Web UI / REST API
      ↓
Controllers (auth, devices, pages)
      ↓
Services (business logic, JWT, audit)
      ↓
DAOs (JdbcTemplate, 17+ data access objects)
      ↓
MariaDB (with proper foreign keys and user isolation)
```

**How security works:**
- JWT tokens for auth (30-day sessions)
- Spring Security filters every request
- Email verification on signup
- Every user's data is isolated via `user_id` foreign keys
- All state-changing operations logged to `audit.csv` with IP tracking

**Key Implementation Details:**
- **DAO Layer:** Each device type has its own DAO with full CRUD (create, read, update, delete) operations
- **Service Layer:** Business logic, validation, and integrations (email, ThingsBoard, statistics)
- **Controllers:** 17+ device controllers plus auth and page routing
- **Security Filter Chain:** `JwtAuthFilter` validates tokens on every request
- **Audit System:** Manual logging via `AuditService.log()` - catches ~50+ operations across all controllers
- **Database:** Schema with user-scoped tables, all queries filtered by authenticated user

**Monitoring Setup:**
- Prometheus scrapes metrics from Spring Boot Actuator endpoints
- Grafana visualizes everything (custom IoT dashboards included)
- Loki aggregates logs from application + containers
- All running via Docker Compose for local dev, K8s manifests for production

---

## Project Structure

```
src/main/java/com/example/ihas/
├── config/          # JWT + Spring Security setup
├── controllers/     # REST APIs + page routes (17+ device controllers)
├── dao/             # Database access (17+ DAOs with CRUD)
├── devices/         # Device models
├── models/          # Domain entities
├── services/        # Business logic (auth, email, audit, stats, etc.)
└── utils/           # Helpers (JWT, IP tracking, email)

src/main/resources/
├── templates/       # Thymeleaf HTML pages
├── static/          # CSS/JS
├── schema.sql       # Database schema
└── application.properties

k8s/                 # Kubernetes deployment manifests
monitoring/          # Prometheus + Grafana + Loki configs
docker-compose.yml   # Local development stack


---

## Getting Started

**What you need:**
Java 21, Maven, Docker Desktop, and your favorite IDE (IntelliJ recommended)

**Setup (5 minutes):**

```bash
# 1. Start the infrastructure
docker-compose up -d

# 2. Build and run
mvn clean install
mvn spring-boot:run
```

**Or just run `Main.java` from your IDE.**

**Access points:**
- Main app: `http://localhost:8080`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000` (admin/admin)

**First time?**
1. Register at `/auth/register`
2. Check your email for verification (might be in spam)
3. Login and start adding devices

That's it. The database, monitoring, and everything else spins up automatically via Docker Compose.

---

## Supported Devices

**17 device types across 9 categories:**
- **Climate:** Thermostat, Air Purifier
- **Lighting:** Smart Lights, Curtains  
- **Security:** Camera, Door Lock, Alarm, Garage Door
- **Appliances:** Refrigerator, Oven, Washing Machine
- **Outdoor:** Sprinkler, Car Charger
- **Automation:** Robot Vacuum, Smart Plug, Hub
- **Entertainment:** Smart TV
- **Monitoring:** Sensors
- **Voice:** Smart Assistant

All devices support power toggle, status checks, and device-specific operations (like setting temperature, scheduling, inventory tracking for the fridge, etc.)

---

## API Examples

**Register & Login:**
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "you@example.com", "password": "yourPassword"}'
```

**Control a device:**
```bash
curl -X POST http://localhost:8080/api/thermostat \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name": "Living Room", "targetTemperature": 22.5}'
```

All endpoints are secured with JWT. Each user only sees their own devices.

---

## Deployment

**For local dev:** Already covered - just `docker-compose up -d`

**For production (Kubernetes):**
```bash
kubectl apply -f k8s/
```

This deploys:
- The app with 3 replicas
- MariaDB as a StatefulSet  
- Full monitoring stack (Prometheus, Grafana, Loki)
- LoadBalancer service

Everything's namespaced under `iot-system`.

---

## Testing & Quality

44+ JUnit tests covering DAOs, services, controllers, and security. Run them with:
```bash
mvn test
```

Plus comprehensive audit logging - every POST/PUT/DELETE operation gets logged to `audit.csv` with timestamp, action, and source IP.

---

## What's Next

Some ideas I'm considering:
- WebSockets for real-time updates (tired of polling)
- Mobile app (probably React Native)
- ML-based usage predictions (like "you usually turn on the heater around 6 PM")
- AWS IoT / Azure IoT Hub integration
- Better scheduling with recurring rules
- Energy consumption analytics

---

## 📹 Demo Video

> **Recording Instructions:** A 3-5 minute walkthrough showcasing the key features of this project.

### What to Demonstrate

**Suggested Timeline:**
- **0:00-0:30** - Project overview and startup
- **0:30-2:00** - Core features demonstration
- **2:00-3:30** - Advanced features and interactions
- **3:30-5:00** - Edge cases and wrap-up

### Features to Showcase

- **Device Control Dashboard** - Add and control multiple device types (lights, thermostats, cameras)
- **User Authentication** - Registration, JWT login, email verification
- **Automation Rules** - Create time-based automations and triggers
- **Real-time Monitoring** - View device status updates and system logs
- **Security Features** - User isolation, audit logging, role-based access
- **ThingsBoard Integration** - External IoT platform connectivity

### Recording Setup

**Prerequisites:**
```bash
# Start MariaDB (Docker or local)
docker-compose up -d mariadb

# Configure application.properties with database credentials
# Set SMTP settings for email verification

# Start the application
mvn spring-boot:run
```

**OBS Studio Settings:**
- Resolution: 1920x1080 (1080p)
- FPS: 30
- Format: MP4 (H.264)
- Audio: Include microphone narration (optional)

**Steps:**
1. Start the application: `mvn spring-boot:run`
2. Open OBS Studio and set up screen capture
3. Navigate to `http://localhost:8080`
4. Record the demonstration following the timeline above
5. Save video as `demo.mp4` in the project root directory
6. (Optional) Upload to YouTube and update README with embed link

### Quick Demo Commands

```bash
# Start application
cd "d:\Personal-Projects\Web projects\IoT Home Automation System"
mvn spring-boot:run

# Access the app
# Open browser to http://localhost:8080
```

**Video file:** Once recorded, save as `demo.mp4` in this directory.

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.