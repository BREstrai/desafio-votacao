package com.brunoestrai.desafio_votacao.controller.voto;

import com.brunoestrai.desafio_votacao.domain.voto.NovoVoto;
import com.brunoestrai.desafio_votacao.domain.voto.Voto;
import com.brunoestrai.desafio_votacao.service.RegistroVotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "Voto", description = "Registro do voto do cooperado")
@RestController
@RequiredArgsConstructor
@RequestMapping("/voto")
public class VotoController {

    private final RegistroVotoService registroVotoService;

    @Operation(
            summary = "Registrar voto",
            description = "Registra o voto do cooperado na pauta em votação. Cada CPF vota uma única vez por pauta"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Cooperado não habilitado a votar"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada ou CPF inválido"),
            @ApiResponse(responseCode = "409", description = "Votação não está aberta ou o CPF já votou na pauta")
    })
    @PostMapping
    public ResponseEntity<Voto> create(@Valid @RequestBody NovoVoto novoVoto) {

        Voto voto = registroVotoService.registrarVoto(novoVoto);

        return ResponseEntity.status(CREATED).body(voto);
    }
}
