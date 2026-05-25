-- Sample Department master (run on masterdb).
-- API base: /admin/auth/master/sample-department

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

-- Optional RBAC menu registration (adjust module slug as needed):
-- INSERT INTO master.modules(name, slug, display_order, active)
-- VALUES ('Master', 'master', 10, true)
-- ON CONFLICT (slug) DO NOTHING;
--
-- INSERT INTO master.menus(module_id, name, slug, path, display_order, active)
-- SELECT m.id, 'Sample Department', 'sample-department', 'sample-department', 1, true
-- FROM master.modules m
-- WHERE m.slug = 'master'
-- ON CONFLICT (module_id, slug) DO NOTHING;

