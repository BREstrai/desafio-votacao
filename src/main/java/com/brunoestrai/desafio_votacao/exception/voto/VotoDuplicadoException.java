package com.brunoestrai.desafio_votacao.exception.voto;

import com.brunoestrai.desafio_votacao.exception.ConflitoException;

public class VotoDuplicadoException extends ConflitoException {

    public VotoDuplicadoException(Long idPauta, Throwable causa) {

        super("O cooperado informado já votou na pauta %d".formatted(idPauta), causa);
    }
}
