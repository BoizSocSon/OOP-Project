package Engine;

import Utils.FileManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Quản lý điểm số cao (high scores) của trò chơi.
 * Lớp này chịu trách nhiệm lưu trữ, đọc, ghi điểm cao vào file và quản lý bảng xếp hạng.
 */
public class HighScoreManager {
    /** Danh sách lưu trữ các entry điểm cao. */
    private List<HighScoreEntry> highScores;
    /** Số lượng entry tối đa được lưu trữ trong bảng xếp hạng. */
    private static final int MAX_ENTRIES = 10;
    /** Tên file dùng để lưu trữ dữ liệu điểm cao. */
    private static final String SAVE_FILE = "highscores.dat";
    /** Định dạng ngày tháng dùng để hiển thị (MM/dd/yyyy). */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    /**
     * Lớp nội bộ (Inner class) để lưu trữ thông tin một entry điểm cao.
     * Mỗi entry bao gồm hạng, tên người chơi, điểm số và ngày đạt được.
     */
    public static class HighScoreEntry {
        private int rank;
        private String playerName;
        private int score;
        private LocalDate date;

        /**
         * Constructor tạo một entry điểm cao mới.
         * @param playerName Tên người chơi.
         * @param score Điểm số.
         * @param date Ngày đạt được.
         */
        public HighScoreEntry(String playerName, int score, LocalDate date) {
            this.playerName = playerName;
            this.score = score;
            this.date = date;
        }

        // --- Getters & Setters ---

        /**
         * Lấy hạng (rank) của entry này.
         * @return Hạng.
         */
        public int getRank() {
            return rank;
        }

        /**
         * Thiết lập hạng (rank) cho entry này.
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
         * Lấy đối tượng ngày (LocalDate) đạt được điểm.
         * @return Ngày.
         */
        public LocalDate getDate() {
            return date;
        }

        /**
         * Lấy ngày đạt điểm dưới dạng chuỗi được định dạng (MM/dd/yyyy).
         * @return Ngày được định dạng.
         */
        public String getFormattedDate() {
            return date.format(DATE_FORMATTER);
        }

        /**
         * Trả về biểu diễn chuỗi của entry, dùng cho việc lưu file.
         * Định dạng: "rank|name|score|date"
         * @return Chuỗi dữ liệu entry.
         */
        @Override
        public String toString() {
            return rank + "|" + playerName + "|" + score + "|" + date.toString();
        }

        /**
         * Phân tích một entry từ chuỗi dữ liệu (đọc từ file).
         * @param line Chuỗi dữ liệu có định dạng: "rank|name|score|date"
         * @return HighScoreEntry được tạo từ chuỗi, hoặc null nếu không hợp lệ.
         */
        public static HighScoreEntry fromString(String line) {
            try {
                String[] parts = line.split("\\|"); // Tách chuỗi bằng dấu "|"
                if (parts.length >= 4) {
                    // Phân tích các thành phần
                    int rank = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    int score = Integer.parseInt(parts[2]);
                    LocalDate date = LocalDate.parse(parts[3]);

                    HighScoreEntry entry = new HighScoreEntry(name, score, date);
                    entry.setRank(rank); // Thiết lập hạng
                    return entry;
                }
            } catch (Exception e) {
                // In lỗi ra console nếu parsing thất bại
                System.err.println("Error parsing high score entry: " + line);
            }
            return null;
        }
    }

    /**
     * Constructor của HighScoreManager.
     * Khởi tạo danh sách điểm cao và tải dữ liệu từ file.
     */
    public HighScoreManager() {
        this.highScores = new ArrayList<>();
        loadFromFile(); // Tải điểm cao khi khởi tạo
    }

    /**
     * Thêm một điểm mới vào danh sách điểm cao.
     * Điểm sẽ được thêm vào nếu đủ điều kiện (nằm trong top MAX_ENTRIES).
     * @param playerName Tên người chơi.
     * @param score Điểm số đạt được.
     * @param date Ngày đạt được.
     * @return true nếu điểm được thêm vào top scores và lưu lại, ngược lại là false.
     */
    public boolean addScore(String playerName, int score, LocalDate date) {
        // Đặt tên mặc định nếu tên người chơi rỗng
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "ANONYMOUS";
        }

        // Tạo entry mới
        HighScoreEntry newEntry = new HighScoreEntry(playerName.toUpperCase(), score, date);

        // Kiểm tra điều kiện: (1) Danh sách chưa đầy HOẶC (2) Điểm mới cao hơn điểm thấp nhất trong danh sách
        if (highScores.size() < MAX_ENTRIES || score > highScores.get(highScores.size() - 1).getScore()) {
            highScores.add(newEntry);

            // Sắp xếp lại danh sách theo điểm (giảm dần: cao xuống thấp)
            Collections.sort(highScores, Comparator.comparingInt(HighScoreEntry::getScore).reversed());

            // Giữ lại tối đa MAX_ENTRIES entries
            if (highScores.size() > MAX_ENTRIES) {
                highScores = highScores.subList(0, MAX_ENTRIES);
            }

            updateRanks(); // Cập nhật lại hạng cho tất cả entries

            saveToFile(); // Lưu danh sách điểm cao mới vào file

            return true;
        }

        return false;
    }

    /**
     * Cập nhật hạng (rank) cho tất cả entries trong danh sách dựa trên vị trí của chúng.
     */
    private void updateRanks() {
        for (int i = 0; i < highScores.size(); i++) {
            highScores.get(i).setRank(i + 1); // Rank bắt đầu từ 1
        }
    }

    /**
     * Lấy danh sách N điểm cao nhất.
     * @param count Số lượng entries muốn lấy.
     * @return Danh sách các HighScoreEntry (được cắt theo số lượng yêu cầu).
     */
    public List<HighScoreEntry> getTopScores(int count) {
        int limit = Math.min(count, highScores.size());
        // Trả về bản sao của danh sách con
        return new ArrayList<>(highScores.subList(0, limit));
    }

    /**
     * Lấy toàn bộ danh sách điểm cao hiện tại.
     * @return Danh sách tất cả các HighScoreEntry.
     */
    public List<HighScoreEntry> getAllScores() {
        // Trả về bản sao để tránh chỉnh sửa danh sách gốc từ bên ngoài
        return new ArrayList<>(highScores);
    }

    /**
     * Kiểm tra xem một điểm số có đủ điều kiện để lọt vào bảng điểm cao không.
     * @param score Điểm cần kiểm tra.
     * @return true nếu điểm đủ điều kiện (danh sách chưa đầy hoặc điểm cao hơn điểm thấp nhất).
     */
    public boolean isHighScore(int score) {
        if (highScores.size() < MAX_ENTRIES) {
            return true; // Nếu danh sách chưa đầy, mọi điểm đều là high score
        }
        // So sánh với điểm thấp nhất trong top
        return score > highScores.get(highScores.size() - 1).getScore();
    }

    /**
     * Lấy điểm số cao nhất hiện tại.
     * @return Điểm số cao nhất, hoặc 0 nếu chưa có scores nào được lưu.
     */
    public int getHighestScore() {
        if (highScores.isEmpty()) {
            return 0;
        }
        return highScores.get(0).getScore(); // Điểm cao nhất luôn ở vị trí 0 sau khi sắp xếp
    }

    /**
     * Lưu danh sách điểm cao hiện tại vào file sử dụng FileManager.
     */
    private void saveToFile() {
        List<String> lines = new ArrayList<>();
        // Chuyển đổi mỗi entry thành chuỗi
        for (HighScoreEntry entry : highScores) {
            lines.add(entry.toString());
        }
        // Ghi tất cả các dòng vào file
        FileManager.writeLinesToFile(SAVE_FILE, lines);
    }

    /**
     * Đọc danh sách điểm cao từ file.
     */
    private void loadFromFile() {
        List<String> lines = FileManager.readLinesFromFile(SAVE_FILE);

        if (lines != null && !lines.isEmpty()) {
            highScores.clear();
            // Phân tích từng dòng thành HighScoreEntry
            for (String line : lines) {
                HighScoreEntry entry = HighScoreEntry.fromString(line);
                if (entry != null) {
                    highScores.add(entry);
                }
            }
            updateRanks(); // Đảm bảo hạng được cập nhật chính xác sau khi tải
        } else {
            // Nếu không có file hoặc file rỗng, tạo điểm mặc định
            createDefaultScores();
        }
    }

    /**
     * Tạo một số điểm cao mặc định để hiển thị khi chưa có dữ liệu lưu trữ.
     */
    private void createDefaultScores() {
        highScores.clear();
        LocalDate today = LocalDate.now();

        // Tạo và thêm các điểm mặc định (hàm addScore sẽ tự động sắp xếp và cập nhật rank)
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
     * Đặt lại (reset) tất cả high scores về điểm mặc định và lưu vào file.
     */
    public void reset() {
        highScores.clear();
        createDefaultScores(); // Tạo lại điểm mặc định
        saveToFile(); // Lưu lại file
    }
}