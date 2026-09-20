package com.brunoestrai.desafio_votacao.exception.sessao;

import com.brunoestrai.desafio_votacao.exception.RecursoNaoEncontradoException;

public class NenhumaSessaoAbertaException extends RecursoNaoEncontradoException {

    public NenhumaSessaoAbertaException() {

        super("Nenhuma sessão de votação aberta no momento");
    }
}
