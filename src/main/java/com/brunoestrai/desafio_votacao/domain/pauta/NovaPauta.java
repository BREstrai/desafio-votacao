package com.brunoestrai.desafio_votacao.domain.pauta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NovaPauta(

        @NotBlank(message = "O título da pauta é obrigatório")
        @Size(max = 255, message = "O título da pauta deve ter no máximo 255 caracteres")
        String dsTitulo,

        String dsPauta) {
}
