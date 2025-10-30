package Objects.PowerUps;

import Engine.GameManager;

/**
 * <p>Lớp đại diện cho PowerUp **Nhân bản bóng (Duplicate PowerUp)**.
 * PowerUp này tạo ra các bản sao của tất cả các quả bóng hiện có trên sân,
 * là một hiệu ứng **tức thời** (instant effect) và không cần gỡ bỏ.</p>
 */
public class DuplicatePowerUp extends PowerUp{

    /**
     * <p>Constructor khởi tạo PowerUp Nhân bản bóng.</p>
     *
     * @param x Tọa độ x của góc trên bên trái PowerUp.
     * @param y Tọa độ y của góc trên bên trái PowerUp.
     */
    public DuplicatePowerUp(double x, double y) {
        // Gọi constructor lớp cha và thiết lập loại PowerUp là DUPLICATE
        super(x, y, PowerUpType.DUPLICATE);
    }

    /**
     * <p>Áp dụng hiệu ứng Nhân bản bóng lên trò chơi.</p>
     * <p>Hiệu ứng này nhân đôi số lượng bóng hiện có, với các bóng mới được tạo
     * có vận tốc lệch đi một góc nhất định so với bóng gốc.</p>
     *
     * @param gameManager Đối tượng quản lý trò chơi, chịu trách nhiệm áp dụng hiệu ứng.
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("DuplicatePowerUp: GameManager is null, cannot apply effect");
            return;
        }

        // Duplicate all balls with ±45° angle offsets
        // Nhân bản tất cả các quả bóng với góc lệch ±45°
        int originalCount = gameManager.getBallCount();
        gameManager.duplicateBalls(); // Gọi phương thức nhân bản bóng của GameManager
        int newCount = gameManager.getBallCount();

        System.out.println("DuplicatePowerUp: Balls duplicated from " +
                originalCount + " to " + newCount);
    }

    /**
     * <p>Gỡ bỏ hiệu ứng Nhân bản bóng khỏi trò chơi.</p>
     * <p>Vì đây là hiệu ứng tức thời, không có hành động gỡ bỏ nào được thực hiện
     * khi thời gian hết hạn. Các quả bóng đã tạo sẽ tồn tại cho đến khi bị phá hủy tự nhiên.</p>
     *
     * @param gameManager Đối tượng quản lý trò chơi.
     */
    @Override
    public void removeEffect(GameManager gameManager) {
        // Instant effect - no removal needed
        // Hiệu ứng tức thời - không cần gỡ bỏ
        // Balls created persist until they die naturally
        // Các quả bóng được tạo ra sẽ tồn tại cho đến khi chúng chết một cách tự nhiên
    }
}