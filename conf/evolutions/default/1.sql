# --- !Ups

CREATE TABLE page (
	page_id uuid NOT NULL,
	created_at timestamptz NOT NULL,
	url text NOT NULL UNIQUE,
	hits integer NOT NULL DEFAULT 0,
	last_fetch timestamptz NOT NULL,
	PRIMARY KEY(page_id)
);

CREATE TABLE content (
	content_id uuid NOT NULL,
	page_id uuid NOT NULL REFERENCES page,
	fetched_at timestamptz NOT NULL,
	title text,
	body text NOT NULL,
	content text NOT NULL,
	PRIMARY KEY(content_id)
);

# --- !Downs

DROP TABLE content;
DROP TABLE page;
