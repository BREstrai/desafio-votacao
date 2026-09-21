package com.brunoestrai.desafio_votacao.service;

import com.brunoestrai.desafio_votacao.cache.SessaoVotacaoCache;
import com.brunoestrai.desafio_votacao.domain.sessao.AberturaSessao;
import com.brunoestrai.desafio_votacao.domain.sessao.SessaoAberta;
import com.brunoestrai.desafio_votacao.domain.sessao.SessaoVotacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessaoVotacaoCacheServiceTest {

    private static final Long ID_PAUTA = 1L;

    private static final SessaoAberta SESSAO_ABERTA = new SessaoAberta(1L, ID_PAUTA, "Pauta", "Descrição",
            LocalDateTime.now(), LocalDateTime.now().plusMinutes(1));

    @Mock
    private SessaoVotacaoService sessaoVotacaoService;

    @Mock
    private SessaoVotacaoCache sessaoVotacaoCache;

    @InjectMocks
    private SessaoVotacaoCacheService sessaoVotacaoCacheService;

    @Test
    @DisplayName("Responde pelo cache sem consultar o banco")
    void deveResponderPeloCache() {

        when(sessaoVotacaoCache.buscarAberta()).thenReturn(Optional.of(SESSAO_ABERTA));

        assertThat(sessaoVotacaoCacheService.buscarSessaoAberta()).isEqualTo(SESSAO_ABERTA);

        verifyNoInteractions(sessaoVotacaoService);
        verify(sessaoVotacaoCache, never()).guardar(SESSAO_ABERTA);
    }

    @Test
    @DisplayName("Consulta o banco e guarda no cache quando o cache está vazio")
    void deveConsultarBancoQuandoCacheVazio() {

        when(sessaoVotacaoCache.buscarAberta()).thenReturn(Optional.empty());
        when(sessaoVotacaoService.buscarSessaoAberta()).thenReturn(SESSAO_ABERTA);

        assertThat(sessaoVotacaoCacheService.buscarSessaoAberta()).isEqualTo(SESSAO_ABERTA);

        verify(sessaoVotacaoCache).guardar(SESSAO_ABERTA);
    }

    @Test
    @DisplayName("Abre a sessão e já deixa a consulta em cache")
    void deveAbrirSessaoEAquecerOCache() {

        AberturaSessao abertura = new AberturaSessao(ID_PAUTA, 5);
        SessaoVotacao sessaoVotacao = SessaoVotacao.builder().idSessao(1L).idPauta(ID_PAUTA).build();

        when(sessaoVotacaoService.abrirSessao(abertura)).thenReturn(sessaoVotacao);
        when(sessaoVotacaoService.buscarSessaoAberta()).thenReturn(SESSAO_ABERTA);

        assertThat(sessaoVotacaoCacheService.abrirSessao(abertura)).isEqualTo(sessaoVotacao);

        verify(sessaoVotacaoCache).guardar(SESSAO_ABERTA);
    }
}
