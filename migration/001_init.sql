CREATE SCHEMA IF NOT EXISTS "gamehub";

CREATE SCHEMA keycloak;

ALTER SCHEMA keycloak OWNER TO postgres;


-- Enum-тип для статуса проекта
CREATE TYPE project_status AS ENUM ('draft', 'active', 'recruiting', 'archived');

-- Роли в проекте (многие-ко-многим)
CREATE TABLE project_type
(
    id   uuid PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);

-- Основная таблица проектов
CREATE TABLE project
(
    id                uuid PRIMARY KEY,
    owner_id          uuid                     NOT NULL REFERENCES users (id),
    name              VARCHAR(128)             NOT NULL,
    short_description VARCHAR(300)             NOT NULL,
    description       VARCHAR(3000),
    type_id           uuid                     NOT NULL REFERENCES project_type (id),
    status            project_status           NOT NULL,
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Технологии проекта (многие-ко-многим)
CREATE TABLE technology
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE project_technology
(
    project_id    uuid    NOT NULL REFERENCES project (id) ON DELETE CASCADE,
    technology_id INTEGER NOT NULL REFERENCES technology (id) ON DELETE CASCADE,
    PRIMARY KEY (project_id, technology_id)
);

-- Роли в проекте (многие-ко-многим)
CREATE TABLE role
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);



CREATE TABLE project_role
(
    project_id uuid    NOT NULL REFERENCES project (id) ON DELETE CASCADE,
    role_id    INTEGER NOT NULL REFERENCES role (id) ON DELETE CASCADE,
    PRIMARY KEY (project_id, role_id)
);

-- Таблица для участников проекта (если потребуется)
CREATE TABLE project_member
(
    project_id uuid                     NOT NULL REFERENCES project (id) ON DELETE CASCADE,
    user_id    uuid                     NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    joined_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    role_id    INTEGER REFERENCES role (id),
    PRIMARY KEY (project_id, user_id)
);

-- Индексы для ускорения поиска (по необходимости)
CREATE INDEX idx_project_status ON project (status);
CREATE INDEX idx_project_owner_id ON project (owner_id);
CREATE INDEX idx_project_created_at ON project (created_at);

-- (Опционально) для поиска по имени/описанию
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_project_name_trgm ON project USING gin (name gin_trgm_ops);
CREATE INDEX idx_project_short_description_trgm ON project USING gin (short_description gin_trgm_ops);

-- A) Участники проекта (membership)
CREATE TABLE IF NOT EXISTS gamehub.project_member (
                                                      id               UUID PRIMARY KEY,
                                                      project_id       UUID NOT NULL REFERENCES gamehub.project(id) ON DELETE CASCADE,
                                                      user_id          UUID NOT NULL REFERENCES gamehub."users"(id) ON DELETE RESTRICT,
                                                      member_status    TEXT NOT NULL DEFAULT 'ACTIVE', -- ACTIVE | INVITED | LEFT | REMOVED | OWNER
                                                      joined_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
                                                      left_at          TIMESTAMPTZ,
                                                      UNIQUE (project_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_project_member_project ON gamehub.project_member(project_id);
CREATE INDEX IF NOT EXISTS idx_project_member_user ON gamehub.project_member(user_id);

-- B) Множественные роли участника
CREATE TABLE IF NOT EXISTS gamehub.project_member_role (
                                                           project_member_id UUID NOT NULL REFERENCES gamehub.project_member(id) ON DELETE CASCADE,
                                                           role_id           int4 NOT NULL REFERENCES gamehub.role(id) ON DELETE RESTRICT,
                                                           PRIMARY KEY (project_member_id, role_id)
);

CREATE INDEX IF NOT EXISTS idx_project_member_role_role ON gamehub.project_member_role(role_id);


CREATE TABLE IF NOT EXISTS gamehub.project_vacancy (
                                                       id               UUID PRIMARY KEY,
                                                       project_id       UUID NOT NULL REFERENCES gamehub.project(id) ON DELETE CASCADE,
                                                       role_id          int4 NOT NULL REFERENCES gamehub.role(id) ON DELETE RESTRICT,
                                                       title            TEXT,                 -- опционально: «Backend (Java)»
                                                       description      TEXT,                 -- требования/задачи (markdown ok, sanitize в приложении)
                                                       slots_total      INT  NOT NULL CHECK (slots_total > 0),
                                                       slots_open       INT  NOT NULL CHECK (slots_open >= 0),
                                                       vacancy_status   TEXT NOT NULL DEFAULT 'OPEN', -- OPEN | PAUSED | CLOSED | DRAFT
                                                       created_by       UUID NOT NULL REFERENCES gamehub."users"(id) ON DELETE RESTRICT,
                                                       created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
                                                       updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_project_vacancy_project ON gamehub.project_vacancy(project_id);
CREATE INDEX IF NOT EXISTS idx_project_vacancy_role ON gamehub.project_vacancy(role_id);
CREATE INDEX IF NOT EXISTS idx_project_vacancy_status ON gamehub.project_vacancy(vacancy_status);

CREATE TABLE IF NOT EXISTS gamehub.project_audit_log (
                                                         id            BIGSERIAL PRIMARY KEY,
                                                         project_id    UUID NOT NULL REFERENCES gamehub.project(id) ON DELETE CASCADE,
                                                         actor_id      UUID,                     -- кто совершил (может быть NULL для системных операций)
                                                         action        TEXT NOT NULL,            -- PROJECT_CREATED, MEMBER_ADDED, MEMBER_ROLE_ADDED, TECHNOLOGY_ADDED, VACANCY_CREATED, ...
                                                         occurred_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
                                                         details       JSONB                     -- произвольный payload: { "memberId": "...", "roleIds": "...", "old":..., "new":... }
);

CREATE INDEX IF NOT EXISTS idx_project_audit_project_time ON gamehub.project_audit_log(project_id, occurred_at DESC);
CREATE INDEX IF NOT EXISTS idx_project_audit_action ON gamehub.project_audit_log(action);

