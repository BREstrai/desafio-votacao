package com.brunoestrai.desafio_votacao.exception;

/**
 * Dados de entrada inválidos. Tratada pelo {@code HandlerExceptions} como 400, com a mensagem exposta ao cliente.
 */
public class ValidacaoException extends RuntimeException {

    public ValidacaoException(String mensagem) {

        super(mensagem);
    }
}
