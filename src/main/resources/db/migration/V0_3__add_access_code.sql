CREATE TABLE access_codes(
  access_code text not null primary key,
  created_timestamp timestamptz,
  revoked_timestamp timestamptz
);

INSERT INTO access_codes VALUES ('123456', '12-31-2013');

ALTER TABLE auth_tokens ADD device_id text;