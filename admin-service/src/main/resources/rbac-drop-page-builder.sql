-- Remove unused page-builder columns from menus (optional cleanup on existing DBs).

ALTER TABLE master.menus DROP COLUMN IF EXISTS list_url;
ALTER TABLE master.menus DROP COLUMN IF EXISTS api_url;
ALTER TABLE master.menus DROP COLUMN IF EXISTS table_columns_json;
