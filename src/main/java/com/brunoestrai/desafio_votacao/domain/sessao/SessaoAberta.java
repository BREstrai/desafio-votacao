package com.brunoestrai.desafio_votacao.domain.sessao;

import java.time.LocalDateTime;

/**
 * Sessão em andamento, com os dados da pauta, para a tela de votação do aplicativo.
 */
public record SessaoAberta(Long idSessao, Long idPauta, String titulo, String descricao, LocalDateTime inicio,
                           LocalDateTime fim) {
}
