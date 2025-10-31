package Engine;

import Utils.FileManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Lớp quản lý điểm cao (High Score Manager) của game.
 * Chịu trách nhiệm lưu trữ, đọc, ghi danh sách điểm cao (top scores) vào file.
 */
public class HighScoreManager {
    private List<HighScoreEntry> highScores; // Danh sách các entry điểm cao.
    private static final int MAX_ENTRIES = 10; // Số lượng entry điểm cao tối đa được lưu.
    private static final String SAVE_FILE = "highscores.dat"; // Tên file lưu điểm cao.
    // Định dạng ngày tháng cho hiển thị.
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    /**
     * Lớp lồng (Inner class) để lưu trữ thông tin một entry điểm cao.
     */
    public static class HighScoreEntry {
        private int rank; // Hạng của người chơi.
        private String playerName; // Tên người chơi.
        private int score; // Điểm số đạt được.
        private LocalDate date; // Ngày đạt được điểm số.

        /**
         * Constructor cho HighScoreEntry.
         *
         * @param playerName Tên người chơi.
         * @param score Điểm số.
         * @param date Ngày đạt được điểm số.
         */
        public HighScoreEntry(String playerName, int score, LocalDate date) {
            this.playerName = playerName;
            this.score = score;
            this.date = date;
        }

        // Getters
        /**
         * Lấy hạng (rank) của entry.
         * @return Hạng.
         */
        public int getRank() {
            return rank;
        }

        /**
         * Đặt hạng (rank) cho entry.
         * @param rank Hạng mới.
         */
        public void setRank(int rank) {
            this.rank = rank;
        }

        /**
         * Lấy tên người chơi.
         * @return Tên người chơi.
         */
        public String getPlayerName() {
            return playerName;
        }

        /**
         * Lấy điểm số.
         * @return Điểm số.
         */
        public int getScore() {
            return score;
        }

        /**
         * Lấy ngày đạt được điểm số.
         * @return Đối tượng LocalDate.
         */
        public LocalDate getDate() {
            return date;
        }

        /**
         * Lấy ngày đạt được điểm số theo định dạng chuỗi.
         * @return Ngày theo định dạng "MM/dd/yyyy".
         */
        public String getFormattedDate() {
            return date.format(DATE_FORMATTER);
        }

        /**
         * Chuyển đổi entry thành chuỗi để lưu file.
         * Format: "rank|name|score|date".
         *
         * @return Chuỗi biểu diễn entry.
         */
        @Override
        public String toString() {
            return rank + "|" + playerName + "|" + score + "|" + date.toString();
        }

        /**
         * Parse một entry từ String được đọc từ file.
         * @param line Chuỗi format: "rank|name|score|date".
         * @return HighScoreEntry hoặc {@code null} nếu chuỗi không hợp lệ.
         */
        public static HighScoreEntry fromString(String line) {
            try {
                String[] parts = line.split("\\|");
                // Kiểm tra đủ 4 phần tử.
                if (parts.length >= 4) {
                    // Chuyển đổi và tạo đối tượng.
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
     * Constructor. Khởi tạo danh sách và tải điểm từ file.
     */
    public HighScoreManager() {
        this.highScores = new ArrayList<>();
        loadFromFile();
    }

    /**
     * Thêm điểm mới vào danh sách high scores (nếu đủ điều kiện).
     *
     * @param playerName Tên người chơi.
     * @param score Điểm số.
     * @param date Ngày đạt được.
     * @return {@code true} nếu điểm số được thêm vào top scores, ngược lại là {@code false}.
     */
    public boolean addScore(String playerName, int score, LocalDate date) {
        // Đặt tên mặc định nếu tên người chơi trống.
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "ANONYMOUS";
        }

        // Tạo entry mới
        HighScoreEntry newEntry = new HighScoreEntry(playerName.toUpperCase(), score, date);

        // Kiểm tra xem có đủ điều kiện vào top scores không
        // (Danh sách chưa đầy HOẶC điểm mới lớn hơn điểm thấp nhất trong danh sách)
        if (highScores.size() < MAX_ENTRIES || score > highScores.get(highScores.size() - 1).getScore()) {
            highScores.add(newEntry);

            // Sắp xếp lại danh sách theo điểm số giảm dần (cao xuống thấp)
            Collections.sort(highScores, Comparator.comparingInt(HighScoreEntry::getScore).reversed());

            // Giữ lại tối đa MAX_ENTRIES
            if (highScores.size() > MAX_ENTRIES) {
                highScores = highScores.subList(0, MAX_ENTRIES);
            }

            // Cập nhật lại hạng (rank)
            updateRanks();

            // Lưu vào file
            saveToFile();

            return true;
        }

        return false;
    }

    /**
     * Cập nhật hạng (rank) cho tất cả các entry trong danh sách.
     */
    private void updateRanks() {
        for (int i = 0; i < highScores.size(); i++) {
            // Rank là chỉ mục + 1
            highScores.get(i).setRank(i + 1);
        }
    }

    /**
     * Lấy danh sách N điểm cao nhất.
     *
     * @param count Số lượng entries muốn lấy.
     * @return List các HighScoreEntry (đã được sao chép).
     */
    public List<HighScoreEntry> getTopScores(int count) {
        int limit = Math.min(count, highScores.size());
        // Trả về một List mới (sao chép)
        return new ArrayList<>(highScores.subList(0, limit));
    }

    /**
     * Lấy tất cả high scores hiện có.
     *
     * @return List các HighScoreEntry (đã được sao chép).
     */
    public List<HighScoreEntry> getAllScores() {
        return new ArrayList<>(highScores);
    }

    /**
     * Kiểm tra xem một điểm có đủ điều kiện là high score (lọt vào top MAX_ENTRIES) không.
     *
     * @param score Điểm cần kiểm tra.
     * @return {@code true} nếu điểm đủ điều kiện là high score.
     */
    public boolean isHighScore(int score) {
        // Nếu danh sách chưa đầy, mọi điểm đều là high score.
        if (highScores.size() < MAX_ENTRIES) {
            return true;
        }
        // So sánh điểm với điểm thấp nhất trong danh sách.
        return score > highScores.get(highScores.size() - 1).getScore();
    }

    /**
     * Lấy điểm cao nhất (Top 1).
     *
     * @return Điểm cao nhất, hoặc 0 nếu danh sách trống.
     */
    public int getHighestScore() {
        if (highScores.isEmpty()) {
            return 0;
        }
        return highScores.get(0).getScore();
    }

    /**
     * Lưu danh sách high scores hiện tại vào file.
     */
    private void saveToFile() {
        List<String> lines = new ArrayList<>();
        // Chuyển đổi từng entry thành chuỗi và thêm vào danh sách.
        for (HighScoreEntry entry : highScores) {
            lines.add(entry.toString());
        }
        // Ghi tất cả các dòng vào file thông qua FileManager.
        FileManager.writeLinesToFile(SAVE_FILE, lines);
    }

    /**
     * Đọc danh sách high scores từ file.
     */
    private void loadFromFile() {
        // Đọc các dòng từ file.
        List<String> lines = FileManager.readLinesFromFile(SAVE_FILE);

        if (lines != null && !lines.isEmpty()) {
            highScores.clear();
            // Parse từng dòng thành HighScoreEntry.
            for (String line : lines) {
                HighScoreEntry entry = HighScoreEntry.fromString(line);
                if (entry != null) {
                    highScores.add(entry);
                }
            }
            // Cập nhật lại rank sau khi tải.
            updateRanks();
        } else {
            // Nếu không có file hoặc file trống, tạo điểm mặc định.
            createDefaultScores();
        }
    }

    /**
     * Tạo danh sách điểm cao mặc định (Default High Scores).
     */
    private void createDefaultScores() {
        highScores.clear();
        LocalDate today = LocalDate.now();

        // Thêm một số điểm mặc định vào danh sách
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
     * Đặt lại (Reset) tất cả high scores về điểm mặc định và lưu file.
     */
    public void reset() {
        highScores.clear();
        createDefaultScores();
        saveToFile();
    }
}