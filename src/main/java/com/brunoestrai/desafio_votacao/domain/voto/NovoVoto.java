package com.brunoestrai.desafio_votacao.domain.voto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NovoVoto(

        @NotNull(message = "O id da pauta é obrigatório")
        Long idPauta,

        @NotBlank(message = "O CPF é obrigatório")
        String cpf,

        @NotNull(message = "O voto é obrigatório")
        Boolean aprovado) {
}
