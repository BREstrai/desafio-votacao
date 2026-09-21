# Sobre o projeto

API REST de votação em assembleias de cooperativas: cadastra pautas, abre uma sessão de votação por vez e recebe um
voto por CPF em cada pautam que valida se o CPF é valido ou não antes de registrar o voto.

# Como executar

## Requisitos

* JDK 25
* Maven wrapper do projeto (`mvnw`), sem necessidade de instalar o Maven
* Podman ou Docker, com provider de compose

## Banco de dados

O `docker-compose.yaml` sobe o PostgreSQL 17 na porta 5432. O banco, o usuário e a senha são `votacao`.

```
podman compose up -d
podman compose ps
```

As tabelas são criadas pelo Liquibase na subida da aplicação.

## Aplicação

```
mvnw spring-boot:run
```

A aplicação sobe na porta 8080, com o contexto `/api/v1`.

O cliente de validação de CPF é simulado e, por padrão, sorteia se o cooperado pode votar. Para que todo CPF válido
seja aceito, o que facilita os testes manuais, suba assim:

```
mvnw spring-boot:run -Dspring-boot.run.arguments="--cpf.cliente.aleatorio=false"
```

ou troque no application.properties para FALSE a propriedade.

## Documentação da API

Com a aplicação no ar, o contrato é gerado pelo springdoc:

* Swagger UI: http://localhost:8080/api/v1/swagger-ui/index.html
* OpenAPI (JSON): http://localhost:8080/api/v1/v3/api-docs

## Testando pelo Postman

1. Importe o arquivo `postman/desafio-votacao.postman_collection.json`.
2. A variável `baseUrl` já vem preenchida com `http://localhost:8080/api/v1`.
3. Execute na ordem: criar pauta, abrir sessão, registrar voto e apurar votação.
4. Os ids nas requisições estão fixos em `1`. Troque pelo id devolvido na criação da pauta.
5. O voto usa o CPF `529.982.247-25`. Outro CPF válido para teste: `168.995.350-09`.
