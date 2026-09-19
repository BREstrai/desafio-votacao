package com.brunoestrai.desafio_votacao.handlers;

import java.time.OffsetDateTime;

public record RespostaHandler(String path, OffsetDateTime timestamp, int status, String error, String message) {
}
