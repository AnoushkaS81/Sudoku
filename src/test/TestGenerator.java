package test;

import components.Solver;
import components.SudokuBoard9By9;
import utils.CommonConstants;

public class TestGenerator {
    public static void printBoard(int[][] board) {
        for (int i = 0; i < 9; i++) {
            if (i % 3 == 0 && i != 0) {
                System.out.println("------|-------|------");
            }
            for (int j = 0; j < 9; j++) {
                if (j % 3 == 0 && j != 0) {
                    System.out.print("| ");
                }
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        SudokuBoard9By9 sudoku = new SudokuBoard9By9();
        System.out.println("EASY PUZZLE:\n");
        sudoku.generatePuzzle(SudokuBoard9By9.Difficulty.MEDIUM);
        Solver solver = new Solver(sudoku);
        System.out.println(solver.solveCompletely());
        printBoard(sudoku.getBoard());
        System.out.println();
    }
}
