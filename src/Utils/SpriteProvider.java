package Utils;

import Objects.GameEntities.PaddleState;
import Objects.PowerUps.PowerUpType;
import javafx.scene.image.Image;

import java.util.List;

/**
 * Interface định nghĩa các phương thức cung cấp hình ảnh (sprite) cho game.
 * <p>
 * Các lớp triển khai interface này (như {@link SpriteCacheProvider})
 * sẽ quản lý việc truy xuất sprite từ bộ nhớ cache đã được nạp sẵn.
 * </p>
 */
public interface SpriteProvider {

    // 1) Ảnh đơn lẻ (ball, edges, logo, bricks static, paddle static...)
    /**
     * Trả về ảnh tĩnh (static) theo tên file.
     * @param filename tên file hình ảnh (ví dụ: "ball.png", "edge_top.png")
     * @return đối tượng Image, hoặc null nếu không tìm thấy
     */
    Image get(String filename);  //  Dùng khi cần lấy ảnh đơn, không có animation

    // 2) PowerUp animated frames
    /**
     * Trả về danh sách khung hình (frames) của hiệu ứng PowerUp.
     * @param type loại PowerUp (ví dụ: EXPAND, LASER, CATCH...)
     * @return danh sách các Image khung hình tương ứng
     */
    List<Image> getPowerUpFrames(PowerUpType type);  //  Dùng để hiển thị hiệu ứng động của PowerUp

    // 3) Paddle animated frames
    /**
     * Trả về các khung hình animation của Paddle theo trạng thái.
     * @param state trạng thái của paddle (WIDE, LASER, MATERIALIZE...)
     * @return danh sách khung hình ảnh động của paddle
     */
    List<Image> getPaddleFrames(PaddleState state);  //  Mỗi trạng thái Paddle có animation riêng

    // 4) Silver brick crack frames
    /**
     * Trả về các khung hình cho hiệu ứng nứt của gạch bạc.
     * @return danh sách Image của hiệu ứng nứt
     */
    List<Image> getSilverCrackFrames();  //  Hiển thị khi gạch bạc bị va chạm

    /**
     * Kiểm tra xem các sprite đã được nạp sẵn trong cache hay chưa.
     * @return true nếu sẵn sàng để dùng, false nếu chưa khởi tạo
     */
    boolean isReady();  // Giúp game kiểm tra trước khi render để tránh null
}
