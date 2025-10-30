package Engine;

import Utils.FileManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Quản lý high scores của game.
 * Lưu trữ, đọc, ghi điểm cao vào file.
 */
public class HighScoreManager {
    private List<HighScoreEntry> highScores;
    private static final int MAX_ENTRIES = 10;
    private static final String SAVE_FILE = "highscores.dat";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    /**
     * Inner class để lưu trữ thông tin một entry high score.
     */
    public static class HighScoreEntry {
        private int rank;
        private String playerName;
        private int score;
        private LocalDate date;

        public HighScoreEntry(String playerName, int score, LocalDate date) {
            this.playerName = playerName;
            this.score = score;
            this.date = date;
        }

        // Getters
        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public String getPlayerName() {
            return playerName;
        }

        public int getScore() {
            return score;
        }

        public LocalDate getDate() {
            return date;
        }

        public String getFormattedDate() {
            return date.format(DATE_FORMATTER);
        }

        @Override
        public String toString() {
            return rank + "|" + playerName + "|" + score + "|" + date.toString();
        }

        /**
         * Parse một entry từ String.
         * @param line String format: "rank|name|score|date"
         * @return HighScoreEntry hoặc null nếu invalid
         */
        public static HighScoreEntry fromString(String line) {
            try {
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    int rank = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    int score = Integer.parseInt(parts[2]);
                    LocalDate date = LocalDate.parse(parts[3]);

                    HighScoreEntry entry = new HighScoreEntry(name, score, date);
                    entry.setRank(rank);
                    return entry;
                }
            } catch (Exception e) {
                System.err.println("Error parsing high score entry: " + line);
            }
            return null;
        }
    }

    /**
     * Constructor.
     */
    public HighScoreManager() {
        this.highScores = new ArrayList<>();
        loadFromFile();
    }

    /**
     * Thêm điểm mới vào danh sách high scores.
     * @param playerName Tên người chơi
     * @param score Điểm số
     * @param date Ngày đạt được
     * @return true nếu score được thêm vào top scores
     */
    public boolean addScore(String playerName, int score, LocalDate date) {
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "ANONYMOUS";
        }

        // Tạo entry mới
        HighScoreEntry newEntry = new HighScoreEntry(playerName.toUpperCase(), score, date);

        // Kiểm tra xem có đủ điều kiện vào top scores không
        if (highScores.size() < MAX_ENTRIES || score > highScores.get(highScores.size() - 1).getScore()) {
            highScores.add(newEntry);

            // Sort lại theo điểm (cao xuống thấp)
            Collections.sort(highScores, Comparator.comparingInt(HighScoreEntry::getScore).reversed());

            // Giữ lại tối đa MAX_ENTRIES
            if (highScores.size() > MAX_ENTRIES) {
                highScores = highScores.subList(0, MAX_ENTRIES);
            }

            // Update rank
            updateRanks();

            // Save to file
            saveToFile();

            return true;
        }

        return false;
    }

    /**
     * Cập nhật rank cho tất cả entries.
     */
    private void updateRanks() {
        for (int i = 0; i < highScores.size(); i++) {
            highScores.get(i).setRank(i + 1);
        }
    }

    /**
     * Lấy danh sách top scores.
     * @param count Số lượng entries muốn lấy
     * @return List các HighScoreEntry
     */
    public List<HighScoreEntry> getTopScores(int count) {
        int limit = Math.min(count, highScores.size());
        return new ArrayList<>(highScores.subList(0, limit));
    }

    /**
     * Lấy tất cả high scores.
     * @return List các HighScoreEntry
     */
    public List<HighScoreEntry> getAllScores() {
        return new ArrayList<>(highScores);
    }

    /**
     * Kiểm tra xem một điểm có phải là high score không.
     * @param score Điểm cần kiểm tra
     * @return true nếu là high score
     */
    public boolean isHighScore(int score) {
        if (highScores.size() < MAX_ENTRIES) {
            return true;
        }
        return score > highScores.get(highScores.size() - 1).getScore();
    }

    /**
     * Lấy điểm cao nhất.
     * @return Điểm cao nhất, hoặc 0 nếu chưa có scores
     */
    public int getHighestScore() {
        if (highScores.isEmpty()) {
            return 0;
        }
        return highScores.get(0).getScore();
    }

    /**
     * Lưu high scores vào file.
     */
    private void saveToFile() {
        List<String> lines = new ArrayList<>();
        for (HighScoreEntry entry : highScores) {
            lines.add(entry.toString());
        }
        FileManager.writeLinesToFile(SAVE_FILE, lines);
    }

    /**
     * Đọc high scores từ file.
     */
    private void loadFromFile() {
        List<String> lines = FileManager.readLinesFromFile(SAVE_FILE);

        if (lines != null && !lines.isEmpty()) {
            highScores.clear();
            for (String line : lines) {
                HighScoreEntry entry = HighScoreEntry.fromString(line);
                if (entry != null) {
                    highScores.add(entry);
                }
            }
            updateRanks();
        } else {
            // Nếu không có file hoặc file empty, tạo default scores
            createDefaultScores();
        }
    }

    /**
     * Tạo default high scores.
     */
    private void createDefaultScores() {
        highScores.clear();
        LocalDate today = LocalDate.now();

        // Tạo một số default scores
        addScore("STEVE", 50000, today.minusDays(7));
        addScore("ALICE", 45000, today.minusDays(6));
        addScore("BOB", 40000, today.minusDays(5));
        addScore("CHARLIE", 35000, today.minusDays(4));
        addScore("DIANA", 30000, today.minusDays(3));
        addScore("EVAN", 25000, today.minusDays(2));
        addScore("FIONA", 20000, today.minusDays(1));
        addScore("GEORGE", 15000, today);
        addScore("HANNAH", 10000, today);
        addScore("IAN", 5000, today);
    }

    /**
     * Reset tất cả high scores.
     */
    public void reset() {
        highScores.clear();
        createDefaultScores();
        saveToFile();
    }
}
