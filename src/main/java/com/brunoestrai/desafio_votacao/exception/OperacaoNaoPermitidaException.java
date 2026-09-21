package com.brunoestrai.desafio_votacao.exception;

/**
 * Operação não permitida para o solicitante. Tratada pelo {@code HandlerExceptions} como 403, com a mensagem exposta
 * ao cliente.
 */
public class OperacaoNaoPermitidaException extends RuntimeException {

    public OperacaoNaoPermitidaException(String mensagem) {

        super(mensagem);
    }
}
