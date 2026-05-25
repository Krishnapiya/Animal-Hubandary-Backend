-- Designation master (run on masterdb). List API: GET .../admin/auth/master/designation/list/all
-- UI "Table columns JSON" must use attr/header, e.g.:
-- [{"attr":"id","header":"ID"},{"attr":"name","header":"Name"}]

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
