package com.brunoestrai.desafio_votacao.controller.pauta;

import com.brunoestrai.desafio_votacao.domain.pauta.Pauta;
import com.brunoestrai.desafio_votacao.domain.pauta.NovaPauta;
import com.brunoestrai.desafio_votacao.service.PautaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "Pauta", description = "Cadastro das pautas submetidas a votação")
@RestController
@RequiredArgsConstructor
@RequestMapping("/pauta")
public class PautaController {

    private final PautaService pautaService;

    @Operation(
            summary = "Criar pauta",
            description = "Cria uma nova pauta para votação"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pauta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<Pauta> create(@Valid @RequestBody NovaPauta novaPauta) {

        Pauta pauta = pautaService.criarPauta(novaPauta);

        return ResponseEntity.status(CREATED).body(pauta);
    }

    @Operation(
            summary = "Buscar pauta",
            description = "Busca uma pauta pelo identificador"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pauta encontrada"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    })
    @GetMapping("/{idPauta}")
    public ResponseEntity<Pauta> findById(@PathVariable Long idPauta) {

        return ResponseEntity.ok(pautaService.buscarPautaPorId(idPauta));
    }

    @Operation(
            summary = "Listar pautas",
            description = "Lista todas as pautas cadastradas"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pautas listadas com sucesso")
    })
    @GetMapping
    public List<Pauta> findAll() {

        return pautaService.buscarTodasPautas();
    }
}
