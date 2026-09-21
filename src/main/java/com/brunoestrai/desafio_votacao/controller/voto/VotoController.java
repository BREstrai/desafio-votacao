package com.brunoestrai.desafio_votacao.controller.voto;

import com.brunoestrai.desafio_votacao.domain.voto.NovoVoto;
import com.brunoestrai.desafio_votacao.domain.voto.Voto;
import com.brunoestrai.desafio_votacao.service.RegistroVotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/voto")
public class VotoController {

    private final RegistroVotoService registroVotoService;

    @PostMapping
    public ResponseEntity<Voto> create(@Valid @RequestBody NovoVoto novoVoto) {

        Voto voto = registroVotoService.registrarVoto(novoVoto);

        return ResponseEntity.status(CREATED).body(voto);
    }
}
