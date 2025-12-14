package test;

import org.junit.Test;
import pages.InteractiveSudokuController;

import static org.junit.Assert.*;

public class InteractiveSudokuControllerMistakeTest {

    @Test
    public void testMistakeCountDoesNotDecreaseAfterErase() {
        InteractiveSudokuController controller = new InteractiveSudokuController();

        boolean selected = controller.selectCell(0, 0);
        assertTrue("Cell (0,0) should be selectable", selected);

        controller.enterNumber(9);
        controller.incrementMistakes();

        int mistakeAfterWrongInput = controller.getMistakeCount();
        assertEquals("Mistake should increase after wrong input",
                1, mistakeAfterWrongInput);

        controller.clearSelectedCell();
        int mistakeAfterErase = controller.getMistakeCount();

        assertEquals("Cell value should be cleared after erase",
                0, controller.getCellValue(0, 0));

        assertEquals("Mistake count should NOT decrease after erasing",
                mistakeAfterWrongInput, mistakeAfterErase);
    }

    @Test
    public void testCorrectInputDoesNotIncreaseMistakeCount() {
        InteractiveSudokuController controller = new InteractiveSudokuController();

        boolean selected = controller.selectCell(0, 1);
        assertTrue("Cell (0,1) should be selectable", selected);

        boolean entered = controller.enterNumber(3);
        assertTrue("enterNumber should return true for a valid input", entered);

        assertEquals("Correct input should not add a mistake",
                0, controller.getMistakeCount());
    }

    @Test
    public void testGameOverAfterMaxMistakes() {
        InteractiveSudokuController controller = new InteractiveSudokuController();

        assertEquals(0, controller.getMistakeCount());
        assertFalse("Game should not be over initially", controller.isGameOver());

        controller.incrementMistakes();
        controller.incrementMistakes();
        assertFalse("Game should not be over before reaching max mistakes",
                controller.isGameOver());

        controller.incrementMistakes();
        assertTrue("Game should be over after reaching max mistakes",
                controller.isGameOver());
    }
}
