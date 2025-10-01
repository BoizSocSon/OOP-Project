package Objects;

import Render.Renderer;
import GeometryPrimitives.Rectangle;

/**
 * Giao diện chung cho mọi đối tượng xuất hiện trong thế giới game.
 *
 * Các đối tượng phải cung cấp:
 * - update(): tiến hành logic mỗi frame.
 * - render(Renderer): vẽ lên bề mặt thông qua abstraction {@code Renderer}.
 * - getBounds(): trả về {@link GeometryPrimitives.Rectangle} cho va chạm và vẽ.
 * - isAlive()/destroy(): quản lý vòng đời đơn giản.
 */
public interface GameObject {
    /**
     * Được gọi một lần mỗi khung hình để cập nhật logic của đối tượng.
     */
    void update();

    /**
     * Vẽ đối tượng bằng cách sử dụng trình kết xuất (renderer) được cung cấp.
     */
    void render(Renderer renderer);

    /**
     * Trả về hình chữ nhật bao quanh (axis-aligned bounding box)
     * để sử dụng cho va chạm và hiển thị.
     */
    Rectangle getBounds();

    /**
     * Trả về true nếu đối tượng hiện đang hoạt động / còn sống trong thế giới.
     */
    boolean isAlive();

    /**
     * Đánh dấu đối tượng đã bị phá hủy / không còn hoạt động.
     */
    void destroy();
}

