package com.brunoestrai.desafio_votacao.cache;

import com.brunoestrai.desafio_votacao.domain.sessao.SessaoAberta;
import com.github.benmanes.caffeine.cache.Expiry;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.time.LocalDateTime;

public class ExpiracaoSessaoAberta implements Expiry<String, SessaoAberta> {

    @Override
    public long expireAfterCreate(@NonNull String chave, @NonNull SessaoAberta sessaoAberta, long tempoAtual) {

        return nanosAteOFim(sessaoAberta);
    }

    @Override
    public long expireAfterUpdate(
            @NonNull String chave, @NonNull SessaoAberta sessaoAberta, long tempoAtual, long duracaoAtual) {

        return nanosAteOFim(sessaoAberta);
    }

    @Override
    public long expireAfterRead(
            @NonNull String chave, @NonNull SessaoAberta sessaoAberta, long tempoAtual, long duracaoAtual) {

        return duracaoAtual;
    }

    private long nanosAteOFim(SessaoAberta sessaoAberta) {

        return Math.max(0, Duration.between(LocalDateTime.now(), sessaoAberta.fim()).toNanos());
    }
}
