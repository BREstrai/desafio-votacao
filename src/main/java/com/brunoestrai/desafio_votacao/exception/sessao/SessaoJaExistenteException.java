package com.brunoestrai.desafio_votacao.exception.sessao;

import com.brunoestrai.desafio_votacao.exception.ConflitoException;

public class SessaoJaExistenteException extends ConflitoException {

    public SessaoJaExistenteException(Long idPauta, Throwable causa) {

        super("Pauta %d já possui sessão de votação".formatted(idPauta), causa);
    }
}
