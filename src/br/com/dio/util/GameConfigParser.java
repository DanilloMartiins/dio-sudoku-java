package br.com.dio.util;

import java.util.HashMap;
import java.util.Map;

import static br.com.dio.util.GameConstants.BOARD_LIMIT;

public final class GameConfigParser {

    private GameConfigParser() {}

    public static Map<String, String> parseArgs(final String[] args) {
        final int expectedPositions = BOARD_LIMIT * BOARD_LIMIT;
        if (args.length != expectedPositions) {
            throw new IllegalArgumentException(
                    "Configuracao invalida: esperado %s argumentos, recebido %s."
                            .formatted(expectedPositions, args.length)
            );
        }

        final Map<String, String> config = new HashMap<>();
        for (final String arg : args) {
            final var keyValue = arg.split(";", 2);
            if (keyValue.length != 2) {
                throw new IllegalArgumentException("Argumento invalido: %s".formatted(arg));
            }

            final var colRow = keyValue[0].split(",", 2);
            if (colRow.length != 2) {
                throw new IllegalArgumentException("Posicao invalida: %s".formatted(keyValue[0]));
            }

            final int col = parseNumberInRange(colRow[0], 0, BOARD_LIMIT - 1, "coluna");
            final int row = parseNumberInRange(colRow[1], 0, BOARD_LIMIT - 1, "linha");

            final var valueFixed = keyValue[1].split(",", 2);
            if (valueFixed.length != 2) {
                throw new IllegalArgumentException("Configuracao de celula invalida: %s".formatted(keyValue[1]));
            }

            final int expected = parseNumberInRange(valueFixed[0], 1, BOARD_LIMIT, "valor esperado");
            final boolean fixed = parseBoolean(valueFixed[1]);

            final String key = "%s,%s".formatted(col, row);
            if (config.containsKey(key)) {
                throw new IllegalArgumentException("Posicao duplicada encontrada: %s".formatted(key));
            }

            config.put(key, "%s,%s".formatted(expected, fixed));
        }

        return config;
    }

    private static int parseNumberInRange(final String value, final int min, final int max, final String fieldName) {
        try {
            final int number = Integer.parseInt(value);
            if (number < min || number > max) {
                throw new IllegalArgumentException(
                        "Campo %s fora da faixa permitida [%s,%s]: %s".formatted(fieldName, min, max, value)
                );
            }
            return number;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Campo %s invalido, esperado numero: %s".formatted(fieldName, value)
            );
        }
    }

    private static boolean parseBoolean(final String value) {
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new IllegalArgumentException(
                "Campo fixed invalido, esperado true/false: %s".formatted(value)
        );
    }
}
