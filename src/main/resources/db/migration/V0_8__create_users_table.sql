CREATE TABLE users (
    id uuid NOT NULL PRIMARY KEY,
    relay_id text,
    granted_timestamp timestamptz,
    revoked_timestamp timestamptz,
    user_level integer
);