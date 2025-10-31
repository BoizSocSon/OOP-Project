package Engine;

import Objects.Bricks.BrickType;
import Rounds.*;
import Objects.Bricks.Brick;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp quản lý các vòng chơi (RoundsManager) chịu trách nhiệm tải,
 * theo dõi tiến độ và chuyển đổi giữa các cấp độ (round) khác nhau trong game.
 */
public class RoundsManager {
    private final List<RoundBase> rounds; // Danh sách tất cả các vòng chơi có sẵn.
    private int currentRoundIndex; // Chỉ số (index) của vòng chơi hiện tại trong danh sách.
    private RoundBase currentRound; // Đối tượng vòng chơi hiện tại.
    private List<Brick> currentBricks; // Danh sách gạch của vòng chơi hiện tại.

    /**
     * Khởi tạo RoundsManager.
     */
    public RoundsManager() {
        this.rounds = new ArrayList<>();
        this.currentRoundIndex = 0;
        this.currentBricks = new ArrayList<>();

        initializeRounds(); // Khởi tạo danh sách các vòng chơi.
    }

    /**
     * Khởi tạo tất cả các vòng chơi có sẵn trong game và thêm vào danh sách.
     */
    private void initializeRounds() {
        rounds.add(new Round1());
        rounds.add(new Round2());
        rounds.add(new Round3());
        rounds.add(new Round4());
    }

    /**
     * Tải một vòng chơi cụ thể theo chỉ số.
     *
     * @param roundNumber Chỉ số (index) của vòng chơi cần tải (bắt đầu từ 0).
     * @return Danh sách các đối tượng {@link Brick} của vòng chơi mới.
     * @throws IllegalArgumentException Nếu chỉ số vòng chơi không hợp lệ.
     */
    public List<Brick> loadRound(int roundNumber) {
        if (roundNumber < 0 || roundNumber >= rounds.size()) {
            throw new IllegalArgumentException("Invalid round number: " + roundNumber);
        }

        currentRoundIndex = roundNumber; // Cập nhật chỉ số.
        currentRound = rounds.get(currentRoundIndex); // Lấy đối tượng vòng chơi.
        currentBricks.clear(); // Xóa gạch cũ.
        currentBricks = currentRound.createBricks(); // Tạo gạch mới.

        return currentBricks;
    }

    /**
     * Tải vòng chơi đầu tiên (Round 1).
     *
     * @return Danh sách gạch của vòng đầu tiên.
     */
    public List<Brick> loadFirstRound() {
        return loadRound(0);
    }

    /**
     * Kiểm tra xem vòng chơi hiện tại đã hoàn thành chưa.
     * Vòng chơi hoàn thành khi tất cả các viên gạch (trừ gạch GOLD) đã bị phá hủy.
     *
     * @return {@code true} nếu vòng chơi hoàn thành, ngược lại là {@code false}.
     */
    public boolean isRoundComplete() {
        // Nếu danh sách gạch trống (chưa tải hoặc đã bị xóa hết), coi là chưa hoàn thành
        if (currentBricks.isEmpty()) {
            return false;
        }

        for (Brick brick : currentBricks) {
            // Kiểm tra gạch còn sống VÀ không phải là gạch Vàng (GOLD).
            if (brick.isAlive() && !(brick.getBrickType() == BrickType.GOLD)) {
                return false; // Còn gạch có thể phá hủy.
            }
        }
        return true; // Tất cả gạch phá hủy đã bị phá.
    }

    /**
     * Chuyển sang vòng chơi tiếp theo.
     *
     * @return {@code true} nếu tải vòng chơi tiếp theo thành công.
     * @throws IllegalStateException Nếu không còn vòng chơi nào nữa.
     */
    public boolean nextRound() {
        int nextRoundIndex = currentRoundIndex + 1;
        if (nextRoundIndex >= rounds.size()) {
            throw new IllegalStateException("No more rounds available.");
        }

        loadRound(nextRoundIndex); // Tải vòng chơi tiếp theo.
        return true;
    }

    /**
     * Kiểm tra xem còn vòng chơi tiếp theo để chuyển sang không.
     *
     * @return {@code true} nếu còn vòng chơi tiếp theo.
     */
    public boolean hasNextRound() {
        return currentRoundIndex + 1 < rounds.size();
    }

    /**
     * Lấy số thứ tự của vòng chơi hiện tại (bắt đầu từ 1).
     *
     * @return Số thứ tự vòng chơi hiện tại.
     */
    public int getCurrentRoundNumber() {
        return currentRoundIndex + 1;
    }

    /**
     * Lấy tên của vòng chơi hiện tại.
     *
     * @return Tên vòng chơi, hoặc "Unknown" nếu chưa có vòng nào được tải.
     */
    public String getCurrentRoundName() {
        if (currentRound == null) {
            return "Unknown";
        }
        return currentRound.getRoundName();
    }

    /**
     * Lấy danh sách các gạch hiện tại của vòng chơi.
     *
     * @return Danh sách các đối tượng {@link Brick}.
     */
    public List<Brick> getCurrentBricks() {
        return currentBricks;
    }

    /**
     * Đếm số lượng gạch còn lại (còn sống) trong vòng chơi hiện tại.
     *
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
     * Đặt lại RoundsManager về trạng thái ban đầu (vòng 1).
     */
    public void reset() {
        currentRoundIndex = 0; // Đặt lại chỉ số.
        currentBricks.clear(); // Xóa gạch cũ.
        loadFirstRound(); // Tải lại vòng chơi đầu tiên.
    }

    /**
     * Trả về thông tin chi tiết về vòng chơi hiện tại dưới dạng chuỗi.
     *
     * @return Chuỗi mô tả thông tin vòng chơi.
     */
    public String getRoundInfo() {
        if (currentRound == null) {
            return "No Round Loaded";
        }

        // Định dạng: "Round [Số]: [Tên Vòng] ([Gạch còn lại]/[Tổng số gạch])"
        return String.format("Round %d: %s (%d/%d bricks)",
                getCurrentRoundNumber(),
                currentRound.getRoundName(),
                getRemainingBrickCount(),
                currentBricks.size()
        );
    }
}