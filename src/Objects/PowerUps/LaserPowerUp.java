package Objects.PowerUps;

import Engine.GameManager;
import Utils.Constants;

/**
 * Lớp LaserPowerUp đại diện cho Power-up 'Laser'.
 * Power-up này trang bị cho thanh đỡ (Paddle) khả năng bắn hai tia laser
 * trong một khoảng thời gian nhất định hoặc cho đến khi hết số lần bắn được quy định.
 * Nó kế thừa từ lớp trừu tượng PowerUp.
 */
public class LaserPowerUp extends PowerUp{
    /**
     * Khởi tạo một đối tượng LaserPowerUp mới.
     *
     * @param x Tọa độ x ban đầu của Power-up khi rơi.
     * @param y Tọa độ y ban đầu của Power-up khi rơi.
     */
    public LaserPowerUp(double x, double y) {
        // Gọi constructor của lớp cha và truyền loại Power-up là LASER
        super(x, y, PowerUpType.LASER);
    }

    /**
     * Áp dụng hiệu ứng 'Laser' lên game.
     * Kích hoạt chế độ laser trên thanh đỡ (Paddle) thông qua GameManager.
     *
     * @param gameManager Đối tượng GameManager hiện tại, cần thiết để truy cập và thay đổi trạng thái Paddle.
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        // Kiểm tra an toàn
        if (gameManager == null) {
            System.err.println("LaserPowerUp: GameManager is null, cannot apply effect");
            return;
        }

        // Kích hoạt logic laser trong GameManager (thường là gọi phương thức enableLaser() trên Paddle)
        gameManager.enableLaser();

        // In ra thông báo cho mục đích debug/theo dõi, hiển thị số đạn và thời gian hiệu lực
        System.out.println("LaserPowerUp: Laser enabled with " +
                Constants.Laser.LASER_SHOTS + " shots for " +
                Constants.PowerUps.LASER_DURATION / 1000.0 + " seconds");
    }

    /**
     * Loại bỏ/Vô hiệu hóa hiệu ứng 'Laser'.
     * Vô hiệu hóa chế độ laser trên thanh đỡ (Paddle) thông qua GameManager.
     * Phương thức này được gọi khi thời gian hết hạn hoặc khi hết đạn laser.
     *
     * @param gameManager Đối tượng GameManager hiện tại.
     */
    @Override
    public void removeEffect(GameManager gameManager) {
        // Kiểm tra an toàn
        if (gameManager == null) {
            System.err.println("LaserPowerUp: GameManager is null, cannot remove effect");
            return;
        }

        // Vô hiệu hóa chế độ laser trong GameManager (thường là gọi phương thức disableLaser() trên Paddle)
        gameManager.disableLaser();

        // In ra thông báo cho mục đích debug/theo dõi
        System.out.println("LaserPowerUp: Laser disabled (expired or shots depleted)");
    }
}