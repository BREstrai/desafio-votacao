package com.brunoestrai.desafio_votacao.service;

import com.brunoestrai.desafio_votacao.domain.pauta.Pauta;
import com.brunoestrai.desafio_votacao.domain.sessao.AberturaSessao;
import com.brunoestrai.desafio_votacao.domain.sessao.SessaoAberta;
import com.brunoestrai.desafio_votacao.domain.sessao.SessaoVotacao;
import com.brunoestrai.desafio_votacao.exception.pauta.PautaNaoEncontradaException;
import com.brunoestrai.desafio_votacao.exception.sessao.NenhumaSessaoAbertaException;
import com.brunoestrai.desafio_votacao.exception.sessao.SessaoEmAndamentoException;
import com.brunoestrai.desafio_votacao.exception.sessao.SessaoJaExistenteException;
import com.brunoestrai.desafio_votacao.repository.sessao.SessaoVotacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessaoVotacaoServiceTest {

    private static final Long ID_PAUTA = 1L;

    @Mock
    private PautaService pautaService;

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @InjectMocks
    private SessaoVotacaoService sessaoVotacaoService;

    @Test
    @DisplayName("Abre a sessão com o tempo limite informado")
    void deveAbrirSessaoComTempoInformado() {

        SessaoVotacao esperada = SessaoVotacao.builder().idSessao(1L).idPauta(ID_PAUTA).build();

        when(sessaoVotacaoRepository.inserir(ID_PAUTA, 5)).thenReturn(esperada);

        assertThat(sessaoVotacaoService.abrirSessao(new AberturaSessao(ID_PAUTA, 5))).isEqualTo(esperada);
    }

    @Test
    @DisplayName("Abre a sessão com 1 minuto quando o tempo limite não é informado")
    void deveAbrirSessaoComTempoPadrao() {

        SessaoVotacao esperada = SessaoVotacao.builder().idSessao(1L).idPauta(ID_PAUTA).build();

        when(sessaoVotacaoRepository.inserir(ID_PAUTA, 1)).thenReturn(esperada);

        assertThat(sessaoVotacaoService.abrirSessao(new AberturaSessao(ID_PAUTA, null))).isEqualTo(esperada);
    }

    @Test
    @DisplayName("Não abre a sessão quando a pauta não existe")
    void deveRecusarPautaInexistente() {

        when(pautaService.buscarPautaPorId(ID_PAUTA)).thenThrow(new PautaNaoEncontradaException(ID_PAUTA));

        assertThatThrownBy(() -> sessaoVotacaoService.abrirSessao(new AberturaSessao(ID_PAUTA, 5)))
                .isInstanceOf(PautaNaoEncontradaException.class);

        verify(sessaoVotacaoRepository, never()).inserir(anyLong(), anyInt());
    }

    @Test
    @DisplayName("Converte chave duplicada em pauta que já possui sessão")
    void deveConverterChaveDuplicada() {

        when(sessaoVotacaoRepository.inserir(ID_PAUTA, 5)).thenThrow(new DuplicateKeyException("uk_sessao"));

        assertThatThrownBy(() -> sessaoVotacaoService.abrirSessao(new AberturaSessao(ID_PAUTA, 5)))
                .isInstanceOf(SessaoJaExistenteException.class)
                .hasMessageContaining("já possui sessão");
    }

    @Test
    @DisplayName("Converte o SQLState 23P01 da trigger em sessão em andamento")
    void deveConverterSessaoEmAndamento() {

        when(sessaoVotacaoRepository.inserir(ID_PAUTA, 5))
                .thenThrow(new DataIntegrityViolationException("trigger", new SQLException("conflito", "23P01")));

        assertThatThrownBy(() -> sessaoVotacaoService.abrirSessao(new AberturaSessao(ID_PAUTA, 5)))
                .isInstanceOf(SessaoEmAndamentoException.class)
                .hasMessage("Já existe uma sessão de votação em andamento");
    }

    @Test
    @DisplayName("Mantém a exceção original quando a violação não é da trigger")
    void deveManterOutraViolacaoDeIntegridade() {

        when(sessaoVotacaoRepository.inserir(ID_PAUTA, 5))
                .thenThrow(new DataIntegrityViolationException("outro", new SQLException("erro", "23514")));

        assertThatThrownBy(() -> sessaoVotacaoService.abrirSessao(new AberturaSessao(ID_PAUTA, 5)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Busca a sessão em andamento")
    void deveBuscarSessaoAberta() {

        SessaoAberta esperada = new SessaoAberta(1L, ID_PAUTA, "Pauta", "Descrição",
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(1));

        when(sessaoVotacaoRepository.buscarAberta()).thenReturn(Optional.of(esperada));

        assertThat(sessaoVotacaoService.buscarSessaoAberta()).isEqualTo(esperada);
    }

    @Test
    @DisplayName("Lança exceção quando não existe sessão em andamento")
    void deveLancarExcecaoSemSessaoAberta() {

        when(sessaoVotacaoRepository.buscarAberta()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessaoVotacaoService.buscarSessaoAberta())
                .isInstanceOf(NenhumaSessaoAbertaException.class);
    }
}
