package Engine;

import Objects.PowerUps.*;
import Objects.Bricks.BrickType;
import Objects.GameEntities.Paddle;
import Utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Lớp PowerUpManager quản lý vòng đời của các PowerUp (Power-up)
 * trong trò chơi, bao gồm sinh ra, cập nhật, kiểm tra va chạm,
 * áp dụng hiệu ứng và hết hạn hiệu ứng.
 * Lớp này được triển khai theo mẫu **Singleton**.
 */
public class PowerUpManager {
    /** Thể hiện duy nhất của lớp PowerUpManager (Singleton). */
    private static PowerUpManager instance;
    /** Danh sách các PowerUp đang rơi và đang hoạt động trên màn hình. */
    private final List<PowerUp> activePowerUps;
    /** Bản đồ lưu trữ các hiệu ứng PowerUp đang hoạt động và thời gian hết hạn (expiry time). */
    private final Map<PowerUpType, Long> activeEffects;
    /** Tham chiếu đến GameManager để áp dụng các hiệu ứng PowerUp. */
    private GameManager gameManager;

    /**
     * Constructor private để đảm bảo chỉ có thể tạo một thể hiện (theo mẫu Singleton).
     * Khởi tạo danh sách PowerUp đang hoạt động và bản đồ hiệu ứng.
     */
    private PowerUpManager() {
        this.activePowerUps = new ArrayList<>();
        this.activeEffects = new HashMap<>();
    }

    /**
     * Lấy thể hiện duy nhất của PowerUpManager (Singleton).
     * @return Thể hiện của PowerUpManager.
     */
    public static PowerUpManager getInstance() {
        if (instance == null) {
            instance = new PowerUpManager();
        }
        return instance;
    }

    /**
     * Đặt lại (reset) thể hiện Singleton và xóa tất cả PowerUp và hiệu ứng đang hoạt động.
     * Thường được gọi khi bắt đầu game mới.
     */
    public static void reset() {
        if (instance != null) {
            instance.activePowerUps.clear();
            instance.activeEffects.clear();
        }
        instance = null;
    }

    /**
     * Thiết lập tham chiếu đến GameManager. Cần thiết để áp dụng các hiệu ứng.
     * @param gameManager Tham chiếu đến GameManager.
     */
    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Sinh ra một PowerUp tại vị trí gạch bị phá hủy (brick).
     * PowerUp có xác suất sinh ra nhất định.
     * @param x Tọa độ X của gạch.
     * @param y Tọa độ Y của gạch.
     * @param brickType Loại gạch bị phá hủy (chưa sử dụng trong logic này nhưng giữ nguyên).
     */
    public void spawnFromBrick(double x, double y, BrickType brickType) {
        // 30% xác suất sinh PowerUp
        if (Math.random() > Constants.GameRules.POWERUP_SPAWN_CHANCE) {
            return; // Không sinh PowerUp
        }

        // Chọn ngẫu nhiên loại PowerUp với xác suất trọng số
        PowerUpType type = PowerUpType.randomWeighted();
        if (type == null) {
            return; // Lỗi khi chọn (không nên xảy ra)
        }

        // Tạo đối tượng PowerUp
        PowerUp powerUp = createPowerUp(x, y, type);
        activePowerUps.add(powerUp); // Thêm vào danh sách PowerUp đang rơi

        System.out.println("PowerUp spawned: " + type + " at (" + x + ", " + y + ")");
    }

    /**
     * Factory method: Tạo một đối tượng PowerUp cụ thể dựa trên loại (type) được cung cấp.
     * @param x Tọa độ X ban đầu.
     * @param y Tọa độ Y ban đầu.
     * @param type Loại PowerUp.
     * @return Đối tượng PowerUp đã được tạo.
     */
    private PowerUp createPowerUp(double x, double y, PowerUpType type) {
        switch (type) {
            case CATCH:
                return new CatchPowerUp(x, y);
            case DUPLICATE:
                return new DuplicatePowerUp(x, y);
            case EXPAND:
                return new ExpandPaddlePowerUp(x, y);
            case LASER:
                return new LaserPowerUp(x, y);
            case LIFE:
                return new LifePowerUp(x, y);
            case SLOW:
                return new SlowBallPowerUp(x, y);
            case WARP:
                return new WarpPowerUp(x, y);
            default:
                System.err.println("Unknown PowerUpType: " + type);
                // Trả về một loại mặc định trong trường hợp lỗi
                return new ExpandPaddlePowerUp(x, y);
        }
    }

    /**
     * Cập nhật vị trí của tất cả PowerUp đang rơi và kiểm tra va chạm với Paddle.
     * @param paddle Đối tượng Paddle của người chơi.
     */
    public void update(Paddle paddle) {
        if (paddle == null) {
            return;
        }

        // Tạo một bản sao để tránh lỗi ConcurrentModificationException.
        // Điều này ngăn ngừa sự cố khi applyPowerUpEffect gọi hàm clearAllPowerUps,
        // làm thay đổi danh sách activePowerUps trong khi đang duyệt.
        List<PowerUp> powerUpsCopy = new ArrayList<>(activePowerUps);

        for (PowerUp powerUp : powerUpsCopy) {
            // Bỏ qua nếu PowerUp này đã bị xóa (ví dụ: bởi clearAllPowerUps)
            if (!activePowerUps.contains(powerUp)) {
                continue;
            }

            powerUp.update(); // Cập nhật vị trí (làm cho PowerUp rơi)

            // 1. Kiểm tra va chạm với Paddle
            if (powerUp.checkPaddleCollision(paddle)) {
                powerUp.collect(); // Đánh dấu là đã được thu thập (có thể dùng cho animation)
                applyPowerUpEffect(powerUp); // Áp dụng hiệu ứng lên GameManager
                scheduleEffectExpiry(powerUp.getType()); // Lên lịch hết hạn hiệu ứng
                activePowerUps.remove(powerUp); // Xóa PowerUp khỏi danh sách đang rơi

                System.out.println("PowerUp collected: " + powerUp.getType());
            }
            // 2. Kiểm tra nếu PowerUp rơi khỏi màn hình
            else if (powerUp.getY() > Constants.Window.WINDOW_HEIGHT) {
                activePowerUps.remove(powerUp); // Xóa PowerUp bị bỏ lỡ
                System.out.println("PowerUp missed and removed: " + powerUp.getType());
            }
        }

        updateActiveEffects(); // Kiểm tra và xóa bỏ các hiệu ứng đã hết hạn
    }

    /**
     * Gọi hàm áp dụng hiệu ứng PowerUp lên GameManager.
     * @param powerUp PowerUp vừa được thu thập.
     */
    private void applyPowerUpEffect(PowerUp powerUp) {
        if (gameManager == null) {
            System.err.println("PowerUpManager: GameManager is null, cannot apply effect");
            return;
        }

        powerUp.applyEffect(gameManager);
    }

    /**
     * Loại bỏ hiệu ứng PowerUp khi nó hết thời gian.
     * @param type Loại PowerUp cần loại bỏ hiệu ứng.
     */
    private void removePowerUpEffect(PowerUpType type) {
        if (gameManager == null) {
            System.err.println("PowerUpManager: GameManager is null, cannot remove effect");
            return;
        }

        // Tạo một đối tượng PowerUp tạm thời để gọi hàm removeEffect().
        // Việc này đảm bảo logic loại bỏ hiệu ứng được chứa trong lớp PowerUp cụ thể.
        PowerUp tempPowerUp = createPowerUp(0, 0, type);
        tempPowerUp.removeEffect(gameManager);
    }

    /**
     * Lên lịch thời gian hết hạn cho hiệu ứng PowerUp có thời gian giới hạn.
     * @param type Loại PowerUp.
     */
    private void scheduleEffectExpiry(PowerUpType type) {
        long duration = type.getDuration();
        // Chỉ lên lịch nếu PowerUp có thời gian hiệu lực (> 0)
        if (duration > 0) {
            long expiryTime = System.currentTimeMillis() + duration;
            // Lưu thời gian hết hạn vào bản đồ
            activeEffects.put(type, expiryTime);

            System.out.println("PowerUpManager: Scheduled expiry for " + type + " at " + expiryTime);
        }
    }

    /**
     * Cập nhật các hiệu ứng đang hoạt động: kiểm tra xem hiệu ứng nào đã hết hạn
     * và loại bỏ chúng.
     */
    private void updateActiveEffects() {
        if (activeEffects.isEmpty()) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        // Sử dụng Iterator để có thể xóa phần tử an toàn khi đang lặp
        Iterator<Map.Entry<PowerUpType, Long>> iterator = activeEffects.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<PowerUpType, Long> entry = iterator.next();
            PowerUpType type = entry.getKey();
            long expiryTime = entry.getValue();

            // Nếu thời gian hiện tại >= thời gian hết hạn
            if (currentTime >= expiryTime) {
                removePowerUpEffect(type); // Loại bỏ hiệu ứng
                iterator.remove(); // Xóa khỏi danh sách hiệu ứng đang hoạt động

                System.out.println("PowerUpManager: Effect expired for " + type);
            }
        }
    }

    /**
     * Lấy danh sách các PowerUp đang rơi và đang hoạt động.
     * @return Danh sách PowerUp đang hoạt động (bản sao).
     */
    public List<PowerUp> getActivePowerUps() {
        // Trả về bản sao để ngăn chặn việc chỉnh sửa danh sách gốc từ bên ngoài
        return new ArrayList<>(activePowerUps);
    }

    /**
     * Xóa tất cả PowerUp đang rơi trên màn hình và tất cả hiệu ứng đang hoạt động.
     * Thường được gọi khi chuyển level hoặc khi Game Over.
     */
    public void clearAllPowerUps() {
        activePowerUps.clear();
        activeEffects.clear(); // Xóa tất cả các hiệu ứng hẹn giờ
        System.out.println("PowerUpManager: Cleared all power-ups and effects");
    }
}