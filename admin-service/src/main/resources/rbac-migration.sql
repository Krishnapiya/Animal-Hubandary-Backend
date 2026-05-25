-- Run this in PostgreSQL (masterdb) before starting admin-service.
-- Schema expected by current entities/controllers: master

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
  action_key VARCHAR(20) NOT NULL UNIQUE,
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

-- Optional multi-role mapping table (keep users.role_id for backward compatibility)
CREATE TABLE IF NOT EXISTS master.user_roles (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES master.users(id),
  role_id INTEGER NOT NULL REFERENCES master.role_master(id),
  UNIQUE(user_id, role_id)
);

CREATE INDEX IF NOT EXISTS idx_user_roles_user ON master.user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role ON master.user_roles(role_id);
