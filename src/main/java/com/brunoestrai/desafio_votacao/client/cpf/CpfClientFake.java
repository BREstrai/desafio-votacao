package com.brunoestrai.desafio_votacao.client.cpf;

import com.brunoestrai.desafio_votacao.domain.cpf.StatusCpf;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Serviço externo simulado. Com {@code cpf.cliente.aleatorio=true} sorteia o resultado, como pede o desafio. Com
 * {@code false} devolve sempre {@code cpf.cliente.resultado-fixo}, o que torna testes e demonstrações previsíveis.
 */
@Log4j2
@Component
public class CpfClientFake implements CpfClient {

    @Value("${cpf.cliente.aleatorio}")
    private boolean aleatorio;

    @Value("${cpf.cliente.resultado-fixo}")
    private StatusCpf resultadoFixo;

    @Override
    public StatusCpf consultar(String cpf) {

        StatusCpf status = aleatorio ? sortear() : resultadoFixo;

        log.debug("Consulta de CPF respondeu {}", status);

        return status;
    }

    private StatusCpf sortear() {

        return ThreadLocalRandom.current().nextBoolean() ? StatusCpf.ABLE_TO_VOTE : StatusCpf.UNABLE_TO_VOTE;
    }
}
