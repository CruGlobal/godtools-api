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
  default_language_id uuid REFERENCES languages(id)
);

CREATE TABLE translations (
  id uuid NOT NULL PRIMARY KEY,
  package_id uuid REFERENCES packages(id),
  language_id uuid REFERENCES languages(id)
);

CREATE TABLE versions (
  id uuid NOT NULL PRIMARY KEY,
  version_number integer NOT NULL,
  released boolean DEFAULT false,
  package_id uuid REFERENCES packages(id),
  translation_id uuid REFERENCES translations(id),
  minimum_interpreter_version integer,
  package_structure xml
);

CREATE TABLE pages (
  id uuid NOT NULL PRIMARY KEY,
  version_id uuid REFERENCES versions(id),
  ordinal integer,
  xml_content xml,
  description text,
  filename text,
  page_hash text
);

CREATE TABLE images (
  id uuid NOT NULL PRIMARY KEY,
  image_content bytea,
  filename text,
  image_hash text
);

CREATE TABLE page_images (
  id uuid NOT NULL PRIMARY KEY,
  page_id uuid REFERENCES pages(id),
  image_id uuid REFERENCES images(id)
)
