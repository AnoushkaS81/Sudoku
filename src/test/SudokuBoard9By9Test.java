package test;

import components.SudokuBoard9By9;
import components.PuzzleDifficultyRater;
import org.junit.Test;
import pages.*;
import utils.CommonConstants;

import static org.junit.Assert.*;


import javax.swing.*;

public class SudokuBoard9By9Test {

    @Test
    public void testResetSudokuBoardAllValuesAreZero() {
        SudokuBoard9By9 sudokuBoard = new SudokuBoard9By9();
        sudokuBoard.resetSudokuBoard();
        for (int[] y : sudokuBoard.getBoard()) {
            for (int x : y) {
                assert x == 0;
            }
        }
    }

    @Test
    public void testResetSudokuBoardHeight() {
        SudokuBoard9By9 sudokuBoard = new SudokuBoard9By9();
        sudokuBoard.resetSudokuBoard();
        int height = sudokuBoard.getBoard().length;
        assert height == 9;
    }

    @Test
    public void testResetSudokuBoardLength() {
        SudokuBoard9By9 sudokuBoard = new SudokuBoard9By9();
        sudokuBoard.resetSudokuBoard();
        for (int[] y : sudokuBoard.getBoard()) {
            int length = y.length;
            assert length == 9;
        }
    }

    @Test
    public void testGetNumberOfEmptySpaces() {
        SudokuBoard9By9 sudokuBoard = new SudokuBoard9By9();
        sudokuBoard.resetSudokuBoard();
        int emptySpacesOnEmptyBoard = sudokuBoard.getNumberOfEmptySpaces();
        assert emptySpacesOnEmptyBoard == 81;
        int[][] SpacesEmptyBoard3 = {{0, 0}, {1, 0}};
        sudokuBoard.setBoard(SpacesEmptyBoard3);
        int emptySpacesOn2By2Board = sudokuBoard.getNumberOfEmptySpaces();
        assert emptySpacesOn2By2Board == 3;
    }

    @Test
    public void testGetSudokuBoardEmptySpaces() {
        SudokuBoard9By9 sudokuBoard = new SudokuBoard9By9();
        int[][] SpacesEmptyBoard3 = {{0, 0}, {1, 0}};
        sudokuBoard.setBoard(SpacesEmptyBoard3);
        int[][] returnedArrayFromGetSudokuBoardEmptySpaces = sudokuBoard.getSudokuBoardEmptySpaces();
        int[][] sudokuEmptySpacesAnswer = {{0, 0}, {0, 1}, {1, 1}};
        for (int yIndex = 0; yIndex < returnedArrayFromGetSudokuBoardEmptySpaces.length; yIndex++) {
            for (int xIndex = 0; xIndex < returnedArrayFromGetSudokuBoardEmptySpaces[yIndex].length; xIndex++) {
                assert returnedArrayFromGetSudokuBoardEmptySpaces[yIndex][xIndex] == sudokuEmptySpacesAnswer[yIndex][xIndex];
            }
        }
    }

    @Test
    public void testIsValidInRow() {
        SudokuBoard9By9 sudokuBoard = new SudokuBoard9By9();
        int[][] board = {
                {1, 2, 3, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
        sudokuBoard.setBoard(board);
        assert !sudokuBoard.isValidInRow(0, 1);
        assert sudokuBoard.isValidInRow(0, 4);
        assert sudokuBoard.isValidInRow(1, 1);
    }

    @Test
    public void testIsValidInColumn() {
        SudokuBoard9By9 sudokuBoard = new SudokuBoard9By9();
        int[][] board = {
                {1, 0, 0, 0, 0, 0, 0, 0, 0},
                {2, 0, 0, 0, 0, 0, 0, 0, 0},
                {3, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
        sudokuBoard.setBoard(board);
        assert !sudokuBoard.isValidInColumn(0, 1);
        assert sudokuBoard.isValidInColumn(0, 4);
        assert sudokuBoard.isValidInColumn(1, 1);
    }

    @Test
    public void testIsValidInBox() {
        SudokuBoard9By9 sudokuBoard = new SudokuBoard9By9();
        int[][] board = {
                {1, 2, 3, 0, 0, 0, 0, 0, 0},
                {4, 5, 6, 0, 0, 0, 0, 0, 0},
                {7, 8, 9, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
        sudokuBoard.setBoard(board);
        assert !sudokuBoard.isValidInBox(0, 0, 1);
        assert sudokuBoard.isValidInBox(3, 3, 1);
    }

    @Test
    public void testIsValidPlacement() {
        SudokuBoard9By9 sudokuBoard = new SudokuBoard9By9();
        sudokuBoard.resetSudokuBoard();
        int[][] board = {{1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
        sudokuBoard.setBoard(board);
        assert !sudokuBoard.isValidPlacement(0, 5, 1);
        assert !sudokuBoard.isValidPlacement(5, 0, 1);
        assert !sudokuBoard.isValidPlacement(2, 2, 1);
        assert sudokuBoard.isValidPlacement(4, 4, 5);
    }

    @Test
    public void testGetSudokuPossibleMoves() {
        SudokuBoard9By9 sudokuBoard = new SudokuBoard9By9();
        sudokuBoard.resetSudokuBoard();
        int[] moves = sudokuBoard.getSudokuPossibleMoves(0, 0);
        assert moves.length == 9;
        int[][] board = {
                {1, 2, 3, 4, 5, 6, 7, 8, 9},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
        sudokuBoard.setBoard(board);
        moves = sudokuBoard.getSudokuPossibleMoves(1, 0);
        assert moves.length == 6;
    }

    @Test
    public void testGenerateCompleteGrid() {
        SudokuBoard9By9 sudokuBoard = new SudokuBoard9By9();
        sudokuBoard.resetSudokuBoard();
        boolean success = sudokuBoard.generateCompleteGrid();
        assert success;
        assert sudokuBoard.getNumberOfEmptySpaces() == 0;
    }

    @Test
    public void testGenerateEasyPuzzle() {
        SudokuBoard9By9 sudokuBoard = new SudokuBoard9By9();
        sudokuBoard.generatePuzzle(SudokuBoard9By9.Difficulty.EASY);
        int empty = sudokuBoard.getNumberOfEmptySpaces();
        assert empty >= 32;
        assert empty <= 45;
    }

    @Test
    public void testGenerateMediumPuzzle() {
        SudokuBoard9By9 sudokuBoard = new SudokuBoard9By9();
        sudokuBoard.generatePuzzle(SudokuBoard9By9.Difficulty.MEDIUM);
        int empty = sudokuBoard.getNumberOfEmptySpaces();
        assert empty >= 40;
        assert empty <= 55;
    }

    @Test
    public void testGenerateHardPuzzle() {
        SudokuBoard9By9 sudokuBoard = new SudokuBoard9By9();
        sudokuBoard.generatePuzzle(SudokuBoard9By9.Difficulty.HARD);
        int empty = sudokuBoard.getNumberOfEmptySpaces();
        assert empty >= 50;
        assert empty <= 64;
    }

    @Test
    public void testHasUniqueSolution() {
        SudokuBoard9By9 sudokuBoard = new SudokuBoard9By9();
        sudokuBoard.generatePuzzle(SudokuBoard9By9.Difficulty.MEDIUM);
        assert sudokuBoard.hasUniqueSolution();
    }

    @Test
    public void testDifficultyProducesDifferentBoards() {
        SudokuBoard9By9 easy = new SudokuBoard9By9();
        SudokuBoard9By9 medium = new SudokuBoard9By9();
        SudokuBoard9By9 hard = new SudokuBoard9By9();
        easy.generatePuzzle(SudokuBoard9By9.Difficulty.EASY);
        medium.generatePuzzle(SudokuBoard9By9.Difficulty.MEDIUM);
        hard.generatePuzzle(SudokuBoard9By9.Difficulty.HARD);
        int e = easy.getNumberOfEmptySpaces();
        int m = medium.getNumberOfEmptySpaces();
        int h = hard.getNumberOfEmptySpaces();
        assert e < m;
        assert m < h;
    }

    @Test
    public void testIsValidBoard_ValidBoard() {
        SudokuBoard9By9 sudokuBoard = new SudokuBoard9By9();
        int[][] validBoard = {
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
        sudokuBoard.setBoard(validBoard);
        assert sudokuBoard.isValidBoard();
    }

    @Test
    public void testIsValidBoard_DuplicateInRow() {
        SudokuBoard9By9 sudokuBoard = new SudokuBoard9By9();
        int[][] invalidRow = new int[9][9];
        invalidRow[0][0] = 1;
        invalidRow[0][1] = 1;
        sudokuBoard.setBoard(invalidRow);
        assert !sudokuBoard.isValidBoard();
    }

    @Test
    public void testIsValidBoard_DuplicateInColumn() {
        SudokuBoard9By9 sudokuBoard = new SudokuBoard9By9();
        int[][] invalidColumn = new int[9][9];
        invalidColumn[0][0] = 7;
        invalidColumn[5][0] = 7;
        sudokuBoard.setBoard(invalidColumn);
        assert !sudokuBoard.isValidBoard();
    }

    @Test
    public void testIsValidBoard_DuplicateInBox() {
        SudokuBoard9By9 sudokuBoard = new SudokuBoard9By9();
        int[][] invalidBox = new int[9][9];
        invalidBox[0][0] = 3;
        invalidBox[1][1] = 3;
        sudokuBoard.setBoard(invalidBox);
        assert !sudokuBoard.isValidBoard();
    }

    @Test
    public void testDigitInputValidation_AllowsValidDigit() {
        HomePageTest home = new HomePageTest();
        JTextField field = new JTextField();
        JPanel panel = new JPanel();
        panel.add(field);
        home.DigitInputValidation(field);
        java.awt.event.KeyEvent e = new java.awt.event.KeyEvent(
                field,
                java.awt.event.KeyEvent.KEY_TYPED,
                System.currentTimeMillis(),
                0,
                java.awt.event.KeyEvent.VK_UNDEFINED,
                '5'
        );
        field.dispatchEvent(e);
        if (!e.isConsumed()) {
            field.setText("5");
        }
        assert field.getText().equals("5");
    }

    @Test
    public void testDigitInputValidation_BlocksInvalidCharacter() {
        HomePageTest home = new HomePageTest();
        JTextField field = new JTextField();
        home.DigitInputValidation(field);
        java.awt.event.KeyEvent e = new java.awt.event.KeyEvent(
                field,
                java.awt.event.KeyEvent.KEY_TYPED,
                System.currentTimeMillis(),
                0,
                java.awt.event.KeyEvent.VK_UNDEFINED,
                'a'
        );
        field.dispatchEvent(e);
        assert field.getText().equals("");
    }

    @Test
    public void testApplyCustomBoardToMainGrid_EmptyCells() {
        HomePageTest home = new HomePageTest();
        JButton[][] fakeGrid = new JButton[9][9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                fakeGrid[r][c] = new JButton();
            }
        }
        home.setGridButtonsForTest(fakeGrid);
        HomePageTest.sudoku_board = new int[9][9];
        home.applyCustomBoardToMainGrid();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                assert fakeGrid[r][c].getText().equals("");
            }
        }
    }

    @Test
    public void testApplyCustomBoardToMainGrid_FilledCells() {
        HomePageTest home = new HomePageTest();
        JButton[][] fakeGrid = new JButton[9][9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                fakeGrid[r][c] = new JButton();
            }
        }
        home.setGridButtonsForTest(fakeGrid);
        HomePageTest.sudoku_board = new int[9][9];
        HomePageTest.sudoku_board[0][0] = 5;
        HomePageTest.sudoku_board[3][4] = 7;
        HomePageTest.sudoku_board[8][8] = 9;
        home.applyCustomBoardToMainGrid();
        assert fakeGrid[0][0].getText().equals("5");
        assert fakeGrid[3][4].getText().equals("7");
        assert fakeGrid[8][8].getText().equals("9");
    }

    @Test
    public void testRatePuzzle_EasyPuzzle() {
        SudokuBoard9By9 board = new SudokuBoard9By9();

        int[][] grid = {
                {5,3,0,  0,7,0,  0,0,0},
                {6,0,0,  1,9,5,  0,0,0},
                {0,9,8,  0,0,0,  0,6,0},

                {8,0,0,  0,6,0,  0,0,3},
                {4,0,0,  8,0,3,  0,0,1},
                {7,0,0,  0,2,0,  0,0,6},

                {0,6,0,  0,0,0,  2,8,0},
                {0,0,0,  4,1,9,  0,0,5},
                {0,0,0,  0,8,0,  0,7,9}
        };

        board.setBoard(grid);

        PuzzleDifficultyRater.Difficulty diff =
                PuzzleDifficultyRater.ratePuzzle(board);

        assertEquals(PuzzleDifficultyRater.Difficulty.EASY, diff);
    }

    @Test
    public void testRatePuzzle_MediumPuzzle() {
        SudokuBoard9By9 board = new SudokuBoard9By9();

        int[][] grid = {
                {0,0,0,  2,6,0,  7,0,1},
                {6,8,0,  0,7,0,  0,9,0},
                {1,9,0,  0,0,4,  5,0,0},

                {8,2,0,  1,0,0,  0,4,0},
                {0,0,4,  6,0,2,  9,0,0},
                {0,5,0,  0,0,3,  0,2,8},

                {0,0,9,  3,0,0,  0,7,4},
                {0,4,0,  0,5,0,  0,3,6},
                {7,0,3,  0,1,8,  0,0,0}
        };

        board.setBoard(grid);

        PuzzleDifficultyRater.Difficulty diff =
                PuzzleDifficultyRater.ratePuzzle(board);

        assertEquals(PuzzleDifficultyRater.Difficulty.MEDIUM, diff);
    }

    @Test
    public void testRatePuzzle_HardPuzzle() {
        SudokuBoard9By9 board = new SudokuBoard9By9();

        int[][] grid = {
                {0,0,0,  0,0,0,  0,1,2},
                {0,0,0,  0,0,0,  0,0,0},
                {0,0,1,  0,0,0,  0,0,0},

                {0,0,0,  0,0,0,  3,0,0},
                {0,0,0,  7,0,0,  0,0,0},
                {0,0,0,  0,0,0,  0,0,0},

                {0,0,0,  0,0,0,  0,0,0},
                {0,0,0,  0,0,0,  0,0,0},
                {9,0,0,  0,0,0,  0,0,0}
        };

        board.setBoard(grid);

        PuzzleDifficultyRater.Difficulty diff =
                PuzzleDifficultyRater.ratePuzzle(board);

        assertEquals(PuzzleDifficultyRater.Difficulty.HARD, diff);
    }

    @Test
    public void testCountCluesEmptyBoard() {
        SudokuBoard9By9 b = new SudokuBoard9By9();
        b.resetSudokuBoard();
        assertEquals(0, PuzzleDifficultyRater.countClues(b));
    }

    @Test
    public void testCountCluesPartialBoard() {
        SudokuBoard9By9 b = new SudokuBoard9By9();
        int[][] grid = new int[9][9];
        grid[0][0] = 5;
        grid[8][8] = 9;
        b.setBoard(grid);
        assertEquals(2, PuzzleDifficultyRater.countClues(b));
    }

    @Test
    public void testCopyBoardArrayIsDeepCopy() {
        int[][] src = new int[9][9];
        src[0][0] = 7;
        int[][] copy = PuzzleDifficultyRater.copyBoardArray(src);
        assertEquals(7, copy[0][0]);
        src[0][0] = 0;
        assertEquals(7, copy[0][0]);
    }

    @Test
    public void testClassifyThresholds() {
        assertEquals(PuzzleDifficultyRater.Difficulty.EASY,
                PuzzleDifficultyRater.classify(36, 5, false));
        assertEquals(PuzzleDifficultyRater.Difficulty.MEDIUM,
                PuzzleDifficultyRater.classify(32, 20, false));
        assertEquals(PuzzleDifficultyRater.Difficulty.HARD,
                PuzzleDifficultyRater.classify(30, 5, true));
    }

}
