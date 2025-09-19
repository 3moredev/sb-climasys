## Climasys 2.0 Migration Plan: WebForms/ASMX → React + Spring Boot

### Goals
- Preserve all existing functionality while modernizing the stack.
- Reduce risk via phased rollout and coexistence with the legacy app.
- Improve security, maintainability, and performance.

### Target Architecture
- Frontend: React (Vite + TypeScript), React Router, component library (MUI/Ant), Axios/React Query, OpenAPI-generated types.
- Backend: Spring Boot (Web, Security, Validation), JDBC Template for stored procedures (initial), JPA (later), MapStruct, Lombok, OpenAPI.
- Auth: JWT (access + refresh), role-based authorization mapped from existing roles.
- DB: Reuse current SQL Server schema and stored procedures initially.
- Coexistence: Reverse proxy routes `/api/**` to Spring Boot; legacy WebForms serves remaining routes until full cutover.

### Phases
1) Foundations
- Create Spring Boot service connected to existing DB.
- Create React app: routing, layout, auth shell.
- Introduce reverse proxy for `/api/**`.

2) Core Auth & Dashboard
- Implement `/auth/login` using existing `USP_Get_LoginDetails` contract.
- Issue JWTs; add guards in React; migrate Dashboard.

3) Registration & Appointments
- Endpoints: patient quick/full registration, search, appointment booking, queue.
- React pages: Registration, Appointments, Queue.

4) Clinical Workflows
- Endpoints: profile, complaints, diagnosis, lab tests, prescriptions, medicines.
- Replace file uploads with multipart endpoints; persist metadata.

5) Billing, Reports, IPD/Discharge
- Implement billing/receipt/invoice/report endpoints and UIs.
- Migrate IPD flows.

6) Decommission Legacy
- Route all traffic to React + Spring Boot.
- Remove ASMX and WebForms.

### API Design Conventions
- RESTful nouns; versioned path: `/api/v1/...`.
- Pagination: `page`, `size`, `sort` query params.
- Validation: Bean Validation; consistent error schema.
- IDs: use existing domain keys; avoid leaking DB-internal integers where possible.

### Data Access Strategy
- Start with JdbcTemplate calling existing stored procedures (stable behavior).
- Map results to typed DTOs; avoid `DataSet/DataTable` style responses.
- Introduce JPA gradually for CRUD domains without SP dependencies.

### Security Hardening (Immediate)
- Move DB credentials to environment-specific secrets (no source control).
- Disable directory browsing on legacy IIS.
- Remove legacy request validation settings where safe.

### Coexistence & Routing
- IIS/NGINX Rewrite: `/api/*` → Spring Boot (8080). All else → legacy site.
- React dev server proxies to 8080 in development.

### Testing Strategy
- Contract tests per endpoint against DB (SP results), seeded test data.
- E2E for top workflows (login, registration, appointment, visit note, billing).

### Rollout Plan
- Pilot: limited users on new Dashboard + Auth.
- Incremental cutover per module; fallback path to legacy preserved until stable.
- Final switchover during low-traffic window; verify metrics and logs.

### Deliverables Checklist
- Spring Boot service repo: build, run, OpenAPI docs, CI.
- React app repo: build, run, auth, layout, CI.
- Reverse proxy config and deployment guide.
- Module-by-module migration notes and parity checkpoints.


