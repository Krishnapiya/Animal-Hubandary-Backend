-- Users: home / primary office. user_roles: role assignments per office (nullable office = legacy global row).
-- Run after office table exists.

ALTER TABLE master.users ADD COLUMN IF NOT EXISTS office_id BIGINT NULL REFERENCES master.office(id);

-- Allow clearing primary role from the user edit form (PATCH); multi-role still lives in user_roles.
ALTER TABLE master.users ALTER COLUMN role_id DROP NOT NULL;

ALTER TABLE master.user_roles DROP CONSTRAINT IF EXISTS user_roles_user_id_role_id_key;

ALTER TABLE master.user_roles ADD COLUMN IF NOT EXISTS office_id BIGINT NULL REFERENCES master.office(id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_user_roles_user_role_office
ON master.user_roles (user_id, role_id, (COALESCE(office_id, -1)));
