package components;

public class PuzzleDifficultyRater {
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
    private static final int BOARD_SIZE = 9;
    private static final int SAFETY_LIMIT = 5000;
    private static class LogicalSolveResult {
        final int steps;
        final boolean solved;

        LogicalSolveResult(int steps, boolean solved) {
            this.steps = steps;
            this.solved = solved;
        }
    }

    public static int countClues(SudokuBoard9By9 board) {
        if (board == null) {
            return 0;
        }

        int[][] grid = board.getBoard();
        int count = 0;

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (grid[row][col] != 0) {
                    count++;
                }
            }
        }
        return count;
    }

    public static int[][] copyBoardArray(int[][] source) {
        int[][] copy = new int[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            System.arraycopy(source[row], 0, copy[row], 0, BOARD_SIZE);
        }
        return copy;
    }

    private static LogicalSolveResult runLogicalSolver(SudokuBoard9By9 board) {
        SudokuBoard9By9 workingBoard = new SudokuBoard9By9();
        workingBoard.setBoard(copyBoardArray(board.getBoard()));
        Solver solver = new Solver(workingBoard);
        Solver.SolveStep step;
        int steps = 0;
        while ((step = solver.getNextStep()) != null) {
            steps++;
            solver.applyStep(step);
            if (steps > SAFETY_LIMIT) {
                break;
            }
        }
        return new LogicalSolveResult(steps, solver.isSolved());
    }

    public static int getLogicalStepCount(SudokuBoard9By9 board) {
        if (board == null) {
            return 0;
        }
        LogicalSolveResult result = runLogicalSolver(board);
        return result.steps;
    }

    public static boolean needsBacktracking(SudokuBoard9By9 board) {
        if (board == null) {
            return false;
        }
        LogicalSolveResult result = runLogicalSolver(board);
        return !result.solved;
    }

    public static Difficulty classify(int clues, int logicalSteps, boolean neededBacktracking) {
        if (clues >= 36 && !neededBacktracking && logicalSteps < 15) {
            return Difficulty.EASY;
        }
        if (clues >= 32 && !neededBacktracking) {
            return Difficulty.MEDIUM;
        }
        return Difficulty.HARD;
    }

    public static Difficulty ratePuzzle(SudokuBoard9By9 board) {
        if (board == null) {
            return Difficulty.HARD;
        }
        int clues = countClues(board);
        int steps = getLogicalStepCount(board);
        boolean backtracking = needsBacktracking(board);

        return classify(clues, steps, backtracking);
    }
}
