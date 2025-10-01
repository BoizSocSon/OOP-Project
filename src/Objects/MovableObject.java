package Objects;

import GeometryPrimitives.Point;
import GeometryPrimitives.Rectangle;
import GeometryPrimitives.Velocity;

/**
 * MovableObject provides a small convenience base implementation for GameObject
 * instances that have a position, size and a per-frame velocity. This class
 * centralizes common movement and bounding rectangle logic.
 *
 * Note: the project uses a per-frame velocity model (dx/dy measured in pixels
 * per frame). If you switch to timestep-based updates, adapt move()/velocity
 * usage accordingly.
 */
public abstract class MovableObject implements GameObject {
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected Velocity velocity; // per-frame dx/dy
    protected boolean alive = true;

    public MovableObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocity = new Velocity(0, 0);
    }

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

    @Override
    public Rectangle getBounds() {
        return new Rectangle(new Point(x, y), width, height);
    }

    @Override
    public boolean isAlive() { return alive; }

    @Override
    public void destroy() { alive = false; }
}
