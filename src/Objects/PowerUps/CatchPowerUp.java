package Objects.PowerUps;

import Engine.GameManager;
import Utils.Constants;

/**
 * Lớp CatchPowerUp đại diện cho Power-up 'Bắt bóng'.
 * Power-up này cho phép thanh đỡ (Paddle) bắt và giữ quả bóng khi chạm vào,
 * thay vì làm chệch hướng nó ngay lập tức.
 * Nó kế thừa từ lớp trừu tượng PowerUp.
 */
public class CatchPowerUp extends PowerUp{
    /**
     * Khởi tạo một đối tượng CatchPowerUp mới.
     *
     * @param x Tọa độ x ban đầu của Power-up khi rơi.
     * @param y Tọa độ y ban đầu của Power-up khi rơi.
     */
    public CatchPowerUp(double x, double y) {
        // Gọi constructor của lớp cha và truyền loại Power-up là CATCH
        super(x, y, PowerUpType.CATCH);
    }

    /**
     * Áp dụng hiệu ứng 'Bắt bóng' lên game.
     * Kích hoạt chế độ bắt bóng trên thanh đỡ (Paddle) thông qua GameManager.
     *
     * @param gameManager Đối tượng GameManager hiện tại, cần thiết để truy cập và thay đổi trạng thái game.
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        // Kiểm tra an toàn
        if (gameManager == null) {
            System.err.println("CatchPowerUp: GameManager is null, cannot apply effect");
            return;
        }

        // Kích hoạt chế độ bắt bóng trong GameManager (thường là gọi phương thức trên Paddle)
        gameManager.enableCatchMode();

        // In ra thông báo cho mục đích debug/theo dõi
        System.out.println("CatchPowerUp: Catch mode enabled for " +
                Constants.PowerUps.CATCH_DURATION / 1000.0 + " seconds");
    }

    /**
     * Loại bỏ/Vô hiệu hóa hiệu ứng 'Bắt bóng' sau khi hết thời gian.
     * Vô hiệu hóa chế độ bắt bóng trên thanh đỡ (Paddle) thông qua GameManager.
     *
     * @param gameManager Đối tượng GameManager hiện tại.
     */
    @Override
    public void removeEffect(GameManager gameManager) {
        // Kiểm tra an toàn
        if (gameManager == null) {
            System.err.println("CatchPowerUp: GameManager is null, cannot remove effect");
            return;
        }

        // Vô hiệu hóa chế độ bắt bóng trong GameManager
        gameManager.disableCatchMode();

        // In ra thông báo cho mục đích debug/theo dõi
        System.out.println("CatchPowerUp: Catch mode disabled (expired)");
    }
}