package com.brunoestrai.desafio_votacao.service;

import com.brunoestrai.desafio_votacao.domain.voto.DiagnosticoVotacao;
import com.brunoestrai.desafio_votacao.domain.voto.NovoVoto;
import com.brunoestrai.desafio_votacao.domain.voto.ResultadoVotacao;
import com.brunoestrai.desafio_votacao.domain.voto.Voto;
import com.brunoestrai.desafio_votacao.exception.pauta.PautaNaoEncontradaException;
import com.brunoestrai.desafio_votacao.exception.voto.SessaoNaoAbertaException;
import com.brunoestrai.desafio_votacao.exception.voto.VotoDuplicadoException;
import com.brunoestrai.desafio_votacao.repository.voto.VotoRepository;
import com.brunoestrai.desafio_votacao.validacao.CpfValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class VotoService {

    private final VotoRepository votoRepository;

    /**
     * Registra o voto em um comando só. A sessão aberta é conferida pelo banco dentro do insert, e a unicidade do voto
     * pela constraint {@code uk_voto_pauta_cpf}. Nenhuma consulta prévia é feita no caminho de sucesso.
     */
    public Voto registrarVoto(NovoVoto novoVoto) {

        Long idPauta = novoVoto.idPauta();

        try {
            return votoRepository.registrar(idPauta, CpfValidator.somenteDigitos(novoVoto.cpf()), novoVoto.aprovado())
                    .orElseThrow(() -> diagnosticarFalha(idPauta));
        } catch (DuplicateKeyException e) {
            throw new VotoDuplicadoException(idPauta, e);
        }
    }

    /**
     * Contabiliza os votos da pauta. Sem nenhum voto, confirma se a pauta existe antes de devolver o resultado zerado.
     */
    public ResultadoVotacao apurarVotacao(Long idPauta) {

        ResultadoVotacao resultado = votoRepository.apurar(idPauta);

        if (resultado.aprovado() == 0 && resultado.rejeitado() == 0 &&
                !votoRepository.diagnosticar(idPauta).pautaExiste()) {

            throw new PautaNaoEncontradaException(idPauta);
        }

        return resultado;
    }

    private RuntimeException diagnosticarFalha(Long idPauta) {

        DiagnosticoVotacao diagnostico = votoRepository.diagnosticar(idPauta);

        if (!diagnostico.pautaExiste()) {

            return new PautaNaoEncontradaException(idPauta);
        }

        return new SessaoNaoAbertaException(idPauta);
    }


}
