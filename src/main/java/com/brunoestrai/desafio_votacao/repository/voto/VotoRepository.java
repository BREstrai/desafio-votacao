package com.brunoestrai.desafio_votacao.repository.voto;

import com.brunoestrai.desafio_votacao.domain.voto.DiagnosticoVotacao;
import com.brunoestrai.desafio_votacao.domain.voto.ResultadoVotacao;
import com.brunoestrai.desafio_votacao.domain.voto.Voto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class VotoRepository {

    private static final short VOTO_APROVADO = 1;
    private static final short VOTO_REJEITADO = 0;

    private static final String SQL_REGISTRAR = """
            INSERT INTO voto
                (pauta_id, cpf, voto)
            SELECT
                :idPauta, :cpf, :voto
            WHERE EXISTS (
                SELECT
                    1
                FROM
                    sessao_votacao s
                WHERE
                    s.pauta_id = :idPauta
                    AND CURRENT_TIMESTAMP >= s.inicio
                    AND CURRENT_TIMESTAMP < s.fim
            )
            RETURNING
                id, pauta_id, voto, registrado_em
            """;

    private static final String SQL_DIAGNOSTICAR = """
            SELECT
                EXISTS (SELECT 1 FROM pauta WHERE id = :idPauta)                AS pauta_existe,
                EXISTS (SELECT 1 FROM sessao_votacao WHERE pauta_id = :idPauta) AS sessao_existe
            """;

    private static final String SQL_APURAR = """
            SELECT
                COUNT(*) FILTER (WHERE voto = 1) AS aprovado,
                COUNT(*) FILTER (WHERE voto = 0) AS rejeitado
            FROM
                voto
            WHERE
                pauta_id = :idPauta
            """;

    private final JdbcClient jdbcClient;

    private static final RowMapper<Voto> MAPEADOR_VOTO = (rs, numeroLinha) ->
            new Voto(
                    rs.getLong("id"),
                    rs.getLong("pauta_id"),
                    rs.getShort("voto") == VOTO_APROVADO,
                    rs.getTimestamp("registrado_em").toLocalDateTime());

    private static final RowMapper<ResultadoVotacao> MAPEADOR_RESULTADO = (rs, numeroLinha) ->
            new ResultadoVotacao(rs.getLong("aprovado"), rs.getLong("rejeitado"));

    private static final RowMapper<DiagnosticoVotacao> MAPEADOR_DIAGNOSTICO = (rs, numeroLinha) ->
            new DiagnosticoVotacao(rs.getBoolean("pauta_existe"), rs.getBoolean("sessao_existe"));

    public VotoRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    /**
     * Registra o voto em um único comando: a sessão aberta é conferida pelo relógio do banco dentro do próprio insert.
     * Retorna vazio quando nada foi inserido, ou seja, quando não existe sessão em andamento para a pauta.
     */
    public Optional<Voto> registrar(Long idPauta, String cpf, boolean aprovado) {

        return jdbcClient.sql(SQL_REGISTRAR)
                .param("idPauta", idPauta)
                .param("cpf", cpf)
                .param("voto", aprovado ? VOTO_APROVADO : VOTO_REJEITADO)
                .query(MAPEADOR_VOTO)
                .optional();
    }

    /**
     * Contagem de votos da pauta. O índice de {@code uk_voto_pauta_cpf} atende esta consulta.
     */
    public ResultadoVotacao apurar(Long idPauta) {

        return jdbcClient.sql(SQL_APURAR)
                .param("idPauta", idPauta)
                .query(MAPEADOR_RESULTADO)
                .single();
    }

    /**
     * Consulta usada somente quando o voto não é registrado, ou quando a apuração não encontra votos, para separar
     * pauta inexistente dos demais casos.
     */
    public DiagnosticoVotacao diagnosticar(Long idPauta) {

        return jdbcClient.sql(SQL_DIAGNOSTICAR)
                .param("idPauta", idPauta)
                .query(MAPEADOR_DIAGNOSTICO)
                .single();
    }
}
