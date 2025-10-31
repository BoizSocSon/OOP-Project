package Objects.GameEntities;

import GeometryPrimitives.Velocity;
import Objects.Core.MovableObject;
import Utils.Constants;

/**
 * Lớp Laser đại diện cho viên đạn laser được bắn ra từ Paddle.
 * Nó kế thừa từ MovableObject và có vận tốc cố định hướng lên trên.
 */
public class Laser extends MovableObject{
    // Cờ báo hiệu laser đã bị phá hủy và không cần cập nhật/vẽ nữa
    private boolean destroyed;

    /**
     * Khởi tạo một đối tượng Laser mới.
     * Laser luôn được tạo với kích thước cố định và vận tốc hướng lên trên.
     *
     * @param x Tọa độ x ban đầu (vị trí bắn).
     * @param y Tọa độ y ban đầu (vị trí bắn).
     */
    public Laser(double x, double y) {
        // Gọi constructor của lớp cha để thiết lập vị trí và kích thước
        super(x, y, Constants.Laser.LASER_WIDTH, Constants.Laser.LASER_HEIGHT);
        // Thiết lập vận tốc: chỉ di chuyển theo trục y (hướng lên) với tốc độ cố định
        setVelocity(new Velocity(0, -Constants.Laser.LASER_SPEED));
        // Khởi tạo trạng thái chưa bị phá hủy
        this.destroyed = false;
    }

    /**
     * Cập nhật trạng thái của laser trong mỗi frame game.
     * Chỉ thực hiện di chuyển nếu laser chưa bị phá hủy.
     */
    @Override
    public void update() {
        if (destroyed) {
            return; // Không làm gì nếu đã bị phá hủy
        }
        // Di chuyển laser dựa trên vận tốc đã thiết lập
        move();
    }

    /**
     * Kiểm tra xem laser đã bay ra khỏi màn hình (vùng chơi phía trên) hay chưa.
     *
     * @return true nếu laser nằm ngoài ranh giới trên của màn hình, ngược lại là false.
     */
    public boolean isOffScreen() {
        // Laser được coi là "off screen" nếu cạnh dưới của nó vượt qua ranh giới trên cùng của khu vực chơi
        // (bao gồm cả offset cửa sổ và chiều cao đường viền trên)
        return getY() + getHeight() < Constants.Window.WINDOW_TOP_OFFSET + Constants.Borders.BORDER_TOP_HEIGHT;
    }

    /**
     * Đánh dấu laser là đã bị phá hủy.
     * Thường được gọi khi laser va chạm với gạch hoặc bay ra khỏi màn hình.
     */
    public void destroy() {
        this.destroyed = true;
    }

    /**
     * Kiểm tra xem laser còn "sống" (chưa bị phá hủy) hay không.
     *
     * @return true nếu laser chưa bị phá hủy, ngược lại là false.
     */
    @Override
    public boolean isAlive() {
        return !destroyed;
    }
}