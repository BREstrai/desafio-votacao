package com.brunoestrai.desafio_votacao.repository.pauta;

import com.brunoestrai.desafio_votacao.domain.pauta.NovaPauta;
import com.brunoestrai.desafio_votacao.domain.pauta.Pauta;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class PautaRepository {

    private static final String SQL_INSERIR = """
            INSERT INTO pauta
                (titulo, descricao)
            VALUES
                (:titulo, :descricao)
            RETURNING
                id, titulo, descricao, criada_em
            """;
    private static final String SQL_BUSCAR_POR_ID = """
            SELECT
                id, titulo, descricao, criada_em
            FROM
                pauta
            WHERE
                id = :id
            """;
    private static final String SQL_LISTAR_TODAS = """
                SELECT
                    id, titulo, descricao, criada_em
                FROM
                    pauta
                ORDER BY
                    id
            """;

    private final JdbcClient jdbcClient;

    private static final RowMapper<Pauta> MAPEADOR_PAUTA = (rs, numeroLinha) ->
            Pauta.builder()
                    .idPauta(rs.getLong("id"))
                    .titulo(rs.getString("titulo"))
                    .descricao(rs.getString("descricao"))
                    .dhCriacao(rs.getTimestamp("criada_em").toLocalDateTime())
                    .build();

    public PautaRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Pauta inserir(NovaPauta novaPauta) {

        return jdbcClient.sql(SQL_INSERIR)
                .param("titulo", novaPauta.dsTitulo())
                .param("descricao", novaPauta.dsPauta())
                .query(MAPEADOR_PAUTA)
                .single();
    }

    public Optional<Pauta> buscarPorId(Long idPauta) {

        return jdbcClient.sql(SQL_BUSCAR_POR_ID)
                .param("id", idPauta)
                .query(MAPEADOR_PAUTA)
                .optional();
    }

    public List<Pauta> listarTodas() {

        return jdbcClient.sql(SQL_LISTAR_TODAS).query(MAPEADOR_PAUTA).list();
    }
}
