package com.brunoestrai.desafio_votacao.exception;

/**
 * Recurso solicitado não existe. Tratada pelo {@code HandlerExceptions} como 404, com a mensagem exposta ao cliente.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {

        super(mensagem);
    }
}
