-- Payment Mode master (run on masterdb). List API: GET .../admin/auth/master/payment-mode/list/all
-- UI "Table columns JSON" can use:
-- [{"attr":"id","header":"ID"},{"attr":"name","header":"Name"}]

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
