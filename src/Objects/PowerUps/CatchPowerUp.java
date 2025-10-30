package Objects.PowerUps;

import Engine.GameManager;
import Utils.Constants;

/**
 * <p>Lớp đại diện cho PowerUp **Bắt bóng (Catch PowerUp)**.
 * PowerUp này kích hoạt chế độ bắt bóng trên thanh đỡ (paddle),
 * cho phép người chơi giữ bóng thay vì làm bóng bật lại ngay lập tức.</p>
 */
public class CatchPowerUp extends PowerUp{

    /**
     * <p>Constructor khởi tạo PowerUp Bắt bóng.</p>
     *
     * @param x Tọa độ x của góc trên bên trái PowerUp.
     * @param y Tọa độ y của góc trên bên trái PowerUp.
     */
    public CatchPowerUp(double x, double y) {
        // Gọi constructor lớp cha và thiết lập loại PowerUp là CATCH
        super(x, y, PowerUpType.CATCH);
    }

    /**
     * <p>Áp dụng hiệu ứng Bắt bóng lên trò chơi.</p>
     * <p>Phương thức này gọi phương thức {@link GameManager#enableCatchMode()}
     * để kích hoạt chế độ bắt bóng trên thanh đỡ.</p>
     *
     * @param gameManager Đối tượng quản lý trò chơi, chịu trách nhiệm áp dụng hiệu ứng.
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("CatchPowerUp: GameManager is null, cannot apply effect");
            return;
        }

        // Enable catch mode on paddle through GameManager
        // Kích hoạt chế độ bắt bóng trên thanh đỡ thông qua GameManager
        gameManager.enableCatchMode();

        System.out.println("CatchPowerUp: Catch mode enabled for " +
                Constants.PowerUps.CATCH_DURATION / 1000.0 + " seconds");
    }

    /**
     * <p>Gỡ bỏ hiệu ứng Bắt bóng khỏi trò chơi.</p>
     * <p>Phương thức này gọi phương thức {@link GameManager#disableCatchMode()}
     * để vô hiệu hóa chế độ bắt bóng trên thanh đỡ.</p>
     *
     * @param gameManager Đối tượng quản lý trò chơi, chịu trách nhiệm gỡ bỏ hiệu ứng.
     */
    @Override
    public void removeEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("CatchPowerUp: GameManager is null, cannot remove effect");
            return;
        }

        // Disable catch mode through GameManager
        // Vô hiệu hóa chế độ bắt bóng thông qua GameManager
        gameManager.disableCatchMode();

        System.out.println("CatchPowerUp: Catch mode disabled (expired)");
    }
}