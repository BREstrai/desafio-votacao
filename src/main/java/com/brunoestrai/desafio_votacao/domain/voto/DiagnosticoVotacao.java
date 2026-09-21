package com.brunoestrai.desafio_votacao.domain.voto;

/**
 * Resultado da consulta feita apenas quando o voto não é registrado, para separar pauta inexistente de sessão
 * indisponível.
 */
public record DiagnosticoVotacao(boolean pautaExiste, boolean sessaoExiste) {
}
