package Engine;

import Objects.Bricks.BrickType;
import Rounds.*;
import Objects.Bricks.Brick;
//import Audio.MusicTrack;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp RoundsManager chịu trách nhiệm quản lý trình tự, tải và kiểm tra
 * trạng thái hoàn thành của các vòng chơi (level) trong game.
 */
public class RoundsManager {
    /** Danh sách tất cả các vòng chơi (RoundBase) có trong game. */
    private final List<RoundBase> rounds;
    /** Chỉ mục (index) của vòng chơi hiện tại trong danh sách 'rounds' (bắt đầu từ 0). */
    private int currentRoundIndex;
    /** Đối tượng vòng chơi hiện tại đang được tải và chơi. */
    private RoundBase currentRound;
    /** Danh sách các viên gạch (Brick) của vòng chơi hiện tại. */
    private List<Brick> currentBricks;

    /**
     * Khởi tạo RoundsManager.
     * Thiết lập danh sách vòng chơi và tải cấu hình các vòng có sẵn.
     */
    public RoundsManager() {
        this.rounds = new ArrayList<>();
        this.currentRoundIndex = 0;
        this.currentBricks = new ArrayList<>();

        initializeRounds();
    }

    /**
     * Khởi tạo và thêm tất cả các đối tượng vòng chơi (Round) vào danh sách.
     */
    private void initializeRounds() {
        rounds.add(new Round1());
        rounds.add(new Round2());
        rounds.add(new Round3());
        rounds.add(new Round4());
        // Thêm các Round khác tại đây khi cần
    }

    /**
     * Tải một vòng chơi cụ thể dựa trên chỉ mục (index) của nó.
     * @param roundNumber Chỉ mục của vòng chơi cần tải (0-based index).
     * @return Danh sách các viên gạch (Brick) của vòng chơi vừa tải.
     * @throws IllegalArgumentException nếu chỉ mục vòng chơi không hợp lệ.
     */
    public List<Brick> loadRound(int roundNumber) {
        if (roundNumber < 0 || roundNumber >= rounds.size()) {
            throw new IllegalArgumentException("Invalid round number: " + roundNumber);
        }

        currentRoundIndex = roundNumber; // Cập nhật chỉ mục hiện tại
        currentRound = rounds.get(currentRoundIndex); // Lấy đối tượng Round
        currentBricks.clear(); // Xóa gạch cũ
        currentBricks = currentRound.createBricks(); // Tạo gạch mới

        return currentBricks;
    }

    /**
     * Tải vòng chơi đầu tiên (Round 0).
     * @return Danh sách các viên gạch của vòng 1.
     */
    public List<Brick> loadFirstRound() {
        return loadRound(0);
    }

    /**
     * Kiểm tra xem vòng chơi hiện tại đã hoàn thành hay chưa.
     * Vòng chơi hoàn thành khi tất cả các viên gạch *không phải* **GOLD** đã bị phá hủy.
     * @return true nếu tất cả gạch ngoài gạch GOLD đã bị phá hủy, ngược lại là false.
     */
    public boolean isRoundComplete() {
        // Nếu danh sách gạch rỗng (có thể do lỗi tải), coi như chưa hoàn thành
        if (currentBricks.isEmpty()) {
            return false;
        }

        // Lặp qua tất cả gạch trong màn chơi hiện tại
        for (Brick brick : currentBricks) {
            // Nếu tìm thấy một viên gạch còn sống VÀ không phải là gạch GOLD
            if (brick.isAlive() && !(brick.getBrickType() == BrickType.GOLD)) {
                return false; // Vòng chơi chưa hoàn thành
            }
        }
        return true; // Tất cả gạch không phải GOLD đã bị phá hủy
    }

    /**
     * Chuyển sang vòng chơi tiếp theo.
     * Phương thức này giả định rằng `hasNextRound()` đã được kiểm tra trước đó.
     * @return true nếu vòng chơi tiếp theo được tải thành công.
     * @throws IllegalStateException nếu không còn vòng chơi nào để tải.
     */
    public boolean nextRound() {
        int nextRoundIndex = currentRoundIndex + 1;
        if (nextRoundIndex >= rounds.size()) {
            // Ném ngoại lệ vì logic chuyển vòng cần được gọi sau khi kiểm tra hasNextRound()
            throw new IllegalStateException("No more rounds available.");
        }

        loadRound(nextRoundIndex); // Tải vòng tiếp theo
        return true;
    }

    /**
     * Kiểm tra xem còn vòng chơi nào tiếp theo không.
     * @return true nếu chỉ mục của vòng tiếp theo nhỏ hơn tổng số vòng, ngược lại là false.
     */
    public boolean hasNextRound() {
        return currentRoundIndex + 1 < rounds.size();
    }

    /**
     * Lấy số thứ tự vòng chơi hiện tại (1-based, ví dụ: Vòng 1, Vòng 2, v.v.).
     * @return Số thứ tự vòng chơi hiện tại.
     */
    public int getCurrentRoundNumber() {
        return currentRoundIndex + 1;
    }

    /**
     * Lấy tên của vòng chơi hiện tại (ví dụ: "The Beginning").
     * @return Tên vòng chơi hoặc "Unknown" nếu chưa có vòng nào được tải.
     */
    public String getCurrentRoundName() {
        if (currentRound == null) {
            return "Unknown";
        }
        return currentRound.getRoundName();
    }

    /**
     * Lấy danh sách các viên gạch của vòng chơi hiện tại.
     * @return Danh sách các đối tượng Brick.
     */
    public List<Brick> getCurrentBricks() {
        return currentBricks;
    }

    /**
     * Đếm số lượng viên gạch còn sống (chưa bị phá hủy hoàn toàn) trong vòng chơi hiện tại.
     * @return Số lượng gạch còn lại.
     */
    public int getRemainingBrickCount() {
        int count = 0;
        for (Brick brick : currentBricks) {
            if (brick.isAlive()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Đặt lại RoundsManager về trạng thái ban đầu (Vòng 1).
     */
    public void reset() {
        currentRoundIndex = 0;
        currentBricks.clear();
        loadFirstRound();
    }

    /**
     * Lấy thông tin chi tiết về vòng chơi hiện tại dưới dạng chuỗi.
     * @return Chuỗi chứa số vòng, tên vòng và số lượng gạch còn lại/tổng số.
     */
    public String getRoundInfo() {
        if (currentRound == null) {
            return "No Round Loaded";
        }

        // Định dạng chuỗi: "Round [Số Vòng]: [Tên Vòng] ([Gạch còn lại]/[Tổng số gạch])"
        return String.format("Round %d: %s (%d/%d bricks)",
                getCurrentRoundNumber(),
                currentRound.getRoundName(),
                getRemainingBrickCount(),
                currentBricks.size()
        );
    }
}