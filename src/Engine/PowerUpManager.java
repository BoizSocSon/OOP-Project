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
 * Lớp Singleton quản lý các vật phẩm bổ trợ (PowerUps) trong game.
 * Lớp này xử lý việc sinh ra vật phẩm, cập nhật vị trí vật phẩm đang rơi,
 * phát hiện va chạm với thanh đỡ và áp dụng/hủy bỏ các hiệu ứng kéo dài.
 */
public class PowerUpManager {
    private static PowerUpManager instance; // Instance Singleton.
    private final List<PowerUp> activePowerUps; // Danh sách các vật phẩm đang rơi trên màn hình.
    private final Map<PowerUpType, Long> activeEffects; // Map lưu trữ các hiệu ứng đang hoạt động và thời gian hết hạn (expiry time).
    private GameManager gameManager; // Tham chiếu đến GameManager để áp dụng/hủy bỏ hiệu ứng.

    /**
     * Constructor private để đảm bảo chỉ có thể tạo instance qua {@link #getInstance()}.
     */
    private PowerUpManager() {
        this.activePowerUps = new ArrayList<>();
        this.activeEffects = new HashMap<>();
    }

    /**
     * Lấy instance duy nhất của PowerUpManager.
     *
     * @return Instance của PowerUpManager.
     */
    public static PowerUpManager getInstance() {
        if (instance == null) {
            instance = new PowerUpManager();
        }
        return instance;
    }

    /**
     * Đặt lại trạng thái của PowerUpManager (hữu ích khi game reset).
     */
    public static void reset() {
        if (instance != null) {
            instance.activePowerUps.clear(); // Xóa vật phẩm đang rơi.
            instance.activeEffects.clear(); // Xóa hiệu ứng đang hoạt động.
        }
        instance = null;
    }

    /**
     * Thiết lập tham chiếu đến GameManager.
     *
     * @param gameManager Tham chiếu đến GameManager.
     */
    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Sinh ra một vật phẩm bổ trợ tại vị trí của gạch bị phá hủy (nếu may mắn).
     *
     * @param x Tọa độ X của gạch.
     * @param y Tọa độ Y của gạch.
     * @param brickType Loại gạch bị phá hủy.
     */
    public void spawnFromBrick(double x, double y, BrickType brickType) {
        // Kiểm tra tỉ lệ sinh vật phẩm (mặc định 30%).
        if (Math.random() > Constants.GameRules.POWERUP_SPAWN_CHANCE) {
            return; // Không sinh vật phẩm.
        }

        // Chọn ngẫu nhiên loại vật phẩm với tỉ lệ có trọng số.
        PowerUpType type = PowerUpType.randomWeighted();
        if (type == null) {
            return; // Chọn thất bại.
        }

        // Tạo đối tượng PowerUp mới.
        PowerUp powerUp = createPowerUp(x, y, type);
        activePowerUps.add(powerUp); // Thêm vào danh sách đang hoạt động.

        System.out.println("PowerUp spawned: " + type + " at (" + x + ", " + y + ")");
    }

    /**
     * Phương thức nhà máy (factory) để tạo đối tượng PowerUp cụ thể.
     *
     * @param x Tọa độ X.
     * @param y Tọa độ Y.
     * @param type Loại PowerUp.
     * @return Đối tượng PowerUp mới.
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
                // Trả về một loại mặc định nếu gặp lỗi.
                return new ExpandPaddlePowerUp(x, y);
        }
    }

    /**
     * Cập nhật vị trí của các vật phẩm đang rơi và kiểm tra va chạm.
     *
     * @param paddle Thanh đỡ của người chơi.
     */
    public void update(Paddle paddle) {
        if (paddle == null) {
            return;
        }

        // Sao chép danh sách để tránh ConcurrentModificationException khi xóa.
        List<PowerUp> powerUpsCopy = new ArrayList<>(activePowerUps);

        for (PowerUp powerUp : powerUpsCopy) {
            // Kiểm tra lại xem vật phẩm có còn trong danh sách gốc không (đề phòng xóa đồng thời).
            if (!activePowerUps.contains(powerUp)) {
                continue;
            }

            powerUp.update(); // Cập nhật vị trí rơi.

            // Kiểm tra va chạm với thanh đỡ.
            if (powerUp.checkPaddleCollision(paddle)) {
                powerUp.collect(); // Đánh dấu đã thu thập.
                applyPowerUpEffect(powerUp); // Áp dụng hiệu ứng.
                scheduleEffectExpiry(powerUp.getType()); // Lên lịch hủy hiệu ứng (nếu có thời gian).
                activePowerUps.remove(powerUp); // Xóa khỏi danh sách vật phẩm đang rơi.

                System.out.println("PowerUp collected: " + powerUp.getType());
            }
            // Kiểm tra vật phẩm rơi ra khỏi màn hình.
            else if (powerUp.getY() > Constants.Window.WINDOW_HEIGHT) {
                activePowerUps.remove(powerUp); // Xóa khỏi danh sách.
                System.out.println("PowerUp missed and removed: " + powerUp.getType());
            }
        }

        // Kiểm tra và loại bỏ các hiệu ứng đã hết hạn.
        updateActiveEffects();
    }

    /**
     * Áp dụng hiệu ứng của vật phẩm thông qua GameManager.
     *
     * @param powerUp Vật phẩm vừa được thu thập.
     */
    private void applyPowerUpEffect(PowerUp powerUp) {
        if (gameManager == null) {
            System.err.println("PowerUpManager: GameManager is null, cannot apply effect");
            return;
        }

        // Gọi phương thức áp dụng hiệu ứng trên chính đối tượng PowerUp.
        powerUp.applyEffect(gameManager);
    }

    /**
     * Hủy bỏ hiệu ứng của vật phẩm đã hết hạn.
     *
     * @param type Loại PowerUp cần hủy hiệu ứng.
     */
    private void removePowerUpEffect(PowerUpType type) {
        if (gameManager == null) {
            System.err.println("PowerUpManager: GameManager is null, cannot remove effect");
            return;
        }

        // Tạo tạm một đối tượng PowerUp (không cần vị trí thực) chỉ để gọi removeEffect.
        PowerUp tempPowerUp = createPowerUp(0, 0, type);
        tempPowerUp.removeEffect(gameManager);
    }

    /**
     * Lên lịch thời gian hết hạn cho hiệu ứng kéo dài.
     *
     * @param type Loại PowerUp.
     */
    private void scheduleEffectExpiry(PowerUpType type) {
        long duration = type.getDuration();
        // Chỉ lên lịch nếu thời gian kéo dài > 0.
        if (duration > 0) {
            long expiryTime = System.currentTimeMillis() + duration; // Tính thời gian hết hạn.
            activeEffects.put(type, expiryTime); // Lưu vào Map.

            System.out.println("PowerUpManager: Scheduled expiry for " + type + " at " + expiryTime);
        }
    }

    /**
     * Cập nhật các hiệu ứng đang hoạt động, loại bỏ các hiệu ứng đã hết hạn.
     */
    private void updateActiveEffects() {
        if (activeEffects.isEmpty()) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        // Sử dụng Iterator để có thể xóa phần tử an toàn trong khi lặp.
        Iterator<Map.Entry<PowerUpType, Long>> iterator = activeEffects.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<PowerUpType, Long> entry = iterator.next();
            PowerUpType type = entry.getKey();
            long expiryTime = entry.getValue();

            // Kiểm tra xem đã hết hạn chưa.
            if (currentTime >= expiryTime) {
                removePowerUpEffect(type); // Hủy hiệu ứng.
                iterator.remove(); // Xóa khỏi danh sách hiệu ứng đang hoạt động.

                System.out.println("PowerUpManager: Effect expired for " + type);
            }
        }
    }

    /**
     * Lấy danh sách các vật phẩm đang rơi trên màn hình.
     *
     * @return Danh sách các PowerUp đang hoạt động (bản sao).
     */
    public List<PowerUp> getActivePowerUps() {
        return new ArrayList<>(activePowerUps); // Trả về bản sao để tránh chỉnh sửa trực tiếp.
    }

    /**
     * Xóa tất cả vật phẩm đang rơi và hiệu ứng đang hoạt động.
     */
    public void clearAllPowerUps() {
        activePowerUps.clear();
        activeEffects.clear();
        System.out.println("PowerUpManager: Cleared all power-ups and effects");
    }
}