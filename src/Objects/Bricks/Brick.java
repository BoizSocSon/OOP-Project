package Objects.Bricks;

import GeometryPrimitives.Point;
import GeometryPrimitives.Rectangle;
import Objects.Core.GameObject;

/**
 * Lớp trừu tượng cho các viên gạch (brick) có thể bị phá hủy.
 *
 * Thuộc tính chính:
 * - Vị trí và kích thước ({@code x, y, width, height}).
 * - {@code hitPoints}: số lần cần trúng để bị phá hủy.
 * - {@code alive}: cờ đánh dấu còn tồn tại hay đã bị hủy.
 *
 * Hành vi:
 * - {@link #takeHit()} giảm một đơn vị HP và gọi {@link #destroy()} khi HP <= 0.
 */
public abstract class Brick implements GameObject {
    private double x;
    private double y;
    private double width;
    private double height;
    private int hitPoints;
    private boolean alive = true;

    public Brick(double x, double y, double width, double height, int hp) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hitPoints = hp;
    }

    // Accessors
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public int getHitPoints() { return hitPoints; }

    /** Giảm số "máu" (hit points) đi 1; phá hủy khi hp <= 0. */
    public void takeHit() {
        hitPoints--;
        if (hitPoints <= 0)
            destroy();
    }

    /** Trả về true nếu viên gạch đã bị phá hủy. */
    public boolean isDestroyed() {
        return !alive;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(new Point(x,y), width, height);
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public void destroy() {
        alive = false;
    }
}
