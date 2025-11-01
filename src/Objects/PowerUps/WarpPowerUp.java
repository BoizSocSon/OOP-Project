package Objects.PowerUps;

import Engine.GameManager;

/**
 * Lớp WarpPowerUp đại diện cho Power-up 'Chuyển cấp'.
 * Power-up này ngay lập tức đưa người chơi đến màn chơi tiếp theo (level),
 * thường được coi là một Power-up hiếm và mạnh mẽ.
 * Đây là một hiệu ứng tức thời và vĩnh viễn (không hết hạn).
 * Nó kế thừa từ lớp trừu tượng PowerUp.
 */
public class WarpPowerUp extends PowerUp{
    /**
     * Khởi tạo một đối tượng WarpPowerUp mới.
     *
     * @param x Tọa độ x ban đầu của Power-up khi rơi.
     * @param y Tọa độ y ban đầu của Power-up khi rơi.
     */
    public WarpPowerUp(double x, double y) {
        // Gọi constructor của lớp cha và truyền loại Power-up là WARP
        super(x, y, PowerUpType.WARP);
    }

    /**
     * Áp dụng hiệu ứng 'Chuyển cấp' lên game.
     * Gọi phương thức trong GameManager để chuyển người chơi sang level tiếp theo.
     *
     * @param gameManager Đối tượng GameManager hiện tại, cần thiết để chuyển cấp độ.
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        // Kiểm tra an toàn
        if (gameManager == null) {
            System.err.println("WarpPowerUp: GameManager is null, cannot apply effect");
            return;
        }

        // Gọi phương thức trong GameManager để thực hiện chuyển cấp.
        // Phương thức này trả về true nếu có level tiếp theo, false nếu đã là level cuối.
        boolean hasNextLevel = gameManager.warpToNextLevel();

        // Xử lý kết quả chuyển cấp
        if (hasNextLevel) {
            // Chuyển cấp thành công
            System.out.println("WarpPowerUp: Warping to next level! " +
                    "Score and lives preserved.");
        } else {
            // Đã là level cuối, kích hoạt màn hình thắng
            System.out.println("WarpPowerUp: No more levels! " +
                    "Triggering win screen.");
        }
    }

    /**
     * Loại bỏ/Vô hiệu hóa hiệu ứng 'Chuyển cấp'.
     *
     * Lưu ý: Hiệu ứng này là tức thời và vĩnh viễn, không có thời gian hết hạn,
     * nên phương thức này luôn được để trống.
     *
     * @param gameManager Đối tượng GameManager hiện tại.
     */
    @Override
    public void removeEffect(GameManager gameManager) {
        // Hiệu ứng WARP là tức thời, không có logic loại bỏ/hết hạn.
    }
}