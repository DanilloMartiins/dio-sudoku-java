package br.com.dio;

import br.com.dio.ui.custom.screen.MainScreen;

import java.util.Map;

import static br.com.dio.util.DefaultGameConfig.resolveArgs;
import static br.com.dio.util.GameConfigParser.parseArgs;

public class UIMain {

    public static void main(String[] args) {
        final var gameConfig = tryParse(resolveArgs(args));
        if (gameConfig == null) {
            return;
        }

        var mainsScreen = new MainScreen(gameConfig);
        mainsScreen.buildMainScreen();
    }

    private static Map<String, String> tryParse(final String[] args) {
        try {
            return parseArgs(args);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

}
