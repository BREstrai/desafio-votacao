package com.brunoestrai.desafio_votacao.client.cpf;

import com.brunoestrai.desafio_votacao.domain.cpf.StatusCpf;

/**
 * Consulta se o cooperado pode votar. Interface própria para que a troca do fake por uma integração real não altere o
 * restante da aplicação.
 */
public interface CpfClient {

    StatusCpf consultar(String cpf);
}
