ALTER TABLE page_structure
  ADD percent_completed decimal,
  ADD string_count integer,
  ADD word_count integer,
  ADD last_updated timestamp;

UPDATE page_structure SET percent_completed = translation_status.percent_completed,
  string_count = translation_status.string_count,
  word_count = translation_status.word_count,
  last_updated = translation_status.last_updated
  FROM translation_status
  WHERE id = translation_status.page_structure_id;

DROP TABLE translation_status;