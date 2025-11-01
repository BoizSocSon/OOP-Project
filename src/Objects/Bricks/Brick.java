package Objects.Bricks;

import Objects.Core.GameObject;
import GeometryPrimitives.Rectangle;
import GeometryPrimitives.Point;

/**
 * Lớp trừu tượng Brick đại diện cho một viên gạch trong trò chơi.
 * Mỗi viên gạch có vị trí, kích thước, số lần bị đánh (hitPoints),
 * và trạng thái sống/chết (alive).
 *
 * <p>Lớp này triển khai interface {@link GameObject} và được kế thừa
 * bởi các loại gạch cụ thể như gạch thường, gạch đặc biệt, v.v.</p>
 */
public abstract class Brick implements GameObject {
    // Tọa độ góc trên bên trái của viên gạch
    private double x;
    private double y;

    // Kích thước của viên gạch
    private double width;
    private double height;

    // Số lần gạch có thể chịu đòn trước khi bị phá hủy
    private int hitPoints;

    // Trạng thái của viên gạch: true = còn sống, false = đã bị phá hủy
    private boolean alive;

    /**
     * Khởi tạo một viên gạch mới.
     *
     * @param x         Tọa độ X của viên gạch
     * @param y         Tọa độ Y của viên gạch
     * @param width     Chiều rộng của viên gạch
     * @param height    Chiều cao của viên gạch
     * @param hitPoints Số lần có thể bị đánh trước khi bị phá
     */
    public Brick(double x, double y, double width, double height, int hitPoints) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hitPoints = hitPoints;
        this.alive = true; // Gạch khởi tạo luôn ở trạng thái sống
    }

    /**
     * Giảm số lần chịu đòn của gạch. Nếu số lần chịu đòn <= 0, gạch bị phá hủy.
     */
    public void takeHit() {
        hitPoints--;
        if (hitPoints <= 0) {
            destroy(); // Gọi phương thức destroy khi hết máu
        }
    }

    /**
     * Kiểm tra xem gạch đã bị phá hủy hay chưa.
     *
     * @return true nếu gạch đã bị phá, ngược lại false.
     */
    public boolean isDestroyed() {
        return !alive;
    }

    /**
     * Trả về hình chữ nhật đại diện cho vùng va chạm (hitbox) của gạch.
     *
     * @return Một đối tượng {@link Rectangle} mô tả vị trí và kích thước gạch.
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(new Point(x, y), width, height);
    }

    /**
     * Trả về trạng thái sống của gạch.
     *
     * @return true nếu còn sống, false nếu đã bị phá.
     */
    @Override
    public boolean isAlive() {
        return alive;
    }

    /**
     * Phá hủy gạch bằng cách đặt alive = false.
     */
    @Override
    public void destroy() {
        alive = false;
    }

    /**
     * Phương thức update — có thể được ghi đè trong các lớp con để cập nhật trạng thái gạch.
     * (ví dụ: gạch đổi màu, gạch chuyển động, v.v.)
     */
    public void update() {
        // Mặc định không làm gì
    }

    /**
     * Trả về loại gạch (dựa vào enum BrickType).
     *
     * @return Loại gạch (BrickType)
     */
    public abstract BrickType getBrickType();

    /** @return Tọa độ X của gạch */
    public double getX() {
        return x;
    }

    /** @return Tọa độ Y của gạch */
    public double getY() {
        return y;
    }

    /** @return Chiều rộng của gạch */
    public double getWidth() {
        return width;
    }

    /** @return Chiều cao của gạch */
    public double getHeight() {
        return height;
    }

    /** @return Số lần gạch còn có thể chịu đòn */
    public int getHitPoints() {
        return hitPoints;
    }
}
