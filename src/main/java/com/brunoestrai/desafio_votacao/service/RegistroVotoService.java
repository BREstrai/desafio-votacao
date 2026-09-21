package com.brunoestrai.desafio_votacao.service;

import com.brunoestrai.desafio_votacao.client.cpf.CpfClient;
import com.brunoestrai.desafio_votacao.domain.cpf.StatusCpf;
import com.brunoestrai.desafio_votacao.domain.voto.NovoVoto;
import com.brunoestrai.desafio_votacao.domain.voto.Voto;
import com.brunoestrai.desafio_votacao.exception.cpf.CooperadoNaoHabilitadoException;
import com.brunoestrai.desafio_votacao.exception.cpf.CpfInvalidoException;
import com.brunoestrai.desafio_votacao.validacao.CpfValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Orquestra o registro do voto: valida o CPF e consulta o serviço externo antes de gravar.
 * <p>
 * Não é transacional de propósito: a chamada externa acontece fora da transação, para não manter uma conexão do pool
 * presa enquanto a aplicação espera a rede.
 */
@Service
@RequiredArgsConstructor
public class RegistroVotoService {

    private final CpfClient cpfClient;

    private final VotoService votoService;

    public Voto registrarVoto(NovoVoto novoVoto) {

        String cpf = CpfValidator.somenteDigitos(novoVoto.cpf());

        if (!CpfValidator.ehValido(cpf)) {

            throw new CpfInvalidoException();
        }

        if (cpfClient.consultar(cpf) == StatusCpf.UNABLE_TO_VOTE) {

            throw new CooperadoNaoHabilitadoException();
        }

        return votoService.registrarVoto(novoVoto);
    }
}
