package components;

import pages.InteractiveSudokuController;
import utils.CommonConstants;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the solving functionality for Sudoku puzzles with animation and victory display
 */
public class SolveHandler {

    private JFrame parentFrame;
    private InteractiveSudokuController controller;
    private SudokuBoard9By9 board;
    private JButton[][] gridButtons;
    private VictoryDialog victoryDialog;
    private JLabel timerLabel;
    private Timer gameTimer;
    private JButton pauseResumeButton;

    public SolveHandler(JFrame parentFrame, InteractiveSudokuController controller,
                        SudokuBoard9By9 board, JButton[][] gridButtons,
                        VictoryDialog victoryDialog, JLabel timerLabel,
                        Timer gameTimer, JButton pauseResumeButton) {
        this.parentFrame = parentFrame;
        this.controller = controller;
        this.board = board;
        this.gridButtons = gridButtons;
        this.victoryDialog = victoryDialog;
        this.timerLabel = timerLabel;
        this.gameTimer = gameTimer;
        this.pauseResumeButton = pauseResumeButton;
    }

    /**
     * Handle the solve button click - solves the puzzle with animation and shows victory
     */
    public void handleSolve() {
        if (board == null || gridButtons == null) {
            JOptionPane.showMessageDialog(parentFrame,
                    "No active puzzle to solve!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Pause the timer during solving
        boolean wasTimerRunning = controller.isTimerStarted() && !controller.isTimerPaused();
        if (wasTimerRunning) {
            controller.pauseTimer();
            pauseResumeButton.repaint();
        }

        // Confirm with user
        int choice = JOptionPane.showConfirmDialog(parentFrame,
                "Are you sure you want to solve the puzzle?\nThis will fill in all remaining cells.",
                "Solve Puzzle",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            if (wasTimerRunning) {
                controller.resumeTimer();
                pauseResumeButton.repaint();
            }
            return;
        }

        // Disable all input during solving
        setGridButtonsEnabled(false);

        // Create a background worker to solve the puzzle
        SwingWorker<Boolean, int[]> worker = new SwingWorker<Boolean, int[]>() {
            private List<int[]> emptyCells;
            private int[][] solvedBoard;

            @Override
            protected Boolean doInBackground() throws Exception {
                // Find all empty cells before solving
                emptyCells = new ArrayList<>();
                for (int row = 0; row < 9; row++) {
                    for (int col = 0; col < 9; col++) {
                        if (board.getBoard()[row][col] == 0) {
                            emptyCells.add(new int[]{row, col});
                        }
                    }
                }

                // Create a copy of the board for solving
                SudokuBoard9By9 solverBoard = new SudokuBoard9By9();
                int[][] boardCopy = new int[9][9];
                for (int row = 0; row < 9; row++) {
                    System.arraycopy(board.getBoard()[row], 0, boardCopy[row], 0, 9);
                }
                solverBoard.setBoard(boardCopy);

                // Solve using the Solver's solveCompletely method
                Solver solver = new Solver(solverBoard);
                boolean solved = solver.solveCompletely();

                if (solved) {
                    solvedBoard = solverBoard.getBoard();
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    Boolean solved = get();

                    if (!solved) {
                        JOptionPane.showMessageDialog(parentFrame,
                                "Unable to solve this puzzle. It may be invalid.",
                                "Solving Failed",
                                JOptionPane.ERROR_MESSAGE);
                        setGridButtonsEnabled(true);
                        if (wasTimerRunning) {
                            controller.resumeTimer();
                            pauseResumeButton.repaint();
                        }
                        return;
                    }

                    // Animate filling the board
                    animateSolution(emptyCells, solvedBoard, wasTimerRunning);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame,
                            "An error occurred while solving: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    setGridButtonsEnabled(true);
                    if (wasTimerRunning) {
                        controller.resumeTimer();
                        pauseResumeButton.repaint();
                    }
                }
            }
        };

        worker.execute();
    }

    /**
     * Animate the solution filling in the grid
     */
    private void animateSolution(List<int[]> emptyCells, int[][] solvedBoard, boolean wasTimerRunning) {
        if (emptyCells.isEmpty()) {
            finishSolving(wasTimerRunning);
            return;
        }

        // Animation timer - fills one cell every 50ms
        final int[] currentIndex = {0};
        Timer animationTimer = new Timer(50, null);

        animationTimer.addActionListener(e -> {
            if (currentIndex[0] >= emptyCells.size()) {
                animationTimer.stop();
                finishSolving(wasTimerRunning);
                return;
            }

            // Fill the next empty cell
            int[] cell = emptyCells.get(currentIndex[0]);
            int row = cell[0];
            int col = cell[1];
            int value = solvedBoard[row][col];

            // Update the grid button
            gridButtons[row][col].setText(String.valueOf(value));
            gridButtons[row][col].setForeground(new Color(76, 175, 80)); // Green for solved cells
            gridButtons[row][col].setFont(new Font("Segoe UI", Font.BOLD, 20));

            // Update the controller and board
            controller.setCellValue(row, col, value);
            board.getBoard()[row][col] = value;

            // Flash animation for the cell
            Color originalBg = gridButtons[row][col].getBackground();
            gridButtons[row][col].setBackground(new Color(200, 230, 201));

            Timer flashTimer = new Timer(200, flashEvent -> {
                gridButtons[row][col].setBackground(originalBg);
            });
            flashTimer.setRepeats(false);
            flashTimer.start();

            currentIndex[0]++;
        });

        animationTimer.start();
    }

    /**
     * Finish the solving process and show victory dialog
     */
    private void finishSolving(boolean wasTimerRunning) {
        // Stop the timer
        if (gameTimer != null) {
            gameTimer.stop();
        }
        if (wasTimerRunning) {
            controller.pauseTimer();
            pauseResumeButton.repaint();
        }

        // Re-enable buttons
        setGridButtonsEnabled(true);

        // Clear any selection
        controller.deselectCell();

        // Show victory dialog after a short delay
        Timer delayTimer = new Timer(800, e -> {
            if (victoryDialog != null) {
                victoryDialog.showVictoryDialog();
            }
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    /**
     * Enable or disable grid buttons during solving
     */
    private void setGridButtonsEnabled(boolean enabled) {
        if (gridButtons != null) {
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    gridButtons[row][col].setEnabled(enabled);
                }
            }
        }
    }

    /**
     * Update the references when board/buttons are recreated
     */
    public void updateReferences(SudokuBoard9By9 newBoard, JButton[][] newGridButtons,
                                 VictoryDialog newVictoryDialog) {
        this.board = newBoard;
        this.gridButtons = newGridButtons;
        this.victoryDialog = newVictoryDialog;
    }
}