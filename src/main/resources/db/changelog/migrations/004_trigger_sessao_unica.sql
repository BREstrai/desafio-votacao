--liquibase formatted sql

--changeset brunoestrai:004 sessao-unica-em-andamento splitStatements:false
-- Garante que exista no maximo uma sessao de votacao em andamento em toda a aplicacao.
-- O lock de transacao serializa as aberturas concorrentes: sem ele, duas transacoes simultaneas
-- nao enxergariam uma a linha da outra e ambas seriam aceitas.
CREATE OR REPLACE FUNCTION valida_sessao_unica() RETURNS TRIGGER AS
$$
BEGIN
    PERFORM pg_advisory_xact_lock(hashtext('sessao_votacao'));

    IF EXISTS (SELECT 1
               FROM sessao_votacao
               WHERE CURRENT_TIMESTAMP >= inicio
                 AND CURRENT_TIMESTAMP < fim) THEN
        RAISE EXCEPTION 'Ja existe uma sessao de votacao em andamento'
            USING ERRCODE = '23P01';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

--changeset brunoestrai:005 trigger-sessao-unica
CREATE TRIGGER tg_sessao_unica
    BEFORE INSERT
    ON sessao_votacao
    FOR EACH ROW
EXECUTE FUNCTION valida_sessao_unica();
