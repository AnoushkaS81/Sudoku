package test;

import components.SudokuBoard9By9;
import db.GameSaveManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


import static org.junit.Assert.*;

/**
 * Comprehensive JUnit 4 test suite for GameSaveManager
 * Tests all JSON read/write operations, data integrity, and edge cases
 */
public class GameSaveManagerTest {

    private GameSaveManager manager;
    private static final String TEST_SAVE_DIR = "saves/";

    // Test data
    private int[][] testBoard1;
    private int[][] testBoard2;
    private boolean[][] testPreFilled1;
    private boolean[][] testPreFilled2;

    @Before
    public void setUp() {
        manager = new GameSaveManager();

        // Clean up any existing test files
        cleanTestFiles();

        // Initialize test boards
        testBoard1 = createTestBoard(1);
        testBoard2 = createTestBoard(2);
        testPreFilled1 = createTestPreFilled(true);
        testPreFilled2 = createTestPreFilled(false);
    }

    @After
    public void tearDown() {
        // Clean up test files after each test
        cleanTestFiles();
    }

    private void cleanTestFiles() {
        deleteFileIfExists(TEST_SAVE_DIR + "easy_games.json");
        deleteFileIfExists(TEST_SAVE_DIR + "medium_games.json");
        deleteFileIfExists(TEST_SAVE_DIR + "hard_games.json");
    }

    private void deleteFileIfExists(String filepath) {
        try {
            File file = new File(filepath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            System.err.println("Could not delete: " + filepath);
        }
    }

    @Test
    public void testSaveAndLoadSingleGame() {
        System.out.println("\n=== TEST: Save and Load Single Game ===");

        boolean saved = manager.saveGame(
                "TestGame1",
                testBoard1,
                testPreFilled1,
                2,
                150,
                3,
                SudokuBoard9By9.Difficulty.EASY
        );

        assertTrue("Game should save successfully", saved);

        GameSaveManager.SavedGameData loaded = manager.loadGame(
                "TestGame1",
                SudokuBoard9By9.Difficulty.EASY
        );

        assertNotNull("Loaded game should not be null", loaded);
        assertEquals("Name should match", "TestGame1", loaded.name);
        assertEquals("Mistakes should match", 2, loaded.mistakes);
        assertEquals("ElapsedSeconds should match", 150, loaded.elapsedSeconds);
        assertEquals("Hints should match", 3, loaded.hints);
        assertEquals("Difficulty should match", SudokuBoard9By9.Difficulty.EASY, loaded.difficulty);

        assertBoardEquals("Board should match", testBoard1, loaded.board);
        assertPreFilledEquals("PreFilled should match", testPreFilled1, loaded.preFilledCells);

        System.out.println("✅ PASSED: Single game save/load");
    }

    @Test
    public void testSaveMultipleGames() {
        System.out.println("\n=== TEST: Save Multiple Games ===");

        manager.saveGame("Game1", testBoard1, testPreFilled1, 0, 100, 3,
                SudokuBoard9By9.Difficulty.EASY);
        manager.saveGame("Game2", testBoard2, testPreFilled2, 1, 200, 2,
                SudokuBoard9By9.Difficulty.EASY);
        manager.saveGame("Game3", testBoard1, testPreFilled1, 2, 300, 1,
                SudokuBoard9By9.Difficulty.EASY);

        List<String> gameNames = manager.getGameNames(SudokuBoard9By9.Difficulty.EASY);

        assertEquals("Should have 3 games", 3, gameNames.size());
        assertTrue("Should contain Game1", gameNames.contains("Game1"));
        assertTrue("Should contain Game2", gameNames.contains("Game2"));
        assertTrue("Should contain Game3", gameNames.contains("Game3"));

        System.out.println("✅ PASSED: Multiple games saved");
    }

    @Test
    public void testLoadAllGames() {
        System.out.println("\n=== TEST: Load All Games ===");

        manager.saveGame("GameA", testBoard1, testPreFilled1, 0, 100, 3,
                SudokuBoard9By9.Difficulty.MEDIUM);
        manager.saveGame("GameB", testBoard2, testPreFilled2, 1, 200, 2,
                SudokuBoard9By9.Difficulty.MEDIUM);

        List<GameSaveManager.SavedGameData> allGames =
                manager.loadAllGames(SudokuBoard9By9.Difficulty.MEDIUM);

        assertEquals("Should have 2 games", 2, allGames.size());

        // Verify both games loaded correctly
        GameSaveManager.SavedGameData gameA = allGames.stream()
                .filter(g -> g.name.equals("GameA"))
                .findFirst()
                .orElse(null);

        GameSaveManager.SavedGameData gameB = allGames.stream()
                .filter(g -> g.name.equals("GameB"))
                .findFirst()
                .orElse(null);

        assertNotNull("GameA should be loaded", gameA);
        assertNotNull("GameB should be loaded", gameB);

        assertEquals("GameA hints should be 3", 3, gameA.hints);
        assertEquals("GameB hints should be 2", 2, gameB.hints);

        System.out.println("✅ PASSED: Load all games");
    }

    // ========== TIMESTAMP TESTS ==========
    @Test
    public void testTimestampPreservation() throws InterruptedException {
        System.out.println("\n=== TEST: Timestamp Preservation ===");

        manager.saveGame("TimestampTest1", testBoard1, testPreFilled1, 0, 100, 3,
                SudokuBoard9By9.Difficulty.EASY);

        GameSaveManager.SavedGameData game1 = manager.loadGame("TimestampTest1",
                SudokuBoard9By9.Difficulty.EASY);
        long timestamp1_original = game1.saveDate;

        Thread.sleep(100);

        // Save second game
        manager.saveGame("TimestampTest2", testBoard2, testPreFilled2, 1, 200, 2,
                SudokuBoard9By9.Difficulty.EASY);

        // Reload first game
        GameSaveManager.SavedGameData game1_reloaded = manager.loadGame("TimestampTest1",
                SudokuBoard9By9.Difficulty.EASY);
        long timestamp1_after = game1_reloaded.saveDate;

        assertEquals("Timestamp should be preserved after loading",
                timestamp1_original, timestamp1_after);

        System.out.println("Original timestamp: " + timestamp1_original);
        System.out.println("After reload: " + timestamp1_after);
        System.out.println("✅ PASSED: Timestamp preserved");
    }

    @Test
    public void testDifferentGamesHaveDifferentTimestamps() throws InterruptedException {
        System.out.println("\n=== TEST: Different Timestamps ===");

        manager.saveGame("TimeGame1", testBoard1, testPreFilled1, 0, 100, 3,
                SudokuBoard9By9.Difficulty.HARD);

        Thread.sleep(100); // Ensure different timestamps

        manager.saveGame("TimeGame2", testBoard2, testPreFilled2, 1, 200, 2,
                SudokuBoard9By9.Difficulty.HARD);

        GameSaveManager.SavedGameData game1 = manager.loadGame("TimeGame1",
                SudokuBoard9By9.Difficulty.HARD);
        GameSaveManager.SavedGameData game2 = manager.loadGame("TimeGame2",
                SudokuBoard9By9.Difficulty.HARD);

        assertTrue("Game2 should be saved after Game1",
                game2.saveDate > game1.saveDate);

        System.out.println("Game1 timestamp: " + game1.saveDate);
        System.out.println("Game2 timestamp: " + game2.saveDate);
        System.out.println("✅ PASSED: Different timestamps");
    }

    // ========== OVERWRITE TESTS ==========

    @Test
    public void testOverwriteExistingGame() {
        System.out.println("\n=== TEST: Overwrite Game ===");

        manager.saveGame("OverwriteTest", testBoard1, testPreFilled1, 1, 100, 3,
                SudokuBoard9By9.Difficulty.EASY);

        GameSaveManager.SavedGameData original = manager.loadGame("OverwriteTest",
                SudokuBoard9By9.Difficulty.EASY);
        assertEquals("Original mistakes should be 1", 1, original.mistakes);
        assertEquals("Original time should be 100", 100, original.elapsedSeconds);

        // Overwrite with new data
        manager.saveGame("OverwriteTest", testBoard2, testPreFilled2, 2, 200, 1,
                SudokuBoard9By9.Difficulty.EASY);

        GameSaveManager.SavedGameData updated = manager.loadGame("OverwriteTest",
                SudokuBoard9By9.Difficulty.EASY);

        assertEquals("Updated mistakes should be 2", 2, updated.mistakes);
        assertEquals("Updated time should be 200", 200, updated.elapsedSeconds);
        assertEquals("Updated hints should be 1", 1, updated.hints);

        List<String> games = manager.getGameNames(SudokuBoard9By9.Difficulty.EASY);
        assertEquals("Should still have only 1 game", 1, games.size());

        System.out.println("✅ PASSED: Game overwrite");
    }

    // ========== DELETE TESTS ==========

    @Test
    public void testDeleteGame() {
        System.out.println("\n=== TEST: Delete Game ===");

        manager.saveGame("DeleteTest1", testBoard1, testPreFilled1, 0, 100, 3,
                SudokuBoard9By9.Difficulty.MEDIUM);
        manager.saveGame("DeleteTest2", testBoard2, testPreFilled2, 1, 200, 2,
                SudokuBoard9By9.Difficulty.MEDIUM);

        List<String> beforeDelete = manager.getGameNames(SudokuBoard9By9.Difficulty.MEDIUM);
        assertEquals("Should have 2 games before delete", 2, beforeDelete.size());

        boolean deleted = manager.deleteGame("DeleteTest1", SudokuBoard9By9.Difficulty.MEDIUM);
        assertTrue("Delete should succeed", deleted);

        List<String> afterDelete = manager.getGameNames(SudokuBoard9By9.Difficulty.MEDIUM);
        assertEquals("Should have 1 game after delete", 1, afterDelete.size());
        assertFalse("Should not contain deleted game", afterDelete.contains("DeleteTest1"));
        assertTrue("Should still contain other game", afterDelete.contains("DeleteTest2"));

        GameSaveManager.SavedGameData deletedGame = manager.loadGame("DeleteTest1",
                SudokuBoard9By9.Difficulty.MEDIUM);
        assertNull("Deleted game should return null", deletedGame);

        System.out.println("✅ PASSED: Delete game");
    }

    @Test
    public void testDeleteNonExistentGame() {
        System.out.println("\n=== TEST: Delete Non-Existent Game ===");

        boolean deleted = manager.deleteGame("NonExistent", SudokuBoard9By9.Difficulty.EASY);
        assertFalse("Delete should fail for non-existent game", deleted);

        System.out.println("✅ PASSED: Delete non-existent game returns false");
    }

    // ========== GAME EXISTS TESTS ==========

    @Test
    public void testGameExists() {
        System.out.println("\n=== TEST: Game Exists ===");

        assertFalse("Game should not exist initially",
                manager.gameExists("ExistTest", SudokuBoard9By9.Difficulty.EASY));

        manager.saveGame("ExistTest", testBoard1, testPreFilled1, 0, 100, 3,
                SudokuBoard9By9.Difficulty.EASY);

        assertTrue("Game should exist after saving",
                manager.gameExists("ExistTest", SudokuBoard9By9.Difficulty.EASY));

        System.out.println("✅ PASSED: Game exists check");
    }

    // ========== DIFFICULTY SEPARATION TESTS ==========

    @Test
    public void testDifficultySeparation() {
        System.out.println("\n=== TEST: Difficulty Separation ===");

        manager.saveGame("SameName", testBoard1, testPreFilled1, 0, 100, 3,
                SudokuBoard9By9.Difficulty.EASY);
        manager.saveGame("SameName", testBoard2, testPreFilled2, 1, 200, 2,
                SudokuBoard9By9.Difficulty.MEDIUM);
        manager.saveGame("SameName", testBoard1, testPreFilled1, 2, 300, 1,
                SudokuBoard9By9.Difficulty.HARD);

        GameSaveManager.SavedGameData easy = manager.loadGame("SameName",
                SudokuBoard9By9.Difficulty.EASY);
        GameSaveManager.SavedGameData medium = manager.loadGame("SameName",
                SudokuBoard9By9.Difficulty.MEDIUM);
        GameSaveManager.SavedGameData hard = manager.loadGame("SameName",
                SudokuBoard9By9.Difficulty.HARD);

        assertNotNull("Easy game should exist", easy);
        assertNotNull("Medium game should exist", medium);
        assertNotNull("Hard game should exist", hard);

        assertEquals("Easy mistakes should be 0", 0, easy.mistakes);
        assertEquals("Medium mistakes should be 1", 1, medium.mistakes);
        assertEquals("Hard mistakes should be 2", 2, hard.mistakes);

        assertEquals("Easy time should be 100", 100, easy.elapsedSeconds);
        assertEquals("Medium time should be 200", 200, medium.elapsedSeconds);
        assertEquals("Hard time should be 300", 300, hard.elapsedSeconds);

        System.out.println("✅ PASSED: Difficulty separation");
    }

    // ========== DATA INTEGRITY TESTS ==========

    @Test
    public void testBoardDataIntegrity() {
        System.out.println("\n=== TEST: Board Data Integrity ===");

        int[][] complexBoard = new int[9][9];
        // Fill with known pattern
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                complexBoard[i][j] = (i * 9 + j) % 10;
            }
        }

        manager.saveGame("IntegrityTest", complexBoard, testPreFilled1, 0, 100, 3,
                SudokuBoard9By9.Difficulty.EASY);

        GameSaveManager.SavedGameData loaded = manager.loadGame("IntegrityTest",
                SudokuBoard9By9.Difficulty.EASY);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                assertEquals("Cell [" + i + "][" + j + "] should match",
                        complexBoard[i][j], loaded.board[i][j]);
            }
        }

        System.out.println("✅ PASSED: Board data integrity");
    }

    @Test
    public void testPreFilledDataIntegrity() {
        System.out.println("\n=== TEST: PreFilled Data Integrity ===");

        boolean[][] complexPreFilled = new boolean[9][9];
        // Checkerboard pattern
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                complexPreFilled[i][j] = ((i + j) % 2 == 0);
            }
        }

        manager.saveGame("PreFilledTest", testBoard1, complexPreFilled, 0, 100, 3,
                SudokuBoard9By9.Difficulty.MEDIUM);

        GameSaveManager.SavedGameData loaded = manager.loadGame("PreFilledTest",
                SudokuBoard9By9.Difficulty.MEDIUM);

        // Verify every cell
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                assertEquals("PreFilled [" + i + "][" + j + "] should match",
                        complexPreFilled[i][j], loaded.preFilledCells[i][j]);
            }
        }

        System.out.println("✅ PASSED: PreFilled data integrity");
    }

    @Test
    public void testZeroValuesPreserved() {
        System.out.println("\n=== TEST: Zero Values Preserved ===");

        int[][] boardWithZeros = new int[9][9];
        // Half zeros, half values
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                boardWithZeros[i][j] = (i < 5) ? 0 : (j + 1);
            }
        }

        manager.saveGame("ZeroTest", boardWithZeros, testPreFilled1, 0, 100, 3,
                SudokuBoard9By9.Difficulty.EASY);

        GameSaveManager.SavedGameData loaded = manager.loadGame("ZeroTest",
                SudokuBoard9By9.Difficulty.EASY);

        assertBoardEquals("Board with zeros should match", boardWithZeros, loaded.board);

        System.out.println("✅ PASSED: Zero values preserved");
    }

    // ========== HINTS PRESERVATION TEST ==========

    @Test
    public void testHintsPreservation() {
        System.out.println("\n=== TEST: Hints Preservation (Case Sensitivity Fix) ===");

        // Test all possible hint values (0-3)
        for (int hints = 0; hints <= 3; hints++) {
            String gameName = "HintsTest" + hints;
            manager.saveGame(gameName, testBoard1, testPreFilled1, 0, 100, hints,
                    SudokuBoard9By9.Difficulty.HARD);

            GameSaveManager.SavedGameData loaded = manager.loadGame(gameName,
                    SudokuBoard9By9.Difficulty.HARD);

            assertEquals("Hints value " + hints + " should be preserved",
                    hints, loaded.hints);
        }

        System.out.println("✅ PASSED: Hints preserved correctly");
    }

    // ========== EDGE CASES ==========

    @Test
    public void testEmptyGameName() {
        System.out.println("\n=== TEST: Empty Game Name ===");

        boolean saved = manager.saveGame("", testBoard1, testPreFilled1, 0, 100, 3,
                SudokuBoard9By9.Difficulty.EASY);

        // Should still save (no validation in saveGame method)
        assertTrue("Empty name should technically save", saved);

        System.out.println("✅ PASSED: Empty name handling");
    }

    @Test
    public void testSpecialCharactersInName() {
        System.out.println("\n=== TEST: Special Characters in Name ===");

        String specialName = "Test\"Game\\With/Special:Chars*?<>|";
        manager.saveGame(specialName, testBoard1, testPreFilled1, 0, 100, 3,
                SudokuBoard9By9.Difficulty.EASY);

        GameSaveManager.SavedGameData loaded = manager.loadGame(specialName,
                SudokuBoard9By9.Difficulty.EASY);

        assertNotNull("Game with special chars should load", loaded);
        assertEquals("Name with special chars should match", specialName, loaded.name);

        System.out.println("✅ PASSED: Special characters handled");
    }

    @Test
    public void testMaximumValues() {
        System.out.println("\n=== TEST: Maximum Values ===");

        int maxMistakes = Integer.MAX_VALUE;
        int maxTime = Integer.MAX_VALUE;

        manager.saveGame("MaxTest", testBoard1, testPreFilled1, maxMistakes, maxTime, 3,
                SudokuBoard9By9.Difficulty.EASY);

        GameSaveManager.SavedGameData loaded = manager.loadGame("MaxTest",
                SudokuBoard9By9.Difficulty.EASY);

        assertEquals("Max mistakes should be preserved", maxMistakes, loaded.mistakes);
        assertEquals("Max time should be preserved", maxTime, loaded.elapsedSeconds);

        System.out.println("✅ PASSED: Maximum values");
    }

    @Test
    public void testLoadNonExistentGame() {
        System.out.println("\n=== TEST: Load Non-Existent Game ===");

        GameSaveManager.SavedGameData loaded = manager.loadGame("DoesNotExist",
                SudokuBoard9By9.Difficulty.EASY);

        assertNull("Non-existent game should return null", loaded);

        System.out.println("✅ PASSED: Non-existent game returns null");
    }

    @Test
    public void testLoadFromEmptyFile() {
        System.out.println("\n=== TEST: Load from Empty/Non-Existent File ===");

        List<GameSaveManager.SavedGameData> games =
                manager.loadAllGames(SudokuBoard9By9.Difficulty.HARD);

        assertNotNull("Should return empty list, not null", games);
        assertEquals("Should return empty list", 0, games.size());

        System.out.println("✅ PASSED: Empty file handling");
    }

    // ========== FORMATTED OUTPUT TESTS ==========

    @Test
    public void testFormattedDate() {
        System.out.println("\n=== TEST: Formatted Date ===");

        manager.saveGame("DateFormatTest", testBoard1, testPreFilled1, 0, 100, 3,
                SudokuBoard9By9.Difficulty.EASY);

        GameSaveManager.SavedGameData loaded = manager.loadGame("DateFormatTest",
                SudokuBoard9By9.Difficulty.EASY);

        String formattedDate = loaded.getFormattedDate();
        assertNotNull("Formatted date should not be null", formattedDate);
        assertFalse("Formatted date should not be empty", formattedDate.isEmpty());

        // Should contain date elements
        assertTrue("Should contain month",
                formattedDate.matches(".*[A-Za-z]{3}.*")); // Month abbreviation
        assertTrue("Should contain year",
                formattedDate.matches(".*\\d{4}.*")); // Year

        System.out.println("Formatted date: " + formattedDate);
        System.out.println("✅ PASSED: Date formatting");
    }

    @Test
    public void testFormattedTime() {
        System.out.println("\n=== TEST: Formatted Time ===");

        // Test various time values
        int[] testTimes = {0, 59, 60, 61, 3599, 3600, 3661};
        String[] expectedFormats = {"00:00", "00:59", "01:00", "01:01", "59:59", "60:00", "61:01"};

        for (int i = 0; i < testTimes.length; i++) {
            String gameName = "TimeFormat" + i;
            manager.saveGame(gameName, testBoard1, testPreFilled1, 0, testTimes[i], 3,
                    SudokuBoard9By9.Difficulty.EASY);

            GameSaveManager.SavedGameData loaded = manager.loadGame(gameName,
                    SudokuBoard9By9.Difficulty.EASY);

            String formatted = loaded.getFormattedTime();
            assertEquals("Time " + testTimes[i] + " should format as " + expectedFormats[i],
                    expectedFormats[i], formatted);
        }

        System.out.println("✅ PASSED: Time formatting");
    }

    // ========== HELPER METHODS ==========

    private int[][] createTestBoard(int variant) {
        int[][] board = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // Create a pattern that varies based on variant
                board[i][j] = ((i + j + variant) % 10);
            }
        }
        return board;
    }

    private boolean[][] createTestPreFilled(boolean pattern1) {
        boolean[][] preFilled = new boolean[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (pattern1) {
                    preFilled[i][j] = ((i + j) % 3 == 0);
                } else {
                    preFilled[i][j] = ((i * j) % 2 == 0);
                }
            }
        }
        return preFilled;
    }

    private void assertBoardEquals(String message, int[][] expected, int[][] actual) {
        assertNotNull(message + " - expected board should not be null", expected);
        assertNotNull(message + " - actual board should not be null", actual);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                assertEquals(message + " at [" + i + "][" + j + "]",
                        expected[i][j], actual[i][j]);
            }
        }
    }

    private void assertPreFilledEquals(String message, boolean[][] expected, boolean[][] actual) {
        assertNotNull(message + " - expected preFilled should not be null", expected);
        assertNotNull(message + " - actual preFilled should not be null", actual);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                assertEquals(message + " at [" + i + "][" + j + "]",
                        expected[i][j], actual[i][j]);
            }
        }
    }

    // ========== PERFORMANCE TEST (Optional) ==========

    @Test
    public void testLargeNumberOfGames() {
        System.out.println("\n=== TEST: Save/Load Many Games ===");

        int numGames = 50;
        long startSave = System.currentTimeMillis();

        // Save many games
        for (int i = 0; i < numGames; i++) {
            manager.saveGame("PerfTest" + i, testBoard1, testPreFilled1, i, i * 10, 3,
                    SudokuBoard9By9.Difficulty.EASY);
        }

        long endSave = System.currentTimeMillis();
        long saveTime = endSave - startSave;

        // Load all games
        long startLoad = System.currentTimeMillis();
        List<GameSaveManager.SavedGameData> allGames =
                manager.loadAllGames(SudokuBoard9By9.Difficulty.EASY);
        long endLoad = System.currentTimeMillis();
        long loadTime = endLoad - startLoad;

        assertEquals("Should have " + numGames + " games", numGames, allGames.size());

        System.out.println("Saved " + numGames + " games in " + saveTime + "ms");
        System.out.println("Loaded " + numGames + " games in " + loadTime + "ms");
        System.out.println("✅ PASSED: Large number of games");
    }
}