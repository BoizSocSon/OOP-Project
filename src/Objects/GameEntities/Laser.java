package Objects.GameEntities;

import GeometryPrimitives.Velocity;
import Objects.Core.MovableObject;
import Utils.Constants;

/**
 * <p>Lớp đại diện cho đối tượng **Tia Laser** được bắn ra trong trò chơi.
 * Tia Laser là một {@link MovableObject} di chuyển thẳng đứng lên trên
 * và tự hủy khi ra khỏi màn hình hoặc va chạm với mục tiêu.</p>
 */
public class Laser extends MovableObject{

    /** Trạng thái bị phá hủy của tia laser (true nếu đã bị phá hủy hoặc ra khỏi màn hình). */
    private boolean destroyed;

    /**
     * <p>Constructor khởi tạo một tia laser.</p>
     * <p>Tự động thiết lập kích thước và vận tốc ban đầu (di chuyển lên trên).</p>
     *
     * @param x Tọa độ x của góc trên bên trái tia laser.
     * @param y Tọa độ y của góc trên bên trái tia laser.
     */
    public Laser(double x, double y) {
        // Gọi constructor của lớp cha (MovableObject) để thiết lập vị trí và kích thước
        super(x, y, Constants.Laser.LASER_WIDTH, Constants.Laser.LASER_HEIGHT);

        // Thiết lập vận tốc: dx=0 (không di chuyển ngang), dy = -LASER_SPEED (di chuyển lên trên)
        setVelocity(new Velocity(0, -Constants.Laser.LASER_SPEED));

        this.destroyed = false; // Ban đầu tia laser chưa bị phá hủy
    }

    /**
     * <p>Cập nhật trạng thái và vị trí của tia laser trong mỗi vòng lặp game.</p>
     * <p>Chỉ di chuyển nếu tia laser chưa bị phá hủy.</p>
     */
    @Override
    public void update() {
        if (destroyed) {
            // Nếu đã bị phá hủy, không làm gì cả
            return;
        }
        // Di chuyển tia laser theo vận tốc đã thiết lập
        move();
    }

    /**
     * <p>Kiểm tra xem tia laser đã di chuyển ra khỏi giới hạn phía trên màn hình hay chưa.</p>
     *
     * @return {@code true} nếu tia laser đã vượt qua giới hạn trên của khu vực chơi, ngược lại {@code false}.
     */
    public boolean isOffScreen() {
        // Kiểm tra nếu tọa độ y + chiều cao (đáy của laser) nhỏ hơn giới hạn trên
        return getY() + getHeight() < Constants.Window.WINDOW_TOP_OFFSET + Constants.Borders.BORDER_TOP_HEIGHT;
    }

    /**
     * <p>Thiết lập trạng thái của tia laser là bị phá hủy.</p>
     * <p>Khi bị phá hủy, tia laser sẽ ngừng cập nhật và sẽ được loại bỏ khỏi game.</p>
     */
    public void destroy() {
        this.destroyed = true;
    }

    /**
     * <p>Kiểm tra xem tia laser có còn hoạt động (alive) hay không.</p>
     *
     * @return {@code true} nếu tia laser chưa bị phá hủy, ngược lại {@code false}.
     */
    @Override
    public boolean isAlive() {
        return !destroyed;
    }
}