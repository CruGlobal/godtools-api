CREATE TABLE auth_tokens(
  id uuid not null primary key,
  username text,
  auth_token text,
  granted_timestamp timestamp with time zone,
  revoked_timestamp timestamp with time zone
);