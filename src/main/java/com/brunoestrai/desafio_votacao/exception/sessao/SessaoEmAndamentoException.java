package com.brunoestrai.desafio_votacao.exception.sessao;

import com.brunoestrai.desafio_votacao.exception.ConflitoException;

/**
 * Já existe uma votação em andamento em outra pauta. Apenas uma sessão fica aberta por vez.
 */
public class SessaoEmAndamentoException extends ConflitoException {

    public SessaoEmAndamentoException(Throwable causa) {

        super("Já existe uma sessão de votação em andamento", causa);
    }
}
