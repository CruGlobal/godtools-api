insert into auth_tokens(id, auth_token, granted_timestamp) VALUES('3b4b6182-4914-4cf2-ac83-1247c98b4df7', 'a', current_timestamp);
ALTER TABLE auth_tokens ADD device_id text;
INSERT INTO access_codes VALUES ('freddy', CURRENT_TIMESTAMP)