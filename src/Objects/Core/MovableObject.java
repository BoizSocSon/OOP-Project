package Objects.Core;

import GeometryPrimitives.Point;
import GeometryPrimitives.Rectangle;
import GeometryPrimitives.Velocity;

/**
 * <p>Lớp trừu tượng (abstract class) đại diện cho một đối tượng trong game có khả năng **di chuyển**.
 * Lớp này mở rộng giao diện {@link GameObject} và cung cấp các thuộc tính cơ bản
 * như vị trí (x, y), kích thước (width, height), vận tốc (velocity), và trạng thái sống (alive).</p>
 */
public abstract class MovableObject implements GameObject {

    /** Tọa độ x của góc trên bên trái đối tượng. */
    private double x;

    /** Tọa độ y của góc trên bên trái đối tượng. */
    private double y;

    /** Chiều rộng của đối tượng. */
    private double width;

    /** Chiều cao của đối tượng. */
    private double height;

    /** Vận tốc (thay đổi vị trí dx/dy) của đối tượng trong mỗi khung hình. */
    private Velocity velocity; // per-frame velocity dx/dy

    /** Trạng thái sống của đối tượng (true: đang hoạt động, false: bị phá hủy). */
    private boolean alive;

    /**
     * <p>Constructor khởi tạo một đối tượng di chuyển mới.</p>
     * <p>Mặc định, vận tốc được đặt là (0, 0) và trạng thái là còn sống (alive).</p>
     *
     * @param x Tọa độ x ban đầu.
     * @param y Tọa độ y ban đầu.
     * @param width Chiều rộng của đối tượng.
     * @param height Chiều cao của đối tượng.
     */
    public MovableObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        // Khởi tạo vận tốc ban đầu bằng 0 (đứng yên)
        this.velocity = new Velocity(0, 0);
        this.alive = true;
    }

    /**
     * <p>Di chuyển đối tượng bằng cách cập nhật tọa độ (x, y) dựa trên vận tốc hiện tại.</p>
     * <p>x mới = x cũ + dx; y mới = y cũ + dy.</p>
     */
    public void move() {
        this.x += this.velocity.getDx();
        this.y += this.velocity.getDy();
    }

    /**
     * <p>Thiết lập vận tốc mới cho đối tượng.</p>
     *
     * @param velocity Đối tượng {@link Velocity} mới.
     */
    public void setVelocity(Velocity velocity) {
        this.velocity = velocity;
    }

    /**
     * <p>Thiết lập tọa độ x mới.</p>
     *
     * @param x Tọa độ x mới.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * <p>Thiết lập tọa độ y mới.</p>
     *
     * @param y Tọa độ y mới.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * <p>Thiết lập chiều rộng mới.</p>
     *
     * @param width Chiều rộng mới.
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * <p>Thiết lập chiều cao mới.</p>
     *
     * @param height Chiều cao mới.
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * <p>Trả về vận tốc hiện tại của đối tượng.</p>
     *
     * @return Đối tượng {@link Velocity} hiện tại.
     */
    public Velocity getVelocity() {
        return velocity;
    }

    /**
     * <p>Trả về tọa độ x hiện tại.</p>
     *
     * @return Tọa độ x.
     */
    public double getX() {
        return x;
    }

    /**
     * <p>Trả về tọa độ y hiện tại.</p>
     *
     * @return Tọa độ y.
     */
    public double getY() {
        return y;
    }

    /**
     * <p>Trả về chiều rộng của đối tượng.</p>
     *
     * @return Chiều rộng.
     */
    public double getWidth() {
        return width;
    }

    /**
     * <p>Trả về chiều cao của đối tượng.</p>
     *
     * @return Chiều cao.
     */
    public double getHeight() {
        return height;
    }

    /**
     * <p>Trả về hình chữ nhật giới hạn (bounding box) của đối tượng.</p>
     * <p>Triển khai từ giao diện {@link GameObject}.</p>
     *
     * @return Đối tượng {@link Rectangle} đại diện cho giới hạn va chạm.
     */
    @Override
    public Rectangle getBounds() {
        // Tạo Rectangle từ vị trí (x, y) và kích thước (width, height) hiện tại
        return new Rectangle(new Point(x, y), width, height);
    }

    /**
     * <p>Kiểm tra xem đối tượng có còn sống (active) hay không.</p>
     * <p>Triển khai từ giao diện {@link GameObject}.</p>
     *
     * @return {@code true} nếu {@link #alive} là true, ngược lại {@code false}.
     */
    @Override
    public boolean isAlive() {
        return alive;
    }

    /**
     * <p>Thiết lập trạng thái của đối tượng là bị phá hủy/chết.</p>
     * <p>Triển khai từ giao diện {@link GameObject}. Đặt {@link #alive} thành {@code false}.</p>
     */
    @Override
    public void destroy() {
        alive = false;
    }
}