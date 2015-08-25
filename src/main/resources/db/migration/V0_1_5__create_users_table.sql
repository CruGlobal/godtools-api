CREATE TABLE users (
    id uuid NOT NULL PRIMARY KEY,
    user_id text,
    user_name text,
    granted_timestamp timestamptz,
    revoked_timestamp timestamptz,
    user_level text
);