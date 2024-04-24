--liquibase formatted sql

--changeset liquibase:1
CREATE TABLE Ark (
    ark VARCHAR(256) PRIMARY KEY,
    url VARCHAR(2048) NOT NULL
);

CREATE INDEX idx_Ark_url ON Ark(url);
