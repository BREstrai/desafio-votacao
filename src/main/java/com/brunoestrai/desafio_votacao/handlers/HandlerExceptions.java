package com.brunoestrai.desafio_votacao.handlers;

import com.brunoestrai.desafio_votacao.exception.ConflitoException;
import com.brunoestrai.desafio_votacao.exception.OperacaoNaoPermitidaException;
import com.brunoestrai.desafio_votacao.exception.RecursoNaoEncontradoException;
import com.brunoestrai.desafio_votacao.exception.ValidacaoException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Log4j2
@RestControllerAdvice
public class HandlerExceptions {

    private static final String SQL_STATE_FOREIGN_KEY = "23503";

    private static final String MSG_GENERICA = "Não foi possível concluir a operação, entre em contato com o suporte";
    private static final String MSG_REGISTRO_DUPLICADO = "O registro informado já existe";
    private static final String MSG_REFERENCIA_INEXISTENTE = "O registro relacionado informado não existe";
    private static final String MSG_VIOLACAO_INTEGRIDADE = "Os dados informados são inválidos";
    private static final String MSG_CORPO_INVALIDO = "O corpo da requisição é inválido, confira os campos e os tipos enviados";
    private static final String MSG_TEMPORARIA = "Serviço temporariamente indisponível, tente novamente em instantes";

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<RespostaHandler> handleRecursoNaoEncontrado(
            HttpServletRequest req, RecursoNaoEncontradoException ex) {

        log.warn("Recurso não encontrado em {}: {}", req.getRequestURI(), ex.getMessage());

        return responder(req, NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespostaHandler> handleCampoInvalido(
            HttpServletRequest req, MethodArgumentNotValidException ex) {

        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        log.warn("Requisição inválida em {}: {}", req.getRequestURI(), mensagem);

        return responder(req, BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespostaHandler> handleCorpoInvalido(
            HttpServletRequest req, HttpMessageNotReadableException ex) {

        log.warn("Corpo da requisição inválido em {}: {}", req.getRequestURI(), ex.getMostSpecificCause().getMessage());

        return responder(req, BAD_REQUEST, MSG_CORPO_INVALIDO);
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<RespostaHandler> handleValidacao(HttpServletRequest req, ValidacaoException ex) {

        log.warn("Requisição inválida em {}: {}", req.getRequestURI(), ex.getMessage());

        return responder(req, BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(OperacaoNaoPermitidaException.class)
    public ResponseEntity<RespostaHandler> handleOperacaoNaoPermitida(
            HttpServletRequest req, OperacaoNaoPermitidaException ex) {

        log.warn("Operação não permitida em {}: {}", req.getRequestURI(), ex.getMessage());

        return responder(req, FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<RespostaHandler> handleConflito(HttpServletRequest req, ConflitoException ex) {

        log.warn("Conflito em {}: {}", req.getRequestURI(), ex.getMessage());

        return responder(req, CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<RespostaHandler> handleRegistroDuplicado(
            HttpServletRequest req, DuplicateKeyException ex) {

        log.warn("Registro duplicado em {}: {}", req.getRequestURI(), ex.getMostSpecificCause().getMessage());

        return responder(req, CONFLICT, MSG_REGISTRO_DUPLICADO);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RespostaHandler> handleViolacaoIntegridade(
            HttpServletRequest req, DataIntegrityViolationException ex) {

        if (isForeignKey(ex)) {

            log.warn("Referência inexistente em {}: {}", req.getRequestURI(),
                    ex.getMostSpecificCause().getMessage());

            return responder(req, NOT_FOUND, MSG_REFERENCIA_INEXISTENTE);
        }

        log.warn("Violação de integridade em {}, possível validação ausente: {}", req.getRequestURI(),
                ex.getMostSpecificCause().getMessage(), ex);

        return responder(req, BAD_REQUEST, MSG_VIOLACAO_INTEGRIDADE);
    }

    @ExceptionHandler(TransientDataAccessException.class)
    public ResponseEntity<RespostaHandler> handleFalhaTransitoria(
            HttpServletRequest req, TransientDataAccessException ex) {

        log.warn("Falha transitória no banco em {}: {}", req.getRequestURI(), ex.getMostSpecificCause().getMessage());

        return responder(req, SERVICE_UNAVAILABLE, MSG_TEMPORARIA);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<RespostaHandler> handleAcessoDados(HttpServletRequest req, DataAccessException ex) {

        log.error("Falha de acesso a dados em {}: {}", req.getRequestURI(), ex.getMessage(), ex);

        return responder(req, INTERNAL_SERVER_ERROR, MSG_GENERICA);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespostaHandler> handleError(HttpServletRequest req, Exception ex) {

        log.error("Tratando retorno de exception: {}", ex.getMessage(), ex);

        return responder(req, INTERNAL_SERVER_ERROR, MSG_GENERICA);
    }

    private boolean isForeignKey(DataIntegrityViolationException ex) {

        return ex.getMostSpecificCause() instanceof SQLException sqlException
                && SQL_STATE_FOREIGN_KEY.equals(sqlException.getSQLState());
    }

    private ResponseEntity<RespostaHandler> responder(HttpServletRequest req, HttpStatus status, String mensagem) {

        RespostaHandler respostaHandler = new RespostaHandler(req.getRequestURI(), LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), mensagem);

        return ResponseEntity.status(status).body(respostaHandler);
    }
}
