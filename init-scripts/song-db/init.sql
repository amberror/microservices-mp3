CREATE TABLE IF NOT EXISTS song_entity (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    artist VARCHAR(100) NOT NULL,
    album VARCHAR(100) NOT NULL,
    duration VARCHAR(7) NOT NULL,
    year INT NOT NULL
);