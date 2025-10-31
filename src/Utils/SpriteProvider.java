package Utils;

import Objects.GameEntities.PaddleState;
import Objects.PowerUps.PowerUpType;
import javafx.scene.image.Image;

import java.util.List;

/**
 * Giao diện (Interface) định nghĩa một hợp đồng cho các lớp có khả năng
 * cung cấp các tài nguyên hình ảnh (sprites) và các khung hình animation
 * cho game.
 *
 * Lớp triển khai giao diện này chịu trách nhiệm truy xuất các tài nguyên
 * đã được tải (ví dụ: từ cache) và cung cấp chúng cho các đối tượng khác.
 */
public interface SpriteProvider {
    /**
     * Trả về một hình ảnh tĩnh (sprite) dựa trên tên file.
     *
     * @param filename Tên file của sprite (ví dụ: "ball.png").
     * @return Đối tượng {@link Image} đã được tải.
     */
    Image get(String filename);

    /**
     * Trả về danh sách các khung hình animation cho một loại PowerUp cụ thể.
     *
     * @param type Loại PowerUp cần lấy khung hình.
     * @return Danh sách các đối tượng {@link Image} tạo nên animation.
     */
    List<Image> getPowerUpFrames(PowerUpType type);

    /**
     * Trả về danh sách các khung hình animation cho một trạng thái Paddle cụ thể.
     *
     * @param state Trạng thái Paddle (ví dụ: WIDE, LASER).
     * @return Danh sách các đối tượng {@link Image} tạo nên animation.
     */
    List<Image> getPaddleFrames(PaddleState state);

    /**
     * Trả về danh sách các khung hình animation cho hiệu ứng gạch bạc bị nứt.
     *
     * @return Danh sách các đối tượng {@link Image} của hiệu ứng nứt gạch.
     */
    List<Image> getSilverCrackFrames();

    /**
     * Kiểm tra xem nhà cung cấp sprite đã sẵn sàng cung cấp tài nguyên chưa
     * (ví dụ: cache đã được khởi tạo xong chưa).
     *
     * @return {@code true} nếu tài nguyên đã sẵn sàng, ngược lại là {@code false}.
     */
    boolean isReady();
}