package com.brunoestrai.desafio_votacao.domain.pauta;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pauta {

    private Long idPauta;

    private String titulo;

    private String descricao;

    private LocalDateTime dhCriacao;
}
