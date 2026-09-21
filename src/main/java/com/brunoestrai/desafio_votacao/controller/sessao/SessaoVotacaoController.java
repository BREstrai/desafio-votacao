package com.brunoestrai.desafio_votacao.controller.sessao;

import com.brunoestrai.desafio_votacao.domain.sessao.AberturaSessao;
import com.brunoestrai.desafio_votacao.domain.sessao.SessaoAberta;
import com.brunoestrai.desafio_votacao.domain.sessao.SessaoVotacao;
import com.brunoestrai.desafio_votacao.service.SessaoVotacaoCacheService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sessao")
public class SessaoVotacaoController {

    private final SessaoVotacaoCacheService sessaoVotacaoCacheService;

    @PostMapping
    public ResponseEntity<SessaoVotacao> abrir(@Valid @RequestBody AberturaSessao aberturaSessao) {

        SessaoVotacao sessaoVotacao = sessaoVotacaoCacheService.abrirSessao(aberturaSessao);

        return ResponseEntity.status(CREATED).body(sessaoVotacao);
    }

    @GetMapping
    public ResponseEntity<SessaoAberta> findAberta() {

        return ResponseEntity.ok(sessaoVotacaoCacheService.buscarSessaoAberta());
    }
}
