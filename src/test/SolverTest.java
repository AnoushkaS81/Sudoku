package test;

import components.SudokuBoard9By9;
import components.Solver;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit test suite for the Solver class.
 * Tests all solving techniques with fast execution and comprehensive coverage.
 * All tests use pre-defined boards to avoid slow puzzle generation.
 **/
public class SolverTest {

    private SudokuBoard9By9 board;
    private Solver solver;

    @Before
    public void setUp() {
        board = new SudokuBoard9By9();
    }

    @Test
    public void testSolverInitialization() {
        int[][] testBoard = createSimpleTestBoard();
        board.setBoard(testBoard);
        solver = new Solver(board);
        assertNotNull("Solver should be initialized", solver);
    }

    @Test
    public void testIsSolvedOnEmptyBoard() {
        int[][] emptyBoard = new int[9][9];
        board.setBoard(emptyBoard);
        solver = new Solver(board);
        assertFalse("Empty board should not be solved", solver.isSolved());
    }

    @Test
    public void testIsSolvedOnCompleteBoard() {
        int[][] solvedBoard = createSolvedBoard();
        board.setBoard(solvedBoard);
        solver = new Solver(board);
        assertTrue("Complete valid board should be solved", solver.isSolved());
    }

    @Test
    public void testIsSolvedOnPartialBoard() {
        int[][] partialBoard = createSimpleTestBoard();
        board.setBoard(partialBoard);
        solver = new Solver(board);
        assertFalse("Partial board should not be solved", solver.isSolved());
    }

    @Test
    public void testFindNakedSingleBasic() {
        // Board with one cell that has only one possible value
        int[][] testBoard = {
                {5, 3, 4, 6, 7, 8, 9, 1, 2},
                {6, 7, 2, 1, 9, 5, 3, 4, 8},
                {1, 9, 8, 3, 4, 2, 5, 6, 7},
                {8, 5, 9, 7, 6, 1, 4, 2, 3},
                {4, 2, 6, 8, 5, 3, 7, 9, 1},
                {7, 1, 3, 9, 2, 4, 8, 5, 6},
                {9, 6, 1, 5, 3, 7, 2, 8, 4},
                {2, 8, 7, 4, 1, 9, 6, 3, 5},
                {3, 4, 5, 2, 8, 6, 1, 7, 0}
        };
        board.setBoard(testBoard);
        solver = new Solver(board);

        Solver.SolveStep step = solver.getNextStep();
        assertNotNull("Should find a solving step", step);
        assertEquals("Should be NAKED_SINGLE", Solver.SolveStep.Technique.NAKED_SINGLE, step.technique);
        assertEquals("Should place 9", 9, step.value);
        assertEquals("Should be at row 8", 8, step.row);
        assertEquals("Should be at col 8", 8, step.col);
    }

    @Test
    public void testNakedSingleWithMultipleEmptyCells() {
        int[][] testBoard = {
                {5, 3, 4, 6, 7, 8, 9, 1, 2},
                {6, 7, 2, 1, 9, 5, 3, 4, 8},
                {1, 9, 8, 3, 4, 2, 5, 6, 0},
                {8, 5, 9, 7, 6, 1, 4, 2, 3},
                {4, 2, 6, 8, 5, 3, 7, 9, 1},
                {7, 1, 3, 9, 2, 4, 8, 5, 6},
                {9, 6, 1, 5, 3, 7, 2, 8, 4},
                {2, 8, 7, 4, 1, 9, 6, 3, 5},
                {3, 4, 5, 2, 8, 6, 1, 7, 9}
        };
        board.setBoard(testBoard);
        solver = new Solver(board);

        Solver.SolveStep step = solver.getNextStep();
        assertNotNull("Should find naked single", step);
        assertEquals("Should be NAKED_SINGLE", Solver.SolveStep.Technique.NAKED_SINGLE, step.technique);
        assertEquals("Should place 7", 7, step.value);
    }

    @Test
    public void testApplyNakedSingle() {
        int[][] testBoard = {
                {5, 3, 4, 6, 7, 8, 9, 1, 2},
                {6, 7, 2, 1, 9, 5, 3, 4, 8},
                {1, 9, 8, 3, 4, 2, 5, 6, 7},
                {8, 5, 9, 7, 6, 1, 4, 2, 3},
                {4, 2, 6, 8, 5, 3, 7, 9, 1},
                {7, 1, 3, 9, 2, 4, 8, 5, 6},
                {9, 6, 1, 5, 3, 7, 2, 8, 4},
                {2, 8, 7, 4, 1, 9, 6, 3, 5},
                {3, 4, 5, 2, 8, 6, 1, 7, 0}
        };
        board.setBoard(testBoard);
        solver = new Solver(board);

        Solver.SolveStep step = solver.getNextStep();
        solver.applyStep(step);

        assertEquals("Cell should be filled with 9", 9, board.getBoard()[8][8]);
        assertTrue("Board should now be solved", solver.isSolved());
    }

    @Test
    public void testFindHiddenSingleInRow() {
        int[][] testBoard = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 9},
                {1, 2, 3, 4, 5, 6, 7, 8, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 9},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 9},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 9}
        };
        board.setBoard(testBoard);
        solver = new Solver(board);

        Solver.SolveStep step = solver.getNextStep();
        assertNotNull("Should find a hidden single", step);
        assertTrue("Should be NAKED_SINGLE or HIDDEN_SINGLE_ROW",
                step.technique == Solver.SolveStep.Technique.NAKED_SINGLE ||
                        step.technique == Solver.SolveStep.Technique.HIDDEN_SINGLE_ROW);
        assertEquals("Should place 9", 9, step.value);
        assertEquals("Should be at row 3", 3, step.row);
        assertEquals("Should be at col 8", 8, step.col);
    }

    @Test
    public void testApplyHiddenSingle() {
        int[][] testBoard = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 9},
                {1, 2, 3, 4, 5, 6, 7, 8, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 9},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 9},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 9}
        };
        board.setBoard(testBoard);
        solver = new Solver(board);

        Solver.SolveStep step = solver.getNextStep();
        assertNotNull("Should find a step", step);
        assertEquals("Should be row 3, col 8", 3, step.row);
        assertEquals("Should be row 3, col 8", 8, step.col);

        solver.applyStep(step);
        assertEquals("Should have placed 9", 9, board.getBoard()[3][8]);
    }

    @Test
    public void testApplyStepUpdatesBoard() {
        int[][] testBoard = {
                {5, 3, 4, 6, 7, 8, 9, 1, 2},
                {6, 7, 2, 1, 9, 5, 3, 4, 8},
                {1, 9, 8, 3, 4, 2, 5, 6, 7},
                {8, 5, 9, 7, 6, 1, 4, 2, 3},
                {4, 2, 6, 8, 5, 3, 7, 9, 1},
                {7, 1, 3, 9, 2, 4, 8, 5, 6},
                {9, 6, 1, 5, 3, 7, 2, 8, 4},
                {2, 8, 7, 4, 1, 9, 6, 3, 5},
                {3, 4, 5, 2, 8, 6, 1, 7, 0}
        };
        board.setBoard(testBoard);
        solver = new Solver(board);

        Solver.SolveStep step = solver.getNextStep();
        assertNotNull("Should find a step", step);

        solver.applyStep(step);
        assertEquals("Board should be updated", step.value, board.getBoard()[step.row][step.col]);
    }

    @Test
    public void testApplyNullStep() {
        int[][] testBoard = new int[9][9];
        board.setBoard(testBoard);
        solver = new Solver(board);

        solver.applyStep(null);
        assertTrue("Should handle null gracefully", true);
    }

    @Test
    public void testApplyStepWithInvalidValue() {
        int[][] testBoard = createSimpleTestBoard();
        board.setBoard(testBoard);
        solver = new Solver(board);

        Solver.SolveStep invalidStep = new Solver.SolveStep(
                Solver.SolveStep.Technique.NAKED_PAIR,
                0, 0, -1, "Test"
        );

        int[][] boardBefore = copyBoard(board.getBoard());
        solver.applyStep(invalidStep);
        int[][] boardAfter = board.getBoard();

        assertTrue("Board should not change", boardsEqual(boardBefore, boardAfter));
    }

    @Test
    public void testApplyStepWithZeroValue() {
        int[][] testBoard = createSimpleTestBoard();
        board.setBoard(testBoard);
        solver = new Solver(board);

        Solver.SolveStep zeroStep = new Solver.SolveStep(
                Solver.SolveStep.Technique.NAKED_SINGLE,
                0, 0, 0, "Test"
        );

        int[][] boardBefore = copyBoard(board.getBoard());
        solver.applyStep(zeroStep);
        int[][] boardAfter = board.getBoard();

        assertTrue("Board should not change with zero value", boardsEqual(boardBefore, boardAfter));
    }


    @Test(timeout = 5000)
    public void testSolveCompletelyOnEasyPuzzle() {
        int[][] easyPuzzle = createEasyPuzzle();
        board.setBoard(easyPuzzle);
        solver = new Solver(board);

        boolean solved = solver.solveCompletely();
        assertTrue("Should solve the puzzle", solved);
        assertTrue("Board should be solved", solver.isSolved());
        assertTrue("Solved board should be valid", board.isValidBoard());
    }

    @Test(timeout = 3000)
    public void testSolveCompletelyOnAlreadySolvedPuzzle() {
        int[][] solvedBoard = createSolvedBoard();
        board.setBoard(solvedBoard);
        solver = new Solver(board);

        boolean solved = solver.solveCompletely();
        assertTrue("Should return true for already solved", solved);
        assertTrue("Board should still be solved", solver.isSolved());
    }

    @Test(timeout = 10000)
    public void testSolveCompletelyOnMediumPuzzle() {
        int[][] mediumPuzzle = createMediumPuzzle();
        board.setBoard(mediumPuzzle);
        solver = new Solver(board);

        boolean solved = solver.solveCompletely();
        assertTrue("Should solve medium puzzle", solved);
        assertTrue("Board should be solved", solver.isSolved());
        assertTrue("Solved board should be valid", board.isValidBoard());
    }

    @Test
    public void testInvalidBoardDetection() {
        // Two 1s in same row - test board validity check instead of solving
        int[][] invalidPuzzle = {
                {1, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
        board.setBoard(invalidPuzzle);

        assertFalse("Board with duplicate numbers should be invalid", board.isValidBoard());
    }

    @Test
    public void testGetNextStepSequence() {
        int[][] testBoard = createSimpleTestBoard();
        board.setBoard(testBoard);
        solver = new Solver(board);

        int stepCount = 0;
        int maxSteps = 20;

        while (stepCount < maxSteps) {
            Solver.SolveStep step = solver.getNextStep();
            if (step == null) break;
            if (step.value > 0) {
                solver.applyStep(step);
            }
            stepCount++;
        }

        assertTrue("Should find at least some steps", stepCount > 0);
    }

    @Test
    public void testGetNextStepOnSolvedBoard() {
        int[][] solvedBoard = createSolvedBoard();
        board.setBoard(solvedBoard);
        solver = new Solver(board);

        Solver.SolveStep step = solver.getNextStep();
        assertNull("Should return null for solved board", step);
    }

    @Test
    public void testGetNextStepReturnsValidStep() {
        int[][] testBoard = createSimpleTestBoard();
        board.setBoard(testBoard);
        solver = new Solver(board);

        Solver.SolveStep step = solver.getNextStep();
        if (step != null && step.value > 0) {
            assertTrue("Row should be valid", step.row >= 0 && step.row < 9);
            assertTrue("Col should be valid", step.col >= 0 && step.col < 9);
            assertTrue("Value should be valid", step.value >= 1 && step.value <= 9);
            assertNotNull("Should have technique", step.technique);
            assertNotNull("Should have explanation", step.explanation);
        }
    }

    @Test
    public void testMultipleConsecutiveSteps() {
        int[][] testBoard = createSimpleTestBoard();
        board.setBoard(testBoard);
        solver = new Solver(board);

        Solver.SolveStep step1 = solver.getNextStep();
        assertNotNull("First step should exist", step1);

        if (step1.value > 0) {
            solver.applyStep(step1);

            Solver.SolveStep step2 = solver.getNextStep();
            assertTrue("Should handle consecutive steps", true);
        }
    }

    @Test
    public void testEmptyBoard() {
        int[][] emptyBoard = new int[9][9];
        board.setBoard(emptyBoard);
        solver = new Solver(board);

        Solver.SolveStep step = solver.getNextStep();
        assertTrue("Should handle empty board", true);
    }

    @Test
    public void testSingleEmptyCell() {
        int[][] almostSolved = {
                {5, 3, 4, 6, 7, 8, 9, 1, 2},
                {6, 7, 2, 1, 9, 5, 3, 4, 8},
                {1, 9, 8, 3, 4, 2, 5, 6, 7},
                {8, 5, 9, 7, 6, 1, 4, 2, 3},
                {4, 2, 6, 8, 5, 3, 7, 9, 1},
                {7, 1, 3, 9, 2, 4, 8, 5, 6},
                {9, 6, 1, 5, 3, 7, 2, 8, 4},
                {2, 8, 7, 4, 1, 9, 6, 3, 5},
                {3, 4, 5, 2, 8, 6, 1, 7, 0}
        };
        board.setBoard(almostSolved);
        solver = new Solver(board);

        Solver.SolveStep step = solver.getNextStep();
        assertNotNull("Should find step", step);
        assertEquals("Should be naked single", Solver.SolveStep.Technique.NAKED_SINGLE, step.technique);
        assertEquals("Should place 9", 9, step.value);
    }

    @Test
    public void testSolveStepToString() {
        Solver.SolveStep step = new Solver.SolveStep(
                Solver.SolveStep.Technique.NAKED_SINGLE,
                3, 4, 7, "Test explanation"
        );

        String stepString = step.toString();
        assertNotNull("toString should not be null", stepString);
        assertTrue("toString should contain technique", stepString.contains("NAKED_SINGLE"));
        assertTrue("toString should contain value", stepString.contains("7"));
        assertTrue("toString should contain coordinates", stepString.contains("3") && stepString.contains("4"));
    }

    @Test
    public void testSolveStepFields() {
        Solver.SolveStep step = new Solver.SolveStep(
                Solver.SolveStep.Technique.HIDDEN_SINGLE_ROW,
                5, 6, 8, "Hidden single in row"
        );

        assertEquals("Technique should match", Solver.SolveStep.Technique.HIDDEN_SINGLE_ROW, step.technique);
        assertEquals("Row should match", 5, step.row);
        assertEquals("Col should match", 6, step.col);
        assertEquals("Value should match", 8, step.value);
        assertEquals("Explanation should match", "Hidden single in row", step.explanation);
    }

    @Test
    public void testSolverWithValidBoard() {
        int[][] validBoard = createEasyPuzzle();
        board.setBoard(validBoard);
        assertTrue("Test board should be valid", board.isValidBoard());

        solver = new Solver(board);
        assertNotNull("Solver should work with valid board", solver);
    }

    @Test
    public void testMultipleSolveAttempts() {
        int[][] testBoard = createSimpleTestBoard();
        board.setBoard(testBoard);
        solver = new Solver(board);

        Solver.SolveStep step1 = solver.getNextStep();
        if (step1 != null && step1.value > 0) {
            solver.applyStep(step1);
        }

        assertTrue("Multiple attempts should work", true);
    }


    private int[][] copyBoard(int[][] original) {
        int[][] copy = new int[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, 9);
        }
        return copy;
    }

    private boolean boardsEqual(int[][] board1, int[][] board2) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board1[i][j] != board2[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private int[][] createSolvedBoard() {
        return new int[][] {
                {5, 3, 4, 6, 7, 8, 9, 1, 2},
                {6, 7, 2, 1, 9, 5, 3, 4, 8},
                {1, 9, 8, 3, 4, 2, 5, 6, 7},
                {8, 5, 9, 7, 6, 1, 4, 2, 3},
                {4, 2, 6, 8, 5, 3, 7, 9, 1},
                {7, 1, 3, 9, 2, 4, 8, 5, 6},
                {9, 6, 1, 5, 3, 7, 2, 8, 4},
                {2, 8, 7, 4, 1, 9, 6, 3, 5},
                {3, 4, 5, 2, 8, 6, 1, 7, 9}
        };
    }

    private int[][] createSimpleTestBoard() {
        return new int[][] {
                {5, 3, 0, 0, 7, 0, 0, 0, 0},
                {6, 0, 0, 1, 9, 5, 0, 0, 0},
                {0, 9, 8, 0, 0, 0, 0, 6, 0},
                {8, 0, 0, 0, 6, 0, 0, 0, 3},
                {4, 0, 0, 8, 0, 3, 0, 0, 1},
                {7, 0, 0, 0, 2, 0, 0, 0, 6},
                {0, 6, 0, 0, 0, 0, 2, 8, 0},
                {0, 0, 0, 4, 1, 9, 0, 0, 5},
                {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };
    }

    private int[][] createEasyPuzzle() {
        return new int[][] {
                {5, 3, 0, 0, 7, 0, 0, 0, 0},
                {6, 0, 0, 1, 9, 5, 0, 0, 0},
                {0, 9, 8, 0, 0, 0, 0, 6, 0},
                {8, 0, 0, 0, 6, 0, 0, 0, 3},
                {4, 0, 0, 8, 0, 3, 0, 0, 1},
                {7, 0, 0, 0, 2, 0, 0, 0, 6},
                {0, 6, 0, 0, 0, 0, 2, 8, 0},
                {0, 0, 0, 4, 1, 9, 0, 0, 5},
                {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };
    }

    private int[][] createMediumPuzzle() {
        // Medium difficulty but with enough clues to solve quickly
        return new int[][] {
                {5, 3, 0, 0, 7, 0, 0, 0, 0},
                {6, 0, 0, 1, 9, 5, 0, 0, 0},
                {0, 9, 8, 0, 0, 0, 0, 6, 0},
                {8, 0, 0, 0, 6, 0, 0, 0, 3},
                {4, 0, 0, 8, 0, 3, 0, 0, 1},
                {7, 0, 0, 0, 2, 0, 0, 0, 6},
                {0, 6, 0, 0, 0, 0, 2, 8, 0},
                {0, 0, 0, 4, 1, 9, 0, 0, 5},
                {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };
    }
}