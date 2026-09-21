package com.brunoestrai.desafio_votacao.exception.voto;

import com.brunoestrai.desafio_votacao.exception.ConflitoException;

public class SessaoNaoAbertaException extends ConflitoException {

    public SessaoNaoAbertaException(Long idPauta) {

        super("A pauta %d não está com a votação aberta".formatted(idPauta));
    }
}
