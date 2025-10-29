package Objects.PowerUps;

import Engine.GameManager;
import Utils.Constants;

/**
 * <p>Lớp đại diện cho PowerUp **Cộng Mạng (Life PowerUp)**.
 * PowerUp này mang lại hiệu ứng **tức thời** và **vĩnh viễn** là
 * tăng số mạng (lives) của người chơi lên 1, nhưng không vượt quá
 * giới hạn mạng tối đa được quy định.</p>
 */
public class LifePowerUp extends PowerUp{

    /**
     * <p>Constructor khởi tạo PowerUp Cộng Mạng.</p>
     *
     * @param x Tọa độ x của góc trên bên trái PowerUp.
     * @param y Tọa độ y của góc trên bên trái PowerUp.
     */
    public LifePowerUp(double x, double y) {
        // Gọi constructor lớp cha và thiết lập loại PowerUp là LIFE
        super(x, y, PowerUpType.LIFE);
    }

    /**
     * <p>Áp dụng hiệu ứng Cộng Mạng lên trò chơi.</p>
     * <p>Hiệu ứng này tăng số mạng của người chơi lên 1 thông qua
     * {@link GameManager#addLife()}, được giới hạn bởi mạng tối đa.</p>
     *
     * @param gameManager Đối tượng quản lý trò chơi, chịu trách nhiệm áp dụng hiệu ứng.
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("LifePowerUp: GameManager is null, cannot apply effect");
            return;
        }

        // Add 1 life (capped at MAX_LIVES)
        // Cộng thêm 1 mạng (giới hạn bởi MAX_LIVES)
        int livesBeforeAdd = gameManager.getLives();
        gameManager.addLife(); // Gọi phương thức cộng mạng của GameManager
        int livesAfter = gameManager.getLives();

        if (livesAfter > livesBeforeAdd) {
            System.out.println("LifePowerUp: Life added! Lives: " +
                    livesBeforeAdd + " → " + livesAfter);
        } else {
            System.out.println("LifePowerUp: Max lives reached (" +
                    Constants.GameRules.MAX_LIVES + "), no effect");
        }
    }

    /**
     * <p>Gỡ bỏ hiệu ứng Cộng Mạng khỏi trò chơi.</p>
     * <p>Vì đây là hiệu ứng tức thời và vĩnh viễn (cho đến khi mất mạng),
     * không có hành động gỡ bỏ nào được thực hiện.</p>
     *
     * @param gameManager Đối tượng quản lý trò chơi.
     */
    @Override
    public void removeEffect(GameManager gameManager) {
        // Instant permanent effect - no removal needed
        // Hiệu ứng tức thời vĩnh viễn - không cần gỡ bỏ
        // Lives persist until lost through normal gameplay
        // Mạng được giữ lại cho đến khi bị mất qua lối chơi bình thường
    }
}