package br.com.dio;

import br.com.dio.model.Board;
import br.com.dio.model.GameStatusEnum;
import br.com.dio.model.MoveValidationResult;

import java.util.Map;
import java.util.Scanner;

import static br.com.dio.util.BoardFactory.createBoard;
import static br.com.dio.util.BoardTemplate.BOARD_TEMPLATE;
import static br.com.dio.util.DefaultGameConfig.resolveArgs;
import static br.com.dio.util.GameConfigParser.parseArgs;
import static br.com.dio.util.GameConstants.BOARD_LIMIT;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static Board board;

    public static void main(String[] args) {
        final Map<String, String> positions;
        try {
            positions = parseArgs(resolveArgs(args));
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            return;
        }

        var option = -1;
        while (true){
            System.out.println("Selecione uma das opcoes a seguir");
            System.out.println("1 - Iniciar um novo Jogo");
            System.out.println("2 - Colocar um novo numero");
            System.out.println("3 - Remover um numero");
            System.out.println("4 - Verificar status do jogo");
            System.out.println("5 - Limpar jogo");
            System.out.println("6 - Finalizar jogo");
            System.out.println("7 - Sair");

            option = runUntilGetValidNumber(1, 7);

            switch (option){
                case 1 -> startGame(positions);
                case 2 -> inputNumber();
                case 3 -> removeNumber();
                case 4 -> showGameStatus();
                case 5 -> clearGame();
                case 6 -> finishGame();
                case 7 -> exitGame();
            }
        }
    }

    private static void startGame(final Map<String, String> positions) {
        if (nonNull(board)){
            System.out.println("O jogo ja foi iniciado");
            return;
        }

        board = createBoard(positions);
        System.out.println("O jogo esta pronto para comecar");
    }

    private static void inputNumber() {
        if (!ensureGameStarted()){
            return;
        }

        System.out.println("Informe a coluna em que o numero sera inserido");
        var col = runUntilGetValidNumber(0, BOARD_LIMIT - 1);
        System.out.println("Informe a linha em que o numero sera inserido");
        var row = runUntilGetValidNumber(0, BOARD_LIMIT - 1);
        System.out.printf("Informe o numero que vai entrar na posicao [%s,%s]\n", col, row);
        var value = runUntilGetValidNumber(1, BOARD_LIMIT);

        final MoveValidationResult moveValidation = board.validateMove(col, row, value);
        if (moveValidation == MoveValidationResult.FIXED_POSITION){
            System.out.printf("Jogada nao aplicada: a posicao [%s,%s] tem um valor fixo\n", col, row);
        } else if (moveValidation == MoveValidationResult.CONFLICT) {
            System.out.println("Jogada nao aplicada: numero repetido na linha, coluna ou setor 3x3");
        } else if (moveValidation == MoveValidationResult.INVALID_POSITION) {
            System.out.println("Jogada nao aplicada: posicao invalida");
        } else {
            board.changeValue(col, row, value);
            System.out.println("Jogada aplicada com sucesso");
        }

        showCurrentGame();
    }

    private static void removeNumber() {
        if (!ensureGameStarted()){
            return;
        }

        System.out.println("Informe a coluna em que o numero sera removido");
        var col = runUntilGetValidNumber(0, BOARD_LIMIT - 1);
        System.out.println("Informe a linha em que o numero sera removido");
        var row = runUntilGetValidNumber(0, BOARD_LIMIT - 1);
        if (!board.clearValue(col, row)){
            System.out.printf("A posicao [%s,%s] tem um valor fixo\n", col, row);
        } else {
            System.out.println("Numero removido com sucesso");
        }

        showCurrentGame();
    }

    private static void showCurrentGame() {
        if (!ensureGameStarted()){
            return;
        }

        var args = new Object[BOARD_LIMIT * BOARD_LIMIT];
        var argPos = 0;
        for (int row = 0; row < BOARD_LIMIT; row++) {
            for (var col : board.getSpaces()){
                args[argPos++] = " " + (isNull(col.get(row).getActual()) ? " " : col.get(row).getActual());
            }
        }
        System.out.println("Seu jogo se encontra da seguinte forma");
        System.out.printf((BOARD_TEMPLATE) + "\n", args);
    }

    private static void showGameStatus() {
        if (!ensureGameStarted()){
            return;
        }

        final GameStatusEnum status = board.getStatus();
        System.out.printf("O jogo atualmente se encontra no status %s\n", status.getLabel());
        if (status == GameStatusEnum.NON_STARTED){
            System.out.println("Ainda nao existe nenhuma jogada valida aplicada no tabuleiro.");
        }

        if (board.hasErrors()){
            System.out.println("O jogo contem erros");
        } else {
            System.out.println("O jogo nao contem erros");
        }
    }

    private static void clearGame() {
        if (!ensureGameStarted()){
            return;
        }

        System.out.println("Tem certeza que deseja limpar seu jogo e perder todo seu progresso?");
        var confirm = scanner.next();
        while (!confirm.equalsIgnoreCase("sim")
                && !confirm.equalsIgnoreCase("nao")
                && !confirm.equalsIgnoreCase("não")){
            System.out.println("Informe 'sim' ou 'nao'");
            confirm = scanner.next();
        }

        if (confirm.equalsIgnoreCase("sim")){
            board.reset();
        }
    }

    private static void finishGame() {
        if (!ensureGameStarted()){
            return;
        }

        if (board.gameIsFinished()){
            System.out.println("Parabens voce concluiu o jogo");
            showCurrentGame();
            board = null;
        } else if (board.hasErrors()) {
            System.out.println("Seu jogo contem erros, verifique o board e ajuste");
        } else {
            System.out.println("Voce ainda precisa preencher algum espaco");
        }
    }

    private static int runUntilGetValidNumber(final int min, final int max){
        var current = readInt();
        while (current < min || current > max){
            System.out.printf("Informe um numero entre %s e %s\n", min, max);
            current = readInt();
        }
        return current;
    }

    private static int readInt() {
        while (!scanner.hasNextInt()) {
            System.out.println("Entrada invalida, informe um numero inteiro");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static boolean ensureGameStarted() {
        if (isNull(board)){
            System.out.println("O jogo ainda nao foi iniciado");
            return false;
        }

        return true;
    }

    private static void exitGame() {
        if (isNull(board)){
            System.exit(0);
            return;
        }

        System.out.println("Tem certeza que deseja sair e perder o progresso? [Y / N]");
        var confirm = scanner.next();
        while (!confirm.equalsIgnoreCase("y") && !confirm.equalsIgnoreCase("n")) {
            System.out.println("Informe Y para sair ou N para continuar");
            confirm = scanner.next();
        }

        if (confirm.equalsIgnoreCase("y")) {
            System.exit(0);
        }
    }

}
