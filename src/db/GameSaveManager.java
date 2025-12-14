package db;

import components.SudokuBoard9By9;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class GameSaveManager {
    private static final String APP_NAME = "SudokuGame";
    private static final String SAVE_DIRECTORY = getSaveDirectory();
    private static final String EASY_FILE = "easy_games.json";
    private static final String MEDIUM_FILE = "medium_games.json";
    private static final String HARD_FILE = "hard_games.json";

    /**
     * Get platform-appropriate save directory.
     * Windows: C:/Users/[User]/AppData/Local/SudokuGame/saves/
     * Mac: /Users/[User]/Library/Application Support/SudokuGame/saves/
     * Linux: /home/[user]/.local/share/SudokuGame/saves/
     */
    private static String getSaveDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");
        String baseDir;

        if (os.contains("win")) {
            // Windows: AppData/Local
            String appData = System.getenv("LOCALAPPDATA");
            if (appData == null) {
                appData = userHome + File.separator + "AppData" + File.separator + "Local";
            }
            baseDir = appData + File.separator + APP_NAME;
        } else if (os.contains("mac")) {
            // macOS: Library/Application Support
            baseDir = userHome + File.separator + "Library" + File.separator +
                    "Application Support" + File.separator + APP_NAME;
        } else {
            // Linux/Unix: .local/share (follows XDG Base Directory specification)
            String xdgDataHome = System.getenv("XDG_DATA_HOME");
            if (xdgDataHome != null && !xdgDataHome.isEmpty()) {
                baseDir = xdgDataHome + File.separator + APP_NAME;
            } else {
                baseDir = userHome + File.separator + ".local" + File.separator +
                        "share" + File.separator + APP_NAME;
            }
        }

        return baseDir + File.separator + "saves" + File.separator;
    }

    public GameSaveManager() {
        migrateOldSaves();  // Check for saves in old location
        createSaveDirectory();
        System.out.println("Game saves location: " + SAVE_DIRECTORY);
    }

    /**
     * Migrate saves from old "saves/" folder (project root) to new location.
     * This helps existing users transition smoothly.
     */
    private void migrateOldSaves() {
        File oldSaves = new File("saves");
        if (oldSaves.exists() && oldSaves.isDirectory()) {
            try {
                Path newLocation = Paths.get(SAVE_DIRECTORY);
                Files.createDirectories(newLocation);

                int migratedCount = 0;
                File[] files = oldSaves.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".json")) {
                            Path targetPath = newLocation.resolve(file.getName());
                            // Only copy if target doesn't exist (don't overwrite newer saves)
                            if (!Files.exists(targetPath)) {
                                Files.copy(file.toPath(), targetPath);
                                migratedCount++;
                            }
                        }
                    }
                }

                if (migratedCount > 0) {
                    System.out.println("Migrated " + migratedCount + " save file(s) to: " + SAVE_DIRECTORY);
                }
            } catch (IOException e) {
                System.err.println("Warning: Failed to migrate old saves: " + e.getMessage());
                // Continue anyway - not critical if migration fails
            }
        }
    }

    private void createSaveDirectory() {
        try {
            Path savePath = Paths.get(SAVE_DIRECTORY);
            Files.createDirectories(savePath);

            // Test write permission
            File testFile = new File(SAVE_DIRECTORY + ".test");
            if (testFile.createNewFile()) {
                testFile.delete();
            }
        } catch (IOException e) {
            System.err.println("ERROR: Cannot create or write to save directory: " + SAVE_DIRECTORY);
            System.err.println("       Saves may not work correctly!");
            e.printStackTrace();
        }
    }

    private String getFilenameForDifficulty(SudokuBoard9By9.Difficulty difficulty) {
        return SAVE_DIRECTORY + switch (difficulty) {
            case EASY -> EASY_FILE;
            case MEDIUM -> MEDIUM_FILE;
            case HARD -> HARD_FILE;
        };
    }

    public boolean saveGame(String gameName, int[][] board, boolean[][] preFilledCells,
                            int mistakes, int elapsedSeconds, int hints, SudokuBoard9By9.Difficulty difficulty) {
        try {
            List<SavedGameData> games = loadAllGames(difficulty);

            // Remove any existing game with the same name (overwrite)
            games.removeIf(game -> game.name.equals(gameName));

            // Add the new game (uses current timestamp)
            SavedGameData newGame = new SavedGameData(
                    gameName, board, preFilledCells, mistakes, elapsedSeconds, hints, difficulty
            );
            games.add(newGame);

            // Write back to file
            writeGamesToFile(games, difficulty);

            return true;

        } catch (Exception e) {
            System.err.println("ERROR saving game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load a specific game by name and difficulty
     */
    public SavedGameData loadGame(String gameName, SudokuBoard9By9.Difficulty difficulty) {
        List<SavedGameData> games = loadAllGames(difficulty);
        return games.stream()
                .filter(game -> game.name.equals(gameName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Load all games for a specific difficulty
     */
    public List<SavedGameData> loadAllGames(SudokuBoard9By9.Difficulty difficulty) {
        String filename = getFilenameForDifficulty(difficulty);
        File file = new File(filename);

        if (!file.exists()) {
            System.out.println("DEBUG: File does not exist: " + filename);
            return new ArrayList<>();
        }

        try {
            String content = Files.readString(Paths.get(filename));
            List<SavedGameData> games = parseGamesFromJson(content, difficulty);

            return games;
        } catch (IOException e) {
            System.err.println("ERROR reading file: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Get list of game names for a specific difficulty
     */
    public List<String> getGameNames(SudokuBoard9By9.Difficulty difficulty) {
        List<SavedGameData> games = loadAllGames(difficulty);
        return games.stream()
                .map(game -> game.name)
                .sorted()
                .toList();
    }

    /**
     * Delete a specific game
     */
    public boolean deleteGame(String gameName, SudokuBoard9By9.Difficulty difficulty) {
        try {
            List<SavedGameData> games = loadAllGames(difficulty);
            boolean removed = games.removeIf(game -> game.name.equals(gameName));

            if (removed) {
                writeGamesToFile(games, difficulty);
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean gameExists(String gameName, SudokuBoard9By9.Difficulty difficulty) {
        return getGameNames(difficulty).contains(gameName);
    }

    private void writeGamesToFile(List<SavedGameData> games, SudokuBoard9By9.Difficulty difficulty)
            throws IOException {
        String filename = getFilenameForDifficulty(difficulty);

        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"difficulty\": \"").append(difficulty).append("\",\n");
        json.append("  \"games\": [\n");

        for (int i = 0; i < games.size(); i++) {
            SavedGameData game = games.get(i);
            json.append("    {\n");
            json.append("      \"name\": \"").append(escapeJson(game.name)).append("\",\n");
            json.append("      \"saveDate\": ").append(game.saveDate).append(",\n");
            json.append("      \"mistakes\": ").append(game.mistakes).append(",\n");
            json.append("      \"elapsedSeconds\": ").append(game.elapsedSeconds).append(",\n");
            json.append("      \"hints\": ").append(game.hints).append(",\n");

            // Board array
            json.append("      \"board\": [\n");
            for (int row = 0; row < 9; row++) {
                json.append("        [");
                for (int col = 0; col < 9; col++) {
                    json.append(game.board[row][col]);
                    if (col < 8) json.append(", ");
                }
                json.append("]");
                if (row < 8) json.append(",");
                json.append("\n");
            }
            json.append("      ],\n");

            // PreFilled array
            json.append("      \"preFilled\": [\n");
            for (int row = 0; row < 9; row++) {
                json.append("        [");
                for (int col = 0; col < 9; col++) {
                    json.append(game.preFilledCells[row][col] ? "true" : "false");
                    if (col < 8) json.append(", ");
                }
                json.append("]");
                if (row < 8) json.append(",");
                json.append("\n");
            }
            json.append("      ]\n");

            json.append("    }");
            if (i < games.size() - 1) json.append(",");
            json.append("\n");
        }

        json.append("  ]\n");
        json.append("}\n");

        Files.writeString(Paths.get(filename), json.toString());
        System.out.println("DEBUG: Wrote " + games.size() + " games to " + filename);
    }

    private List<SavedGameData> parseGamesFromJson(String json, SudokuBoard9By9.Difficulty difficulty) {
        List<SavedGameData> games = new ArrayList<>();

        try {
            int gamesStart = json.indexOf("\"games\": [");
            if (gamesStart == -1) {
                System.err.println("ERROR: Could not find 'games' array in JSON");
                return games;
            }

            // Extract just the games array content
            int arrayStart = json.indexOf("[", gamesStart);
            int arrayEnd = findMatchingBracket(json, arrayStart);

            if (arrayEnd == -1) {
                System.err.println("ERROR: Could not find closing bracket for games array");
                return games;
            }

            String gamesSection = json.substring(arrayStart + 1, arrayEnd);

            // Split by game objects
            List<String> gameObjects = extractGameObjects(gamesSection);

            for (String gameJson : gameObjects) {
                try {
                    SavedGameData game = parseGameObject(gameJson, difficulty);
                    if (game != null) {
                        games.add(game);
                    }
                } catch (Exception e) {
                    System.err.println("ERROR parsing game object: " + e.getMessage());
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            System.err.println("ERROR parsing JSON: " + e.getMessage());
            e.printStackTrace();
        }

        return games;
    }

    private int findMatchingBracket(String json, int openBracketPos) {
        int depth = 0;
        for (int i = openBracketPos; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[' || c == '{') depth++;
            else if (c == ']' || c == '}') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    private List<String> extractGameObjects(String gamesSection) {
        List<String> objects = new ArrayList<>();
        int depth = 0;
        int start = -1;

        for (int i = 0; i < gamesSection.length(); i++) {
            char c = gamesSection.charAt(i);

            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start != -1) {
                    objects.add(gamesSection.substring(start, i + 1));
                    start = -1;
                }
            }
        }

        return objects;
    }

    private SavedGameData parseGameObject(String gameJson, SudokuBoard9By9.Difficulty difficulty) {
        try {
            String name = extractStringValue(gameJson, "name");
            long saveDate = extractLongValue(gameJson, "saveDate");
            int mistakes = (int) extractLongValue(gameJson, "mistakes");
            int elapsedSeconds = (int) extractLongValue(gameJson, "elapsedSeconds");
            int hints = (int) extractLongValue(gameJson, "hints");

            int[][] board = extractBoardArray(gameJson, "board");
            boolean[][] preFilled = extractBooleanArray(gameJson, "preFilled");

            if (name.isEmpty()) {
                System.err.println("WARNING: Game with empty name, skipping");
                return null;
            }

            if (board == null || preFilled == null) {
                System.err.println("WARNING: Game with null arrays, skipping");
                return null;
            }

            return new SavedGameData(name, board, preFilled, mistakes,
                    elapsedSeconds, hints, difficulty, saveDate);

        } catch (Exception e) {
            System.err.println("ERROR parsing game object: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String extractStringValue(String json, String key) {
        String searchKey = "\"" + key + "\": \"";
        int start = json.indexOf(searchKey);
        if (start == -1) return "";

        start += searchKey.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return "";

        return unescapeJson(json.substring(start, end));
    }

    private long extractLongValue(String json, String key) {
        String searchKey = "\"" + key + "\": ";
        int start = json.indexOf(searchKey);
        if (start == -1) return 0;

        start += searchKey.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("\n", start);
        if (end == -1) end = json.length();

        String value = json.substring(start, end).trim();
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            System.err.println("ERROR parsing long value for key '" + key + "': " + value);
            return 0;
        }
    }

    /**
     *
     * @param json
     * @param key
     * @return the board in the current game
     * it finds the starting key and reads until the end
     */
    private int[][] extractBoardArray(String json, String key) {
        int[][] board = new int[9][9];
        String searchKey = "\"" + key + "\": ";
        int start = json.indexOf(searchKey);
        if (start == -1) {
            System.err.println("ERROR: Could not find key: " + key);
            return board;
        }

        start += searchKey.length();

        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }

        if (start >= json.length() || json.charAt(start) != '[') {
            System.err.println("ERROR: Expected '[' for " + key);
            return board;
        }

        int outerArrayEnd = findMatchingBracket(json, start);
        if (outerArrayEnd == -1) {
            System.err.println("ERROR: Could not find matching bracket for " + key);
            return board;
        }

        String boardSection = json.substring(start + 1, outerArrayEnd);


        int rowsParsed = 0;
        int pos = 0;

        while (rowsParsed < 9 && pos < boardSection.length()) {
            while (pos < boardSection.length() &&
                    (Character.isWhitespace(boardSection.charAt(pos)) || boardSection.charAt(pos) == ',')) {
                pos++;
            }

            if (pos >= boardSection.length()) break;

            if (boardSection.charAt(pos) == '[') {
                int rowStart = pos + 1;
                int rowEnd = boardSection.indexOf("]", rowStart);
                if (rowEnd == -1) {
                    System.err.println("ERROR: Could not find end of row " + rowsParsed);
                    break;
                }

                String rowStr = boardSection.substring(rowStart, rowEnd);
                String[] values = rowStr.split(",");

                int colsParsed = 0;
                for (int col = 0; col < 9 && col < values.length; col++) {
                    try {
                        board[rowsParsed][col] = Integer.parseInt(values[col].trim());
                        colsParsed++;
                    } catch (NumberFormatException e) {
                        System.err.println("ERROR parsing board value at [" + rowsParsed + "][" + col + "]: '" + values[col] + "'");
                        board[rowsParsed][col] = 0;
                    }
                }

                rowsParsed++;
                pos = rowEnd + 1;
            } else {
                // Unexpected character, skip it
                pos++;
            }
        }

        return board;
    }

    /**
     *
     * @param json
     * @param key
     * @return returns a boolean area where true means it was generated not inserted by user
     * In these method we do similar as board find key and read until its end bracket
     */
    private boolean[][] extractBooleanArray(String json, String key) {
        boolean[][] array = new boolean[9][9];
        String searchKey = "\"" + key + "\": ";
        int start = json.indexOf(searchKey);
        if (start == -1) {
            System.err.println("ERROR: Could not find key: " + key);
            return array;
        }

        start += searchKey.length();

        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }

        if (start >= json.length() || json.charAt(start) != '[') {
            System.err.println("ERROR: Expected '[' for " + key);
            return array;
        }

        int outerArrayEnd = findMatchingBracket(json, start);
        if (outerArrayEnd == -1) {
            System.err.println("ERROR: Could not find matching bracket for " + key);
            return array;
        }

        String arraySection = json.substring(start + 1, outerArrayEnd);

        int rowsParsed = 0;
        int pos = 0;

        while (rowsParsed < 9 && pos < arraySection.length()) {
            while (pos < arraySection.length() &&
                    (Character.isWhitespace(arraySection.charAt(pos)) || arraySection.charAt(pos) == ',')) {
                pos++;
            }

            if (pos >= arraySection.length()) break;

            if (arraySection.charAt(pos) == '[') {
                int rowStart = pos + 1;
                int rowEnd = arraySection.indexOf("]", rowStart);
                if (rowEnd == -1) {
                    System.err.println("ERROR: Could not find end of row " + rowsParsed);
                    break;
                }

                String rowStr = arraySection.substring(rowStart, rowEnd);
                String[] values = rowStr.split(",");

                int colsParsed = 0;
                for (int col = 0; col < 9 && col < values.length; col++) {
                    String trimmed = values[col].trim();
                    array[rowsParsed][col] = trimmed.equalsIgnoreCase("true");
                    colsParsed++;
                }

                rowsParsed++;
                pos = rowEnd + 1;
            } else {
                // Unexpected character, skip it
                pos++;
            }
        }

        return array;
    }

    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String unescapeJson(String str) {
        return str.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }

    // ========== Inner Class: Saved Game Data ==========
    public static class SavedGameData {
        public String name;
        public int[][] board;
        public boolean[][] preFilledCells;
        public int mistakes;
        public int hints;
        public int elapsedSeconds;
        public SudokuBoard9By9.Difficulty difficulty;
        public long saveDate;

        // Constructor for NEW games (creates current timestamp)
        public SavedGameData(String name, int[][] board, boolean[][] preFilledCells,
                             int mistakes, int elapsedSeconds, int hints,
                             SudokuBoard9By9.Difficulty difficulty) {
            // Delegate to full constructor with current timestamp
            this(name, board, preFilledCells, mistakes, elapsedSeconds, hints,
                    difficulty, System.currentTimeMillis());
        }

        // Constructor for LOADED games (preserves existing timestamp)
        public SavedGameData(String name, int[][] board, boolean[][] preFilledCells,
                             int mistakes, int elapsedSeconds, int hints,
                             SudokuBoard9By9.Difficulty difficulty, long saveDate) {
            this.name = name;
            this.board = copyBoard(board);
            this.preFilledCells = copyBooleanBoard(preFilledCells);
            this.mistakes = mistakes;
            this.hints = hints;
            this.elapsedSeconds = elapsedSeconds;
            this.difficulty = difficulty;
            this.saveDate = saveDate;
        }

        private int[][] copyBoard(int[][] original) {
            if (original == null) return new int[9][9];
            int[][] copy = new int[9][9];
            for (int i = 0; i < 9; i++) {
                System.arraycopy(original[i], 0, copy[i], 0, 9);
            }
            return copy;
        }

        private boolean[][] copyBooleanBoard(boolean[][] original) {
            if (original == null) return new boolean[9][9];
            boolean[][] copy = new boolean[9][9];
            for (int i = 0; i < 9; i++) {
                System.arraycopy(original[i], 0, copy[i], 0, 9);
            }
            return copy;
        }

        public String getFormattedDate() {
            return new java.text.SimpleDateFormat("MMM dd, yyyy HH:mm")
                    .format(new Date(saveDate));
        }

        public String getFormattedTime() {
            int minutes = elapsedSeconds / 60;
            int seconds = elapsedSeconds % 60;
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

}