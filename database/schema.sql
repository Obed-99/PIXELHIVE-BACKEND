-- PixelHive database schema
-- Applied to the `pixelhive` database on local PostgreSQL 17.
-- Tables are added here one at a time, in dependency order.

-- =====================================================================
-- Table: users
-- Every person who uses PixelHive: creators, clients, and admins.
-- Everything else in the app (projects, files, payments) points back here.
-- =====================================================================
CREATE TABLE IF NOT EXISTS users (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(150) NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'client',
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),

    -- a user can only be one of these three kinds
    CONSTRAINT users_role_check CHECK (role IN ('creator', 'client', 'admin'))
);

-- =====================================================================
-- Table: projects
-- A job/engagement between a creator and a client. This is the hub that
-- media_assets, contracts, transactions and messages all attach to.
-- =====================================================================
CREATE TABLE IF NOT EXISTS projects (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    creator_id  BIGINT       NOT NULL REFERENCES users(id),  -- who owns the project
    client_id   BIGINT       REFERENCES users(id),           -- the client (may be unset early on)
    title       VARCHAR(200) NOT NULL,
    description TEXT,
    price       NUMERIC(12,2),                               -- exact money value, 2 decimals
    status      VARCHAR(20)  NOT NULL DEFAULT 'draft',
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT projects_status_check
        CHECK (status IN ('draft', 'active', 'delivered', 'completed', 'cancelled'))
);

-- =====================================================================
-- Table: media_assets
-- The uploaded files for a project. Holds the S3 path to the original
-- file AND to the watermarked preview. status tracks the pipeline:
-- uploaded -> watermarked -> released (unlocked after payment).
-- =====================================================================
CREATE TABLE IF NOT EXISTS media_assets (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    project_id      BIGINT       NOT NULL REFERENCES projects(id),
    file_name       VARCHAR(255) NOT NULL,
    s3_key_original VARCHAR(500) NOT NULL,   -- path to the real high-res file in S3
    s3_key_preview  VARCHAR(500),            -- path to the watermarked preview
    file_size       BIGINT,                  -- size in bytes
    status          VARCHAR(20)  NOT NULL DEFAULT 'uploaded',
    view_count      INTEGER      NOT NULL DEFAULT 0,   -- analytics: preview views
    download_count  INTEGER      NOT NULL DEFAULT 0,   -- analytics: downloads
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT media_assets_status_check
        CHECK (status IN ('uploaded', 'watermarked', 'released'))
);

-- =====================================================================
-- Table: contracts
-- One AI-generated contract per project. signed_at is filled in only
-- once the client actually signs.
-- =====================================================================
CREATE TABLE IF NOT EXISTS contracts (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    project_id  BIGINT      NOT NULL REFERENCES projects(id),
    content     TEXT        NOT NULL,        -- the generated contract text
    status      VARCHAR(20) NOT NULL DEFAULT 'draft',
    signed_at   TIMESTAMPTZ,                 -- NULL until signed
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT contracts_status_check
        CHECK (status IN ('draft', 'sent', 'signed'))
);

-- =====================================================================
-- Table: transactions
-- A Paystack payment for a project. paystack_ref is UNIQUE so the same
-- payment can never be recorded twice. Payment success unlocks the files.
-- =====================================================================
CREATE TABLE IF NOT EXISTS transactions (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    project_id   BIGINT        NOT NULL REFERENCES projects(id),
    amount       NUMERIC(12,2) NOT NULL,
    currency     VARCHAR(3)    NOT NULL DEFAULT 'GHS',
    paystack_ref VARCHAR(100)  NOT NULL UNIQUE,   -- Paystack's reference id
    status       VARCHAR(20)   NOT NULL DEFAULT 'pending',
    paid_at      TIMESTAMPTZ,
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT transactions_status_check
        CHECK (status IN ('pending', 'success', 'failed'))
);

-- =====================================================================
-- Table: messages
-- Chat between the creator and client, scoped to a project. Has TWO
-- foreign keys: which project it belongs to, and who sent it.
-- read_at is NULL while the message is unread.
-- =====================================================================
CREATE TABLE IF NOT EXISTS messages (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    project_id BIGINT      NOT NULL REFERENCES projects(id),
    sender_id  BIGINT      NOT NULL REFERENCES users(id),
    content    TEXT        NOT NULL,
    read_at    TIMESTAMPTZ,                 -- NULL = not read yet
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =====================================================================
-- Table: notifications
-- In-app alerts for a user (payment received, file viewed, contract signed).
-- =====================================================================
CREATE TABLE IF NOT EXISTS notifications (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    BIGINT       NOT NULL REFERENCES users(id),
    type       VARCHAR(30)  NOT NULL,          -- payment | view | contract | message
    message    VARCHAR(255) NOT NULL,
    is_read    BOOLEAN      NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- =====================================================================
-- Migrations for databases created before the analytics columns existed.
-- Safe to run repeatedly.
-- =====================================================================
ALTER TABLE media_assets ADD COLUMN IF NOT EXISTS view_count     INTEGER NOT NULL DEFAULT 0;
ALTER TABLE media_assets ADD COLUMN IF NOT EXISTS download_count INTEGER NOT NULL DEFAULT 0;
