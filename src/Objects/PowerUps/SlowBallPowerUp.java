package Objects.PowerUps;

import Engine.GameManager;
import Utils.Constants;

/**
 * Lớp SlowBallPowerUp đại diện cho Power-up 'Làm chậm bóng'.
 * Power-up này giảm tốc độ di chuyển của tất cả các quả bóng hiện có trong game
 * trong một khoảng thời gian nhất định, giúp người chơi dễ kiểm soát hơn.
 * Nó kế thừa từ lớp trừu tượng PowerUp.
 */
public class SlowBallPowerUp extends PowerUp{
    /**
     * Khởi tạo một đối tượng SlowBallPowerUp mới.
     *
     * @param x Tọa độ x ban đầu của Power-up khi rơi.
     * @param y Tọa độ y ban đầu của Power-up khi rơi.
     */
    public SlowBallPowerUp(double x, double y) {
        // Gọi constructor của lớp cha và truyền loại Power-up là SLOW
        super(x, y, PowerUpType.SLOW);
    }

    /**
     * Áp dụng hiệu ứng 'Làm chậm bóng' lên game.
     * Giảm tốc độ của tất cả các quả bóng hiện tại thông qua GameManager.
     *
     * @param gameManager Đối tượng GameManager hiện tại, cần thiết để truy cập và thay đổi tốc độ bóng.
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        // Kiểm tra an toàn
        if (gameManager == null) {
            System.err.println("SlowBallPowerUp: GameManager is null, cannot apply effect");
            return;
        }

        // Gọi phương thức trong GameManager để áp dụng hệ số làm chậm lên tất cả Ball
        gameManager.slowBalls(Constants.PowerUps.SLOW_MULTIPLIER);

        // In ra thông báo cho mục đích debug/theo dõi
        System.out.println("SlowBallPowerUp: Balls slowed to " +
                (Constants.PowerUps.SLOW_MULTIPLIER * 100) + "% speed for " +
                Constants.PowerUps.SLOW_DURATION / 1000.0 + " seconds");
    }

    /**
     * Loại bỏ/Vô hiệu hóa hiệu ứng 'Làm chậm bóng' sau khi hết thời gian.
     * Khôi phục tốc độ ban đầu của tất cả các quả bóng thông qua GameManager.
     *
     * @param gameManager Đối tượng GameManager hiện tại.
     */
    @Override
    public void removeEffect(GameManager gameManager) {
        // Kiểm tra an toàn
        if (gameManager == null) {
            System.err.println("SlowBallPowerUp: GameManager is null, cannot remove effect");
            return;
        }

        // Gọi phương thức trong GameManager để khôi phục tốc độ bóng về trạng thái bình thường
        gameManager.restoreBallSpeed();

        // In ra thông báo cho mục đích debug/theo dõi
        System.out.println("SlowBallPowerUp: Ball speed restored (slow expired)");
    }
}