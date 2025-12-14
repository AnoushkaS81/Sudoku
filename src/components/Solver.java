
/**========================================
 Solver class uses a different and most optimized way,

 Optimization technique:
 Searching using bitmask so each number 1-9,
 is being checked on whether the bitmask of candidates
 has that digit(position number in the bit mask)

 Adding and removing candidates:
 * Adding:  AND operation on bits candidate
 * removing: OR operation on bits candidate

 Inner class:
 It is an important part since the solver class use it continuously
 to check and apply one step at a time
 ======================================= **/

package components;

import java.util.Set;

public class Solver {
    private SudokuBoard9By9 sudokuBoard;
    private int[][] candidates;

    // Precomputed bitmasks for quick operations
    private static final int FULL_MASK = 0b1111111110;  // bits 1-9 set (all candidates)
    private static final int[] BIT_MASKS = new int[10]; // BIT_MASKS[i] = 1 << i

    static {
        for (int i = 1; i <= 9; i++) {
            BIT_MASKS[i] = 1 << i;
        }
    }

    public Solver(SudokuBoard9By9 board) {
        this.sudokuBoard = board;
        initializeCandidates();
    }


    private boolean hasCandidate(int row, int col, int num) {
       return (candidates[row][col] & BIT_MASKS[num]) != 0;
    }

    private void addCandidate(int row, int col, int num) {
        candidates[row][col] |= BIT_MASKS[num];
    }

    private void removeCandidate(int row, int col, int num) {
        candidates[row][col] &= ~BIT_MASKS[num];
    }

    //Get count of candidates using Brian Kernighan's algorithm
    private int countCandidates(int row, int col) {
        int count = 0;
        int mask = candidates[row][col];
        while (mask != 0) {
            mask &= (mask - 1);  // Clear rightmost set bit
            count++;
        }
        return count;
    }


    private int getSingleCandidate(int row, int col) {
        return Integer.numberOfTrailingZeros(candidates[row][col]);
    }

    private int[] getCandidatesArray(int row, int col) {
        int mask = candidates[row][col];
        int count = countCandidates(row, col);
        int[] result = new int[count];
        int index = 0;

        for (int num = 1; num <= 9; num++) {
            if ((mask & BIT_MASKS[num]) != 0) {
                result[index++] = num;
            }
        }
        return result;
    }

    private void clearCandidates(int row, int col) {
        candidates[row][col] = 0;
    }

    private boolean sameCandidates(int r1, int c1, int r2, int c2) {
        return candidates[r1][c1] == candidates[r2][c2];
    }


    private void initializeCandidates() {
        candidates = new int[9][9];

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (sudokuBoard.getBoard()[row][col] == 0) {
                    candidates[row][col] = FULL_MASK;

                    // Remove candidates based on existing numbers
                    for (int num = 1; num <= 9; num++) {
                        if (!sudokuBoard.isValidPlacement(row, col, num)) {
                            removeCandidate(row, col, num);
                        }
                    }
                }
            }
        }
    }
    // Apply logical techniques first
    public boolean solveCompletely() {
        // Apply logical techniques first
        boolean progress = true;
        while (progress) {
            progress = applyAllLogicalTechniques();
        }

        return backtrackWithMRV();
    }
    // Find cell with minimum candidates using bitmask counting
    private boolean backtrackWithMRV() {
        int minRow = -1, minCol = -1;
        int minCount = 10;

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (sudokuBoard.getBoard()[row][col] == 0) {
                    int count = countCandidates(row, col);

                    if (count == 0) {
                        return false; // Dead end
                    }

                    if (count < minCount) {
                        minCount = count;
                        minRow = row;
                        minCol = col;

                        if (count == 1) break;
                    }
                }
            }
            if (minCount == 1) break;
        }

        if (minRow == -1) {
            return true;
        }


        int[] candidatesList = getCandidatesArray(minRow, minCol);

        for (int num : candidatesList) {
            int[][] savedCandidates = copyCandidates();

            //Place number
            sudokuBoard.getBoard()[minRow][minCol] = num;
            clearCandidates(minRow, minCol);

            if (propagateConstraints(minRow, minCol, num)) {
                if (backtrackWithMRV()) {
                    return true;
                }
            }

            sudokuBoard.getBoard()[minRow][minCol] = 0;
            candidates = savedCandidates;
        }

        return false;
    }

    private boolean propagateConstraints(int row, int col, int num) {
        // Remove num from all related cells
        int removeMask = ~BIT_MASKS[num];

        // Row and column
        for (int i = 0; i < 9; i++) {
            if (sudokuBoard.getBoard()[row][i] == 0) {
                candidates[row][i] &= removeMask;
                if (candidates[row][i] == 0) return false;
            }

            if (sudokuBoard.getBoard()[i][col] == 0) {
                candidates[i][col] &= removeMask;
                if (candidates[i][col] == 0) return false;
            }
        }

        int boxStartRow = (row / 3) * 3;
        int boxStartCol = (col / 3) * 3;

        for (int r = boxStartRow; r < boxStartRow + 3; r++) {
            for (int c = boxStartCol; c < boxStartCol + 3; c++) {
                if (sudokuBoard.getBoard()[r][c] == 0) {
                    candidates[r][c] &= removeMask;
                    if (candidates[r][c] == 0) return false;
                }
            }
        }

        return true;
    }

    //class to solve the sudoku step by step(Educational)
    public static class SolveStep {
        public enum Technique {
            NAKED_SINGLE,
            HIDDEN_SINGLE_ROW,
            HIDDEN_SINGLE_COL,
            HIDDEN_SINGLE_BOX,
            NAKED_PAIR,
            POINTING_PAIR
        }

        public Technique technique;
        public int row;
        public int col;
        public int value;
        public String explanation;

        public SolveStep(Technique technique, int row, int col, int value, String explanation) {
            this.technique = technique;
            this.row = row;
            this.col = col;
            this.value = value;
            this.explanation = explanation;
        }

        @Override
        public String toString() {
            return String.format("[%s] Place %d at (%d,%d): %s",
                    technique, value, row, col, explanation);
        }
    }

    public SolveStep getNextStep() {
        updateCandidates();

        SolveStep step = findNakedSingle();
        if (step != null) return step;

        step = findHiddenSingle();
        if (step != null) return step;

        step = findNakedPairs();
        if (step != null) return step;

        return null;
    }

    public void applyStep(SolveStep step) {
        if (step != null && step.value > 0) {
            sudokuBoard.getBoard()[step.row][step.col] = step.value;
            clearCandidates(step.row, step.col);
            updateCandidates();
        }
    }

    // ===== LOGICAL TECHNIQUES  =====
    private SolveStep findNakedSingle() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (sudokuBoard.getBoard()[row][col] == 0 &&
                        countCandidates(row, col) == 1) {

                    int num = getSingleCandidate(row, col);
                    return new SolveStep(
                            SolveStep.Technique.NAKED_SINGLE,
                            row, col, num,
                            "Only candidate remaining for this cell"
                    );
                }
            }
        }
        return null;
    }

    private SolveStep findHiddenSingle() {
        // Check rows using bitmask operations
        for (int row = 0; row < 9; row++) {
            for (int num = 1; num <= 9; num++) {
                if (isNumberInRow(row, num)) continue;

                int possibleCol = -1;
                int count = 0;

                for (int col = 0; col < 9; col++) {
                    if (sudokuBoard.getBoard()[row][col] == 0 &&
                            hasCandidate(row, col, num)) {
                        possibleCol = col;
                        count++;
                        if (count > 1) break; // Early exit optimization
                    }
                }

                if (count == 1) {
                    return new SolveStep(
                            SolveStep.Technique.HIDDEN_SINGLE_ROW,
                            row, possibleCol, num,
                            num + " can only go in one place in row " + row
                    );
                }
            }
        }

        return null;
    }

    private SolveStep findNakedPairs() {
        // Check rows for naked pairs using bitmask equality
        for (int row = 0; row < 9; row++) {
            for (int col1 = 0; col1 < 8; col1++) {
                if (sudokuBoard.getBoard()[row][col1] != 0 ||
                        countCandidates(row, col1) != 2) continue;

                for (int col2 = col1 + 1; col2 < 9; col2++) {
                    if (sudokuBoard.getBoard()[row][col2] == 0 &&
                            sameCandidates(row, col1, row, col2)) {

                        // Found naked pair - eliminate from other cells
                        int pairMask = candidates[row][col1];
                        boolean eliminated = false;

                        for (int col = 0; col < 9; col++) {
                            if (col != col1 && col != col2 &&
                                    sudokuBoard.getBoard()[row][col] == 0) {

                                int before = candidates[row][col];
                                candidates[row][col] &= ~pairMask; // Remove pair candidates

                                if (before != candidates[row][col]) {
                                    eliminated = true;
                                }
                            }
                        }

                        if (eliminated) {
                            return new SolveStep(
                                    SolveStep.Technique.NAKED_PAIR,
                                    row, col1, -1,
                                    "Naked pair found at columns " + col1 + " and " + col2
                            );
                        }
                    }
                }
            }
        }

        return null;
    }

    // ===== HELPER METHODS =====
    private boolean applyAllLogicalTechniques() {
        SolveStep step = getNextStep();
        if (step != null && step.value > 0) {
            applyStep(step);
            return true;
        }
        return false;
    }

    private void updateCandidates() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (sudokuBoard.getBoard()[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (!sudokuBoard.isValidPlacement(row, col, num)) {
                            removeCandidate(row, col, num);
                        }
                    }
                }
            }
        }
    }

    private boolean isNumberInRow(int row, int num) {
        for (int col = 0; col < 9; col++) {
            if (sudokuBoard.getBoard()[row][col] == num) return true;
        }
        return false;
    }

    private int[][] copyCandidates() {
        int[][] copy = new int[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(candidates[i], 0, copy[i], 0, 9);
        }
        return copy;
    }

    public boolean isSolved() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (sudokuBoard.getBoard()[row][col] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

}
