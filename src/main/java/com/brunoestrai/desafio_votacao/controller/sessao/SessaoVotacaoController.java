package com.brunoestrai.desafio_votacao.controller.sessao;

import com.brunoestrai.desafio_votacao.domain.sessao.AberturaSessao;
import com.brunoestrai.desafio_votacao.domain.sessao.SessaoAberta;
import com.brunoestrai.desafio_votacao.domain.sessao.SessaoVotacao;
import com.brunoestrai.desafio_votacao.service.SessaoVotacaoCacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "Sessão de votação", description = "Abertura e consulta da sessão em andamento")
@RestController
@RequiredArgsConstructor
@RequestMapping("/sessao")
public class SessaoVotacaoController {

    private final SessaoVotacaoCacheService sessaoVotacaoCacheService;

    @Operation(
            summary = "Abrir sessão",
            description = "Abre a sessão de votação de uma pauta pelo tempo informado, ou por 1 minuto quando o tempo"
                    + " limite não é enviado. Apenas uma sessão fica aberta por vez"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sessão aberta com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada"),
            @ApiResponse(responseCode = "409", description = "Já existe uma sessão em andamento")
    })
    @PostMapping
    public ResponseEntity<SessaoVotacao> abrir(@Valid @RequestBody AberturaSessao aberturaSessao) {

        SessaoVotacao sessaoVotacao = sessaoVotacaoCacheService.abrirSessao(aberturaSessao);

        return ResponseEntity.status(CREATED).body(sessaoVotacao);
    }

    @Operation(
            summary = "Buscar sessão aberta",
            description = "Devolve a sessão em andamento, com os dados da pauta em votação"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sessão em andamento encontrada"),
            @ApiResponse(responseCode = "404", description = "Nenhuma sessão de votação aberta")
    })
    @GetMapping
    public ResponseEntity<SessaoAberta> findAberta() {

        return ResponseEntity.ok(sessaoVotacaoCacheService.buscarSessaoAberta());
    }
}
