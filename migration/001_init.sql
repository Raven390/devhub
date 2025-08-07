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

