package Objects.PowerUps;

import Engine.GameManager;

/**
 * <p>Lớp đại diện cho PowerUp **Dịch chuyển (Warp PowerUp)**.
 * PowerUp này mang lại hiệu ứng **tức thời** và **vĩnh viễn** là
 * chuyển người chơi ngay lập tức sang cấp độ (level) tiếp theo,
 * thường là một PowerUp hiếm.</p>
 */
public class WarpPowerUp extends PowerUp{

    /**
     * <p>Constructor khởi tạo PowerUp Dịch chuyển.</p>
     *
     * @param x Tọa độ x của góc trên bên trái PowerUp.
     * @param y Tọa độ y của góc trên bên trái PowerUp.
     */
    public WarpPowerUp(double x, double y) {
        // Gọi constructor lớp cha và thiết lập loại PowerUp là WARP
        super(x, y, PowerUpType.WARP);
    }

    /**
     * <p>Áp dụng hiệu ứng Dịch chuyển lên trò chơi.</p>
     * <p>Hiệu ứng này gọi phương thức {@link GameManager#warpToNextLevel()}
     * để chuyển ngay lập tức sang cấp độ tiếp theo, hoặc kết thúc game nếu
     * không còn cấp độ nào nữa.</p>
     *
     * @param gameManager Đối tượng quản lý trò chơi, chịu trách nhiệm áp dụng hiệu ứng.
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("WarpPowerUp: GameManager is null, cannot apply effect");
            return;
        }

        // Warp to next level
        // Dịch chuyển đến cấp độ tiếp theo
        boolean hasNextLevel = gameManager.warpToNextLevel();

        if (hasNextLevel) {
            System.out.println("WarpPowerUp: Warping to next level! " +
                    "Score and lives preserved.");
        } else {
            // Trường hợp không còn cấp độ nào để chuyển đến
            System.out.println("WarpPowerUp: No more levels! " +
                    "Triggering win screen.");
        }
    }

    /**
     * <p>Gỡ bỏ hiệu ứng Dịch chuyển khỏi trò chơi.</p>
     * <p>Vì đây là hiệu ứng tức thời và không thể đảo ngược,
     * không có hành động gỡ bỏ nào được thực hiện.</p>
     *
     * @param gameManager Đối tượng quản lý trò chơi.
     */
    @Override
    public void removeEffect(GameManager gameManager) {
        // Instant irreversible effect - no removal needed
        // Hiệu ứng tức thời và không thể đảo ngược - không cần gỡ bỏ
        // Once warped, the transition is permanent
        // Một khi đã dịch chuyển, quá trình chuyển đổi là vĩnh viễn
    }
}