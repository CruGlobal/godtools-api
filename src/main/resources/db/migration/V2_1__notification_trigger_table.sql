CREATE TABLE notifications (
  id UUID NOT NULL PRIMARY KEY,
  registration_id TEXT,
  notification_type INT,
  presentations INT,
  notification_sent BOOLEAN,
  timestamp TIMESTAMPTZ
);