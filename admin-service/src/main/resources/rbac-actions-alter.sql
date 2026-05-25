-- Run once on existing DBs to add non-CRUD action keys.
INSERT INTO master.permission_actions(action_key, description)
VALUES ('export', 'Export/download records')
ON CONFLICT (action_key) DO NOTHING;
