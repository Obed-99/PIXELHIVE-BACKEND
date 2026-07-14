# PixelHive — Backend API

Spring Boot REST API for PixelHive: digital asset management and secure
delivery for freelance videographers and creators.

## Live deployment
- **API:** https://pixelhive-backend-production.up.railway.app (Railway, Docker)
- **Database:** Supabase PostgreSQL (schema in `database/schema.sql`)

## Features
- **JWT authentication** — `POST /api/auth/login` returns a signed 24-hour
  token; every other `/api` endpoint rejects requests without a valid
  `Authorization: Bearer` header (see `config/JwtFilter`)
- **Users** — registration with BCrypt password hashing
- **Projects** — CRM basics: client, status, price
- **Media** — uploads store the picture as a base64 data-URL
  (`preview_data`; demo storage, S3-ready design), plus view/download
  analytics counters
- **Conditional release** — a successful payment flips every media file on
  the project from `uploaded` to `released` (`TransactionService`)
- **Payments** — `POST /api/pay/initialize` starts a hosted Paystack
  test-mode checkout when `PAYSTACK_SECRET_KEY` is set, and
  `/api/pay/verify` confirms with Paystack before releasing files;
  without a key the API reports demo mode
- **Contracts** — per-project generation + e-signature endpoint
- **Messaging** — persisted per-project chat
- **Notifications** — in-app alerts with read state
- **Dynamic pricing** — `POST /api/pricing/suggest` from resolution,
  duration and quality

## Tech
Spring Boot 4 · Java 21 · Spring Data JPA · PostgreSQL · jjwt · Docker

## Run locally
Requires a local PostgreSQL with the `pixelhive` database (run
`database/schema.sql` first).

```bash
./mvnw spring-boot:run     # http://localhost:8080
```

## Environment variables (production)
| Variable | Purpose |
|---|---|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Supabase connection |
| `JWT_SECRET` | JWT signing key |
| `PAYSTACK_SECRET_KEY` | Paystack test secret (`sk_test_...`); empty = demo payments |
| `PORT` | Provided by the host |

## Layout
```
src/main/java/com/pixelhive/backend/
  entity/       JPA entities (User, Project, MediaAsset, ...)
  repository/   Spring Data repositories
  controller/   REST endpoints
  service/      Business rules (payments, JWT, Paystack, contracts)
  dto/          Request/response records
  config/       CORS + JWT filter
database/schema.sql   Source of truth for the schema + migrations
```
