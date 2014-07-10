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
  version_number integer,
  released boolean
);

CREATE TABLE package_structure (
  id uuid NOT NULL PRIMARY KEY,
  package_id uuid REFERENCES packages(id),
  version_number integer,
  xml_content xml
);

CREATE TABLE page_structure (
  id uuid NOT NULL PRIMARY KEY,
  translation_id uuid references translations(id),
  xml_content xml,
  description text,
  filename text
);

CREATE TABLE translation_elements (
  id uuid NOT NULL,
  translation_id uuid NOT NULL REFERENCES translations(id),
  page_structure_id uuid REFERENCES page_structure(id),
  base_text text,
  translated_text text,
  element_type text,
  page_name text,
  display_order integer,
  PRIMARY KEY (id, translation_id)
);

CREATE TABLE translation_status (
  page_structure_id uuid REFERENCES page_structure(id),
  translation_id uuid REFERENCES translations(id),
  percent_completed decimal,
  string_count integer,
  word_count integer,
  last_updated timestamp,
  PRIMARY KEY(page_structure_id, translation_id)
);

CREATE TABLE images (
  id uuid NOT NULL PRIMARY KEY,
  filename text,
  resolution text,
  image_content bytea
);

CREATE TABLE referenced_images (
  image_id uuid REFERENCES images(id),
  package_structure_id uuid REFERENCES package_structure(id)
);

CREATE TABLE auth_tokens(
  id uuid not null primary key,
  username text,
  auth_token text,
  granted_timestamp timestamp with time zone,
  revoked_timestamp timestamp with time zone,
  draft_access boolean
);

CREATE TABLE access_codes(
  access_code text not null primary key,
  created_timestamp timestamptz,
  revoked_timestamp timestamptz
)