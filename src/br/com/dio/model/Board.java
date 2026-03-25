package br.com.dio.model;

import java.util.Collection;
import java.util.List;

import static br.com.dio.model.GameStatusEnum.COMPLETE;
import static br.com.dio.model.GameStatusEnum.INCOMPLETE;
import static br.com.dio.model.GameStatusEnum.NON_STARTED;
import static br.com.dio.model.MoveValidationResult.CONFLICT;
import static br.com.dio.model.MoveValidationResult.FIXED_POSITION;
import static br.com.dio.model.MoveValidationResult.INVALID_POSITION;
import static br.com.dio.model.MoveValidationResult.VALID;
import static br.com.dio.util.GameConstants.BOARD_LIMIT;
import static br.com.dio.util.GameConstants.SECTOR_SIZE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Board {

    private final List<List<Space>> spaces;

    public Board(final List<List<Space>> spaces) {
        this.spaces = spaces;
    }

    public List<List<Space>> getSpaces() {
        return spaces;
    }

    public GameStatusEnum getStatus(){
        if (spaces.stream().flatMap(Collection::stream).noneMatch(s -> !s.isFixed() && nonNull(s.getActual()))){
            return NON_STARTED;
        }

        return spaces.stream().flatMap(Collection::stream).anyMatch(s -> isNull(s.getActual())) ? INCOMPLETE : COMPLETE;
    }

    public boolean hasErrors(){
        if(getStatus() == NON_STARTED){
            return false;
        }

        return spaces.stream().flatMap(Collection::stream)
                .anyMatch(s -> nonNull(s.getActual()) && !s.getActual().equals(s.getExpected()));
    }

    public boolean changeValue(final int col, final int row, final int value){
        if (validateMove(col, row, value) != VALID){
            return false;
        }

        spaces.get(col).get(row).setActual(value);
        return true;
    }

    public boolean clearValue(final int col, final int row){
        if (!positionExists(col, row)){
            return false;
        }

        var space = spaces.get(col).get(row);
        if (space.isFixed()){
            return false;
        }

        space.clearSpace();
        return true;
    }

    public void reset(){
        spaces.forEach(c -> c.forEach(Space::clearSpace));
    }

    public boolean gameIsFinished(){
        return !hasErrors() && getStatus().equals(COMPLETE);
    }

    public MoveValidationResult validateMove(final int col, final int row, final int value) {
        if (!positionExists(col, row)){
            return INVALID_POSITION;
        }

        final var space = spaces.get(col).get(row);
        if (space.isFixed()){
            return FIXED_POSITION;
        }

        if (hasConflicts(col, row, value)){
            return CONFLICT;
        }

        return VALID;
    }

    private boolean hasConflicts(final int col, final int row, final int value) {
        return hasConflictInRow(col, row, value)
                || hasConflictInColumn(col, row, value)
                || hasConflictInSector(col, row, value);
    }

    private boolean hasConflictInRow(final int currentCol, final int row, final int value) {
        for (int col = 0; col < BOARD_LIMIT; col++) {
            if (col == currentCol){
                continue;
            }
            final var actual = spaces.get(col).get(row).getActual();
            if (nonNull(actual) && actual == value){
                return true;
            }
        }

        return false;
    }

    private boolean hasConflictInColumn(final int col, final int currentRow, final int value) {
        for (int row = 0; row < BOARD_LIMIT; row++) {
            if (row == currentRow){
                continue;
            }
            final var actual = spaces.get(col).get(row).getActual();
            if (nonNull(actual) && actual == value){
                return true;
            }
        }

        return false;
    }

    private boolean hasConflictInSector(final int currentCol, final int currentRow, final int value) {
        final int initialCol = (currentCol / SECTOR_SIZE) * SECTOR_SIZE;
        final int initialRow = (currentRow / SECTOR_SIZE) * SECTOR_SIZE;

        for (int col = initialCol; col < initialCol + SECTOR_SIZE; col++) {
            for (int row = initialRow; row < initialRow + SECTOR_SIZE; row++) {
                if (col == currentCol && row == currentRow){
                    continue;
                }
                final var actual = spaces.get(col).get(row).getActual();
                if (nonNull(actual) && actual == value){
                    return true;
                }
            }
        }

        return false;
    }

    private boolean positionExists(final int col, final int row) {
        return col >= 0
                && col < spaces.size()
                && row >= 0
                && row < spaces.get(col).size();
    }

}
