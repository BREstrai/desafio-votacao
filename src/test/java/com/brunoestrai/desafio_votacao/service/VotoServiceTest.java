package com.brunoestrai.desafio_votacao.service;

import com.brunoestrai.desafio_votacao.domain.voto.DiagnosticoVotacao;
import com.brunoestrai.desafio_votacao.domain.voto.NovoVoto;
import com.brunoestrai.desafio_votacao.domain.voto.ResultadoVotacao;
import com.brunoestrai.desafio_votacao.domain.voto.Voto;
import com.brunoestrai.desafio_votacao.exception.pauta.PautaNaoEncontradaException;
import com.brunoestrai.desafio_votacao.exception.voto.SessaoNaoAbertaException;
import com.brunoestrai.desafio_votacao.exception.voto.VotoDuplicadoException;
import com.brunoestrai.desafio_votacao.repository.voto.VotoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    private static final Long ID_PAUTA = 1L;

    private static final String CPF_COM_MASCARA = "529.982.247-25";

    private static final String CPF_LIMPO = "52998224725";

    @Mock
    private VotoRepository votoRepository;

    @InjectMocks
    private VotoService votoService;

    @Test
    @DisplayName("Registra o voto removendo a máscara do CPF")
    void deveRegistrarVoto() {

        Voto esperado = new Voto(1L, ID_PAUTA, true, LocalDateTime.now());

        when(votoRepository.registrar(ID_PAUTA, CPF_LIMPO, true)).thenReturn(Optional.of(esperado));

        assertThat(votoService.registrarVoto(new NovoVoto(ID_PAUTA, CPF_COM_MASCARA, true))).isEqualTo(esperado);
    }

    @Test
    @DisplayName("Converte chave duplicada em voto duplicado")
    void deveConverterVotoDuplicado() {

        when(votoRepository.registrar(ID_PAUTA, CPF_LIMPO, true))
                .thenThrow(new DuplicateKeyException("uk_voto_pauta_cpf"));

        assertThatThrownBy(() -> votoService.registrarVoto(new NovoVoto(ID_PAUTA, CPF_LIMPO, true)))
                .isInstanceOf(VotoDuplicadoException.class)
                .hasMessageContaining("já votou na pauta 1");
    }

    @Test
    @DisplayName("Informa pauta inexistente quando o insert não grava e a pauta não existe")
    void deveInformarPautaInexistente() {

        when(votoRepository.registrar(ID_PAUTA, CPF_LIMPO, true)).thenReturn(Optional.empty());
        when(votoRepository.diagnosticar(ID_PAUTA)).thenReturn(new DiagnosticoVotacao(false, false));

        assertThatThrownBy(() -> votoService.registrarVoto(new NovoVoto(ID_PAUTA, CPF_LIMPO, true)))
                .isInstanceOf(PautaNaoEncontradaException.class);
    }

    @Test
    @DisplayName("Informa votação fechada quando o insert não grava e a pauta existe")
    void deveInformarVotacaoFechada() {

        when(votoRepository.registrar(ID_PAUTA, CPF_LIMPO, true)).thenReturn(Optional.empty());
        when(votoRepository.diagnosticar(ID_PAUTA)).thenReturn(new DiagnosticoVotacao(true, true));

        assertThatThrownBy(() -> votoService.registrarVoto(new NovoVoto(ID_PAUTA, CPF_LIMPO, true)))
                .isInstanceOf(SessaoNaoAbertaException.class)
                .hasMessageContaining("não está com a votação aberta");
    }

    @Test
    @DisplayName("Apura a votação sem consultar o diagnóstico quando existem votos")
    void deveApurarVotacao() {

        ResultadoVotacao esperado = new ResultadoVotacao(3L, 2L);

        when(votoRepository.apurar(ID_PAUTA)).thenReturn(esperado);

        assertThat(votoService.apurarVotacao(ID_PAUTA)).isEqualTo(esperado);

        verify(votoRepository, never()).diagnosticar(ID_PAUTA);
    }

    @Test
    @DisplayName("Apura zerado quando a pauta existe e ainda não tem votos")
    void deveApurarZeradoParaPautaSemVotos() {

        ResultadoVotacao zerado = new ResultadoVotacao(0L, 0L);

        when(votoRepository.apurar(ID_PAUTA)).thenReturn(zerado);
        when(votoRepository.diagnosticar(ID_PAUTA)).thenReturn(new DiagnosticoVotacao(true, false));

        assertThat(votoService.apurarVotacao(ID_PAUTA)).isEqualTo(zerado);
    }

    @Test
    @DisplayName("Lança exceção ao apurar pauta inexistente")
    void deveRecusarApuracaoDePautaInexistente() {

        when(votoRepository.apurar(ID_PAUTA)).thenReturn(new ResultadoVotacao(0L, 0L));
        when(votoRepository.diagnosticar(ID_PAUTA)).thenReturn(new DiagnosticoVotacao(false, false));

        assertThatThrownBy(() -> votoService.apurarVotacao(ID_PAUTA))
                .isInstanceOf(PautaNaoEncontradaException.class);
    }
}
