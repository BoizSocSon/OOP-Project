package Objects.Core;

//import Render.Renderer;
import GeometryPrimitives.Rectangle;

/**
 * <p>Giao diện (Interface) cơ bản cho tất cả các đối tượng trong trò chơi
 * (Game Object) có thể tương tác, có vị trí và trạng thái sống/chết.
 * Tất cả các thực thể trong game nên triển khai giao diện này.</p>
 */
public interface GameObject {

    /**
     * <p>Cập nhật trạng thái của đối tượng trong mỗi vòng lặp game.
     * Phương thức này được gọi thường xuyên để xử lý logic, chuyển động,
     * hoặc thay đổi trạng thái theo thời gian.</p>
     */
    void update();

    /**
     * <p>Trả về hình chữ nhật giới hạn (bounding box) của đối tượng.
     * Hình chữ nhật này được sử dụng để kiểm tra va chạm (collision detection).</p>
     *
     * @return Đối tượng {@link Rectangle} đại diện cho giới hạn vật lý của đối tượng.
     */
    Rectangle getBounds();

    /**
     * <p>Kiểm tra xem đối tượng còn "sống" (active) và có nên được xử lý trong game hay không.</p>
     *
     * @return {@code true} nếu đối tượng còn sống, ngược lại {@code false}.
     */
    boolean isAlive();

    /**
     * <p>Thiết lập trạng thái của đối tượng là bị phá hủy (destroyed) hoặc không còn sống.
     * Sau khi gọi phương thức này, {@link #isAlive()} thường sẽ trả về {@code false}.</p>
     */
    void destroy();
}