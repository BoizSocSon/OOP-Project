package Objects.PowerUps;

import Engine.GameManager;
import Utils.Constants;

/**
 * Lớp ExpandPaddlePowerUp đại diện cho Power-up 'Mở rộng thanh đỡ'.
 * Power-up này tăng chiều rộng của thanh đỡ (Paddle) trong một khoảng thời gian nhất định,
 * giúp người chơi dễ dàng bắt bóng hơn.
 * Nó kế thừa từ lớp trừu tượng PowerUp.
 */
public class ExpandPaddlePowerUp extends PowerUp{
    /**
     * Khởi tạo một đối tượng ExpandPaddlePowerUp mới.
     *
     * @param x Tọa độ x ban đầu của Power-up khi rơi.
     * @param y Tọa độ y ban đầu của Power-up khi rơi.
     */
    public ExpandPaddlePowerUp(double x, double y) {
        // Gọi constructor của lớp cha và truyền loại Power-up là EXPAND
        super(x, y, PowerUpType.EXPAND);
    }

    /**
     * Áp dụng hiệu ứng 'Mở rộng thanh đỡ' lên game.
     * Kích hoạt việc mở rộng Paddle thông qua GameManager.
     *
     * @param gameManager Đối tượng GameManager hiện tại, cần thiết để truy cập và thay đổi trạng thái Paddle.
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        // Kiểm tra an toàn
        if (gameManager == null) {
            System.err.println("ExpandPaddlePowerUp: GameManager is null, cannot apply effect");
            return;
        }

        // Kích hoạt logic mở rộng Paddle trong GameManager (thường là gọi phương thức expand() trên Paddle)
        gameManager.expandPaddle();

        // In ra thông báo cho mục đích debug/theo dõi
        System.out.println("ExpandPaddlePowerUp: Paddle expanded to " +
                (Constants.PowerUps.EXPAND_MULTIPLIER * 100) + "% for " + // Hiển thị tỷ lệ mở rộng
                Constants.PowerUps.EXPAND_DURATION / 1000.0 + " seconds"); // Hiển thị thời gian hiệu lực
    }

    /**
     * Loại bỏ/Vô hiệu hóa hiệu ứng 'Mở rộng thanh đỡ' sau khi hết thời gian.
     * Khôi phục chiều rộng của Paddle về kích thước ban đầu thông qua GameManager.
     *
     * @param gameManager Đối tượng GameManager hiện tại.
     */
    @Override
    public void removeEffect(GameManager gameManager) {
        // Kiểm tra an toàn
        if (gameManager == null) {
            System.err.println("ExpandPaddlePowerUp: GameManager is null, cannot remove effect");
            return;
        }

        // Khôi phục kích thước Paddle về bình thường trong GameManager (thường là gọi shrinkToNormal() trên Paddle)
        gameManager.revertPaddleSize();

        // In ra thông báo cho mục đích debug/theo dõi
        System.out.println("ExpandPaddlePowerUp: Paddle reverted to normal size (expired)");
    }
}