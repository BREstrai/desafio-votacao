package com.brunoestrai.desafio_votacao.controller.pauta;

import com.brunoestrai.desafio_votacao.domain.pauta.Pauta;
import com.brunoestrai.desafio_votacao.domain.pauta.NovaPauta;
import com.brunoestrai.desafio_votacao.service.PautaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pauta")
public class PautaController {

    private final PautaService pautaService;

    @PostMapping
    public ResponseEntity<Pauta> create(@Valid @RequestBody NovaPauta novaPauta) {

        Pauta pauta = pautaService.criarPauta(novaPauta);

        return ResponseEntity.status(CREATED).body(pauta);
    }

    @GetMapping("/{idPauta}")
    public ResponseEntity<Pauta> findById(@PathVariable Long idPauta) {

        return ResponseEntity.ok(pautaService.buscarPautaPorId(idPauta));
    }

    @GetMapping
    public List<Pauta> findAll() {

        return pautaService.buscarTodasPautas();
    }
}
