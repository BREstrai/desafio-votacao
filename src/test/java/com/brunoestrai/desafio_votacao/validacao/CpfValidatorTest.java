package com.brunoestrai.desafio_votacao.validacao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class CpfValidatorTest {

    @ParameterizedTest
    @DisplayName("Aceita CPF válido, com ou sem máscara")
    @ValueSource(strings = {"52998224725", "529.982.247-25", "16899535009", "168.995.350-09"})
    void deveAceitarCpfValido(String cpf) {

        assertThat(CpfValidator.ehValido(cpf)).isTrue();
    }

    @ParameterizedTest
    @DisplayName("Rejeita CPF com dígito verificador errado")
    @ValueSource(strings = {"52998224726", "16899535008", "12345678901"})
    void deveRejeitarDigitoVerificadorErrado(String cpf) {

        assertThat(CpfValidator.ehValido(cpf)).isFalse();
    }

    @ParameterizedTest
    @DisplayName("Rejeita sequência de dígitos repetidos")
    @ValueSource(strings = {"00000000000", "11111111111", "99999999999"})
    void deveRejeitarDigitosRepetidos(String cpf) {

        assertThat(CpfValidator.ehValido(cpf)).isFalse();
    }

    @ParameterizedTest
    @DisplayName("Rejeita CPF com quantidade de dígitos diferente de 11")
    @ValueSource(strings = {"123", "5299822472", "529982247251", "abc"})
    void deveRejeitarTamanhoInvalido(String cpf) {

        assertThat(CpfValidator.ehValido(cpf)).isFalse();
    }

    @ParameterizedTest
    @DisplayName("Rejeita CPF nulo ou vazio")
    @NullAndEmptySource
    void deveRejeitarNuloOuVazio(String cpf) {

        assertThat(CpfValidator.ehValido(cpf)).isFalse();
    }

    @Test
    @DisplayName("Remove a máscara do CPF")
    void deveRemoverMascara() {

        assertThat(CpfValidator.somenteDigitos("529.982.247-25")).isEqualTo("52998224725");
        assertThat(CpfValidator.somenteDigitos(null)).isEmpty();
    }
}
