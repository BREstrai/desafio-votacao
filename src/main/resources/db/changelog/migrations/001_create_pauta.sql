--liquibase formatted sql

--changeset brunoestrai:001 create-table-pauta
CREATE TABLE pauta
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY,
    titulo    VARCHAR(255) NOT NULL,
    descricao TEXT,
    criada_em TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE pauta
    ADD CONSTRAINT pk_pauta PRIMARY KEY (id);

