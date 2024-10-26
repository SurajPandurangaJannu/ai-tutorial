-- Create a table named "users"
CREATE TABLE IF NOT EXISTS movies (
    id SERIAL PRIMARY KEY,
    rank BIGINT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    genre TEXT,
    rating DOUBLE PRECISION,
    year INTEGER
);

CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS vector_store (
	id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
	content text,
	metadata json,
	embedding vector(1536) -- 1536 is the default embedding dimension
);

CREATE INDEX ON vector_store USING HNSW (embedding vector_cosine_ops);

-- Load data from the CSV file into the "users" table
COPY movies(rank, title, description, genre, rating, year)
FROM '/data/top-100-imdb-movies.csv'
DELIMITER ','
CSV HEADER;