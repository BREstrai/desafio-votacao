package com.brunoestrai.desafio_votacao.cache;

import com.brunoestrai.desafio_votacao.domain.sessao.SessaoAberta;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SessaoVotacaoCache {

    private static final String CHAVE_SESSAO_ABERTA = "sessao:aberta";

    private final Cache<String, SessaoAberta> sessaoAbertaCache;

    public Optional<SessaoAberta> buscarAberta() {

        return Optional.ofNullable(sessaoAbertaCache.getIfPresent(CHAVE_SESSAO_ABERTA));
    }

    public void guardar(SessaoAberta sessaoAberta) {

        sessaoAbertaCache.put(CHAVE_SESSAO_ABERTA, sessaoAberta);
    }
}
