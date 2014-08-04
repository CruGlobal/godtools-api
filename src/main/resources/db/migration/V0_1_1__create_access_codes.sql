CREATE TABLE access_codes(
  access_code text not null primary key,
  created_timestamp timestamptz,
  revoked_timestamp timestamptz
);