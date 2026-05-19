-- =============================================================================
-- DevHub V0.0.2: User profile fields + user_technology join table
-- Purpose: extend users table with profile fields (avatar, bio, github);
--          add many-to-many relationship between users and technologies
-- =============================================================================

BEGIN;

SET search_path TO devhub, public;

-- Add profile columns to users table
ALTER TABLE devhub.users
    ADD COLUMN IF NOT EXISTS avatar_url  VARCHAR(500),
    ADD COLUMN IF NOT EXISTS bio         VARCHAR(1000),
    ADD COLUMN IF NOT EXISTS github_url  VARCHAR(500);

COMMENT ON COLUMN devhub.users.avatar_url IS 'Gravatar URL или кастомный аватар (если загружен)';
COMMENT ON COLUMN devhub.users.bio        IS 'Расширенное описание профиля пользователя (до 1000 символов)';
COMMENT ON COLUMN devhub.users.github_url IS 'Ссылка на GitHub-профиль пользователя';

-- user ↔ technology many-to-many (skill list)
CREATE TABLE IF NOT EXISTS devhub.user_technology
(
    user_id       UUID    NOT NULL REFERENCES devhub.users (id) ON DELETE CASCADE,
    technology_id INTEGER NOT NULL REFERENCES devhub.technology (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, technology_id)
);

COMMENT ON TABLE devhub.user_technology IS 'Навыки пользователя: связь пользователей со справочником технологий';

-- Backfill avatar_url for existing users using Gravatar
-- MD5 hash is computed in Java at registration time; existing rows remain NULL
-- (they will be populated on first PUT /users/me or re-login)

COMMIT;
