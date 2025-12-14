package pages;
import components.SudokuBoard9By9;

import java.util.ArrayList;
import java.util.List;


/**
 * Controller class to manage interactive Sudoku game state and logic like cell selection etc.
 */

public class InteractiveSudokuController {

    private static final int BOARD_SIZE = 9;
    private static final int MAX_MISTAKES = 3;
    private static final int BOX_SIZE = 3;
    private static final int MAX_HINTS = 3;

    private int selected_row;
    private int selected_col;
    private boolean[][] pre_filled_cells;
    private int[][] cell_values;
    private int mistake_count;
    private SudokuBoard9By9 sudoku_board;
    private int elapsed_seconds;
    private boolean timer_started;
    private boolean timer_paused;
    private List<int[]> conflicting_cells;
    private int hints_remaining;
    private int last_move_row = -1;
    private int last_move_col = -1;
    private int last_move_previous_value = 0;
    private boolean has_last_move = false;

    public InteractiveSudokuController() {
        this.selected_row = -1;
        this.selected_col = -1;
        this.pre_filled_cells = new boolean[BOARD_SIZE][BOARD_SIZE];
        this.cell_values = new int[BOARD_SIZE][BOARD_SIZE];
        this.mistake_count = 0;
        this.sudoku_board = null;
        this.elapsed_seconds = 0;
        this.timer_started = false;
        this.conflicting_cells = new ArrayList<>();
        this.hints_remaining = MAX_HINTS;
        this.timer_paused = false;
    }

    public boolean selectCell(int row, int col) {
        if (!isValidPosition(row, col)) {
            return false;
        }
        if (pre_filled_cells[row][col]) {
            return false;
        }
        this.selected_row = row;
        this.selected_col = col;
        return true;
    }

    public void deselectCell() {
        this.selected_row = -1;
        this.selected_col = -1;
    }

    public boolean isCellSelected(int row, int col) {
        return this.selected_row == row && this.selected_col == col;
    }

    public int getSelectedRow() {
        return this.selected_row;
    }

    public int getSelectedCol() {
        return this.selected_col;
    }

    public boolean hasSelection() {
        return this.selected_row != -1 && this.selected_col != -1;
    }

    public void markCellAsPreFilled(int row, int col, boolean is_pre_filled) {
        if (isValidPosition(row, col)) {
            this.pre_filled_cells[row][col] = is_pre_filled;
        }
    }

    public boolean isCellPreFilled(int row, int col) {
        if (!isValidPosition(row, col)) {
            return false;
        }
        return this.pre_filled_cells[row][col];
    }

    public boolean[][] getPre_filled_cells() {
        return pre_filled_cells;
    }

    public void setCellValue(int row, int col, int value) {
        if (isValidPosition(row, col) && value >= 0 && value <= 9) {
            this.cell_values[row][col] = value;
        }
    }

    public int getCellValue(int row, int col) {
        if (!isValidPosition(row, col)) {
            return 0;
        }
        return this.cell_values[row][col];
    }

    public boolean enterNumber(int number) {
        if (!hasSelection()) {
            return false;
        }

        if (number < 1 || number > 9) {
            return false;
        }

        if (pre_filled_cells[selected_row][selected_col]) {
            return false;
        }

        int previous = cell_values[selected_row][selected_col];
        cell_values[selected_row][selected_col] = number;
        recordLastMove(selected_row, selected_col, previous);

        return true;
    }


    public void clearSelectedCell() {
        if (hasSelection() && !pre_filled_cells[selected_row][selected_col]) {
            int previous = cell_values[selected_row][selected_col];
            if (previous != 0) {
                recordLastMove(selected_row, selected_col, previous);
            }
            cell_values[selected_row][selected_col] = 0;
        }
    }

    private void recordLastMove(int row, int col, int previousValue) {
        this.last_move_row = row;
        this.last_move_col = col;
        this.last_move_previous_value = previousValue;
        this.has_last_move = true;
    }
    public int[] undoLastMove() {
        if (!has_last_move) {
            return null;
        }

        cell_values[last_move_row][last_move_col] = last_move_previous_value;
        has_last_move = false;

        return new int[]{last_move_row, last_move_col};
    }
    public int getMistakeCount() {
        return this.mistake_count;
    }

    public void incrementMistakes() {
        this.mistake_count++;
    }

    public void resetMistakes() {
        this.mistake_count = 0;
    }

    public void setMistake_count(int mistake_count){
        this.mistake_count = mistake_count;
    }

    public boolean isGameOver() {
        return this.mistake_count >= MAX_MISTAKES;
    }

    public void setSudokuBoard(SudokuBoard9By9 board) {
        this.sudoku_board = board;
    }

    public boolean isValidPlacement(int row, int col, int number) {
        if (sudoku_board == null) {
            return true;
        }

        return sudoku_board.isValidPlacement(row, col, number);
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    public void initializeBoard(int[][] board) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int value = board[row][col];
                cell_values[row][col] = value;
                pre_filled_cells[row][col] = (value != 0);
            }
        }
    }

    public void resetGame() {
        this.selected_row = -1;
        this.selected_col = -1;
        this.mistake_count = 0;
        this.pre_filled_cells = new boolean[BOARD_SIZE][BOARD_SIZE];
        this.cell_values = new int[BOARD_SIZE][BOARD_SIZE];
        this.elapsed_seconds = 0;
        this.timer_started = false;
        this.hints_remaining = MAX_HINTS;
        this.timer_paused = false;
    }

    public void selectCellForViewing(int row, int col) {
        if (isValidPosition(row, col)) {
            this.selected_row = row;
            this.selected_col = col;
        }
    }

    public int getElapsedSeconds() {
        return this.elapsed_seconds;
    }

    public void incrementTimer() {if (!timer_paused) {this.elapsed_seconds++;}}

    public void startTimer() {
        this.timer_started = true;
    }

    public boolean isTimerStarted() {
        return this.timer_started;
    }

    public void pauseTimer() {
        this.timer_paused = true;
    }

    public boolean isTimerPaused() {
        return this.timer_paused;
    }

    public void resumeTimer() {
        this.timer_paused = false;
    }

    public void resetTimer() {
        this.elapsed_seconds = 0;
        this.timer_started = false;
        this.timer_paused = false;
    }

    public void setTimer(int time) {
        this.elapsed_seconds = time;
        this.timer_started = false;
        this.timer_paused = false;
    }

    public int getHintsRemaining() {
        return this.hints_remaining;
    }

    public void setHintsRemaining(int hints_remaining) {
        this.hints_remaining = hints_remaining;
    }

    public void decrementHints() {
        if (this.hints_remaining > 0) {
            this.hints_remaining--;
        }
    }

    public boolean hasHintsRemaining() {
        return this.hints_remaining > 0;
    }

    public void resetHints() {
        this.hints_remaining = MAX_HINTS;
    }



    public List<int[]> findConflictingCells(int row, int col, int number) {
        List<int[]> conflicts = new ArrayList<>();

        for (int c = 0; c < BOARD_SIZE; c++) {
            if (c != col && cell_values[row][c] == number) {
                conflicts.add(new int[]{row, c});
            }
        }

        for (int r = 0; r < BOARD_SIZE; r++) {
            if (r != row && cell_values[r][col] == number) {
                conflicts.add(new int[]{r, col});
            }
        }

        int box_row = (row / BOX_SIZE) * BOX_SIZE;
        int box_col = (col / BOX_SIZE) * BOX_SIZE;

        for (int r = box_row; r < box_row + BOX_SIZE; r++) {
            for (int c = box_col; c < box_col + BOX_SIZE; c++) {
                if ((r != row || c != col) && cell_values[r][c] == number) {
                    conflicts.add(new int[]{r, c});
                }
            }
        }
        return conflicts;
    }

    public void setConflictingCells(List<int[]> conflicts) {
        this.conflicting_cells = conflicts;
    }

    public List<int[]> getConflictingCells() {
        return this.conflicting_cells;
    }

    public void clearConflictingCells() {
        this.conflicting_cells.clear();
    }
}