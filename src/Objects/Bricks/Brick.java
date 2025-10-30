package Objects.Bricks;

import Objects.Core.GameObject;
import GeometryPrimitives.Rectangle;
import GeometryPrimitives.Point;

/**
 * Đại diện cho một viên gạch trong trò chơi.
 * Lưu trữ vị trí, kích thước, số lần bị đánh và trạng thái sống/chết.
 * Represents a brick object in the game with position, size,
 * durability (hit points) and alive/dead state.
 */
/**
 * Lớp trừu tượng mô tả một viên gạch trong game phá gạch.
 * Viên gạch có vị trí trong không gian 2D, kích thước, số lần bị đánh (hit points)
 * và trạng thái tồn tại trong màn chơi.
 *
 * Đây là lớp nền để xây dựng các loại gạch khác nhau như:
 * - SilverBrick: cần nhiều hit hơn để phá
 * - ExplosiveBrick: gây nổ phá vùng xung quanh khi bị phá
 * - MovingBrick: gạch có chuyển động theo thời gian
 *
 * Abstract class representing a brick object in the game.
 * Stores position, dimensions, durability, and alive/dead state.
 */
public abstract class Brick implements GameObject {
    /** Vị trí trục X của viên gạch - The X coordinate */
    private double x;
    /** Vị trí trục Y của viên gạch - The Y coordinate */
    private double y;
    /** Chiều rộng của viên gạch - Brick width */
    private double width;
    /** Chiều cao của viên gạch - Brick height */
    private double height;
    /** Số lần bóng cần va chạm để phá hủy - Remaining durability */
    private int hitPoints;
    /** Trạng thái còn sống hay đã bị phá - Alive/Destroyed state */
    private boolean alive;

    /**
     * Khởi tạo một viên gạch mới với vị trí, kích thước và số hit ban đầu.
     * Initialize a brick with position, size and hit points.
     */
    public Brick(double x, double y, double width, double height, int hitPoints) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hitPoints = hitPoints;
        this.alive = true;
    }

    /**
     * Xử lý khi viên gạch bị bóng đánh trúng.
     * Giảm hitPoint và kiểm tra phá hủy.
     * Called when brick is hit; decreases hit points.
     */
    public void takeHit() {
        hitPoints--;
        if (hitPoints <= 0) {
            destroy();
        }
    }

    /**
     * Kiểm tra viên gạch đã bị phá hủy chưa.
     * Checks if the brick is destroyed.
     */
    public boolean isDestroyed() {
        return !alive;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(new Point(x, y), width, height);
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public void destroy() {
        alive = false;
    }

    /**
     * Cập nhật trạng thái viên gạch (hiệu ứng, animation ...)
     * Update brick state (animations, effects, etc.)
     * Override trong các lớp con cần xử lý theo từng frame.
     */
    public void update() {
        // Mặc định: không có logic cập nhật
        // Default: no update logic
        // Subclasses like SilverBrick override this
    }

    /**
     * Trả về loại của viên gạch (vàng, bạc,...)
     * Returns the brick type
     */
    public abstract BrickType getBrickType();

    /** Lấy vị trí X của gạch - Get X position */
    public double getX() {
        return x;
    }

    /** Lấy vị trí Y của gạch - Get Y position */
    public double getY() {
        return y;
    }

    /** Lấy chiều rộng - Get width */
    public double getWidth() {
        return width;
    }

    /** Lấy chiều cao - Get height */
    public double getHeight() {
        return height;
    }

    /** Lấy số hit còn lại - Get remaining hit points */
    public int getHitPoints() {
        return hitPoints;
    }

}
