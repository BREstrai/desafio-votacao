package com.brunoestrai.desafio_votacao.service;

import com.brunoestrai.desafio_votacao.domain.pauta.NovaPauta;
import com.brunoestrai.desafio_votacao.domain.pauta.Pauta;
import com.brunoestrai.desafio_votacao.exception.pauta.PautaNaoEncontradaException;
import com.brunoestrai.desafio_votacao.repository.pauta.PautaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private PautaService pautaService;

    @Test
    @DisplayName("Cria a pauta delegando ao repositório")
    void deveCriarPauta() {

        NovaPauta novaPauta = new NovaPauta("Aquisição de terreno", "Compra do terreno ao lado da sede");
        Pauta esperada = Pauta.builder().idPauta(1L).titulo(novaPauta.dsTitulo()).build();

        when(pautaRepository.inserir(novaPauta)).thenReturn(esperada);

        assertThat(pautaService.criarPauta(novaPauta)).isEqualTo(esperada);
    }

    @Test
    @DisplayName("Busca a pauta pelo id")
    void deveBuscarPautaPorId() {

        Pauta esperada = Pauta.builder().idPauta(1L).titulo("Aquisição de terreno").build();

        when(pautaRepository.buscarPorId(1L)).thenReturn(Optional.of(esperada));

        assertThat(pautaService.buscarPautaPorId(1L)).isEqualTo(esperada);
    }

    @Test
    @DisplayName("Lança exceção quando a pauta não existe")
    void deveLancarExcecaoQuandoPautaNaoExiste() {

        when(pautaRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pautaService.buscarPautaPorId(99L))
                .isInstanceOf(PautaNaoEncontradaException.class)
                .hasMessage("Pauta 99 não encontrada");
    }

    @Test
    @DisplayName("Lista todas as pautas")
    void deveListarPautas() {

        List<Pauta> pautas = List.of(Pauta.builder().idPauta(1L).build(), Pauta.builder().idPauta(2L).build());

        when(pautaRepository.listarTodas()).thenReturn(pautas);

        assertThat(pautaService.buscarTodasPautas()).isEqualTo(pautas);
    }
}
