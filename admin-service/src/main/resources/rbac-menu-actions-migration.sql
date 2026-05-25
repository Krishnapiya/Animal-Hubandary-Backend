-- Page-specific buttons/actions per menu (endpoint + action slug for RBAC).
-- Run after rbac-migration.sql.

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
