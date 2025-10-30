package Objects.PowerUps;

import Engine.GameManager;
import Utils.Constants;

/**
 * <p>Lớp đại diện cho PowerUp **Mở rộng Thanh đỡ (Expand Paddle PowerUp)**.
 * PowerUp này làm tăng chiều rộng của thanh đỡ lên kích thước lớn hơn
 * trong một khoảng thời gian nhất định, giúp người chơi dễ bắt bóng hơn.</p>
 */
public class ExpandPaddlePowerUp extends PowerUp{

    /**
     * <p>Constructor khởi tạo PowerUp Mở rộng Thanh đỡ.</p>
     *
     * @param x Tọa độ x của góc trên bên trái PowerUp.
     * @param y Tọa độ y của góc trên bên trái PowerUp.
     */
    public ExpandPaddlePowerUp(double x, double y) {
        // Gọi constructor lớp cha và thiết lập loại PowerUp là EXPAND
        super(x, y, PowerUpType.EXPAND);
    }

    /**
     * <p>Áp dụng hiệu ứng Mở rộng Thanh đỡ lên trò chơi.</p>
     * <p>Phương thức này gọi phương thức {@link GameManager#expandPaddle()}
     * để tăng kích thước thanh đỡ.</p>
     *
     * @param gameManager Đối tượng quản lý trò chơi, chịu trách nhiệm áp dụng hiệu ứng.
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("ExpandPaddlePowerUp: GameManager is null, cannot apply effect");
            return;
        }

        // Expand paddle width through GameManager
        // Mở rộng chiều rộng thanh đỡ thông qua GameManager
//        gameManager.expandPaddle(Constants.PowerUps.EXPAND_MULTIPLIER); // Chú thích cũ giữa nguyên
        gameManager.expandPaddle(); // Gọi phương thức mở rộng thanh đỡ

        System.out.println("ExpandPaddlePowerUp: Paddle expanded to " +
                (Constants.PowerUps.EXPAND_MULTIPLIER * 100) + "% for " +
                Constants.PowerUps.EXPAND_DURATION / 1000.0 + " seconds");
    }

    /**
     * <p>Gỡ bỏ hiệu ứng Mở rộng Thanh đỡ khỏi trò chơi.</p>
     * <p>Phương thức này gọi phương thức {@link GameManager#revertPaddleSize()}
     * để đưa thanh đỡ về kích thước ban đầu.</p>
     *
     * @param gameManager Đối tượng quản lý trò chơi, chịu trách nhiệm gỡ bỏ hiệu ứng.
     */
    @Override
    public void removeEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("ExpandPaddlePowerUp: GameManager is null, cannot remove effect");
            return;
        }

        // Revert paddle to original width
        // Đưa thanh đỡ về chiều rộng ban đầu
        gameManager.revertPaddleSize();

        System.out.println("ExpandPaddlePowerUp: Paddle reverted to normal size (expired)");
    }
}