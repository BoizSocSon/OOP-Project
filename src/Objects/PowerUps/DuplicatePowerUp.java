package Objects.PowerUps;

import Engine.GameManager;

/**
 * Lớp DuplicatePowerUp đại diện cho Power-up 'Nhân đôi bóng'.
 * Power-up này tạo ra một bản sao của tất cả các quả bóng hiện có trong game,
 * tăng số lượng bóng đang chơi.
 * Nó kế thừa từ lớp trừu tượng PowerUp.
 */
public class DuplicatePowerUp extends  PowerUp{
    /**
     * Khởi tạo một đối tượng DuplicatePowerUp mới.
     *
     * @param x Tọa độ x ban đầu của Power-up khi rơi.
     * @param y Tọa độ y ban đầu của Power-up khi rơi.
     */
    public DuplicatePowerUp(double x, double y) {
        // Gọi constructor của lớp cha và truyền loại Power-up là DUPLICATE
        super(x, y, PowerUpType.DUPLICATE);
    }

    /**
     * Áp dụng hiệu ứng 'Nhân đôi bóng' lên game.
     * Gọi phương thức trong GameManager để tạo bản sao của tất cả các quả bóng hiện tại.
     *
     * @param gameManager Đối tượng GameManager hiện tại, cần thiết để truy cập và thay đổi trạng thái game.
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        // Kiểm tra an toàn
        if (gameManager == null) {
            System.err.println("DuplicatePowerUp: GameManager is null, cannot apply effect");
            return;
        }

        // Lấy số lượng bóng ban đầu trước khi nhân đôi (cho mục đích log)
        int originalCount = gameManager.getBallCount();

        // Thực hiện nhân đôi bóng trong GameManager
        // GameManager sẽ chịu trách nhiệm tạo các bản sao của Ball và thêm chúng vào danh sách
        gameManager.duplicateBalls();

        // Lấy số lượng bóng mới sau khi nhân đôi
        int newCount = gameManager.getBallCount();

        // In ra thông báo cho mục đích debug/theo dõi
        System.out.println("DuplicatePowerUp: Balls duplicated from " +
                originalCount + " to " + newCount);
    }

    /**
     * Loại bỏ/Vô hiệu hóa hiệu ứng 'Nhân đôi bóng'.
     *
     * Lưu ý: Hiệu ứng nhân đôi bóng là tức thời và vĩnh viễn (cho đến khi bóng bị mất),
     * nên phương thức này thường để trống hoặc không được gọi.
     *
     * @param gameManager Đối tượng GameManager hiện tại.
     */
    @Override
    public void removeEffect(GameManager gameManager) {
        // Hiệu ứng DUPLICATE là một hiệu ứng tức thời và không có thời gian hết hạn (expired).
        // Vì vậy, phương thức removeEffect() được để trống.
    }
}