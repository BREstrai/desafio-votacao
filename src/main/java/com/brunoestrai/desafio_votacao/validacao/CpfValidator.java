package com.brunoestrai.desafio_votacao.validacao;

import java.util.regex.Pattern;

public class CpfValidator {

    private static final int TAMANHO_CPF = 11;

    private static final Pattern NAO_DIGITO = Pattern.compile("\\D");

    private static final Pattern DIGITOS_REPETIDOS = Pattern.compile("(\\d)\\1{10}");

    private CpfValidator() {
    }

    public static boolean ehValido(String cpf) {

        String digitos = somenteDigitos(cpf);

        if (digitos.length() != TAMANHO_CPF || DIGITOS_REPETIDOS.matcher(digitos).matches()) {

            return false;
        }

        return digitoVerificador(digitos, 9) == valorEm(digitos, 9)
                && digitoVerificador(digitos, 10) == valorEm(digitos, 10);
    }

    public static String somenteDigitos(String cpf) {

        return cpf == null ? "" : NAO_DIGITO.matcher(cpf).replaceAll("");
    }

    private static int digitoVerificador(String digitos, int posicao) {

        int soma = 0;

        for (int i = 0; i < posicao; i++) {

            soma += valorEm(digitos, i) * (posicao + 1 - i);
        }

        int resto = soma % TAMANHO_CPF;

        return resto < 2 ? 0 : TAMANHO_CPF - resto;
    }

    private static int valorEm(String digitos, int posicao) {

        return Character.getNumericValue(digitos.charAt(posicao));
    }
}
