CREATE TABLE IF NOT EXISTS storage_entity (
    id BIGINT PRIMARY KEY,
    storage_type VARCHAR(50) NOT NULL,
    bucket VARCHAR(255) NOT NULL,
    path VARCHAR(255) NOT NULL,
    CONSTRAINT storage_type_check CHECK (storage_type IN ('PERMANENT', 'STAGING')),
    CONSTRAINT unique_bucket_path UNIQUE (bucket, path)
);

INSERT INTO storage_entity (id, storage_type, bucket, path)
VALUES (1, 'PERMANENT', 'resource-permanent-bucket-mp3-microservices-stack', '/permanent');

INSERT INTO storage_entity (id, storage_type, bucket, path)
VALUES (2, 'STAGING', 'resource-staging-bucket-mp3-microservices-stack', '/staging');