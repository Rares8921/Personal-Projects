# GitSpace

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Grafana](https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white)

**A full-featured GitHub alternative built from scratch. Complete version control, pull requests, code review, and collaboration—all on your own infrastructure.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

This is a production-ready Git hosting platform that rivals GitHub's core functionality. Built with enterprise-grade Spring Boot and Angular, it implements Git protocol from scratch with a custom Java library.

**The good stuff:**
- **Complete Git Operations:** Push, pull, clone, merge, branch—all via custom HTTP protocol implementation
- **Pull Requests:** Full PR workflow with diff visualization, file-by-file review, and merge conflict detection
- **Issue Tracking:** GitHub-style issues with comments, status tracking, and auto-increment numbers
- **Repository Management:** Public/private repos, branch protection, collaborator permissions (READ/WRITE)
- **Code Review:** Line-by-line comments, approval system, merge blocking on reviews
- **Access Control:** Role-based authorization (Admin/User), JWT authentication, BOLA protection
- **Activity Feed:** Follow users and see their commits, PRs, and issues in real-time
- **User Profiles:** Avatars, bio, repository lists, follower/following system
- **Search Engine:** Full-text search across repositories, users, and code
- **Avatar Management:** Image uploads with lifecycle management and admin controls
- **Markdown Support:** Full Markdown rendering for README files, comments, and issues

**What makes it different:**
- **Custom Git Implementation:** Built a complete Git library in Java (hash-object, write-tree, commit-tree, merge detection)
- **Real Git Protocol:** Implements the actual Git HTTP smart protocol—works with standard Git CLI
- **Hybrid Storage:** Metadata in PostgreSQL, actual Git objects on disk (like real GitHub)
- **Production Monitoring:** Grafana + Loki stack for log aggregation across all containers
- **Security Hardened:** Path traversal protection, XSS sanitization, access control on every operation
- **Comprehensive Testing:** 40+ integration tests, 15+ unit tests covering end-to-end flows
- **Dockerized:** Multi-stage builds, Docker Compose orchestration, production-ready

The platform runs on Spring Boot 3 with Angular 15+, fully containerized with PostgreSQL and monitoring stack included.

---

## Tech Stack

**Backend:** Java 17 + Spring Boot 3 + Spring Security + Spring Data JPA  
**Frontend:** Angular 15+ + TypeScript + RxJS  
**Database:** PostgreSQL 15 (with HikariCP connection pooling)  
**Authentication:** JWT tokens + bcrypt password hashing  
**Git Library:** Custom Java implementation (git-core-lib module)  
**Monitoring:** Grafana + Loki + Promtail (automated log aggregation)  
**Deployment:** Docker + Docker Compose + Nginx reverse proxy  
**Testing:** JUnit 5 + Spring Boot Test + MockWebServer + TestRestTemplate

### Architecture

Microservices-style architecture with separation of concerns:

```
Angular Frontend (Nginx + proxy)
           ↓
     Reverse Proxy (/api → backend:8080)
           ↓
Spring Boot Backend (JWT validation)
           ↓
     ┌──────┴──────┐
Service Layer      Git Core Library (custom Java Git)
     ↓                      ↓
JPA Repository          Disk Storage (blob objects)
     ↓
PostgreSQL (metadata: users, repos, PRs, issues)
```

**Git Protocol Flow:**

```
Git CLI (git clone/push)
         ↓
HTTP Smart Protocol (GET /info/refs, POST /git-receive-pack)
         ↓
GitHttpController (Spring REST)
         ↓
Git.java (custom library: pack files, object parsing)
         ↓
Disk Storage (.git folders per repo)
```

**How the core features work:**

**Custom Git Implementation:**
- `hash-object`: Computes SHA-1 hash and stores blob objects
- `write-tree`: Recursively generates tree objects from working directory
- `commit-tree`: Creates commit objects with parent references
- `merge`: Detects conflicts by simulating merge in memory before disk write
- `cat-file`: Reads and decompresses zlib-compressed Git objects
- `pack/unpack`: Implements Git pack protocol for efficient network transfer

**Pull Request Workflow:**
- User creates branch and pushes commits
- PR creation triggers diff calculation (compare base and head commits)
- Review system allows APPROVED/CHANGES_REQUESTED/COMMENTED
- Merge button runs `canMerge()` to detect conflicts before attempting merge
- On success, fast-forward or three-way merge updates target branch

**Access Control:**
- `AccessControlService` checks permissions on every sensitive operation
- Public repos: anyone can clone, only collaborators can push
- Private repos: 404 for unauthorized users (security through obscurity)
- Collaborator invitations: PENDING (no access) vs ACCEPTED (READ/WRITE)
- Admin role: can delete any user/repo, access /admin/stats endpoint

**Security Measures:**
- **Authentication:** JWT tokens with expiration, stored in localStorage
- **Authorization:** Spring Security + custom `@PreAuthorize` annotations
- **BOLA Prevention:** `accessControl.canWriteRepo(user, repo)` on every write operation
- **Path Traversal:** All file paths resolved relative to `gitRootPath` with `Path.resolve()`
- **XSS Protection:** Angular automatic sanitization + Markdown library filtering
- **Password Security:** bcrypt with 10 salt rounds

**Monitoring Stack:**
- **Promtail**: Auto-discovers Docker containers and streams logs to Loki
- **Loki**: Lightweight log aggregation with label-based indexing
- **Grafana**: Web UI at http://localhost:3000 with pre-configured datasources
- **JSON Logging**: Backend uses `logstash-logback-encoder` for structured logs

---

## Getting Started

### Prerequisites
- Docker & Docker Compose
- Java 17+ (for local development)
- Node.js 18+ & npm (for local development)
- Git CLI (to test protocol integration)

### Quick Start (Docker)

```bash
# Clone the repository
git clone <repository-url>
cd GitSpace

# Start all services (backend, frontend, postgres, monitoring)
docker-compose up -d

# Frontend: http://localhost:80
# Backend API: http://localhost:8080/api
# Grafana: http://localhost:3000 (admin/admin123)
```

### Local Development

**Backend:**
```bash
cd backend
mvn clean install
mvn spring-boot:run
# Runs on http://localhost:8080
```

**Frontend:**
```bash
cd frontend
npm install
ng serve
# Runs on http://localhost:4200 with proxy to backend
```

**Database:**
- PostgreSQL runs on port 5432
- Database: `gitspace_db`
- Configured in `backend/src/main/resources/application.properties`

### Testing

**Run all integration tests:**
```bash
cd backend
mvn test
```

**Test categories:**
- **Auth**: Registration, login, password reset (5 tests)
- **Access Control**: Public/private repo permissions (6 tests)
- **Repository**: CRUD operations, rename, disk sync (5 tests)
- **Pull Requests**: Create, review, merge, conflicts (7 tests)
- **Issues**: Create, comment, close, auto-numbering (4 tests)
- **Git Protocol**: CLI push/clone/pull integration (8 tests)
- **Activity Feed**: Follow system and notifications (3 tests)

**Coverage:** 55+ test methods validating end-to-end workflows

---

## Features Deep Dive

### Repository Management
- Create public or private repositories
- Rename repositories (updates file system + database atomically)
- Delete repositories (cleanup from DB and disk)
- Clone via browser or Git CLI
- Branch management with checkout and merge
- Default branch configuration
- Collaborator invitations with READ/WRITE permissions

### Pull Request System
- Create PR from any branch to any branch
- Automatic diff generation with file-by-file view
- Syntax-highlighted code viewer
- Comment on specific lines
- Review actions: Approve, Request Changes, Comment
- Merge conflict detection before merge
- Squash and merge option
- Automatic PR closure on merge

### Issue Tracking
- Auto-incrementing issue numbers per repository
- Rich Markdown support in descriptions and comments
- Open/Closed status tracking
- Assignees and labels
- Comment threads with timestamps
- Activity timeline

### User System
- Registration with email verification
- JWT-based authentication
- Avatar upload with automatic cleanup
- User profiles with bio and social links
- Follow/unfollow functionality
- Activity feed showing followed users' actions
- Repository discovery page

### Git CLI Integration
Works with standard Git commands:
```bash
# Clone repository
git clone http://localhost:8080/git/username/reponame.git

# Push to repository (with authentication)
git push origin main

# Pull changes
git pull origin main
```

### Admin Panel
- Delete any user (cascades to repos, issues, PRs)
- View platform statistics
- Monitor system health via Grafana
- Access all logs via Loki

---

## Project Structure

```
GitSpace/
├── backend/                    # Spring Boot application
│   ├── src/main/java/
│   │   ├── controller/         # REST endpoints (Auth, Repo, PR, Issue, Git)
│   │   ├── service/            # Business logic layer
│   │   ├── model/              # JPA entities
│   │   ├── repository/         # Spring Data repositories
│   │   ├── security/           # JWT + Spring Security config
│   │   └── git/                # Git HTTP protocol implementation
│   ├── src/test/java/          # Integration tests
│   └── Dockerfile              # Multi-stage backend build
├── frontend/                   # Angular application
│   ├── src/app/
│   │   ├── components/         # UI components (Repo, PR, Issue views)
│   │   ├── services/           # HTTP services (API calls)
│   │   ├── guards/             # Route guards (auth)
│   │   └── interceptors/       # JWT interceptor
│   ├── nginx.conf              # Reverse proxy config
│   └── Dockerfile              # Multi-stage frontend build
├── git-core-lib/               # Custom Git implementation
│   ├── src/main/java/git/
│   │   ├── Git.java            # Main API (init, commit, merge, etc.)
│   │   ├── ObjectReader.java  # Read Git objects
│   │   ├── PackProtocol.java  # Git pack/unpack
│   │   └── TreeEntry.java     # Tree object representation
│   └── src/test/java/          # Unit tests for Git operations
├── monitoring/                 # Observability stack
│   ├── grafana/                # Grafana config + dashboards
│   ├── loki/                   # Loki configuration
│   └── promtail/               # Log collection config
├── docs/                       # Documentation and diagrams
└── docker-compose.yml          # Full stack orchestration
```

---

## Technical Highlights

### Custom Git Library
The `git-core-lib` module implements core Git operations in pure Java:
- **Object Storage:** zlib compression, SHA-1 hashing, loose object format
- **Tree Algorithms:** Recursive directory traversal, tree object generation
- **Commit Graph:** Parent references, branch pointers, HEAD management
- **Merge Logic:** Three-way merge with conflict detection
- **Pack Protocol:** Deltification, pack file generation/parsing
- **HTTP Transport:** Smart HTTP protocol (info/refs, receive-pack, upload-pack)

### Database Schema
- **users:** id, username, email, password_hash, avatar_url, bio, role
- **repositories:** id, name, owner_id, is_public, default_branch, created_at
- **pull_requests:** id, repo_id, author_id, source_branch, target_branch, status
- **issues:** id, repo_id, author_id, number (auto-increment), title, body, is_open
- **comments:** id, issue_id/pr_id, author_id, body, created_at
- **collaborators:** id, repo_id, user_id, permission (READ/WRITE), status (PENDING/ACCEPTED)
- **follows:** follower_id, following_id
- **activities:** id, user_id, action_type, target_id, created_at

### Performance Optimizations
- **Connection Pooling:** HikariCP with optimized pool size
- **Lazy Loading:** JPA entities use lazy fetch to avoid N+1 queries
- **Index Strategy:** Database indexes on foreign keys and frequently queried columns
- **Angular AOT:** Ahead-of-time compilation for production builds
- **Code Splitting:** Angular lazy loading for route-based code splitting
- **Nginx Caching:** Static assets cached with long-lived cache headers
- **Hybrid Storage:** Large Git objects on disk, only metadata in PostgreSQL

---

## Security Implementation

### Authentication Flow
```
Registration → bcrypt hash → Save to DB
Login → Validate credentials → Generate JWT → Return token
Subsequent Requests → JWT in Authorization header → Validate signature → Extract user
```

### Authorization Layers
1. **Spring Security:** Global method security with `@PreAuthorize`
2. **AccessControlService:** Repository-level permissions (owner, collaborator, public)
3. **JWT Validation:** Token expiration and signature verification
4. **Role-Based:** Admin vs User permissions

### Threat Mitigation
- **BOLA (Broken Object Level Authorization):** Every sensitive operation checks `canAccess(user, resource)`
- **Path Traversal:** `Path.resolve()` prevents `../../etc/passwd` attacks
- **XSS:** Angular DOM sanitization + Markdown library strips dangerous HTML
- **SQL Injection:** JPA parameterized queries
- **CSRF:** Stateless JWT (no cookies, no CSRF risk)

**Missing (known limitations):**
- HTTPS/TLS (should be added via reverse proxy in production)
- Rate limiting (should use Redis + Spring Rate Limiter)
- Email verification (SendGrid integration planned)

---

## Testing Strategy

### Integration Tests (40+ tests)
- **Full stack:** TestRestTemplate makes real HTTP requests
- **Isolated:** H2 in-memory database, cleanup after each test
- **Coverage:** Auth, CRUD, Git protocol, access control, PR workflow, merge conflicts

### Unit Tests (15+ tests)
- **Git Library:** Testing hash algorithms, merge logic, pack protocol
- **Isolated:** @TempDir for file system operations
- **Mock Server:** MockWebServer for HTTP protocol tests

### Example Test Flow (PR with Merge)
```java
1. Create repo → POST /api/repos
2. Push initial commit → POST /git/receive-pack
3. Create branch → POST /api/repos/{id}/branches
4. Push feature commit → POST /git/receive-pack
5. Create PR → POST /api/pull-requests
6. Add review → POST /api/pull-requests/{id}/reviews
7. Merge PR → POST /api/pull-requests/{id}/merge
8. Verify: File exists on target branch → GET /api/repos/{id}/tree/{branch}
```

---

## CI/CD Pipeline

### Continuous Integration
```bash
mvn clean install  # Compiles backend + git-core-lib
mvn test           # Runs 55+ tests (fails build if any test fails)
```

### Continuous Deployment
```dockerfile
# Backend Dockerfile
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```dockerfile
# Frontend Dockerfile
FROM node:18 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build --prod

FROM nginx:alpine
COPY --from=build /app/dist/frontend /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
```

### Docker Compose
```yaml
services:
  db: postgres:15 with persistent volume
  backend: Spring Boot app (depends on db)
  frontend: Nginx + Angular (reverse proxy to backend)
  loki: Log aggregation
  promtail: Log collection
  grafana: Monitoring dashboard
```

---

## Monitoring & Observability

### Grafana Dashboard
Access at **http://localhost:3000** (admin/admin123)

**Pre-configured panels:**
- Container logs in real-time
- Error rate tracking
- Request latency percentiles
- Active user sessions

### LogQL Query Examples
```logql
# All backend logs
{container="gitspace_backend"}

# Only errors
{container="gitspace_backend"} |= "ERROR"

# Authentication failures
{container="gitspace_backend"} |= "Login Failed"

# Git operations
{container="gitspace_backend"} |= "git-receive-pack"
```

### Loki Architecture
- **Promtail** runs as Docker container with access to Docker socket
- Auto-discovers all containers and tails their logs
- Sends logs to **Loki** with labels (container, stream)
- **Grafana** queries Loki datasource

---

## Acknowledgments

This project implements the Git protocol based on the official [Git HTTP Transfer Protocols documentation](https://git-scm.com/docs/http-protocol) and [Git Internals](https://git-scm.com/book/en/v2/Git-Internals-Plumbing-and-Porcelain).

---

## License

This project is provided as-is for educational purposes.

---

# Technical Documentation

## Product Vision

**FOR**  
software development teams, tech organizations, and independent developers.

**WHO**  
need a high-performance, secure, easy-to-use platform for managing repositories, branches, pull requests, and issues with less overhead than existing systems.

**THE PRODUCT**  
GitSpace is a web-based version control and collaboration platform created for enhancing the Git workflow.

**THAT**  
enables seamless repository management, real-time collaboration, and quicker project delivery by integrating powerful Git operations, advanced permission control, and modern UI feedback loops.

**UNLIKE**  
legacy and enterprise-heavy platforms that prioritize integrations and ecosystem lock-in over speed and usability.

**OUR PRODUCT**  
performance, simplicity, and developer productivity are emphasized, while completely rethinking how teams collaborate on code by introducing an improved architecture with reduced latency.

---

# Non-Functional Requirements and Architectural Solutions

## 2. Scalabilitate si performanta
- **Cerinta:** raspuns rapid pentru operatiuni comune (listari, vizualizari).
- **Solutie:** aplicatiile sunt containerizate (`backend/` si `frontend/` au Dockerfile); folosim `docker-compose.yml` pentru orchestration in mediu de dezvoltare. Pentru productie se poate trece la orchestrare (ex. Kubernetes), dar asta nu este parte din repo-ul actual.
- **Masuri concrete in proiect:**
  - `backend` ruleaza Spring Boot cu HikariCP (pool conexiuni JDBC) — configurare in `src/main/resources/application.properties`.
  - `frontend` este construit in productie si servit cu `nginx` (vezi `frontend/Dockerfile` si `nginx.conf`).
  - Optimizare build Angular: `npm run build` genereaza `dist/` care este copiat in imaginea nginx; code-splitting si lazy-loading sunt folosite prin configuratia Angular (`angular.json`), minimizand payload-urile initiale.

### Performanta (Manipulare fisiere mari)
- **Problema:** Stocarea codului sursa (cu mii de fisiere) intr-o baza de date relationala ar incetini interogarile SQL.
- **Solutia:** Stocare hibrida. Metadatele (ex: cine a facut commit-ul) sunt in PostgreSQL, iar continutul efectiv (blob-uri) este stocat pe disc, accesat prin Java NIO. Astfel se permite citirea rapida a fisierelor mari fara a bloca baza de date.

---

## 3. Persistenta si consistenta datelor
- **Cerinta:** integritate date si posibilitate de migrari controlate.
- **Solutie:** PostgreSQL este depozitul principal. Proiectul foloseste JPA/Hibernate; in mediul de dezvoltare `spring.jpa.hibernate.ddl-auto` permite actualizarea automata a schemei. Pentru productie se are in vedere folosirea migrarilor gestionate (Flyway/Liquibase), dar acestea nu sunt prezente in repo.

---

## 4. Securitate

### 1. Autentificare si autorizare
- **Solutie:** Spring Security pe backend, autentificare bazata pe token JWT.
- **Masuri concrete:**
  - Interceptor JWT in frontend care adauga header-ul `Authorization`.
  - Backend-ul valideaza token-ul si aplica roluri/permisiuni.

### 2. Broken Object Level Authorization (BOLA)
- **Risc:** Utilizator autentificat incearca sa stearga repository-ul altui utilizator modificand ID-ul din URL.
- **Solutie:** `AccessControlService`. In fiecare metoda sensibila (ex: `deleteFile`, `mergePr`) se apeleaza `accessControl.canWriteRepo(user, repo)`. In caz contrar se returneaza `403 Forbidden`.

### 3. Path Traversal
- **Risc:** Acces la fisiere de sistem (ex: `../../etc/passwd`).
- **Solutie:** In `GitOperationService`, toate caile sunt rezolvate relativ la `gitRootPath` si utilizator. `Path.resolve()` previne iesirea din directorul radacina.

### 4. Cross-Site Scripting (XSS)
- **Risc:** Injectare de scripturi in baza de date.
- **Solutie:** Angular face sanitization automat la randare (`{{ comment.body }}`). Pentru Markdown se foloseste o librarie care elimina tag-urile periculoase.

**Ce lipseste:** HTTPS, rate limiting.

---

## 5. Observabilitate (logging și diagnostic)

**Cerinta:** Colectare logging pentru debugging si monitorizare in timp real.

**Solutie implementată:** Am integrat un stack complet de observabilitate bazat pe **Grafana Loki** pentru centralizarea si vizualizarea logurilor din toate containerele Docker.

### Arhitectura:
- **Promtail** - agent care colecteaza automat logurile din toate containerele Docker si le trimite catre Loki
- **Grafana Loki** - sistem de agregare si stocare a logurilor (lightweight cu index labeling)
- **Grafana** - interfata web pentru vizualizare, query-uri si alerting

### Configurare:
- Backend-ul Spring Boot emite loguri in **format JSON** (via `logstash-logback-encoder`) pentru parsare automata
- Promtail se conecteaza la Docker si auto-descopera containerele noi
- Datasource-ul Loki este pre-configurat

### Acces:
- **Grafana Dashboard:** http://localhost:3000 (admin/admin123)
- **Loki API:** http://localhost:3100

### Exemple query-uri LogQL:
```logql
{container="gitclone_backend"}              # Loguri backend
{container="gitclone_backend"} |= "ERROR"   # Doar erori
{container=~".+"}                           # Toate containerele
```

---

## 6. Backup si recuperare
- **Cerinta:** posibilitate de recuperare date.
- **Solutie:** PostgreSQL ruleaza cu volum extern definit in `docker-compose.yml` (`postgres_data`).

---

## 7. CI/CD si Environment-uri

### Environment-uri

#### Development (Local)
- Frontend: `ng serve` (port 4200) cu proxy catre backend.
- Backend: rulare din IntelliJ (port 8080).
- DB: PostgreSQL local sau H2 in-memory.
- Date volatile de test.
- lib compilat manual.

#### Production (Dockerized)
- Configurat prin `docker-compose.yml`.
- Aplicatii izolate in containere.
- Retea Docker interna (`app-network`).

### Pipeline CI/CD

#### CI
- Comanda: `mvn clean install`
- Compileaza backend + git-core-lib.
- Ruleaza testele unitare. Daca un test pica -> build-ul se opreste.
- Genereaza JAR-ul executabil.

#### CD
- Docker multi-stage:
  - Stage 1: build frontend (`npm run build`).
  - Stage 2: copiere JAR backend.
- Docker Compose:
  - `db`: PostgreSQL cu volume persistente.
  - `app`: backend Spring Boot.
  - `frontend`: Nginx + reverse proxy `/api -> app:8080`. Nginx serveste pentru fisiere statice generate.

---

## 8. Testare

**Nivel 1: Unit Testing (Backend Logic):**  
Ce: Testarea algoritmilor din Git.java (calcul hash, write tree).  
Metoda: White-box testing.  
Justificare: Logica de versionare este critică; dacă un hash e greșit, tot repo-ul e corupt.

**Nivel 2: Integration/System Testing (Frontend + Backend):**  
Ce: Fluxul de Pull Request (Create -> Conflict Check -> Merge).  
Metoda: Black-box testing (prin interfață).  
Rezultate/Observații: "Am identificat că inițial merge-ul nu detecta conflictele. Am introdus metoda canMerge în Git.java care simulează un merge în memorie înainte de a scrie pe disc." (Asta e fix ce am reparat noi, dă bine să zici că a fost un rezultat al testării).

---

### Integration:
Proiectul include 15 clase de teste de integrare cu aproximativ 40 de metode de test care validează funcționalitatea end-to-end a platformei Git clone. Testele utilizează Spring Boot Test cu server pe port random, TestRestTemplate pentru request-uri HTTP, MockMvc pentru testare controller-uri, și JUnit 5. Toate testele rulează pe un profil de test cu bază de date H2 în memorie și cleanup automat după fiecare test pentru izolare completă.

---

### Teste de Autentificare (AuthIntegrationTest)
Testează fluxurile complete de autentificare: înregistrare utilizator nou cu verificare în baza de date, login cu credențiale corecte care returnează JWT token, login eșuat cu parolă greșită, forgot password care generează token de resetare salvat în DB, și resetare parolă completă cu verificare că noul login funcționează.

### Teste de Control Acces (AccessControlIntegrationTest)
Validează sistemul de permisiuni pentru repository-uri publice și private. Testează că străinii pot accesa repository-uri publice dar primesc 404 pentru cele private, că owner-ul poate accesa propriile repository-uri private, și că collaboratorii cu status ACCEPTED au acces (READ poate vizualiza, WRITE poate și modifica). De asemenea, verifică că invitațiile PENDING nu oferă acces.

### Teste Administrative (AdminIntegrationTest)
Testează funcționalitățile bazate pe roluri: administratorul poate șterge utilizatori (returnează 204 No Content), utilizatorii normali primesc 403 Forbidden la încercarea de ștergere, și accesul admin la endpoint-uri sensibile precum /api/admin/stats.

### Teste Activity Feed (ActivityIntegrationTest)
Validează sistemul de feed și follow: când Alice urmărește pe Bob, iar Bob creează un issue, feed-ul lui Alice trebuie să conțină activitatea lui Bob cu descrierea "opened issue".

### Teste Avatar (AvatarLifecycleIntegrationTest + FileUploadIntegrationTest)
Testează ciclul complet de viață pentru avatare: upload avatar cu verificare URL salvat în DB, update avatar cu înlocuirea celui vechi, ștergere avatar propriu, admin poate șterge avatarul oricărui utilizator, și utilizatorii normali nu pot șterge avatarele altora (403 Forbidden).

### Teste Repository (RepositoryIntegrationTest)
Acoperă operațiile CRUD complete: creare repository cu verificare în DB și pe disc (folder .git creat), prevenire duplicate (400 Bad Request la același nume), listare repository-uri ale unui utilizator, redenumire repository cu verificare că folderul vechi e șters și cel nou există, și ștergere repository din DB și de pe disc.

### Teste Issues (IssueIntegrationTest)
Testează ciclul complet de viață: creare issue cu auto-increment number, listare issues cu verificare count, adăugare comentariu la issue, și închidere issue cu verificare status isOpen: false.

### Teste Pull Request (PullRequestIntegrationTest)
Validează fluxul complet de PR cu integrare Git reală: setup cu push initial pe master, creare branch feature-login și checkout, adăugare fișier și commit, push branch pe remote, creare Pull Request via API, vizualizare diff/files modificate, adăugare review APPROVED, merge PR, și verificare că fișierul există acum pe master.

### Teste Git HTTP Protocol (GitHttpIntegrationTest)
Testează protocolul Git HTTP folosind CLI-ul custom: push și clone funcțional (init → commit → push → clone → verifică conținut identic), și sincronizare modificări (Client A push v1 → Client B clone → A push v2 → B clone din nou → verifică v2 primit).

### Teste Git Access Control (GitAccessControlIntegrationTest)
Validează controlul accesului la nivel de protocol Git: clone repo public fără autentificare (200 OK), clone repo privat fără auth (404), owner poate clona repo privat (200), străin nu poate clona repo privat (404), străin nu poate push la repo public (403 Forbidden), owner poate push la repo propriu (200), și push fără auth la repo privat (404).

### Teste Git Data API (GitDataReadIntegrationTest)
Testează API-urile de citire date Git: listare fișiere la rădăcină (GET tree → returnează README.md și src/), listare în subfolder (GET tree/src/main → returnează Main.java), citire conținut fișier (GET blob → returnează conținutul exact), și listare istoric commituri (GET commits → returnează cel puțin 2 commituri cu mesajele corecte).

### Teste Markdown (MarkdownIntegrationTest)
Testează serviciul de randare: conversie Markdown în HTML verificând că heading-urile devin h1 și tabelele devin table.

### Teste Git Core Library (GitIntegrationTest)
Testează biblioteca Git core independent: workflow local complet (init → hash-object → write-tree → commit-tree → cat-file cu verificare conținut), și clone de pe server mock folosind MockWebServer cu pack file simulat și verificare checkout corect.

---

### Unit:
Proiectul conține 10 clase de unit teste în modulul git-core-lib cu aproximativ 15 metode de test care validează funcționalitatea bibliotecii Git custom. Testele utilizează JUnit 5 cu @TempDir pentru directoare temporare izolate, MockWebServer pentru simularea serverului Git HTTP, și captura stream-urilor System.out/System.err pentru verificarea output-ului CLI.

---

### Teste Operații Locale (GitLocalTest)
Testează ciclul complet de viață al operațiilor Git locale printr-un flow end-to-end: init pentru inițializare repository, hash-object pentru salvare blob, write-tree pentru generare tree hash, commit-tree pentru creare commit, branch pentru creare branch nou, checkout pentru switch între branch-uri, log pentru vizualizare istoric (verifică că ambele commituri apar), status pentru detectare fișiere noi/modificate, și merge pentru combinare branch-uri. Un test separat verifică cat-file pentru citirea conținutului obiectelor și debug-object pentru informații de debug.

### Teste Branching și Checkout (GitBranchingTest)
Validează operațiile de branching cu verificarea restaurării conținutului fișierelor. Flow-ul testează: commit pe master cu "Versiunea 1", creare branch feature, checkout pe feature cu modificare la "Versiunea 2" și commit, checkout înapoi pe master unde verifică că fișierul revine automat la "Versiunea 1", apoi checkout pe feature unde verifică că fișierul revine la "Versiunea 2". Se validează și că HEAD și refs pointează corect după fiecare operație.

### Teste Merge (GitMergeTest)
Testează merge-ul real între două branch-uri divergente. Se creează un commit base, apoi pe master se adaugă master_only.txt, pe feature se adaugă feature_only.txt, iar după merge se verifică că toate cele 3 fișiere există (base.txt, master_only.txt, feature_only.txt) și că output-ul este un hash valid de 40 caractere hexazecimale.

### Teste Log (GitLogTest)
Validează traversarea istoricului de commituri. După crearea a 3 commituri succesive ("First Commit" → "Second Commit" → "Third Commit"), comanda log trebuie să le afișeze în ordine cronologică inversă, să conțină toate mesajele și hash-urile, cu "Third Commit" apărând înaintea "First Commit".

### Teste Status (GitStatusTest)
Testează detectarea modificărilor în working directory. După un commit inițial cu 3 fișiere, se modifică unul, se șterge altul și se creează unul nou. Comanda status trebuie să afișeze M\tmodify.txt (modificat), D\tdelete.txt (șters), A\tnew.txt (nou/untracked), iar fișierul nemodificat clean.txt nu trebuie să apară.

### Teste Diff (GitDiffTest)
Validează calcularea diferențelor între commituri. Cu 3 commituri (c1: file.txt v1, c2: file.txt modificat + new.txt adăugat, c3: new.txt șters), comanda diff c1 c2 trebuie să afișeze M\tfile.txt și A\tnew.txt, iar diff c2 c3 trebuie să afișeze doar D\tnew.txt (fișierele nemodificate nu apar).

### Teste Push (GitPushTest)
Testează fluxul de push folosind MockWebServer. După inițializare și commit local, se verifică că push-ul efectuează corect: GET /info/refs pentru discovery, POST cu Content-Type application/x-git-receive-pack-request, și body-ul conține semnătura binară PACK.

### Teste Remote cu Autentificare (GitRemoteTest)
Validează operațiile remote cu scenarii de autentificare: push autorizat verifică prezența header-ului Authorization: Bearer în request-uri, push neautorizat (fără login) afișează "You must be logged in to push", push forbidden la repo fără acces afișează "Fatal Error", iar clone repo privat trimite corect token-ul JWT în header.

### Teste Autentificare CLI (GitAuthTest)
Testează comenzile de autentificare: login cu succes afișează "SUCCESS: Logged in as gituser" și whoami confirmă, login eșuat afișează "Login Failed" și whoami arată "Not logged in", iar logout afișează "Credentials removed" și whoami confirmă delogarea.

---
# Detailed Technical Documentation

## 1. Scalability and Performance

**Requirement:** Fast response times for common operations (listings, views).

**Solution:** Applications are containerized (`backend/` and `frontend/` have Dockerfiles); we use `docker-compose.yml` for development orchestration. For production, can transition to orchestration (e.g., Kubernetes), but this is not part of the current repo.

**Concrete measures in the project:**
- `backend` runs Spring Boot with HikariCP (JDBC connection pool) — configured in `src/main/resources/application.properties`.
- `frontend` is built for production and served with `nginx` (see `frontend/Dockerfile` and `nginx.conf`).
- Angular build optimization: `npm run build` generates `dist/` which is copied to the nginx image; code-splitting and lazy-loading are used through Angular configuration (`angular.json`), minimizing initial payloads.

### Performance (Large File Handling)
**Problem:** Storing source code (with thousands of files) in a relational database would slow down SQL queries.

**Solution:** Hybrid storage. Metadata (e.g., who made the commit) is in PostgreSQL, while actual content (blobs) is stored on disk, accessed through Java NIO. This allows fast reading of large files without blocking the database.

---

## 2. Data Persistence and Consistency

**Requirement:** Data integrity and possibility of controlled migrations.

**Solution:** PostgreSQL is the main repository. The project uses JPA/Hibernate; in development environment `spring.jpa.hibernate.ddl-auto` allows automatic schema updates. For production, managed migrations (Flyway/Liquibase) are considered, but these are not present in the repo.

---

## 3. Security Implementation Details

### 1. Authentication and Authorization
**Solution:** Spring Security on backend, JWT token-based authentication.

**Concrete measures:**
- JWT interceptor in frontend adds `Authorization` header.
- Backend validates token and applies roles/permissions.

### 2. Broken Object Level Authorization (BOLA)
**Risk:** Authenticated user tries to delete another user's repository by modifying ID in URL.

**Solution:** `AccessControlService`. In each sensitive method (e.g., `deleteFile`, `mergePr`) we call `accessControl.canWriteRepo(user, repo)`. Otherwise returns `403 Forbidden`.

### 3. Path Traversal
**Risk:** Access to system files (e.g., `../../etc/passwd`).

**Solution:** In `GitOperationService`, all paths are resolved relative to `gitRootPath` and user. `Path.resolve()` prevents exiting root directory.

### 4. Cross-Site Scripting (XSS)
**Risk:** Injection of scripts into database.

**Solution:** Angular automatically sanitizes on render (`{{ comment.body }}`). For Markdown, a library is used that removes dangerous tags.

**What's missing:** HTTPS, rate limiting.

---

## 4. Observability (Logging and Diagnostics)

**Requirement:** Log collection for debugging and real-time monitoring.

**Implemented Solution:** We integrated a complete observability stack based on **Grafana Loki** for centralization and visualization of logs from all Docker containers.

### Architecture:
- **Promtail** - agent that automatically collects logs from all Docker containers and sends them to Loki
- **Grafana Loki** - log aggregation and storage system (lightweight with index labeling)
- **Grafana** - web interface for visualization, queries, and alerting

### Configuration:
- Spring Boot backend emits logs in **JSON format** (via `logstash-logback-encoder`) for automatic parsing
- Promtail connects to Docker and auto-discovers new containers
- Loki datasource is pre-configured

### Access:
- **Grafana Dashboard:** http://localhost:3000 (admin/admin123)
- **Loki API:** http://localhost:3100

### LogQL Query Examples:
```logql
{container="gitspace_backend"}              # Backend logs
{container="gitspace_backend"} |= "ERROR"   # Only errors
{container=~".+"}                           # All containers
```

---

## 5. Backup and Recovery

**Requirement:** Possibility of data recovery.

**Solution:** PostgreSQL runs with external volume defined in `docker-compose.yml` (`postgres_data`).

---

## 6. Environments

### Development (Local)
- Frontend: `ng serve` (port 4200) with proxy to backend.
- Backend: run from IntelliJ (port 8080).
- DB: Local PostgreSQL or H2 in-memory.
- Volatile test data.
- Manually compiled library.

### Production (Dockerized)
- Configured through `docker-compose.yml`.
- Applications isolated in containers.
- Internal Docker network (`app-network`).

---

## 7. Testing Methodology

**Level 1: Unit Testing (Backend Logic):**  
What: Testing algorithms in Git.java (hash calculation, write tree).  
Method: White-box testing.  
Justification: Versioning logic is critical; if a hash is wrong, the entire repo is corrupt.

**Level 2: Integration/System Testing (Frontend + Backend):**  
What: Pull Request flow (Create → Conflict Check → Merge).  
Method: Black-box testing (through interface).  
Results/Observations: "We identified that initially merge did not detect conflicts. We introduced the canMerge method in Git.java that simulates a merge in memory before writing to disk."

---

## Demo

Check the `demo.mp4` file in the root directory for a video walkthrough of the platform's main features.

---

*For GitHub issues, we have product features, and in the issue description you'll find user stories and scenarios. In docs/diagrams are the diagrams.*