package Render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;

/**
 * SpriteRenderer - Lớp tiện ích cho các thao tác vẽ sprite nâng cao.
 *
 * Trách nhiệm:
 * - Vẽ sprite với nhiều phép biến đổi (tỉ lệ, xoay, alpha)
 * - Hỗ trợ vẽ hàng loạt để tăng hiệu suất
 * - Xử lý vị trí và căn chỉnh sprite
 * - Cung cấp các phương thức hỗ trợ cho các tác vụ vẽ phổ biến
 *
 * Tính năng:
 * - Vẽ sprite cơ bản (có/không có kích thước)
 * - Vẽ sprite theo tỉ lệ (giữ nguyên tỉ lệ khung hình)
 * - Vẽ sprite xoay quanh tâm
 * - Pha trộn alpha (hiệu ứng trong suốt)
 * - Vẽ hàng loạt để tăng hiệu suất
 *
 * Lưu ý về hiệu suất:
 * - Giảm thiểu thay đổi trạng thái GraphicsContext
 * - Gom nhóm các thao tác tương tự
 * - Cache ảnh đã biến đổi khi có thể
 * - Mục tiêu: 60fps với hơn 100 sprite
 *
 * Cách sử dụng:
 * - Được sử dụng bởi CanvasRenderer cho tất cả thao tác vẽ sprite
 * - Được gọi bởi các phương thức GameObject.render()
 * - Xử lý tất cả thao tác vẽ Image của JavaFX
 *
 */
public class SpriteRenderer {

    private final GraphicsContext gc; // GraphicsContext dùng để vẽ trên Canvas

    /**
     * Tạo một SpriteRenderer với GraphicsContext chỉ định.
     *
     * @param gc GraphicsContext của JavaFX để vẽ
     */
    public SpriteRenderer(GraphicsContext gc) {
        this.gc = gc; // Lưu lại tham chiếu GC để dùng cho mọi thao tác vẽ
    }

    /**
     * Vẽ một sprite tại vị trí chỉ định với kích thước gốc.
     *
     * Đây là thao tác vẽ sprite cơ bản nhất.
     * Ảnh được vẽ ở độ phân giải gốc.
     *
     * Cách sử dụng:
     * - Đối tượng tĩnh (gạch, tường)
     * - Thành phần UI (nút, biểu tượng)
     * - Sprite không cần thay đổi kích thước
     *
     * @param img Ảnh cần vẽ (không được null)
     * @param x Tọa độ X của góc trên bên trái
     * @param y Tọa độ Y của góc trên bên trái
     */
    public void drawSprite(Image img, double x, double y) {
        if (img == null) {
            System.err.println("SpriteRenderer.drawSprite: Image is null");
            return; // Tránh NPE khi ảnh chưa sẵn sàng
        }

        gc.drawImage(img, x, y); // Vẽ ảnh với kích thước gốc tại (x, y)
    }

    /**
     * Vẽ một sprite tại vị trí chỉ định với kích thước tùy chỉnh.
     *
     * Ảnh sẽ bị kéo/dãn để vừa với chiều rộng và chiều cao chỉ định.
     * Hữu ích cho:
     * - Thành phần UI đáp ứng
     * - Sprite cần thay đổi kích thước động
     * - Kết cấu kéo dãn (viền, nền)
     *
     * Lưu ý: Có thể làm méo ảnh nếu thay đổi tỉ lệ khung hình.
     * Sử dụng drawSpriteScaled() để giữ nguyên tỉ lệ.
     *
     * @param img Ảnh cần vẽ
     * @param x Tọa độ X của góc trên bên trái
     * @param y Tọa độ Y của góc trên bên trái
     * @param w Chiều rộng cần vẽ (có thể khác chiều rộng ảnh gốc)
     * @param h Chiều cao cần vẽ (có thể khác chiều cao ảnh gốc)
     */
    public void drawSprite(Image img, double x, double y, double w, double h) {
        if (img == null) {
            System.err.println("SpriteRenderer.drawSprite: Image is null");
            return;
        }

        gc.drawImage(img, x, y, w, h); // Vẽ ảnh theo kích thước tùy chỉnh (có thể méo)
    }

    /**
     * Vẽ một sprite xoay quanh tâm.
     *
     * Cơ chế xoay:
     * 1. Tịnh tiến đến tâm sprite
     * 2. Áp dụng phép xoay
     * 3. Vẽ sprite
     * 4. Khôi phục phép biến đổi
     *
     * Cách sử dụng:
     * - Đạn xoay (laser, tên lửa)
     * - Powerup xoay
     * - Chỉ báo hướng
     * - Hiệu ứng động
     *
     * Lưu ý hiệu suất:
     * - Phép biến đổi tốn tài nguyên
     * - Giảm số lượng sprite xoay mỗi frame
     * - Cân nhắc vẽ trước các sprite xoay với góc cố định
     *
     * @param img Ảnh cần vẽ
     * @param x Tọa độ X của tâm sprite
     * @param y Tọa độ Y của tâm sprite
     * @param angle Góc xoay (độ, chiều kim đồng hồ)
     */
    public void drawSpriteRotated(Image img, double x, double y, double angle) {
        if (img == null) {
            System.err.println("SpriteRenderer.drawSpriteRotated: Image is null");
            return;
        }

        // Lưu trạng thái biến đổi hiện tại
        gc.save(); // Bắt đầu một "scope" transform an toàn

        // Tạo phép xoay
        Rotate rotate = new Rotate(angle, x + img.getWidth() / 2, y + img.getHeight() / 2); // Xoay quanh tâm ảnh
        gc.setTransform(
                rotate.getMxx(), rotate.getMyx(),
                rotate.getMxy(), rotate.getMyy(),
                rotate.getTx(), rotate.getTy()
        ); // Áp dụng ma trận xoay vào GC

        // Vẽ sprite
        gc.drawImage(img, x, y); // Vẽ dưới transform đã áp dụng

        // Khôi phục phép biến đổi
        gc.restore(); // Trả GC về trạng thái trước khi xoay
    }

    /**
     * Vẽ một sprite với độ trong suốt (pha trộn alpha).
     *
     * Alpha cho phép sprite bán trong suốt cho:
     * - Hiệu ứng mờ dần xuất hiện/biến mất
     * - Hiệu ứng bóng/đuôi
     * - Gợi ý powerup
     * - Hiệu ứng nhấp nháy khi bị thương
     *
     * Giá trị alpha:
     * - 0.0 = Hoàn toàn trong suốt (ẩn)
     * - 0.5 = Trong suốt 50%
     * - 1.0 = Hoàn toàn không trong suốt (bình thường)
     *
     * Lưu ý hiệu suất:
     * - Pha trộn alpha cần thêm xử lý
     * - Sử dụng hợp lý để đảm bảo hiệu suất
     * - Alpha mặc định = 1.0 cho vẽ bình thường
     *
     * @param img Ảnh cần vẽ
     * @param x Tọa độ X của góc trên bên trái
     * @param y Tọa độ Y của góc trên bên trái
     * @param alpha Mức độ trong suốt (0.0 = ẩn, 1.0 = hiện)
     */
    public void drawSpriteWithAlpha(Image img, double x, double y, double alpha) {
        if (img == null) {
            System.err.println("SpriteRenderer.drawSpriteWithAlpha: Image is null");
            return;
        }

        // Giới hạn alpha trong khoảng hợp lệ
        alpha = Math.max(0.0, Math.min(1.0, alpha)); // Clamp 0..1

        // Lưu alpha hiện tại
        double oldAlpha = gc.getGlobalAlpha(); // Ghi lại để khôi phục sau

        // Đặt alpha mới
        gc.setGlobalAlpha(alpha); // Bật vẽ trong suốt theo mức alpha

        // Vẽ sprite
        gc.drawImage(img, x, y);

        // Khôi phục alpha
        gc.setGlobalAlpha(oldAlpha); // Trả về alpha cũ, tránh ảnh hưởng các lần vẽ sau
    }

    /**
     * Vẽ một sprite theo tỉ lệ đồng nhất (giữ nguyên tỉ lệ khung hình).
     *
     * Tỉ lệ được áp dụng đồng đều cho cả chiều rộng và chiều cao,
     * giữ nguyên tỉ lệ gốc của ảnh.
     *
     * Cách sử dụng:
     * - Powerup thay đổi kích thước
     * - Hiệu ứng zoom
     * - UI co giãn
     * - Hiệu ứng hạt thay đổi kích thước
     *
     * Giá trị scale:
     * - 0.5 = Nhỏ bằng nửa
     * - 1.0 = Kích thước gốc
     * - 2.0 = Gấp đôi
     *
     * Căn giữa:
     * - Sprite được vẽ căn giữa tại (x, y)
     * - Tỉ lệ thay đổi từ tâm
     * - Dùng cho hiệu ứng co giãn đối xứng
     *
     * @param img Ảnh cần vẽ
     * @param x Tọa độ X của tâm sprite
     * @param y Tọa độ Y của tâm sprite
     * @param scale Hệ số tỉ lệ (1.0 = gốc)
     */
    public void drawSpriteScaled(Image img, double x, double y, double scale) {
        if (img == null) {
            System.err.println("SpriteRenderer.drawSpriteScaled: Image is null");
            return;
        }

        // Tính toán kích thước sau khi scale
        double scaledWidth = img.getWidth() * scale;  // W sau scale
        double scaledHeight = img.getHeight() * scale; // H sau scale

        // Căn giữa sprite đã scale
        double drawX = x - scaledWidth / 2.0; // Dời về top-left để tâm trùng (x,y)
        double drawY = y - scaledHeight / 2.0;

        // Vẽ sprite đã scale
        gc.drawImage(img, drawX, drawY, scaledWidth, scaledHeight); // Giữ nguyên tỉ lệ
    }

    /**
     * Vẽ một sprite với kích thước cụ thể (width và height riêng biệt).
     *
     * Khác với drawSpriteScaled(scale) ở chỗ này cho phép set width và height
     * độc lập nhau, không cần giữ nguyên aspect ratio.
     *
     * Use cases:
     * - Paddle có thể expand width nhưng giữ nguyên height
     * - Bricks với kích thước cố định
     * - UI elements cần fit vào bounds cụ thể
     *
     * Lưu ý: Ảnh có thể bị méo nếu aspect ratio thay đổi.
     *
     * @param img Ảnh cần vẽ
     * @param x Tọa độ X (top-left)
     * @param y Tọa độ Y (top-left)
     * @param width Chiều rộng mục tiêu
     * @param height Chiều cao mục tiêu
     */
    public void drawSpriteScaled(Image img, double x, double y, double width, double height) {
        if (img == null) {
            System.err.println("SpriteRenderer.drawSpriteScaled: Image is null");
            return;
        }

        gc.drawImage(img, x, y, width, height); // Scale không đồng nhất theo W/H
    }

    /**
     * Vẽ một sprite với các phép biến đổi kết hợp (tỉ lệ, xoay, alpha).
     *
     * Kết hợp nhiều phép biến đổi trong một lần gọi cho hiệu ứng phức tạp.
     * Thứ tự biến đổi: Scale → Xoay → Alpha
     *
     * Cách sử dụng:
     * - Hiệu ứng hạt phức tạp
     * - Hiệu ứng xuất hiện powerup
     * - Hiệu ứng boss/kẻ địch
     * - Hiệu ứng chuyển cảnh
     *
     * Lưu ý hiệu suất:
     * - Đây là thao tác vẽ tốn tài nguyên nhất
     * - Chỉ dùng khi cần nhiều biến đổi cùng lúc
     * - Cân nhắc cache cho các mẫu lặp lại
     *
     * @param img Ảnh cần vẽ
     * @param x Tọa độ X của tâm sprite
     * @param y Tọa độ Y của tâm sprite
     * @param scale Hệ số tỉ lệ (1.0 = gốc)
     * @param angle Góc xoay (độ)
     * @param alpha Độ trong suốt (0.0 đến 1.0)
     */
    public void drawSpriteTransformed(Image img, double x, double y,
                                      double scale, double angle, double alpha) {
        if (img == null) {
            System.err.println("SpriteRenderer.drawSpriteTransformed: Image is null");
            return;
        }

        // Lưu trạng thái
        gc.save(); // Bảo toàn transform + alpha hiện tại

        // Áp dụng alpha
        alpha = Math.max(0.0, Math.min(1.0, alpha)); // Clamp 0..1
        gc.setGlobalAlpha(alpha); // Thiết lập độ trong suốt

        // Tính toán kích thước sau khi scale
        double scaledWidth = img.getWidth() * scale;  // W sau scale
        double scaledHeight = img.getHeight() * scale; // H sau scale

        // Tạo phép xoay quanh tâm
        double centerX = x;
        double centerY = y;
        Rotate rotate = new Rotate(angle, centerX, centerY); // Xoay quanh (x, y)
        gc.setTransform(
                rotate.getMxx(), rotate.getMyx(),
                rotate.getMxy(), rotate.getMyy(),
                rotate.getTx(), rotate.getTy()
        ); // Áp ma trận xoay

        // Vẽ sprite đã scale căn giữa
        double drawX = centerX - scaledWidth / 2.0; // Tính toạ độ top-left để tâm khớp (x,y)
        double drawY = centerY - scaledHeight / 2.0;
        gc.drawImage(img, drawX, drawY, scaledWidth, scaledHeight); // Vẽ với transform + alpha

        // Khôi phục trạng thái
        gc.restore(); // Kết thúc: GC trở về như trước khi vẽ
    }

    // ==================== ANIMATION RENDERING ====================

    /**
     * Vẽ một Animation object (được sử dụng cho PowerUps và SilverBrick).
     *
     * Method này vẽ frame hiện tại của animation tại vị trí chỉ định.
     * Animation phải được update() ở nơi khác trước khi render.
     *
     * Sử dụng:
     * - PowerUp falling animation (xoay liên tục)
     * - SilverBrick crack animation (1 lần)
     *
     * @param animation Animation object chứa frames
     * @param x Tọa độ X (top-left)
     * @param y Tọa độ Y (top-left)
     */
    public void drawAnimation(Animation animation, double x, double y) {
        if (animation == null) {
            System.err.println("SpriteRenderer.drawAnimation: Animation is null");
            return;
        }

        Image currentFrame = animation.getCurrentFrame(); // Lấy frame hiện hành để vẽ
        if (currentFrame != null) {
            drawSprite(currentFrame, x, y); // Tận dụng hàm vẽ cơ bản
        }
    }

    /**
     * Vẽ một Animation object với width và height cụ thể.
     *
     * Dùng khi cần scale animation về kích thước khác với sprite gốc.
     *
     * @param animation Animation object chứa frames
     * @param x Tọa độ X (top-left)
     * @param y Tọa độ Y (top-left)
     * @param width Chiều rộng vẽ
     * @param height Chiều cao vẽ
     */
    public void drawAnimation(Animation animation, double x, double y, double width, double height) {
        if (animation == null) {
            System.err.println("SpriteRenderer.drawAnimation: Animation is null");
            return;
        }

        Image currentFrame = animation.getCurrentFrame(); // Lấy frame hiện hành
        if (currentFrame != null) {
            drawSprite(currentFrame, x, y, width, height); // Vẽ với W/H mong muốn
        }
    }

    /**
     * Vẽ một Animation object với rotation (dùng cho PowerUp rơi).
     *
     * PowerUp sẽ vừa rơi xuống vừa xoay animation frame, tạo hiệu ứng
     * chuyển động mượt mà và bắt mắt.
     *
     * @param animation Animation object chứa frames
     * @param x Tọa độ X (center)
     * @param y Tọa độ Y (center)
     * @param angle Góc xoay (độ)
     */
    public void drawAnimationRotated(Animation animation, double x, double y, double angle) {
        if (animation == null) {
            System.err.println("SpriteRenderer.drawAnimationRotated: Animation is null");
            return;
        }

        Image currentFrame = animation.getCurrentFrame(); // Lấy frame hiện hành
        if (currentFrame != null) {
            drawSpriteRotated(currentFrame, x, y, angle); // Vẽ frame dưới dạng xoay quanh tâm
        }
    }
}
