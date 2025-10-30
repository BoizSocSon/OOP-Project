package Objects.PowerUps;

import Engine.GameManager;
import Utils.Constants;

/**
 * <p>Lớp đại diện cho PowerUp **Làm chậm Bóng (Slow Ball PowerUp)**.
 * PowerUp này làm giảm tốc độ của tất cả các quả bóng hiện có trên sân
 * trong một khoảng thời gian nhất định, giúp người chơi phản ứng dễ dàng hơn.</p>
 */
public class SlowBallPowerUp extends PowerUp{

    /**
     * <p>Constructor khởi tạo PowerUp Làm chậm Bóng.</p>
     *
     * @param x Tọa độ x của góc trên bên trái PowerUp.
     * @param y Tọa độ y của góc trên bên trái PowerUp.
     */
    public SlowBallPowerUp(double x, double y) {
        // Gọi constructor lớp cha và thiết lập loại PowerUp là SLOW
        super(x, y, PowerUpType.SLOW);
    }

    /**
     * <p>Áp dụng hiệu ứng Làm chậm Bóng lên trò chơi.</p>
     * <p>Phương thức này gọi {@link GameManager#slowBalls(double)} để giảm
     * vận tốc của tất cả các quả bóng bằng cách nhân với hệ số chậm (slow multiplier).</p>
     *
     * @param gameManager Đối tượng quản lý trò chơi, chịu trách nhiệm áp dụng hiệu ứng.
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("SlowBallPowerUp: GameManager is null, cannot apply effect");
            return;
        }

        // Slow all balls by multiplier (0.7x = 30% reduction)
        // Làm chậm tất cả các quả bóng bằng cách nhân với hệ số chậm
        gameManager.slowBalls(Constants.PowerUps.SLOW_MULTIPLIER);

        System.out.println("SlowBallPowerUp: Balls slowed to " +
                (Constants.PowerUps.SLOW_MULTIPLIER * 100) + "% speed for " +
                Constants.PowerUps.SLOW_DURATION / 1000.0 + " seconds");
    }

    /**
     * <p>Gỡ bỏ hiệu ứng Làm chậm Bóng khỏi trò chơi.</p>
     * <p>Phương thức này gọi {@link GameManager#restoreBallSpeed()} để đưa
     * vận tốc bóng về lại tốc độ ban đầu trước khi hiệu ứng này được áp dụng.</p>
     *
     * @param gameManager Đối tượng quản lý trò chơi, chịu trách nhiệm gỡ bỏ hiệu ứng.
     */
    @Override
    public void removeEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("SlowBallPowerUp: GameManager is null, cannot remove effect");
            return;
        }

        // Restore ball speed (divide by slow multiplier)
        // Phục hồi tốc độ bóng (thường là chia cho hệ số chậm)
        gameManager.restoreBallSpeed();

        System.out.println("SlowBallPowerUp: Ball speed restored (slow expired)");
    }
}