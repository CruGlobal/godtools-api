CREATE TABLE languages (
  id uuid NOT NULL PRIMARY KEY,
  name text,
  code text
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
  minimum_interpreter_version integer
);

CREATE TABLE pages (
  id uuid NOT NULL PRIMARY KEY,
  version_id uuid REFERENCES versions(id),
  ordinal integer,
  xml_content xml,
  description text
);

CREATE TABLE images (
  id uuid NOT NULL PRIMARY KEY,
  version_id uuid REFERENCES versions(id),
  image bytea
);

CREATE TABLE page_images (
  id uuid NOT NULL PRIMARY KEY,
  page_id uuid REFERENCES pages(id),
  image_id uuid REFERENCES images(id)
)
