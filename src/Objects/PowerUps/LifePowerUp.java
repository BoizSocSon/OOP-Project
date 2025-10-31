package Objects.PowerUps;

import Engine.GameManager;
import Utils.Constants;

/**
 * Lớp LifePowerUp đại diện cho Power-up 'Mạng sống'.
 * Power-up này cung cấp cho người chơi thêm một mạng sống, tăng cơ hội chơi game.
 * Đây là một hiệu ứng tức thời và vĩnh viễn (không hết hạn).
 * Nó kế thừa từ lớp trừu tượng PowerUp.
 */
public class LifePowerUp extends PowerUp{
    /**
     * Khởi tạo một đối tượng LifePowerUp mới.
     *
     * @param x Tọa độ x ban đầu của Power-up khi rơi.
     * @param y Tọa độ y ban đầu của Power-up khi rơi.
     */
    public LifePowerUp(double x, double y) {
        // Gọi constructor của lớp cha và truyền loại Power-up là LIFE
        super(x, y, PowerUpType.LIFE);
    }

    /**
     * Áp dụng hiệu ứng 'Mạng sống' lên game.
     * Tăng số mạng sống của người chơi thông qua GameManager.
     *
     * @param gameManager Đối tượng GameManager hiện tại, cần thiết để truy cập và thay đổi trạng thái game.
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        // Kiểm tra an toàn
        if (gameManager == null) {
            System.err.println("LifePowerUp: GameManager is null, cannot apply effect");
            return;
        }

        // Lấy số mạng sống hiện tại trước khi cộng (cho mục đích log)
        int livesBeforeAdd = gameManager.getLives();

        // Thực hiện thêm mạng sống (GameManager có trách nhiệm kiểm tra MAX_LIVES)
        gameManager.addLife();

        // Lấy số mạng sống sau khi đã gọi addLife()
        int livesAfter = gameManager.getLives();

        // Kiểm tra xem mạng sống có thực sự được tăng lên hay không (để tránh vượt quá MAX_LIVES)
        if (livesAfter > livesBeforeAdd) {
            // In ra thông báo thành công
            System.out.println("LifePowerUp: Life added! Lives: " +
                    livesBeforeAdd + " → " + livesAfter);
        } else {
            // In ra thông báo khi đã đạt giới hạn mạng sống
            System.out.println("LifePowerUp: Max lives reached (" +
                    Constants.GameRules.MAX_LIVES + "), no effect");
        }
    }

    /**
     * Loại bỏ/Vô hiệu hóa hiệu ứng 'Mạng sống'.
     *
     * Lưu ý: Hiệu ứng này là tức thời và vĩnh viễn, không có thời gian hết hạn,
     * nên phương thức này luôn được để trống.
     *
     * @param gameManager Đối tượng GameManager hiện tại.
     */
    @Override
    public void removeEffect(GameManager gameManager) {
        // Hiệu ứng LIFE là tức thời và không có thời gian hết hạn.
        // Vì vậy, phương thức removeEffect() được để trống.
    }
}