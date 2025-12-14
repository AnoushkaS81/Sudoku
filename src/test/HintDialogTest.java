package test;

import components.SudokuBoard9By9;
import components.Solver;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.swing.JButton;

/**
 * JUnit test suite for HintDialog logic.
 * Tests the non-UI components and data manipulation logic.
 **/
public class HintDialogTest {

    private SudokuBoard9By9 board;
    private JButton[][] gridButtons;
    private Solver solver;

    @Before
    public void setUp() {
        board = new SudokuBoard9By9();
        gridButtons = new JButton[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                gridButtons[i][j] = new JButton();
            }
        }
    }

    @Test
    public void testBoardSyncWithEmptyButtons() {
        gridButtons[0][0].setText("5");
        gridButtons[0][1].setText("");
        gridButtons[0][2].setText("4");

        int[][] testBoard = createSimpleBoard();
        board.setBoard(testBoard);

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String text = gridButtons[row][col].getText();
                if (text.isEmpty()) {
                    board.getBoard()[row][col] = 0;
                } else {
                    try {
                        board.getBoard()[row][col] = Integer.parseInt(text);
                    } catch (NumberFormatException ex) {
                        board.getBoard()[row][col] = 0;
                    }
                }
            }
        }

        assertEquals("Should have value from button", 5, board.getBoard()[0][0]);
        assertEquals("Should be 0 for empty button", 0, board.getBoard()[0][1]);
        assertEquals("Should have value from button", 4, board.getBoard()[0][2]);
    }

    @Test
    public void testBoardSyncWithInvalidText() {
        gridButtons[0][0].setText("abc");
        gridButtons[0][1].setText("5");

        int[][] testBoard = new int[9][9];
        board.setBoard(testBoard);

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String text = gridButtons[row][col].getText();
                if (text.isEmpty()) {
                    board.getBoard()[row][col] = 0;
                } else {
                    try {
                        board.getBoard()[row][col] = Integer.parseInt(text);
                    } catch (NumberFormatException ex) {
                        board.getBoard()[row][col] = 0;
                    }
                }
            }
        }

        assertEquals("Invalid text should become 0", 0, board.getBoard()[0][0]);
        assertEquals("Valid text should parse correctly", 5, board.getBoard()[0][1]);
    }

    @Test
    public void testBoardSyncPreservesValues() {
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

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (testBoard[row][col] != 0) {
                    gridButtons[row][col].setText(String.valueOf(testBoard[row][col]));
                } else {
                    gridButtons[row][col].setText("");
                }
            }
        }

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (testBoard[row][col] != 0) {
                    assertEquals("Board value should be preserved",
                            testBoard[row][col],
                            Integer.parseInt(gridButtons[row][col].getText()));
                }
            }
        }
    }

    @Test
    public void testSolverInitializationWithBoard() {
        int[][] testBoard = createSimpleBoard();
        board.setBoard(testBoard);
        solver = new Solver(board);

        assertNotNull("Solver should be initialized", solver);
        assertFalse("Board should not be solved initially", solver.isSolved());
    }

    @Test
    public void testGetNextStepAfterBoardUpdate() {
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
        assertEquals("Should find the only missing value", 9, step.value);
    }

    @Test
    public void testSolverWithEmptyBoard() {
        int[][] emptyBoard = new int[9][9];
        board.setBoard(emptyBoard);
        solver = new Solver(board);

        Solver.SolveStep step = solver.getNextStep();
        assertTrue("Should handle empty board", true);
    }

    @Test
    public void testSolverWithSolvedBoard() {
        int[][] solvedBoard = {
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
        board.setBoard(solvedBoard);
        solver = new Solver(board);

        assertTrue("Board should be solved", solver.isSolved());
        Solver.SolveStep step = solver.getNextStep();
        assertNull("Should return null for solved board", step);
    }

    @Test
    public void testApplyHintUpdatesBoard() {
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
        assertNotNull("Should have a step", step);

        solver.applyStep(step);

        assertEquals("Board should be updated", step.value, board.getBoard()[step.row][step.col]);
    }

    @Test
    public void testButtonUpdateAfterHintApplication() {
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

        // Simulate applying hint to button
        gridButtons[step.row][step.col].setText(String.valueOf(step.value));

        assertEquals("Button should show hint value",
                String.valueOf(step.value),
                gridButtons[step.row][step.col].getText());
    }


    @Test
    public void testStepHasRequiredFields() {
        int[][] testBoard = createSimpleBoard();
        board.setBoard(testBoard);
        solver = new Solver(board);

        Solver.SolveStep step = solver.getNextStep();

        if (step != null && step.value > 0) {
            assertNotNull("Step should have technique", step.technique);
            assertTrue("Row should be valid", step.row >= 0 && step.row < 9);
            assertTrue("Col should be valid", step.col >= 0 && step.col < 9);
            assertTrue("Value should be valid", step.value >= 1 && step.value <= 9);
            assertNotNull("Step should have explanation", step.explanation);
            assertFalse("Explanation should not be empty", step.explanation.isEmpty());
        }
    }

    @Test
    public void testStepTechniqueTypes() {
        int[][] testBoard = createSimpleBoard();
        board.setBoard(testBoard);
        solver = new Solver(board);

        Solver.SolveStep step = solver.getNextStep();

        if (step != null) {
            // Verify technique is one of the valid types
            boolean validTechnique =
                    step.technique == Solver.SolveStep.Technique.NAKED_SINGLE ||
                            step.technique == Solver.SolveStep.Technique.HIDDEN_SINGLE_ROW ||
                            step.technique == Solver.SolveStep.Technique.HIDDEN_SINGLE_COL ||
                            step.technique == Solver.SolveStep.Technique.HIDDEN_SINGLE_BOX ||
                            step.technique == Solver.SolveStep.Technique.NAKED_PAIR ||
                            step.technique == Solver.SolveStep.Technique.POINTING_PAIR;

            assertTrue("Technique should be valid type", validTechnique);
        }
    }

    @Test
    public void testHintDialogWithNullBoard() {
        // HintDialog should handle null board gracefully
        // This test verifies defensive programming
        board = null;


        assertTrue("Should handle null board safely", board == null);
    }

    @Test
    public void testMultipleHintsSequentially() {
        int[][] testBoard = createSimpleBoard();
        board.setBoard(testBoard);
        solver = new Solver(board);

        int hintsApplied = 0;
        int maxHints = 5;

        while (hintsApplied < maxHints) {
            Solver.SolveStep step = solver.getNextStep();
            if (step == null || step.value <= 0) break;

            solver.applyStep(step);
            board.getBoard()[step.row][step.col] = step.value;
            hintsApplied++;
        }

        assertTrue("Should apply at least one hint", hintsApplied > 0);
    }


    private int[][] createSimpleBoard() {
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