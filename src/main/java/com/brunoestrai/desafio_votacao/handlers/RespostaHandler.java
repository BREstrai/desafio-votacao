package com.brunoestrai.desafio_votacao.handlers;

import java.time.LocalDateTime;

public record RespostaHandler(String path, LocalDateTime timestamp, int status, String error, String message) {
}
