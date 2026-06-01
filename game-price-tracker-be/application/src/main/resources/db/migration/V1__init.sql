-- V1: Initial schema placeholder.
-- Real tables (game, price, favorite) land in Phase 1.
CREATE TABLE IF NOT EXISTS schema_version_marker (
    id INTEGER PRIMARY KEY,
    note TEXT NOT NULL
);
INSERT INTO schema_version_marker (id, note) VALUES (1, 'V1 placeholder applied')
    ON CONFLICT (id) DO NOTHING;
