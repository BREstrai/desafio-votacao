package com.brunoestrai.desafio_votacao.domain.voto;

import java.time.LocalDateTime;

/**
 * Voto registrado. O CPF não é devolvido na resposta: identifica o cooperado e não precisa sair da aplicação.
 */
public record Voto(Long idVoto, Long idPauta, boolean aprovado, LocalDateTime dhRegistro) {
}
