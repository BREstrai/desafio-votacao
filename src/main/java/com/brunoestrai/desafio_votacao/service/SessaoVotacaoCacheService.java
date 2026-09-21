package com.brunoestrai.desafio_votacao.service;

import com.brunoestrai.desafio_votacao.cache.SessaoVotacaoCache;
import com.brunoestrai.desafio_votacao.domain.sessao.AberturaSessao;
import com.brunoestrai.desafio_votacao.domain.sessao.SessaoAberta;
import com.brunoestrai.desafio_votacao.domain.sessao.SessaoVotacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Abertura e consulta da sessão em andamento, com cache em memória.
 * <p>
 * Não é transacional de propósito: o acesso ao cache acontece fora da transação, para não manter uma conexão do pool
 * presa enquanto a aplicação trabalha em memória.
 */
@Service
@RequiredArgsConstructor
public class SessaoVotacaoCacheService {

    private final SessaoVotacaoService sessaoVotacaoService;

    private final SessaoVotacaoCache sessaoVotacaoCache;

    /**
     * Abre a sessão e já deixa a consulta pronta em cache, para que a primeira chamada do aplicativo não precise ir ao
     * banco.
     */
    public SessaoVotacao abrirSessao(AberturaSessao aberturaSessao) {

        SessaoVotacao sessaoVotacao = sessaoVotacaoService.abrirSessao(aberturaSessao);

        buscarNoBancoEGuardar();

        return sessaoVotacao;
    }

    public SessaoAberta buscarSessaoAberta() {

        return sessaoVotacaoCache.buscarAberta()
                .orElseGet(this::buscarNoBancoEGuardar);
    }

    private SessaoAberta buscarNoBancoEGuardar() {

        SessaoAberta sessaoAberta = sessaoVotacaoService.buscarSessaoAberta();

        sessaoVotacaoCache.guardar(sessaoAberta);

        return sessaoAberta;
    }
}
