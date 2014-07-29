CREATE TABLE access_codes(
  access_code text not null primary key,
  created_timestamp timestamptz,
  revoked_timestamp timestamptz
);

INSERT INTO access_codes VALUES ('freddy', CURRENT_TIMESTAMP);

ALTER TABLE auth_tokens ADD device_id text;