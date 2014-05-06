CREATE TABLE languages (
  id uuid NOT NULL PRIMARY KEY,
  name text,
  code varchar(4),
  locale varchar(4),
  subculture text
);

CREATE TABLE packages (
  id uuid NOT NULL PRIMARY KEY,
  name text,
  code text,
  default_language_id uuid REFERENCES languages(id),
  onesky_project_id integer
);

CREATE TABLE translations (
  id uuid NOT NULL PRIMARY KEY,
  package_id uuid REFERENCES packages(id),
  language_id uuid REFERENCES languages(id),
  version_number integer
);

CREATE TABLE translation_elements (
  id uuid NOT NULL,
  translation_id uuid NOT NULL REFERENCES translations(id),
  base_text text,
  translated_text text,
  element_type text,
  page_name text,
  display_order integer,
  PRIMARY KEY (id, translation_id)
);

CREATE TABLE package_structure (
  id uuid NOT NULL PRIMARY KEY,
  package_id uuid REFERENCES packages(id),
  version_number integer,
  xml_content xml
);

CREATE TABLE page_structure (
  id uuid NOT NULL PRIMARY KEY,
  package_structure_id uuid references package_structure(id),
  xml_content xml,
  description text,
  filename text
);

CREATE TABLE images (
  id uuid NOT NULL PRIMARY KEY,
  image_content bytea,
  image_hash text
);

CREATE TABLE referenced_images (
  image_id uuid REFERENCES images(id),
  page_id uuid REFERENCES page_structure(id),
  translation_id uuid REFERENCES translations(id)
);

CREATE TABLE auth_tokens(
  id uuid not null primary key,
  username text,
  auth_token text,
  granted_timestamp timestamp with time zone,
  revoked_timestamp timestamp with time zone
);