-- =============================================================================
-- Keltron Framework — full PostgreSQL migration (single file)
-- =============================================================================
-- Database: masterdb (adjust connection as needed)
-- Schema:   master
--
-- USE CASES
--   • Fresh database: run this entire file once.
--   • Existing database: run only sections you have not applied yet, or use
--     the per-file scripts under admin-service/src/main/resources/ instead.
--
-- ORDER (do not reorder sections on a fresh install)
--   1. Schema
--   2. Core auth (role_master, users)
--   3. Master entities
--   4. RBAC tables
--   5. RBAC extensions (menu_actions, user_roles per office)
--   6. Cleanup (drop unused page-builder columns if present)
--   7. Seed modules, menus, permission actions, sample page actions
-- =============================================================================

BEGIN;

-- -----------------------------------------------------------------------------
-- 1. Schema
-- -----------------------------------------------------------------------------
CREATE SCHEMA IF NOT EXISTS master;

-- -----------------------------------------------------------------------------
-- 2. Core auth tables (required before RBAC FKs)
-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS master.role_master (
  id SERIAL PRIMARY KEY,
  role_name VARCHAR(255) NOT NULL,
  created_by VARCHAR(100),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100),
  last_modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_role_master_name UNIQUE (role_name)
);

CREATE TABLE IF NOT EXISTS master.users (
  id BIGSERIAL PRIMARY KEY,
  fname VARCHAR(255) NOT NULL,
  lname VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  mobile_no VARCHAR(50) NOT NULL,
  username VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  role_id INTEGER NULL REFERENCES master.role_master(id),
  office_id BIGINT NULL,
  created_by VARCHAR(100),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100),
  last_modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_users_username UNIQUE (username),
  CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE INDEX IF NOT EXISTS idx_users_username ON master.users (lower(username));
CREATE INDEX IF NOT EXISTS idx_users_role ON master.users (role_id);
CREATE INDEX IF NOT EXISTS idx_users_office ON master.users (office_id);

-- -----------------------------------------------------------------------------
-- 3. Master entity tables
-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS master.designation (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  created_by VARCHAR(100),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100),
  last_modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_designation_name UNIQUE (name)
);
CREATE INDEX IF NOT EXISTS idx_designation_name ON master.designation (lower(name));

CREATE TABLE IF NOT EXISTS master.payment_mode (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  created_by VARCHAR(100),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100),
  last_modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_payment_mode_name UNIQUE (name)
);
CREATE INDEX IF NOT EXISTS idx_payment_mode_name ON master.payment_mode (lower(name));

CREATE TABLE IF NOT EXISTS master.sample_department (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  created_by VARCHAR(100),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100),
  last_modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_sample_department_name UNIQUE (name)
);
CREATE INDEX IF NOT EXISTS idx_sample_department_name ON master.sample_department (lower(name));

CREATE TABLE IF NOT EXISTS master.office (
  id BIGSERIAL PRIMARY KEY,
  office_type VARCHAR(100) NOT NULL,
  name VARCHAR(255) NOT NULL,
  parent_id BIGINT NULL REFERENCES master.office(id) ON DELETE SET NULL,
  created_by VARCHAR(100),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100),
  last_modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_office_name ON master.office (lower(name));
CREATE INDEX IF NOT EXISTS idx_office_type ON master.office (lower(office_type));
CREATE INDEX IF NOT EXISTS idx_office_parent ON master.office (parent_id);

-- FK users.office_id → office (after office exists)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'fk_users_office' AND connamespace = 'master'::regnamespace
  ) THEN
    ALTER TABLE master.users
      ADD CONSTRAINT fk_users_office
      FOREIGN KEY (office_id) REFERENCES master.office(id);
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS master.store_item (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  created_by VARCHAR(100),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100),
  last_modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_store_item_name UNIQUE (name)
);
CREATE INDEX IF NOT EXISTS idx_store_item_name ON master.store_item (lower(name));

-- -----------------------------------------------------------------------------
-- 4. RBAC core
-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS master.modules (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  slug VARCHAR(120) NOT NULL UNIQUE,
  display_order INT DEFAULT 0,
  active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS master.menus (
  id BIGSERIAL PRIMARY KEY,
  module_id BIGINT NOT NULL REFERENCES master.modules(id),
  parent_id BIGINT NULL REFERENCES master.menus(id),
  name VARCHAR(120) NOT NULL,
  slug VARCHAR(120) NOT NULL,
  path VARCHAR(255) NOT NULL,
  display_order INT DEFAULT 0,
  active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(module_id, slug)
);

CREATE TABLE IF NOT EXISTS master.permission_actions (
  id BIGSERIAL PRIMARY KEY,
  action_key VARCHAR(60) NOT NULL UNIQUE,
  description VARCHAR(255)
);

INSERT INTO master.permission_actions(action_key, description)
VALUES
  ('list', 'Read/list records'),
  ('save', 'Create/save records'),
  ('edit', 'Update records'),
  ('delete', 'Delete records'),
  ('export', 'Export/download records')
ON CONFLICT (action_key) DO NOTHING;

CREATE TABLE IF NOT EXISTS master.role_menu_permissions (
  id BIGSERIAL PRIMARY KEY,
  role_id INTEGER NOT NULL REFERENCES master.role_master(id),
  menu_id BIGINT NOT NULL REFERENCES master.menus(id),
  action_id BIGINT NOT NULL REFERENCES master.permission_actions(id),
  allowed BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(role_id, menu_id, action_id)
);

CREATE INDEX IF NOT EXISTS idx_role_menu_permissions_role ON master.role_menu_permissions(role_id);
CREATE INDEX IF NOT EXISTS idx_role_menu_permissions_menu ON master.role_menu_permissions(menu_id);
CREATE INDEX IF NOT EXISTS idx_menus_module ON master.menus(module_id);

-- Multi-role per user (legacy); office-scoped assignments added in section 5
CREATE TABLE IF NOT EXISTS master.user_roles (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES master.users(id) ON DELETE CASCADE,
  role_id INTEGER NOT NULL REFERENCES master.role_master(id),
  office_id BIGINT NULL REFERENCES master.office(id)
);

CREATE INDEX IF NOT EXISTS idx_user_roles_user ON master.user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role ON master.user_roles(role_id);

-- -----------------------------------------------------------------------------
-- 5. RBAC extensions (page-specific actions; user_roles per office)
-- -----------------------------------------------------------------------------

-- Widen action_key for custom slugs (e.g. assign-roles)
ALTER TABLE master.permission_actions
  ALTER COLUMN action_key TYPE VARCHAR(60);

CREATE TABLE IF NOT EXISTS master.menu_actions (
  id BIGSERIAL PRIMARY KEY,
  menu_id BIGINT NOT NULL REFERENCES master.menus(id) ON DELETE CASCADE,
  action_key VARCHAR(60) NOT NULL,
  label VARCHAR(120) NOT NULL,
  endpoint VARCHAR(512) NULL,
  display_order INT DEFAULT 0,
  UNIQUE(menu_id, action_key)
);

CREATE INDEX IF NOT EXISTS idx_menu_actions_menu ON master.menu_actions(menu_id);

-- Upgrades on existing DBs (safe on fresh DB too)
ALTER TABLE master.users ADD COLUMN IF NOT EXISTS office_id BIGINT NULL;
ALTER TABLE master.users ALTER COLUMN role_id DROP NOT NULL;

ALTER TABLE master.user_roles DROP CONSTRAINT IF EXISTS user_roles_user_id_role_id_key;
ALTER TABLE master.user_roles ADD COLUMN IF NOT EXISTS office_id BIGINT NULL;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'user_roles_office_id_fkey'
      AND conrelid = 'master.user_roles'::regclass
  ) THEN
    ALTER TABLE master.user_roles
      ADD CONSTRAINT user_roles_office_id_fkey
      FOREIGN KEY (office_id) REFERENCES master.office(id);
  END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS uq_user_roles_user_role_office
  ON master.user_roles (user_id, role_id, (COALESCE(office_id, -1)));

-- -----------------------------------------------------------------------------
-- 6. Cleanup — remove deprecated page-builder columns (if they exist)
-- -----------------------------------------------------------------------------
ALTER TABLE master.menus DROP COLUMN IF EXISTS list_url;
ALTER TABLE master.menus DROP COLUMN IF EXISTS api_url;
ALTER TABLE master.menus DROP COLUMN IF EXISTS table_columns_json;

-- -----------------------------------------------------------------------------
-- 7. Seed data — modules & menus (match React routes / @RequirePermission)
-- -----------------------------------------------------------------------------

INSERT INTO master.modules(name, slug, display_order, active)
VALUES
  ('Master', 'master', 10, true),
  ('Store', 'store', 30, true)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO master.menus(module_id, name, slug, path, display_order, active)
SELECT m.id, 'Users', 'test-page', 'test-page', 10, true
FROM master.modules m WHERE m.slug = 'master'
ON CONFLICT (module_id, slug) DO NOTHING;

INSERT INTO master.menus(module_id, name, slug, path, display_order, active)
SELECT m.id, 'Office', 'office', 'office', 20, true
FROM master.modules m WHERE m.slug = 'master'
ON CONFLICT (module_id, slug) DO NOTHING;

INSERT INTO master.menus(module_id, name, slug, path, display_order, active)
SELECT m.id, 'Designation', 'designation', 'designation', 30, true
FROM master.modules m WHERE m.slug = 'master'
ON CONFLICT (module_id, slug) DO NOTHING;

INSERT INTO master.menus(module_id, name, slug, path, display_order, active)
SELECT m.id, 'Payment Mode', 'payment-mode', 'payment-mode', 40, true
FROM master.modules m WHERE m.slug = 'master'
ON CONFLICT (module_id, slug) DO NOTHING;

INSERT INTO master.menus(module_id, name, slug, path, display_order, active)
SELECT m.id, 'Sample Department', 'sample-department', 'sample-department', 50, true
FROM master.modules m WHERE m.slug = 'master'
ON CONFLICT (module_id, slug) DO NOTHING;

INSERT INTO master.menus(module_id, name, slug, path, display_order, active)
SELECT m.id, 'Access Control', 'rbac-admin', 'rbac-admin', 90, true
FROM master.modules m WHERE m.slug = 'master'
ON CONFLICT (module_id, slug) DO NOTHING;

INSERT INTO master.menus(module_id, name, slug, path, display_order, active)
SELECT m.id, 'Store items', 'store-item', 'store-item', 1, true
FROM master.modules m WHERE m.slug = 'store'
ON CONFLICT (module_id, slug) DO NOTHING;

-- Custom permission action: assign-roles (Users / test-page)
INSERT INTO master.permission_actions(action_key, description)
VALUES ('assign-roles', 'Assign roles by office')
ON CONFLICT (action_key) DO NOTHING;

INSERT INTO master.menu_actions(menu_id, action_key, label, endpoint, display_order)
SELECT me.id, 'assign-roles', 'Assign Roles',
  'admin/auth/master/users/{id}/assign-roles', 0
FROM master.menus me
JOIN master.modules mo ON mo.id = me.module_id
WHERE mo.slug = 'master' AND me.slug = 'test-page'
ON CONFLICT (menu_id, action_key) DO UPDATE
  SET label = EXCLUDED.label,
      endpoint = EXCLUDED.endpoint;

-- -----------------------------------------------------------------------------
-- 8. Optional seed roles (uncomment and adjust passwords for your environment)
-- -----------------------------------------------------------------------------
-- INSERT INTO master.role_master(role_name) VALUES ('ADMIN'), ('LAB USER')
-- ON CONFLICT (role_name) DO NOTHING;

COMMIT;

-- =============================================================================
-- After migration
-- =============================================================================
-- 1. Grant role_menu_permissions via Access Control UI (rbac-admin) or SQL.
-- 2. Create users and link role_id / user_roles as needed.
-- 3. Restart admin-service and api_gateway.
-- =============================================================================
