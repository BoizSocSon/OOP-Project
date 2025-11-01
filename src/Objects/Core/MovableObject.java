package Objects.Core;

import GeometryPrimitives.Point;
import GeometryPrimitives.Rectangle;
import GeometryPrimitives.Velocity;

/**
 * Lớp trừu tượng đại diện cho mọi đối tượng có thể di chuyển trong trò chơi.
 * <p>
 * Cung cấp các thuộc tính và hành vi cơ bản cho việc xử lý chuyển động,
 * bao gồm vị trí, kích thước, vận tốc và trạng thái sống/chết.
 * Các lớp con như {@code Ball}, {@code Paddle}... sẽ kế thừa để
 * định nghĩa hành vi cụ thể.
 * </p>
 */
public abstract class MovableObject implements GameObject {
    /** Tọa độ góc trên bên trái của đối tượng. */
    private double x;
    private double y;

    /** Kích thước chiều rộng và chiều cao của đối tượng. */
    private double width;
    private double height;

    /** Vận tốc di chuyển (theo trục X và Y). */
    private Velocity velocity;

    /** Trạng thái sống của đối tượng (true = còn tồn tại, false = bị phá hủy). */
    private boolean alive;

    /**
     * Khởi tạo một đối tượng có thể di chuyển với vị trí và kích thước xác định.
     *
     * @param x      hoành độ của đối tượng.
     * @param y      tung độ của đối tượng.
     * @param width  chiều rộng của đối tượng.
     * @param height chiều cao của đối tượng.
     */
    public MovableObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocity = new Velocity(0, 0);
        this.alive = true;
    }

    /**
     * Di chuyển đối tượng dựa trên vận tốc hiện tại.
     * <p>
     * Phương thức này cộng thêm dx, dy của {@link Velocity}
     * vào tọa độ hiện tại của đối tượng.
     * </p>
     */
    public void move() {
        this.x += this.velocity.getDx();
        this.y += this.velocity.getDy();
    }

    /** Thiết lập vận tốc mới cho đối tượng. */
    public void setVelocity(Velocity velocity) {
        this.velocity = velocity;
    }

    /** @return vận tốc hiện tại của đối tượng. */
    public Velocity getVelocity() {
        return velocity;
    }

    /** @return hoành độ hiện tại. */
    public double getX() {
        return x;
    }

    /** @return tung độ hiện tại. */
    public double getY() {
        return y;
    }

    /** @return chiều rộng của đối tượng. */
    public double getWidth() {
        return width;
    }

    /** @return chiều cao của đối tượng. */
    public double getHeight() {
        return height;
    }

    /** Thiết lập hoành độ mới. */
    public void setX(double x) {
        this.x = x;
    }

    /** Thiết lập tung độ mới. */
    public void setY(double y) {
        this.y = y;
    }

    /** Thiết lập chiều rộng mới. */
    public void setWidth(double width) {
        this.width = width;
    }

    /** Thiết lập chiều cao mới. */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * Lấy vùng bao (hitbox) của đối tượng — dùng cho va chạm.
     *
     * @return đối tượng {@link Rectangle} mô tả vùng chiếm chỗ của đối tượng.
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(new Point(x, y), width, height);
    }

    /**
     * Kiểm tra trạng thái sống của đối tượng.
     *
     * @return {@code true} nếu đối tượng còn tồn tại trong game, ngược lại {@code false}.
     */
    @Override
    public boolean isAlive() {
        return alive;
    }

    /**
     * Phá hủy đối tượng — đặt trạng thái {@code alive = false}.
     */
    @Override
    public void destroy() {
        alive = false;
    }
}
