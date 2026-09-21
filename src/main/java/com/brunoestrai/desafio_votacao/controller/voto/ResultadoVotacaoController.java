package com.brunoestrai.desafio_votacao.controller.voto;

import com.brunoestrai.desafio_votacao.domain.voto.ResultadoVotacao;
import com.brunoestrai.desafio_votacao.service.VotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pauta/{idPauta}/resultado")
public class ResultadoVotacaoController {

    private final VotoService votoService;

    @GetMapping
    public ResponseEntity<ResultadoVotacao> findByPauta(@PathVariable Long idPauta) {

        return ResponseEntity.ok(votoService.apurarVotacao(idPauta));
    }
}
