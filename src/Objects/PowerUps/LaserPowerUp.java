package Objects.PowerUps;

import Engine.GameManager;
import Utils.Constants;

/**
 * <p>Lớp đại diện cho PowerUp **Laser (Laser PowerUp)**.
 * PowerUp này kích hoạt chế độ bắn laser trên thanh đỡ (paddle),
 * cho phép người chơi bắn phá gạch bằng tia laser trong một khoảng thời gian
 * hoặc cho đến khi hết số lần bắn được quy định.</p>
 */
public class LaserPowerUp extends PowerUp{

    /**
     * <p>Constructor khởi tạo PowerUp Laser.</p>
     *
     * @param x Tọa độ x của góc trên bên trái PowerUp.
     * @param y Tọa độ y của góc trên bên trái PowerUp.
     */
    public LaserPowerUp(double x, double y) {
        // Gọi constructor lớp cha và thiết lập loại PowerUp là LASER
        super(x, y, PowerUpType.LASER);
    }

    /**
     * <p>Áp dụng hiệu ứng Laser lên trò chơi.</p>
     * <p>Phương thức này gọi phương thức {@link GameManager#enableLaser()}
     * để kích hoạt chế độ bắn laser trên thanh đỡ, thiết lập số lần bắn
     * và thời gian hết hạn.</p>
     *
     * @param gameManager Đối tượng quản lý trò chơi, chịu trách nhiệm áp dụng hiệu ứng.
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("LaserPowerUp: GameManager is null, cannot apply effect");
            return;
        }

        // Enable laser mode with 5 shots
        // Kích hoạt chế độ laser với số lần bắn đã cấu hình
        gameManager.enableLaser();

        System.out.println("LaserPowerUp: Laser enabled with " +
                Constants.Laser.LASER_SHOTS + " shots for " +
                Constants.PowerUps.LASER_DURATION / 1000.0 + " seconds");
    }

    /**
     * <p>Gỡ bỏ hiệu ứng Laser khỏi trò chơi.</p>
     * <p>Phương thức này gọi phương thức {@link GameManager#disableLaser()}
     * để vô hiệu hóa chế độ bắn laser trên thanh đỡ (thường là khi hết thời gian).</p>
     *
     * @param gameManager Đối tượng quản lý trò chơi, chịu trách nhiệm gỡ bỏ hiệu ứng.
     */
    @Override
    public void removeEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("LaserPowerUp: GameManager is null, cannot remove effect");
            return;
        }

        // Disable laser mode
        // Vô hiệu hóa chế độ laser
        gameManager.disableLaser();

        System.out.println("LaserPowerUp: Laser disabled (expired or shots depleted)");
    }
}