-- =============================================================================
-- DevHub V0.0.3: Indexes for Project Filtering
-- Purpose: Create indexes on project table and its join tables to support efficient filtering
-- =============================================================================

BEGIN;

SET search_path TO devhub, public;

-- Filtering by status
CREATE INDEX IF NOT EXISTS idx_project_status ON devhub.project(status);

-- Search by name using trigram index
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX IF NOT EXISTS idx_project_name_trgm
    ON devhub.project USING gin(lower(name) gin_trgm_ops);

-- Filtering by technologies
CREATE INDEX IF NOT EXISTS idx_project_tech_tech_id
    ON devhub.project_technology(technology_id);

-- Filtering by roles
CREATE INDEX IF NOT EXISTS idx_project_role_role_id
    ON devhub.project_role(role_id);

-- Owner ID (for filtering by owner)
CREATE INDEX IF NOT EXISTS idx_project_owner_id
    ON devhub.project(owner_id);

COMMIT;
