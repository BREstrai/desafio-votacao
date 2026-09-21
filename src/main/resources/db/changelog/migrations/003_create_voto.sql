--liquibase formatted sql

--changeset brunoestrai:003 create-table-voto
CREATE TABLE voto
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY,
    pauta_id      BIGINT      NOT NULL,
    cpf           CHAR(11)    NOT NULL,
    voto          smallint    NOT NULL,
    registrado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE voto
    ADD CONSTRAINT pk_voto PRIMARY KEY (id);

ALTER TABLE voto
    ADD CONSTRAINT fk_voto_pauta FOREIGN KEY (pauta_id) REFERENCES pauta (id);

ALTER TABLE voto
    ADD CONSTRAINT uk_voto_pauta_cpf UNIQUE (pauta_id, cpf);

ALTER TABLE voto
    ADD CONSTRAINT ck_voto_cpf CHECK (cpf ~ '^[0-9]{11}$');

ALTER TABLE voto
    ADD CONSTRAINT ck_voto_valor CHECK (voto IN (0, 1));
