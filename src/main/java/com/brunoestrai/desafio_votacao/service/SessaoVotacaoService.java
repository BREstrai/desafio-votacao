package com.brunoestrai.desafio_votacao.service;

import com.brunoestrai.desafio_votacao.domain.sessao.AberturaSessao;
import com.brunoestrai.desafio_votacao.domain.sessao.SessaoAberta;
import com.brunoestrai.desafio_votacao.domain.sessao.SessaoVotacao;
import com.brunoestrai.desafio_votacao.exception.sessao.NenhumaSessaoAbertaException;
import com.brunoestrai.desafio_votacao.exception.sessao.SessaoEmAndamentoException;
import com.brunoestrai.desafio_votacao.exception.sessao.SessaoJaExistenteException;
import com.brunoestrai.desafio_votacao.repository.sessao.SessaoVotacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Service
@Transactional
@RequiredArgsConstructor
public class SessaoVotacaoService {

    private static final String SQL_STATE_SESSAO_EM_ANDAMENTO = "23P01";

    private final PautaService pautaService;

    private final SessaoVotacaoRepository sessaoVotacaoRepository;

    public SessaoVotacao abrirSessao(AberturaSessao aberturaSessao) {

        Long idPauta = aberturaSessao.idPauta();
        pautaService.buscarPautaPorId(idPauta);

        try {

            return sessaoVotacaoRepository.inserir(idPauta, aberturaSessao.tempoLimite());
        } catch (DuplicateKeyException e) {

            throw new SessaoJaExistenteException(idPauta, e);
        } catch (DataIntegrityViolationException e) {

            throw traduzirIntegridade(e);
        }
    }

    public SessaoAberta buscarSessaoAberta() {

        return sessaoVotacaoRepository.buscarAberta()
                .orElseThrow(NenhumaSessaoAbertaException::new);
    }

    /**
     * A trigger tg_sessao_unica recusa a abertura quando já existe votação em andamento, usando o SQLState 23P01.
     */
    private RuntimeException traduzirIntegridade(DataIntegrityViolationException e) {

        if (e.getMostSpecificCause() instanceof SQLException sqlException
                && SQL_STATE_SESSAO_EM_ANDAMENTO.equals(sqlException.getSQLState())) {

            return new SessaoEmAndamentoException(e);
        }

        return e;
    }
}
