--liquibase formatted sql

--changeset brunoestrai:002 create-table-sessao-votacao
CREATE TABLE sessao_votacao
(
    id       BIGINT GENERATED ALWAYS AS IDENTITY,
    pauta_id BIGINT      NOT NULL,
    inicio   TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fim      TIMESTAMPTZ NOT NULL
);

ALTER TABLE sessao_votacao
    ADD CONSTRAINT pk_sessao_votacao PRIMARY KEY (id);

ALTER TABLE sessao_votacao
    ADD CONSTRAINT fk_sessao_votacao_pauta FOREIGN KEY (pauta_id) REFERENCES pauta (id);

ALTER TABLE sessao_votacao
    ADD CONSTRAINT uk_sessao_votacao_pauta UNIQUE (pauta_id);

ALTER TABLE sessao_votacao
    ADD CONSTRAINT ck_sessao_votacao_periodo CHECK (fim > inicio);
