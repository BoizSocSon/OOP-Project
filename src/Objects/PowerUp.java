package Objects;

import GeometryPrimitives.Point;
import GeometryPrimitives.Rectangle;

/**
 * Lớp cơ sở cho các power-up rơi từ viên gạch bị phá.
 *
 * Thuộc tính:
 * - Vị trí/kích thước (x,y,width,height).
 * - duration: thời lượng hiệu ứng (tính bằng frame hoặc đơn vị do game quyết định).
 * - active: cờ còn tồn tại trên bản đồ hay đã bị thu/huỷ.
 */
public abstract class PowerUp implements GameObject {
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected double duration; // frames
    protected boolean active = true;

    public PowerUp(double x, double y, double width, double height, double duration) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.duration = duration;
    }

    /** Áp dụng hiệu ứng cho thanh trượt (paddle) hoặc mục tiêu khác. */
    public abstract void applyEffect(Paddle paddle);

    /** Gỡ bỏ hiệu ứng khi hết thời gian hoặc khi power-up bị xóa. */
    public abstract void removeEffect(Paddle paddle);

    @Override
    public Rectangle getBounds() {
        return new Rectangle(new Point(x,y), width, height);
    }

    @Override
    public boolean isAlive() {
        return active;
    }

    @Override
    public void destroy() {
        active = false;
    }
}
