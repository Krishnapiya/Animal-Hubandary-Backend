-- Store module: store items master (run on masterdb).
-- API base: /admin/auth/master/store-item

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

-- RBAC: Store module + menu (assign permissions to roles via RBAC admin UI or SQL below).
INSERT INTO master.modules(name, slug, display_order, active)
VALUES ('Store', 'store', 30, true)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO master.menus(module_id, name, slug, path, display_order, active)
SELECT m.id, 'Store items', 'store-item', 'store-item', 1, true
FROM master.modules m
WHERE m.slug = 'store'
ON CONFLICT (module_id, slug) DO NOTHING;

-- Optional: grant all actions on this menu to role id 1 (adjust role_id to your admin role).
-- INSERT INTO master.role_menu_permissions(role_id, menu_id, action_id, allowed)
-- SELECT 1, me.id, pa.id, true
-- FROM master.menus me
-- JOIN master.modules mo ON mo.id = me.module_id AND mo.slug = 'store' AND me.slug = 'store-item'
-- CROSS JOIN master.permission_actions pa
-- ON CONFLICT (role_id, menu_id, action_id) DO UPDATE SET allowed = EXCLUDED.allowed;
