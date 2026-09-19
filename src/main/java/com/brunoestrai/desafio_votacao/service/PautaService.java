package com.brunoestrai.desafio_votacao.service;

import com.brunoestrai.desafio_votacao.domain.pauta.Pauta;
import com.brunoestrai.desafio_votacao.domain.pauta.NovaPauta;
import com.brunoestrai.desafio_votacao.exception.pauta.PautaNaoEncontradaException;
import com.brunoestrai.desafio_votacao.repository.pauta.PautaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PautaService {

    private final PautaRepository pautaRepository;

    public Pauta criarPauta(NovaPauta novaPauta) {

        return pautaRepository.inserir(novaPauta);
    }

    public Pauta buscarPautaPorId(Long idPauta) {

        return pautaRepository.buscarPorId(idPauta)
                .orElseThrow(() -> new PautaNaoEncontradaException(idPauta));
    }


    public List<Pauta> buscarTodasPautas() {

        return pautaRepository.listarTodas();
    }
}
