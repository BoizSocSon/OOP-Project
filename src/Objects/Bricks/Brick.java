package Objects.Bricks;

import Objects.Core.GameObject;
import GeometryPrimitives.Rectangle;
import GeometryPrimitives.Point;

public abstract class Brick implements GameObject {
    private double x;
    private double y;
    private double width;
    private double height;
    private int hitPoints;
    private boolean alive;

    public Brick(double x, double y, double width, double height, int hitPoints) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hitPoints = hitPoints;
        this.alive = true;
    }

    public void takeHit() {
        hitPoints--;
        if (hitPoints <= 0) {
            destroy();
        }
    }

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
     * Update brick state (animations, effects, etc.)
     * Override in subclasses that need per-frame updates
     */
    public void update() {
        // Default: no update logic
        // Subclasses like SilverBrick override this
    }

    public abstract BrickType getBrickType();

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public int getHitPoints() {
        return hitPoints;
    }


}
