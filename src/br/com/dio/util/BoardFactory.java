package br.com.dio.util;

import br.com.dio.model.Board;
import br.com.dio.model.Space;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static br.com.dio.util.GameConstants.BOARD_LIMIT;

public final class BoardFactory {

    private BoardFactory() {}

    public static Board createBoard(final Map<String, String> gameConfig) {
        List<List<Space>> spaces = new ArrayList<>();
        for (int col = 0; col < BOARD_LIMIT; col++) {
            spaces.add(new ArrayList<>());
            for (int row = 0; row < BOARD_LIMIT; row++) {
                final String key = "%s,%s".formatted(col, row);
                final var positionConfig = gameConfig.get(key);
                if (positionConfig == null) {
                    throw new IllegalArgumentException("Posicao nao configurada: %s".formatted(key));
                }

                final var expectedFixed = positionConfig.split(",", 2);
                if (expectedFixed.length != 2) {
                    throw new IllegalArgumentException("Configuracao invalida para a posicao %s".formatted(key));
                }

                final var expected = Integer.parseInt(expectedFixed[0]);
                final var fixed = parseBoolean(expectedFixed[1]);
                spaces.get(col).add(new Space(expected, fixed));
            }
        }

        return new Board(spaces);
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
