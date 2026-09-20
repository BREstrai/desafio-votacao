package com.brunoestrai.desafio_votacao.domain.sessao;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessaoVotacao {

    private Long idSessao;

    private Long idPauta;

    private LocalDateTime inicio;

    private LocalDateTime fim;

    private boolean aberta;
}
