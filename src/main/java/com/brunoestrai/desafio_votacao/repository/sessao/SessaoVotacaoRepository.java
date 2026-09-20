package com.brunoestrai.desafio_votacao.repository.sessao;

import com.brunoestrai.desafio_votacao.domain.sessao.SessaoAberta;
import com.brunoestrai.desafio_votacao.domain.sessao.SessaoVotacao;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class SessaoVotacaoRepository {

    private static final String SQL_INSERIR = """
            INSERT INTO sessao_votacao
                (pauta_id, fim)
            VALUES
                (:idPauta, CURRENT_TIMESTAMP + make_interval(mins => :tempoLimite))
            RETURNING
                id, pauta_id, inicio, fim,
                CURRENT_TIMESTAMP >= inicio AND CURRENT_TIMESTAMP < fim AS aberta
            """;
    private static final String SQL_BUSCAR_ABERTA = """
            SELECT
                s.id, s.pauta_id, p.titulo, p.descricao, s.inicio, s.fim
            FROM
                sessao_votacao s
                JOIN pauta p ON p.id = s.pauta_id
            WHERE
                CURRENT_TIMESTAMP >= s.inicio
                AND CURRENT_TIMESTAMP < s.fim
            ORDER BY
                s.inicio DESC
            LIMIT 1
            """;

    private final JdbcClient jdbcClient;


    private static final RowMapper<SessaoVotacao> MAPEADOR_SESSAO = (rs, numeroLinha) ->
            SessaoVotacao.builder()
                    .idSessao(rs.getLong("id"))
                    .idPauta(rs.getLong("pauta_id"))
                    .inicio(rs.getTimestamp("inicio").toLocalDateTime())
                    .fim(rs.getTimestamp("fim").toLocalDateTime())
                    .aberta(rs.getBoolean("aberta"))
                    .build();

    private static final RowMapper<SessaoAberta> MAPEADOR_SESSAO_ABERTA = (rs, numeroLinha) ->
            new SessaoAberta(
                    rs.getLong("id"),
                    rs.getLong("pauta_id"),
                    rs.getString("titulo"),
                    rs.getString("descricao"),
                    rs.getTimestamp("inicio").toLocalDateTime(),
                    rs.getTimestamp("fim").toLocalDateTime());

    public SessaoVotacaoRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public SessaoVotacao inserir(Long idPauta, Integer tempoLimite) {

        return jdbcClient.sql(SQL_INSERIR)
                .param("idPauta", idPauta)
                .param("tempoLimite", tempoLimite)
                .query(MAPEADOR_SESSAO)
                .single();
    }

    /**
     * Sessão em andamento segundo o relógio do banco. Havendo mais de uma, devolve a que abriu por último.
     */
    public Optional<SessaoAberta> buscarAberta() {

        return jdbcClient.sql(SQL_BUSCAR_ABERTA)
                .query(MAPEADOR_SESSAO_ABERTA)
                .optional();
    }
}
