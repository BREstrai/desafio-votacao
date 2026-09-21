package com.brunoestrai.desafio_votacao.configuracao;

import com.brunoestrai.desafio_votacao.cache.ExpiracaoSessaoAberta;
import com.brunoestrai.desafio_votacao.domain.sessao.SessaoAberta;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    private static final long TAMANHO_MAXIMO = 1;

    @Bean
    public Cache<String, SessaoAberta> sessaoAbertaCache() {

        return Caffeine.newBuilder()
                .maximumSize(TAMANHO_MAXIMO)
                .expireAfter(new ExpiracaoSessaoAberta())
                .build();
    }
}
