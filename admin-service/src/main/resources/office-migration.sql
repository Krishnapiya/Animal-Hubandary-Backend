-- Office master (run on masterdb).
-- API base: /admin/auth/master/office

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

INSERT INTO master.modules(name, slug, display_order, active)
VALUES ('Master', 'master', 10, true)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO master.menus(module_id, name, slug, path, display_order, active)
SELECT m.id, 'Office', 'office', 'office', 20, true
FROM master.modules m
WHERE m.slug = 'master'
ON CONFLICT (module_id, slug) DO NOTHING;
