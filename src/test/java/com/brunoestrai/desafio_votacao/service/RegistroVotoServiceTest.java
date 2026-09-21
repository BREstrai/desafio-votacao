package com.brunoestrai.desafio_votacao.service;

import com.brunoestrai.desafio_votacao.client.cpf.CpfClient;
import com.brunoestrai.desafio_votacao.domain.cpf.StatusCpf;
import com.brunoestrai.desafio_votacao.domain.voto.NovoVoto;
import com.brunoestrai.desafio_votacao.domain.voto.Voto;
import com.brunoestrai.desafio_votacao.exception.cpf.CooperadoNaoHabilitadoException;
import com.brunoestrai.desafio_votacao.exception.cpf.CpfInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistroVotoServiceTest {

    private static final String CPF_VALIDO = "529.982.247-25";

    @Mock
    private CpfClient cpfClient;

    @Mock
    private VotoService votoService;

    @InjectMocks
    private RegistroVotoService registroVotoService;

    @Test
    @DisplayName("Registra o voto quando o CPF é válido e o cooperado está habilitado")
    void deveRegistrarVotoQuandoHabilitado() {

        NovoVoto novoVoto = new NovoVoto(1L, CPF_VALIDO, true);
        Voto esperado = new Voto(1L, 1L, true, LocalDateTime.now());

        when(cpfClient.consultar("52998224725")).thenReturn(StatusCpf.ABLE_TO_VOTE);
        when(votoService.registrarVoto(novoVoto)).thenReturn(esperado);

        assertThat(registroVotoService.registrarVoto(novoVoto)).isEqualTo(esperado);
    }

    @Test
    @DisplayName("Rejeita CPF inválido sem consultar o serviço externo nem gravar")
    void deveRejeitarCpfInvalido() {

        NovoVoto novoVoto = new NovoVoto(1L, "12345678901", true);

        assertThatThrownBy(() -> registroVotoService.registrarVoto(novoVoto))
                .isInstanceOf(CpfInvalidoException.class);

        verifyNoInteractions(cpfClient, votoService);
    }

    @Test
    @DisplayName("Não grava o voto quando o cooperado não está habilitado")
    void deveRejeitarCooperadoNaoHabilitado() {

        NovoVoto novoVoto = new NovoVoto(1L, CPF_VALIDO, true);

        when(cpfClient.consultar(anyString())).thenReturn(StatusCpf.UNABLE_TO_VOTE);

        assertThatThrownBy(() -> registroVotoService.registrarVoto(novoVoto))
                .isInstanceOf(CooperadoNaoHabilitadoException.class);

        verify(votoService, never()).registrarVoto(any());
    }
}
