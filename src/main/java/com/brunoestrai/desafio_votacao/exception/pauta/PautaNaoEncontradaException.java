package com.brunoestrai.desafio_votacao.exception.pauta;

import com.brunoestrai.desafio_votacao.exception.RecursoNaoEncontradoException;

public class PautaNaoEncontradaException extends RecursoNaoEncontradoException {

    public PautaNaoEncontradaException(Long idPauta) {

        super("Pauta %d não encontrada".formatted(idPauta));
    }
}
