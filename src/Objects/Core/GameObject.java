package Objects.Core;

import GeometryPrimitives.Rectangle;

/**
 * Giao diện {@code GameObject} định nghĩa các hành vi cơ bản
 * mà mọi đối tượng trong trò chơi (như gạch, bóng, thanh trượt, vật thể, v.v.) cần phải có.
 *
 * <p>Các lớp triển khai interface này phải cung cấp logic cập nhật,
 * vùng va chạm, trạng thái sống/chết và cơ chế phá hủy của đối tượng.</p>
 *
 * <p>Interface này là một phần của hệ thống lõi trò chơi ({@code Objects.Core}).</p>
 */
public interface GameObject {

    /**
     * Cập nhật trạng thái của đối tượng trong mỗi khung hình (frame).
     * <p>Phương thức này thường được gọi trong vòng lặp game để cập nhật vị trí,
     * hiệu ứng, hoặc các thay đổi theo thời gian.</p>
     */
    void update();

    /**
     * Trả về vùng bao (hitbox) của đối tượng, được sử dụng cho việc phát hiện va chạm.
     *
     * @return một {@link Rectangle} biểu diễn vùng va chạm của đối tượng.
     */
    Rectangle getBounds();

    /**
     * Kiểm tra xem đối tượng còn tồn tại (alive) trong trò chơi hay không.
     *
     * @return {@code true} nếu đối tượng còn hoạt động, ngược lại {@code false}.
     */
    boolean isAlive();

    /**
     * Phá hủy đối tượng, đánh dấu rằng nó không còn tồn tại hoặc không còn được vẽ/cập nhật.
     * <p>Phương thức này thường được gọi khi đối tượng bị loại bỏ (ví dụ: gạch vỡ, bóng mất, v.v.).</p>
     */
    void destroy();
}
