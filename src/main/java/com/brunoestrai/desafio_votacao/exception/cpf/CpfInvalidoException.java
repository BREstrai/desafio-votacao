package com.brunoestrai.desafio_votacao.exception.cpf;

import com.brunoestrai.desafio_votacao.exception.RecursoNaoEncontradoException;

/**
 * CPF reprovado na validação dos dígitos verificadores. Responde 404, conforme o enunciado do desafio.
 */
public class CpfInvalidoException extends RecursoNaoEncontradoException {

    public CpfInvalidoException() {

        super("CPF inválido");
    }
}
