package components;

public class SudokuBoard9By9 {
    private static final int BOARD_SIZE = 9;
    private static final int BOX_SIZE = 3;

    private int[][] board;
    private int number_of_solutions_found;

    public SudokuBoard9By9(){
        resetSudokuBoard();
    }

    public int[][] getBoard(){
        return board;
    }

    public void setBoard(int[][] board){
        this.board = board;
    }

    public void resetSudokuBoard(){
        this.board = new int[BOARD_SIZE][BOARD_SIZE];
    }

    public int getNumberOfEmptySpaces(){
        int num_empty_spaces = 0;
        for(int[] x_board_array : this.board){ for(int piece : x_board_array){
                if(piece == 0){num_empty_spaces += 1;}}}
        return num_empty_spaces;
    }

    public int[][] getSudokuBoardEmptySpaces(){
        int[][] empty_spaces_locations = new int[getNumberOfEmptySpaces()][2];
        int index = 0;
        for(int yAxis = 0; yAxis < getBoard().length; yAxis++){ for(int xAxis = 0; xAxis < this.board[yAxis].length; xAxis++){
                if(this.board[yAxis][xAxis] == 0){
                    empty_spaces_locations[index] = new int[] {yAxis, xAxis}; index ++;
                }
        }}
        return empty_spaces_locations;
    }

    public boolean isValidInRow(int row, int num) {
        for (int col = 0; col < BOARD_SIZE; col++) {
            if (board[row][col] == num) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidInColumn(int col, int num) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            if (board[row][col] == num) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidInBox(int row, int col, int num) {
        int boxStartRow = (row / BOX_SIZE) * BOX_SIZE;
        int boxStartCol = (col / BOX_SIZE) * BOX_SIZE;

        for (int r = boxStartRow; r < boxStartRow + BOX_SIZE; r++) {
            for (int c = boxStartCol; c < boxStartCol + BOX_SIZE; c++) {
                if (board[r][c] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isValidPlacement(int row, int col, int num) {
        return isValidInRow(row, num) &&
                isValidInColumn(col, num) &&
                isValidInBox(row, col, num);
    }

    public int[] getSudokuPossibleMoves(int row, int col) {
        boolean[] possible = new boolean[BOARD_SIZE + 1];
        int count = 0;

        for (int num = 1; num <= BOARD_SIZE; num++) {
            if (isValidPlacement(row, col, num)) {
                possible[num] = true;
                count++;
            }
        }

        int[] result = new int[count];
        int index = 0;
        for (int num = 1; num <= BOARD_SIZE; num++) {
            if (possible[num]) {
                result[index++] = num;
            }
        }

        return result;
    }

    public boolean generateCompleteGrid() {
        return fillGrid(0, 0);
    }

    private boolean fillGrid(int row, int col) {
        if (col == BOARD_SIZE) {
            row++;
            col = 0;
        }
        if (row == BOARD_SIZE) {
            return true;
        }
        if (board[row][col] != 0) {
            return fillGrid(row, col + 1);
        }
        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        shuffleArray(numbers);
        for (int num : numbers) {
            if (isValidPlacement(row, col, num)) {
                board[row][col] = num;

                if (fillGrid(row, col + 1)) {
                    return true;
                }
                board[row][col] = 0;
            }
        }
        return false;
    }

    private void shuffleArray(int[] array) {
        java.util.Random rand = new java.util.Random();
        for (int i = array.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }
    private Difficulty boardDifficulty;

    public void generatePuzzle(Difficulty difficulty) {
        int clues = switch (difficulty) {
            case EASY -> getRandomInRange(36, 49);
            case MEDIUM -> getRandomInRange(32, 35);
            case HARD -> getRandomInRange(22, 31);
        };
        int cells_to_remove = (BOARD_SIZE * BOARD_SIZE) - clues;
        generateUniquePuzzle(cells_to_remove);
        boardDifficulty = difficulty;
    }

    public Difficulty getBoardDifficulty() {return this.boardDifficulty;}

    private int getRandomInRange(int min, int max) {
        java.util.Random rand = new java.util.Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public void generateUniquePuzzle(int cells_to_remove) {
        resetSudokuBoard();
        generateCompleteGrid();
        java.util.Random rand = new java.util.Random();
        while (cells_to_remove > 0) {
            int row = rand.nextInt(BOARD_SIZE);
            int col = rand.nextInt(BOARD_SIZE);
            if (board[row][col] == 0) continue;
            int backup = board[row][col];
            board[row][col] = 0;
            if (!hasUniqueSolution()) {
                board[row][col] = backup;
            } else {
                cells_to_remove--;
            }
        }
    }

    public boolean hasUniqueSolution() {
        number_of_solutions_found = 0;
        int[][] boardCopy = copyOfBoard(board);
        solveCount(boardCopy);
        return number_of_solutions_found == 1;
    }

    private void solveCount(int[][] b) {
        if (number_of_solutions_found > 1) return;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (b[row][col] == 0) {
                    for (int num = 1; num <= BOARD_SIZE; num++) {
                        if (isValidPlacementFor(b, row, col, num)) {
                            b[row][col] = num;
                            solveCount(b);
                            b[row][col] = 0;
                        }
                    }
                    return;
                }
            }
        }
        number_of_solutions_found++;
    }

    private boolean isValidPlacementFor(int[][] b, int row, int col, int num) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (b[row][i] == num || b[i][col] == num) return false;
        }
        int box_start_row = (row / BOX_SIZE) * BOX_SIZE;
        int box_start_col = (col / BOX_SIZE) * BOX_SIZE;
        for (int r = box_start_row; r < box_start_row + BOX_SIZE; r++) {
            for (int c = box_start_col; c < box_start_col + BOX_SIZE; c++) {
                if (b[r][c] == num) return false;
            }
        }
        return true;
    }

    private int[][] copyOfBoard(int[][] original_board) {
        int[][] copy = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(original_board[i], 0, copy[i], 0, BOARD_SIZE);
        }
        return copy;
    }

    public boolean isValidBoard() {
        for (int i = 0; i < 9; i++) {
            boolean[] numberAlreadyInRow = new boolean[10];
            boolean[] numberAlreadyInColumn = new boolean[10];
            for (int j = 0; j < 9; j++) {
                int rowValue = board[i][j];
                int colValue = board[j][i];
                if (rowValue != 0) {
                    if (numberAlreadyInRow[rowValue]) {
                        return false;
                    }
                    numberAlreadyInRow[rowValue] = true;
                }
                if (colValue != 0) {
                    if (numberAlreadyInColumn[colValue]) {
                        return false;
                    }
                    numberAlreadyInColumn[colValue] = true;
                }
            }
        }
        for (int boxRow = 0; boxRow < 9; boxRow += 3) {
            for (int boxCol = 0; boxCol < 9; boxCol += 3) {
                boolean[] numberAlreadyInBox = new boolean[10];
                for (int r = 0; r < 3; r++) {
                    for (int c = 0; c < 3; c++) {
                        int value = board[boxRow + r][boxCol + c];
                        if (value != 0) {
                            if (numberAlreadyInBox[value]) {
                                return false;
                            }
                            numberAlreadyInBox[value] = true;
                        }
                    }
                }
            }
        }
        return true;
    }
}
