package Objects.Core;

import GeometryPrimitives.Point;
import GeometryPrimitives.Rectangle;
import GeometryPrimitives.Velocity;

/**
 * Lớp cơ sở cho các đối tượng có thể di chuyển (có vị trí, kích thước và vận tốc).
 *
 * Cung cấp tiện ích:
 * - move(): cập nhật vị trí theo {@link GeometryPrimitives.Velocity} (dx/dy per-frame).
 * - getBounds(): trả về bounding box cho va chạm.
 */
public abstract class MovableObject implements GameObject {
    private double x;
    private double y;
    private double width;
    private double height;
    private Velocity velocity; // per-frame dx/dy
    private boolean alive = true;

    public MovableObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocity = new Velocity(0, 0);
    }

    /**
     * Chuyển đối tượng theo vận tốc hiện tại (gọi mỗi frame trong update).
     */
    public void move() {
        // velocity is per-frame
        this.x += velocity.getDx();
        this.y += velocity.getDy();
    }

    public void setVelocity(Velocity v) { this.velocity = v; }
    public Velocity getVelocity() { return this.velocity; }

    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public double getWidthValue() { return width; }
    public double getHeightValue() { return height; }
    
    // Standard accessors (preferred)
    public double getWidth() { return this.width; }
    public double getHeight() { return this.height; }
    public void setWidth(double width) { this.width = width; }
    public void setHeight(double height) { this.height = height; }

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
}
