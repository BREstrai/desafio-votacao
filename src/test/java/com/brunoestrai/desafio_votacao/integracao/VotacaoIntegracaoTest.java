package com.brunoestrai.desafio_votacao.integracao;

import com.brunoestrai.desafio_votacao.domain.pauta.NovaPauta;
import com.brunoestrai.desafio_votacao.domain.pauta.Pauta;
import com.brunoestrai.desafio_votacao.domain.sessao.AberturaSessao;
import com.brunoestrai.desafio_votacao.domain.sessao.SessaoAberta;
import com.brunoestrai.desafio_votacao.domain.voto.NovoVoto;
import com.brunoestrai.desafio_votacao.domain.voto.ResultadoVotacao;
import com.brunoestrai.desafio_votacao.handlers.RespostaHandler;
import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Sobe a aplicação inteira contra o PostgreSQL do docker-compose e exercita a API pelo HTTP.
 * Precisa banco rodando: {@code podman compose up -d}.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = "cpf.cliente.aleatorio=false")
@AutoConfigureRestTestClient
class VotacaoIntegracaoTest {

    private static final String CPF_VALIDO = "529.982.247-25";

    private static final String OUTRO_CPF_VALIDO = "168.995.350-09";

    private static final String CPF_INVALIDO = "12345678901";

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private JdbcClient jdbcClient;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private Cache<String, SessaoAberta> sessaoAbertaCache;

    @BeforeEach
    void limparEstado() {

        transactionTemplate.executeWithoutResult(status ->
                jdbcClient.sql("TRUNCATE voto, sessao_votacao, pauta RESTART IDENTITY CASCADE").update());

        sessaoAbertaCache.invalidateAll();
    }

    @Test
    @DisplayName("Fluxo completo: cadastra a pauta, abre a sessão, recebe os votos e apura")
    void deveExecutarFluxoCompleto() {

        Long idPauta = criarPauta("Aquisição de terreno");

        abrirSessao(idPauta, 5).expectStatus().isCreated();
        votar(idPauta, CPF_VALIDO, true).expectStatus().isCreated();
        votar(idPauta, OUTRO_CPF_VALIDO, false).expectStatus().isCreated();

        ResultadoVotacao resultado = corpo(
                restTestClient.get().uri("/pauta/{id}/resultado", idPauta).exchange().expectStatus().isOk(),
                ResultadoVotacao.class);

        assertThat(resultado).isEqualTo(new ResultadoVotacao(1L, 1L));
    }

    @Test
    @DisplayName("A abertura da sessão já deixa a consulta em cache")
    void deveAquecerOCacheNaAbertura() {

        Long idPauta = criarPauta("Pauta com cache");

        abrirSessao(idPauta, 5).expectStatus().isCreated();

        assertThat(sessaoAbertaCache.getIfPresent("sessao:aberta")).isNotNull();

        SessaoAberta aberta = corpo(
                restTestClient.get().uri("/sessao").exchange().expectStatus().isOk(),
                SessaoAberta.class);

        assertThat(aberta.idPauta()).isEqualTo(idPauta);
        assertThat(aberta.titulo()).isEqualTo("Pauta com cache");
    }

    @Test
    @DisplayName("Só permite uma sessão em andamento: a trigger recusa a segunda pauta")
    void deveRecusarSegundaSessaoEmAndamento() {

        abrirSessao(criarPauta("Primeira"), 5).expectStatus().isCreated();

        RespostaHandler erro = erro(abrirSessao(criarPauta("Segunda"), 5), 409);

        assertThat(erro.message()).isEqualTo("Já existe uma sessão de votação em andamento");
    }

    @Test
    @DisplayName("O mesmo CPF vota uma única vez na pauta")
    void deveRecusarVotoDuplicado() {

        Long idPauta = criarPauta("Pauta com voto duplicado");
        abrirSessao(idPauta, 5).expectStatus().isCreated();
        votar(idPauta, CPF_VALIDO, true).expectStatus().isCreated();

        RespostaHandler erro = erro(votar(idPauta, CPF_VALIDO, false), 409);

        assertThat(erro.message()).contains("já votou na pauta");
    }

    @Test
    @DisplayName("Recusa o voto quando a pauta não tem votação aberta")
    void deveRecusarVotoSemSessao() {

        RespostaHandler erro = erro(votar(criarPauta("Pauta sem sessão"), CPF_VALIDO, true), 409);

        assertThat(erro.message()).contains("não está com a votação aberta");
    }

    @Test
    @DisplayName("Recusa CPF com dígitos verificadores inválidos")
    void deveRecusarCpfInvalido() {

        Long idPauta = criarPauta("Pauta com CPF inválido");
        abrirSessao(idPauta, 5).expectStatus().isCreated();

        RespostaHandler erro = erro(votar(idPauta, CPF_INVALIDO, true), 404);

        assertThat(erro.message()).isEqualTo("CPF inválido");
    }

    @Test
    @DisplayName("Responde 404 quando não existe sessão em andamento")
    void deveResponder404SemSessaoAberta() {

        restTestClient.get().uri("/sessao").exchange().expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Recusa corpo com tipo fora do contrato")
    void deveRecusarCorpoInvalido() {

        Long idPauta = criarPauta("Pauta com corpo inválido");
        abrirSessao(idPauta, 5).expectStatus().isCreated();

        // JSON cru porque o record não permite representar um booleano fora do contrato.
        enviar("/voto", "{\"idPauta\":%d,\"cpf\":\"%s\",\"aprovado\":2}".formatted(idPauta, CPF_VALIDO))
                .expectStatus().isBadRequest();
    }

    private Long criarPauta(String titulo) {

        Pauta pauta = corpo(
                enviar(
                        "/pauta", new NovaPauta(titulo,
                                "Criada pelo teste de integração")).expectStatus().isCreated(), Pauta.class);

        return pauta.getIdPauta();
    }

    private RestTestClient.ResponseSpec abrirSessao(Long idPauta, Integer tempoLimite) {

        return enviar("/sessao", new AberturaSessao(idPauta, tempoLimite));
    }

    private RestTestClient.ResponseSpec votar(Long idPauta, String cpf, boolean aprovado) {

        return enviar("/voto", new NovoVoto(idPauta, cpf, aprovado));
    }

    private RestTestClient.ResponseSpec enviar(String caminho, Object corpo) {

        return restTestClient.post().uri(caminho)
                .contentType(MediaType.APPLICATION_JSON)
                .body(corpo)
                .exchange();
    }

    private RespostaHandler erro(RestTestClient.ResponseSpec resposta, int status) {

        return corpo(resposta.expectStatus().isEqualTo(status), RespostaHandler.class);
    }

    private <T> T corpo(RestTestClient.ResponseSpec resposta, Class<T> tipo) {

        return resposta.expectBody(tipo).returnResult().getResponseBody();
    }
}
