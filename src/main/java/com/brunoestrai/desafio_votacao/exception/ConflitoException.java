package com.brunoestrai.desafio_votacao.exception;

/**
 * Operação conflita com o estado atual do recurso. Tratada pelo {@code HandlerExceptions} como 409, com a mensagem
 * exposta ao cliente.
 */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {

        this(mensagem, null);
    }

    public ConflitoException(String mensagem, Throwable causa) {

        super(mensagem, causa);
    }
}
