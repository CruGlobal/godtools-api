ALTER TABLE access_codes ADD COLUMN admin boolean DEFAULT false;
INSERT INTO access_codes(access_code, created_timestamp, admin) VALUES ('Knowing God Personally! 777', current_timestamp , true);
ALTER TABLE auth_tokens ADD COLUMN admin boolean DEFAULT false;