# --- !Ups

ALTER TABLE content ADD COLUMN first boolean NOT NULL DEFAULT FALSE;
UPDATE content SET first = TRUE WHERE content_id IN (SELECT (array_agg(content_id))[1] FROM (SELECT * FROM content ORDER BY fetched_at ASC) AS c GROUP BY page_id);

# --- !Downs

ALTER TABLE content DROP COLUMN first;
