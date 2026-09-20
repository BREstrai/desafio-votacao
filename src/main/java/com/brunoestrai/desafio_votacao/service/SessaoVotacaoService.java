package com.brunoestrai.desafio_votacao.service;

import com.brunoestrai.desafio_votacao.domain.sessao.AberturaSessao;
import com.brunoestrai.desafio_votacao.domain.sessao.SessaoAberta;
import com.brunoestrai.desafio_votacao.domain.sessao.SessaoVotacao;
import com.brunoestrai.desafio_votacao.exception.sessao.NenhumaSessaoAbertaException;
import com.brunoestrai.desafio_votacao.exception.sessao.SessaoJaExistenteException;
import com.brunoestrai.desafio_votacao.repository.sessao.SessaoVotacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SessaoVotacaoService {

    private final PautaService pautaService;

    private final SessaoVotacaoRepository sessaoVotacaoRepository;

    public SessaoVotacao abrirSessao(AberturaSessao aberturaSessao) {

        Long idPauta = aberturaSessao.idPauta();
        pautaService.buscarPautaPorId(idPauta);

        try {

            return sessaoVotacaoRepository.inserir(idPauta, aberturaSessao.tempoLimite());
        } catch (DuplicateKeyException e) {

            throw new SessaoJaExistenteException(idPauta, e);
        }
    }

    public SessaoAberta buscarSessaoAberta() {

        return sessaoVotacaoRepository.buscarAberta()
                .orElseThrow(NenhumaSessaoAbertaException::new);
    }

}
