package com.brunoestrai.desafio_votacao.controller.voto;

import com.brunoestrai.desafio_votacao.domain.voto.ResultadoVotacao;
import com.brunoestrai.desafio_votacao.service.VotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Resultado", description = "Apuração dos votos da pauta")
@RestController
@RequiredArgsConstructor
@RequestMapping("/pauta/{idPauta}/resultado")
public class ResultadoVotacaoController {

    private final VotoService votoService;

    @Operation(
            summary = "Apurar votação",
            description = "Contabiliza os votos aprovados e rejeitados da pauta"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Votação apurada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    })
    @GetMapping
    public ResponseEntity<ResultadoVotacao> findByPauta(@PathVariable Long idPauta) {

        return ResponseEntity.ok(votoService.apurarVotacao(idPauta));
    }
}
