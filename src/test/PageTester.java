package test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import pages.*;

import javax.swing.*;
import java.awt.*;

public class PageTester {
    private InteractiveSudokuController controller;

    @Test
    public void testGetUserInput() {
        JTextField[][] input_fields = new JTextField[9][9];
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                input_fields[y][x] = new JTextField();
                input_fields[y][x].setText(String.valueOf(y + x));
            }
        }

        HomePageTest page = new HomePageTest();
        page.getUserInput(input_fields);
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                int num = Integer.parseInt(input_fields[y][x].getText());
                assert num == page.sudoku_board[y][x];
            }
        }
    }

    @Before
    public void setUp() {
        controller = new InteractiveSudokuController();
    }

    @Test
    public void testEmptyCellCanBeSelected() {
        controller.markCellAsPreFilled(2, 3, false);
        boolean selected = controller.selectCell(2, 3);
        assertTrue("Empty cell should be selectable", selected);
    }

    @Test
    public void testPreFilledCellCannotBeSelected() {
        controller.markCellAsPreFilled(5, 5, true);

        boolean selected = controller.selectCell(5, 5);

        assertFalse("Pre-filled cell should not be selectable", selected);
        assertEquals("No cell should be selected", -1, controller.getSelectedRow());
        assertEquals("No cell should be selected", -1, controller.getSelectedCol());
    }

    @Test
    public void testOnlyOneCellSelectedAtATime() {
        controller.markCellAsPreFilled(1, 1, false);
        controller.markCellAsPreFilled(3, 3, false);

        controller.selectCell(1, 1);
        assertEquals("First cell should be selected", 1, controller.getSelectedRow());

        controller.selectCell(3, 3);

        assertEquals("Only second cell row should be selected", 3, controller.getSelectedRow());
        assertEquals("Only second cell col should be selected", 3, controller.getSelectedCol());
    }

    @Test
    public void testDeselectCell() {
        controller.markCellAsPreFilled(2, 2, false);
        controller.selectCell(2, 2);

        controller.deselectCell();

        assertEquals("No cell should be selected after deselect", -1, controller.getSelectedRow());
        assertEquals("No cell should be selected after deselect", -1, controller.getSelectedCol());
    }

    @Test
    public void testIsCellSelected() {
        controller.markCellAsPreFilled(4, 4, false);
        controller.selectCell(4, 4);
        assertTrue("Cell (4,4) should be selected", controller.isCellSelected(4, 4));
    }

    @Test
    public void testEnterNumberInSelectedCell() {
        controller.markCellAsPreFilled(3, 3, false);
        controller.selectCell(3, 3);

        boolean entered = controller.enterNumber(5);
        assertTrue("Number should be entered in selected cell", entered);
        assertEquals("Cell should contain 5", 5, controller.getCellValue(3, 3));
    }

    @Test
    public void testCannotEnterNumberWhenNoCellSelected() {
        boolean entered = controller.enterNumber(7);
        assertFalse("Cannot enter number when no cell is selected", entered);
    }

    @Test
    public void testCannotEnterNumberInPreFilledCell() {
        controller.markCellAsPreFilled(1, 1, true);
        controller.setCellValue(1, 1, 8); // Pre-fill with 8

        boolean entered = controller.enterNumber(9);

        assertFalse("Cannot enter number in pre-filled cell", entered);
    }

    @Test
    public void testClearCellValue() {
        controller.markCellAsPreFilled(2, 2, false);
        controller.selectCell(2, 2);
        controller.enterNumber(4);
        controller.clearSelectedCell();

        assertEquals("Cell should be cleared to 0", 0, controller.getCellValue(2, 2));
    }


    @Test
    public void testValidNumberPlacement() {
        controller.markCellAsPreFilled(0, 0, false);
        controller.selectCell(0, 0);
        controller.enterNumber(5);

        assertEquals("No mistakes for valid placement", 0, controller.getMistakeCount());
    }

    @Test
    public void testInvalidNumberPlacement() {
        controller.markCellAsPreFilled(0, 0, false);
        controller.selectCell(0, 0);
    }

    @Test
    public void testMistakeCounterIncrementsOnInvalidMove() {
        controller.incrementMistakes();
        assertEquals("Mistake count should be 1", 1, controller.getMistakeCount());
    }

    @Test
    public void testGameOverAfterThreeMistakes() {
        controller.incrementMistakes();
        controller.incrementMistakes();
        assertFalse("Game should not be over after 2 mistakes", controller.isGameOver());
        controller.incrementMistakes();
        assertTrue("Game should be over after 3 mistakes", controller.isGameOver());
    }

    @Test
    public void testResetMistakeCounter() {
        controller.incrementMistakes();
        controller.incrementMistakes();
        controller.resetMistakes();

        assertEquals("Mistakes should be reset to 0", 0, controller.getMistakeCount());

    }

    @Test
    public void testSameNumberHighlighting() {
        controller.markCellAsPreFilled(0, 0, false);
        controller.markCellAsPreFilled(1, 1, false);
        controller.markCellAsPreFilled(2, 2, false);

        controller.selectCell(0, 0);
        controller.enterNumber(5);
        controller.selectCell(1, 1);
        controller.enterNumber(5);
        controller.selectCell(2, 2);
        controller.enterNumber(5);
        controller.selectCell(0, 0);

        assertEquals("Cell should have value 5", 5, controller.getCellValue(0, 0));
        assertEquals("Cell should have value 5", 5, controller.getCellValue(1, 1));
        assertEquals("Cell should have value 5", 5, controller.getCellValue(2, 2));
    }

    @Test
    public void testTimerInitialization() {
        // Timer should start at 0
        assertEquals("Timer should start at 0", 0, controller.getElapsedSeconds());
    }

    @Test
    public void testIdentifyConflictingCells() {
        components.SudokuBoard9By9 testBoard = new components.SudokuBoard9By9();
        int[][] board = testBoard.getBoard();
        board[0][0] = 5;
        controller.setSudokuBoard(testBoard);
        controller.initializeBoard(board);
        controller.markCellAsPreFilled(0, 0, true);
        controller.markCellAsPreFilled(0, 5, false);
        controller.selectCell(0, 5);
        assertFalse("Placement should be invalid (duplicate 5 in row 0)",
                controller.isValidPlacement(0, 5, 5));
    }

    @Test
    public void testHowToPlayPageCreation() {
        HowToPlayPage page = new HowToPlayPage();
        assertNotNull("HowToPlayPage should be created", page);
        assertTrue("Page should be visible", page.isVisible());
    }
    @Test
    public void testHowToPlayPageHasAllSections() {
        HowToPlayPage page = new HowToPlayPage();
        assertTrue("Should have 'The Goal' section", findComponentByText(page, "The Goal"));
        assertTrue("Should have 'How to Play' section", findComponentByText(page, "How to Play"));
        assertTrue("Should have 'Mistakes & Conflicts' section", findComponentByText(page, "Mistakes & Conflicts"));
        assertTrue("Should have 'Visual Highlighting' section", findComponentByText(page, "Visual Highlighting"));
        assertTrue("Should have 'Game Features' section", findComponentByText(page, "Game Features"));
        assertTrue("Should have 'Difficulty Levels' section", findComponentByText(page, "Difficulty Levels"));
    }

    @Test
    public void testHowToPlayPageHasScrollPane() {
        HowToPlayPage page = new HowToPlayPage();
        boolean hasScrollPane = findComponent(page, JScrollPane.class);
        assertTrue("Page should have a scroll pane", hasScrollPane);
    }

    @Test
    public void testHowToPlayPageMentionsKeyFeatures() {
        HowToPlayPage page = new HowToPlayPage();
        assertTrue("Should mention '9×9 grid'", findComponentByText(page, "9×9 grid"));
        assertTrue("Should mention 'keyboard'", findComponentByText(page, "keyboard"));
        assertTrue("Should mention '3 mistakes'", findComponentByText(page, "3 mistakes"));
        assertTrue("Should mention 'Timer'", findComponentByText(page, "Timer"));
    }

    private boolean findComponentByText(Container container, String text) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getText().contains(text)) {
                    return true;
                }
            } else if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                if (button.getText().contains(text)) {
                    return true;
                }
            } else if (comp instanceof Container) {
                if (findComponentByText((Container) comp, text)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean findComponent(Container container, Class<?> componentClass) {
        for (Component comp : container.getComponents()) {
            if (componentClass.isInstance(comp)) {
                return true;
            } else if (comp instanceof Container) {
                if (findComponent((Container) comp, componentClass)) {
                    return true;
                }
            }
        }
        return false;
    }
}
