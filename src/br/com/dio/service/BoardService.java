package br.com.dio.service;

import br.com.dio.model.Board;
import br.com.dio.model.GameStatusEnum;
import br.com.dio.model.Space;

import java.util.List;
import java.util.Map;

import static br.com.dio.util.BoardFactory.createBoard;

public class BoardService {

    private final Board board;

    public BoardService(final Map<String, String> gameConfig) {
        this.board = createBoard(gameConfig);
    }

    public List<List<Space>> getSpaces(){
        return board.getSpaces();
    }

    public void reset(){
        board.reset();
    }

    public boolean hasErrors(){
        return board.hasErrors();
    }

    public GameStatusEnum getStatus(){
        return board.getStatus();
    }

    public boolean gameIsFinished(){
        return board.gameIsFinished();
    }
}
