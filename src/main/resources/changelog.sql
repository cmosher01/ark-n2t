--liquibase formatted sql

--changeset liquibase:1
CREATE TABLE Ark (
    ark VARCHAR(256),
    url VARCHAR(2048)
);
