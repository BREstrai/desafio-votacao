package com.brunoestrai.desafio_votacao.domain.sessao;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Objects;

public record AberturaSessao(

        @NotNull(message = "O id da pauta é obrigatório")
        Long idPauta,

        @Positive(message = "O tempo limite da sessão deve ser maior que zero")
        Integer tempoLimite) {

    @Override
    public Integer tempoLimite() {

        return Objects.requireNonNullElse(tempoLimite, 1);
    }
}
