package com.brunoestrai.desafio_votacao.exception.cpf;

import com.brunoestrai.desafio_votacao.exception.OperacaoNaoPermitidaException;

/**
 * Cooperado existe, mas o serviço externo respondeu {@code UNABLE_TO_VOTE}.
 */
public class CooperadoNaoHabilitadoException extends OperacaoNaoPermitidaException {

    public CooperadoNaoHabilitadoException() {

        super("O cooperado informado não está habilitado a votar");
    }
}
